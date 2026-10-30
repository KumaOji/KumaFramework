// Real Electron windows and sandboxed IPC, with simulated monitoring, cursor and display conditions.
const {app,BrowserWindow,screen}=require('electron');
const assert=require('node:assert/strict');
const http=require('node:http');
const fs=require('node:fs/promises');
const path=require('node:path');
const {createFloatingOverview}=require('../electron/floating-overview.cjs');
const output=path.resolve(__dirname,'../build/floating-overview-smoke');
app.setPath('userData',path.join(output,'profile'));
app.commandLine.appendSwitch('disable-gpu');
let main,server,fail=false,requests=0;
const pause=ms=>new Promise(resolve=>setTimeout(resolve,ms));
async function until(check,message){for(let i=0;i<100;i++){if(await check())return;await pause(40);}throw Error(message);}
app.whenReady().then(async()=>{
  await fs.mkdir(output,{recursive:true});
  server=http.createServer((request,response)=>{
    assert.equal(request.url,'/api/host');requests++;
    if(fail){response.writeHead(503);response.end();return;}
    response.setHeader('Content-Type','application/json');
    response.end(JSON.stringify({ready:true,sampledAt:new Date().toISOString(),hostname:'Kuma Desktop',os:'Windows 11',
      uptime:7380,cpu:{percent:23.5,logicalCores:16},memory:{total:32*1024**3,available:12*1024**3},
      disks:[{total:1024**4,free:400*1024**3}],processCount:236,
      networks:[{download:1024**2,upload:256*1024}],processes:[{name:'private-process'}],hardware:{computer:'private-id'}}));
  });
  await new Promise(resolve=>server.listen(0,'127.0.0.1',resolve));
  main=new BrowserWindow({width:1060,height:720,show:false,webPreferences:{
    preload:path.resolve(__dirname,'../electron/floating-preload.cjs'),nodeIntegration:false,contextIsolation:true,sandbox:true}});
  await main.loadURL('data:text/html,<h1>Console test window</h1>');
  const control=createFloatingOverview({mainWindow:main,origin:`http://127.0.0.1:${server.address().port}`});
  main.show();main.minimize();
  await until(()=>control.getWindow()?.isVisible(),'Minimize did not show the orb');
  const floating=control.getWindow(),evaluate=source=>floating.webContents.executeJavaScript(source);
  await until(()=>evaluate('document.getElementById("orb-value").textContent==="CPU 24%"'),'CPU did not update');
  assert.equal(floating.isAlwaysOnTop(),true);
  assert.equal(floating.getBounds().width,84);
  assert.equal(await evaluate('typeof window.require'),'undefined');
  assert.equal(await evaluate('document.getElementById("overview").hidden'),true);
  await assert.rejects(main.webContents.executeJavaScript('window.kumaOverview.expand(true)'),/不允许此页面/);
  await assert.rejects(main.webContents.executeJavaScript('window.kumaOverview.quit()'),/不允许此页面/);
  const sanitized=await evaluate('window.kumaOverview.host()');
  assert.equal(sanitized.processes,undefined);assert.equal(sanitized.hardware,undefined);
  // A drag uses OS screen coordinates, clamps to the monitor and must not open the card.
  const originalCursor=screen.getCursorScreenPoint;
  const initial=floating.getBounds();let cursor={x:initial.x+30,y:initial.y+30};
  screen.getCursorScreenPoint=()=>cursor;
  await evaluate(`document.getElementById('orb').dispatchEvent(new PointerEvent('pointerdown',{button:0,pointerId:1,screenX:${cursor.x},screenY:${cursor.y}}))`);
  cursor={x:cursor.x-140,y:cursor.y-90};
  await evaluate(`document.getElementById('orb').dispatchEvent(new PointerEvent('pointermove',{pointerId:1,screenX:${cursor.x},screenY:${cursor.y}}))`);
  await evaluate(`document.getElementById('orb').dispatchEvent(new PointerEvent('pointerup',{pointerId:1}));document.getElementById('orb').dispatchEvent(new MouseEvent('click',{detail:1}))`);
  assert.equal(floating.getBounds().x,initial.x-140);
  assert.equal(floating.getBounds().y,initial.y-90);
  assert.equal(await evaluate('document.getElementById("overview").hidden'),true);
  await evaluate('window.kumaOverview.drag("start")');cursor={x:-100000,y:-100000};
  await evaluate('window.kumaOverview.drag("move")');await evaluate('window.kumaOverview.drag("end")');
  const area=screen.getDisplayMatching(initial).workArea;
  assert.ok(floating.getBounds().x>=area.x);assert.ok(floating.getBounds().y>=area.y);
  const edge=floating.getBounds();
  await evaluate('window.kumaOverview.drag("start")');
  cursor={x:cursor.x-200,y:cursor.y-200};
  await evaluate('window.kumaOverview.drag("move")');
  cursor={x:cursor.x+12,y:cursor.y+12};
  await evaluate('window.kumaOverview.drag("move")');
  await evaluate('window.kumaOverview.drag("end")');
  assert.equal(floating.getBounds().x,edge.x+12,'Reversing at an edge left a horizontal dead zone');
  assert.equal(floating.getBounds().y,edge.y+12,'Reversing at an edge left a vertical dead zone');
  // Split the real work area into two monitors to reproduce crossing with the cursor
  // while the orb's center is still on the previous monitor.
  const originalNearest=screen.getDisplayNearestPoint;
  const seam=area.x+Math.floor(area.width/2);
  screen.getDisplayNearestPoint=point=>({workArea:point.x<seam?
    {...area,width:seam-area.x}:{...area,x:seam,width:area.x+area.width-seam}});
  floating.setBounds({x:seam-84,y:area.y+100,width:84,height:84});
  cursor={x:seam-10,y:area.y+142};
  await evaluate('window.kumaOverview.drag("start")');
  cursor={x:seam+5,y:cursor.y};
  await evaluate('window.kumaOverview.drag("move")');
  await evaluate('window.kumaOverview.drag("end")');
  assert.equal(floating.getBounds().x,seam,'The previous monitor blocked crossing at the cursor');
  screen.getDisplayNearestPoint=originalNearest;
  // Repeated counterclockwise loops must keep the cursor-to-orb offset and size.
  floating.setPosition(area.x+Math.floor(area.width/2),area.y+Math.floor(area.height/2));
  const loopOrigin=floating.getBounds(),nativeGetBounds=floating.getBounds.bind(floating);
  const loopCursor={x:loopOrigin.x+42,y:loopOrigin.y+42};
  for(const delayedReadback of [false,true]){
    cursor={...loopCursor};
    await evaluate('window.kumaOverview.drag("start")');
    if(delayedReadback)floating.getBounds=()=>({...loopOrigin});
    try {
      for(let step=1;step<=96;step++){
        const angle=step*Math.PI/12;
        cursor={x:loopCursor.x+Math.round(60*(Math.cos(angle)-1)),
          y:loopCursor.y-Math.round(60*Math.sin(angle))};
        await evaluate('window.kumaOverview.drag("move")');
        const actual=nativeGetBounds();
        assert.equal(actual.x,cursor.x-42,'Counterclockwise dragging accumulated horizontal drift');
        assert.equal(actual.y,cursor.y-42,'Counterclockwise dragging accumulated vertical drift');
        // Native pixels may round one DIP; they must never accumulate across moves.
        assert.ok(Math.abs(actual.width-loopOrigin.width)<=1,`Dragging grew the invisible window at step ${step}: ${actual.width}`);
        assert.ok(Math.abs(actual.height-loopOrigin.height)<=1,`Dragging grew the invisible window at step ${step}: ${actual.height}`);
      }
    } finally {
      floating.getBounds=nativeGetBounds;
      await evaluate('window.kumaOverview.drag("end")');
    }
  }
  screen.getCursorScreenPoint=originalCursor;
  // Real click opens; keyboard Escape closes; values are fetched through the real preload.
  floating.webContents.sendInputEvent({type:'mouseDown',x:42,y:42,button:'left',clickCount:1});
  floating.webContents.sendInputEvent({type:'mouseUp',x:42,y:42,button:'left',clickCount:1});
  await until(()=>evaluate('!document.getElementById("overview").hidden'),'Click did not expand');
  assert.equal(floating.getBounds().width,360);
  assert.equal(await evaluate('document.getElementById("memory").textContent'),'62.5%');
  assert.equal(await evaluate('document.getElementById("processes").textContent'),'236');
  assert.equal(await evaluate('document.getElementById("download").textContent'),'1.0 MiB/s');
  assert.ok(await evaluate('document.getElementById("overview").getBoundingClientRect().bottom<innerHeight'),'Panel is clipped');
  await pause(200);
  await fs.writeFile(path.join(output,'expanded.png'),(await floating.webContents.capturePage()).toPNG());
  const expandedBounds=floating.getBounds();
  cursor={x:expandedBounds.x+42,y:expandedBounds.y+42};
  screen.getCursorScreenPoint=()=>cursor;
  await evaluate('window.kumaOverview.drag("start")');
  cursor={x:area.x+area.width-42,y:area.y+area.height-42};
  await evaluate('window.kumaOverview.drag("move")');
  await evaluate('window.kumaOverview.drag("end")');
  assert.equal(floating.getBounds().width,84,'Dragging kept the expanded card boundary');
  assert.equal(floating.getBounds().x,area.x+area.width-84);
  assert.equal(floating.getBounds().y,area.y+area.height-84);
  await until(()=>evaluate('document.getElementById("overview").hidden'),'Dragging did not collapse the card');
  screen.getCursorScreenPoint=originalCursor;
  await evaluate('window.kumaOverview.expand(true)');
  fail=true;
  await until(()=>evaluate('document.getElementById("orb-value").textContent==="未连接"'),'Connection failure not shown');
  assert.equal(await evaluate('document.getElementById("cpu").textContent'),'23.5%');
  fail=false;
  await until(()=>evaluate('document.body.dataset.stale==="false"'),'Did not recover from disconnection');
  await evaluate('document.dispatchEvent(new KeyboardEvent("keydown",{key:"Escape"}))');
  await until(()=>floating.getBounds().width===84,'Escape did not collapse');
  await fs.writeFile(path.join(output,'orb.png'),(await floating.webContents.capturePage()).toPNG());
  await evaluate('document.getElementById("orb").click()');
  await until(()=>evaluate('!document.getElementById("overview").hidden'),'Second click did not expand');
  await evaluate('document.getElementById("restore").click()');
  await until(()=>!main.isMinimized()&&!floating.isVisible(),'Restore did not hide orb');
  await pause(100);const hiddenRequests=requests;await pause(2200);
  assert.equal(requests,hiddenRequests,'Hidden orb kept polling');
  main.minimize();
  await until(()=>floating.isVisible(),'Second minimize did not reuse orb');
  assert.equal(await evaluate('document.getElementById("overview").hidden'),true);
  main.destroy();
  assert.equal(floating.isDestroyed(),true,'Closing console left an orphan orb');
  server.close();
  console.log('PASS: minimize, click, drag vs click, edge reversal, monitor crossing, counterclockwise loops, delayed bounds readback, stable window size, expanded drag, screen bounds, overview data, disconnect/recovery, Escape, restore, hidden polling, IPC isolation and cleanup');
  app.exit(0);
}).catch(error=>{console.error(error);server?.close();app.exit(1);});
