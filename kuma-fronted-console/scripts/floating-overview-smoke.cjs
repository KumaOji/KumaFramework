// Real Electron windows and sandboxed IPC; only the monitoring endpoint and cursor are mocked.
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
  console.log('PASS: minimize, click, drag vs click, screen bounds, overview data, disconnect/recovery, Escape, restore, hidden polling, IPC isolation and cleanup');
  app.exit(0);
}).catch(error=>{console.error(error);server?.close();app.exit(1);});
