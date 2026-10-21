const {app,BrowserWindow,ipcMain,screen,Menu}=require('electron');
const path=require('node:path');
const {pathToFileURL}=require('node:url');

function createFloatingOverview({mainWindow,origin,isQuitting=()=>false}) {
  const page=pathToFileURL(path.join(__dirname,'floating-overview.html')).href;
  const channels=['overview-float:expand','overview-float:restore','overview-float:host','overview-float:drag','overview-float:quit'];
  let floating,loading,expanded=false,disposed=false,dragStart;
  function authorize(event) {
    if(!floating||event.sender!==floating.webContents||event.senderFrame!==floating.webContents.mainFrame||event.senderFrame.url!==page)
      throw Error('不允许此页面操作悬浮概览');
  }
  function clamp(bounds) {
    const area=screen.getDisplayNearestPoint({x:bounds.x+Math.min(bounds.width,84)/2,y:bounds.y+42}).workArea;
    return {x:Math.round(Math.max(area.x,Math.min(bounds.x,area.x+area.width-bounds.width))),
      y:Math.round(Math.max(area.y,Math.min(bounds.y,area.y+area.height-bounds.height))),
      width:bounds.width,height:bounds.height};
  }
  function publish() {
    if(floating&&!floating.isDestroyed())floating.webContents.send('overview-float:state',{visible:floating.isVisible(),expanded});
  }
  function expand(value) {
    if(!floating||floating.isDestroyed())return;
    expanded=value;
    floating.setBounds(clamp({...floating.getBounds(),width:value?360:84,height:value?560:84}));
    publish();
    if(value)floating.focus();
  }
  function hide() {
    if(!floating||floating.isDestroyed())return;
    dragStart=null;floating.hide();expand(false);
  }
  function restore() {
    if(disposed||isQuitting()||mainWindow.isDestroyed())return;
    hide();if(mainWindow.isMinimized())mainWindow.restore();mainWindow.show();mainWindow.focus();
  }
  async function show() {
    if(disposed||isQuitting())return;
    if(!floating) {
      const area=screen.getDisplayMatching(mainWindow.getBounds()).workArea;
      floating=new BrowserWindow({x:area.x+area.width-104,y:area.y+Math.round(area.height*.6),
        width:84,height:84,frame:false,transparent:true,hasShadow:false,show:false,
        resizable:false,maximizable:false,minimizable:false,skipTaskbar:true,alwaysOnTop:true,
        title:'Kuma 本机概览',webPreferences:{preload:path.join(__dirname,'floating-preload.cjs'),
          nodeIntegration:false,contextIsolation:true,sandbox:true,backgroundThrottling:false}});
      floating.webContents.setWindowOpenHandler(()=>({action:'deny'}));
      floating.webContents.on('will-navigate',event=>event.preventDefault());
      floating.on('close',event=>{if(!disposed&&!isQuitting()){event.preventDefault();restore();}});
      floating.webContents.on('context-menu',()=>{
        if(disposed||isQuitting())return;
        Menu.buildFromTemplate([
          {label:'打开 Kuma Console',click:restore},
          {type:'separator'},
          {label:'彻底退出 Kuma Console',click:()=>app.quit()}
        ]).popup({window:floating});
      });
      loading=floating.loadURL(page);
      // Handle load failures without creating unhandled minimize-event rejections.
      loading.catch(error=>console.error('悬浮概览加载失败:',error.message));
    }
    try {await loading;} catch {return;}
    if(disposed||isQuitting()||mainWindow.isDestroyed()||(mainWindow.isVisible()&&!mainWindow.isMinimized()))return;
    expand(false);floating.showInactive();publish();
  }
  ipcMain.handle(channels[0],(event,value)=>{authorize(event);if(typeof value!=='boolean')throw Error('无效状态');expand(value);});
  ipcMain.handle(channels[1],event=>{
    authorize(event);restore();
  });
  ipcMain.handle(channels[2],async event=>{
    authorize(event);
    if(!floating.isVisible())return null;
    const response=await fetch(new URL('/api/host',origin),{signal:AbortSignal.timeout(2500)});
    if(!response.ok)throw Error('本机监控暂时无法连接');
    const host=await response.json();
    // Share only the overview fields, never process lists or hardware identifiers.
    return {ready:host.ready,error:host.error,hostname:host.hostname,os:host.os,uptime:host.uptime,
      sampledAt:host.sampledAt,cpu:{percent:host.cpu?.percent,logicalCores:host.cpu?.logicalCores},
      memory:{total:host.memory?.total,available:host.memory?.available},
      disks:(host.disks||[]).map(({total,free})=>({total,free})),processCount:host.processCount,
      networks:(host.networks||[]).map(({download,upload})=>({download,upload}))};
  });
  ipcMain.handle(channels[3],(event,phase)=>{
    authorize(event);
    if(!floating.isVisible())return;
    if(phase==='start'){dragStart={cursor:screen.getCursorScreenPoint(),bounds:floating.getBounds()};return;}
    if(phase==='end'){dragStart=null;return;}
    if(phase!=='move'||!dragStart)return;
    const cursor=screen.getCursorScreenPoint();
    floating.setBounds(clamp({...floating.getBounds(),x:dragStart.bounds.x+cursor.x-dragStart.cursor.x,
      y:dragStart.bounds.y+cursor.y-dragStart.cursor.y}));
  });
  ipcMain.handle(channels[4],event=>{authorize(event);app.quit();});
  function fit(){if(floating&&!floating.isDestroyed())floating.setBounds(clamp(floating.getBounds()));}
  function dispose() {
    disposed=true;channels.forEach(channel=>ipcMain.removeHandler(channel));
    screen.removeListener('display-removed',fit);screen.removeListener('display-metrics-changed',fit);
    mainWindow.removeListener('minimize',show);mainWindow.removeListener('hide',show);mainWindow.removeListener('restore',hide);mainWindow.removeListener('show',hide);
    if(floating&&!floating.isDestroyed())floating.destroy();
  }
  mainWindow.on('minimize',show);mainWindow.on('hide',show);mainWindow.on('restore',hide);mainWindow.on('show',hide);
  mainWindow.once('closed',dispose);
  screen.on('display-removed',fit);screen.on('display-metrics-changed',fit);
  return {show,hide,dispose,getWindow:()=>floating};
}
module.exports={createFloatingOverview};
