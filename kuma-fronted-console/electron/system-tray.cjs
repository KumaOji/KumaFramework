const {app,Menu,Tray}=require('electron');
const path=require('node:path');

function createSystemTray({mainWindow,isQuitting}) {
  const tray=new Tray(path.join(__dirname,'../renderer/assets',process.platform==='win32'?'kuma-console.ico':'kuma-console.png'));
  function restore() {
    if(mainWindow.isDestroyed()||isQuitting())return;
    if(mainWindow.isMinimized())mainWindow.restore();
    mainWindow.show();mainWindow.focus();
  }
  function hide() {
    if(!isQuitting())mainWindow.hide();
  }
  function close(event) {
    if(isQuitting())return;
    event.preventDefault();hide();
  }
  tray.setToolTip('Kuma Console');
  const menu=Menu.buildFromTemplate([
    {label:'打开 Kuma Console',click:restore},
    {label:'隐藏到系统托盘',click:hide},
    {type:'separator'},
    {label:'退出 Kuma Console',click:()=>app.quit()}
  ]);
  tray.setContextMenu(menu);
  tray.on('click',restore);
  tray.on('double-click',restore);
  mainWindow.on('minimize',hide);
  mainWindow.on('close',close);
  // Windows shutdown must exit instead of retaining the hidden window.
  mainWindow.on('session-end',()=>app.quit());
  mainWindow.once('closed',()=>tray.destroy());
  return {tray,menu,restore,hide};
}
module.exports={createSystemTray};
