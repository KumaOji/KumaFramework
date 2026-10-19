// Verify the real sandboxed preload and UI without changing Windows startup settings.
const {app,BrowserWindow,ipcMain}=require('electron');
const assert=require('node:assert/strict');
const fs=require('node:fs/promises');
const path=require('node:path');
app.setPath('userData',path.resolve(__dirname,'../build/startup-settings-ui-profile'));
app.commandLine.appendSwitch('disable-gpu');
let enabled=true,failWrite=false,window;
const writes=[];
const pause=ms=>new Promise(resolve=>setTimeout(resolve,ms));
ipcMain.handle('desktop:startup:get',()=>enabled);
ipcMain.handle('desktop:startup:set',async(_event,value)=>{
  await pause(40);
  if(failWrite)throw Error('Simulated Windows settings failure');
  assert.equal(typeof value,'boolean');
  writes.push(value);enabled=value;return enabled;
});
app.whenReady().then(async()=>{
  const script=await fs.readFile(path.resolve(__dirname,'../renderer/desktop.js'),'utf8');
  const html='<body><div id="desktop-bar" hidden></div><button id="theme-toggle"></button><section id="overview"><div class="page-heading"></div></section><section id="settings"><div id="console-desktop-settings"></div></section></body>';
  window=new BrowserWindow({show:false,webPreferences:{preload:path.resolve(__dirname,'../electron/preload.cjs'),contextIsolation:true,sandbox:true,nodeIntegration:false}});
  await window.loadURL('data:text/html,'+encodeURIComponent(html));
  const evaluate=source=>window.webContents.executeJavaScript(source);
  await evaluate(script);
  const waitReady=async()=>{
    for(let i=0;i<100;i++){
      if(await evaluate('!document.getElementById("desktop-startup-toggle").disabled'))return;
      await pause(20);
    }
    throw Error('Startup settings did not become ready');
  };
  await waitReady();
  assert.equal(await evaluate('document.getElementById("desktop-settings").parentElement.id'),'console-desktop-settings');
  assert.equal(await evaluate('document.getElementById("desktop-startup-toggle").checked'),true);
  assert.equal(await evaluate('typeof window.require'), 'undefined');
  await evaluate('document.getElementById("desktop-startup-toggle").click()');
  assert.equal(await evaluate('document.getElementById("desktop-startup-toggle").disabled'),true);
  await waitReady();
  assert.equal(enabled,false);
  failWrite=true;
  await evaluate('document.getElementById("desktop-startup-toggle").click()');
  await waitReady();
  assert.equal(await evaluate('document.getElementById("desktop-startup-toggle").checked'),false);
  assert.equal(enabled,false);
  failWrite=false;
  await evaluate('document.getElementById("desktop-startup-toggle").click()');
  await waitReady();
  assert.equal(enabled,true);
  assert.deepEqual(writes,[false,true]);
  const desktopWindow=window;
  window=new BrowserWindow({show:false,webPreferences:{contextIsolation:true,sandbox:true,nodeIntegration:false}});
  await window.loadURL('data:text/html,'+encodeURIComponent(html));
  await window.webContents.executeJavaScript(script);
  assert.equal(await window.webContents.executeJavaScript('document.getElementById("desktop-settings")'),null);
  desktopWindow.destroy();
  console.log('PASS: startup state, save, pending disable, failure rollback, sandbox isolation, browser mode');
  app.exit(0);
}).catch(error=>{console.error(error);app.exit(1);});
