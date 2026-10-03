const {app, BrowserWindow, shell, dialog, Menu} = require('electron');
const {spawn} = require('node:child_process');
const fs = require('node:fs/promises');
const path = require('node:path');
const smoke=process.argv.includes('--smoke');
const port=Number(process.env.KUMA_CONSOLE_PORT||18090);
if(!Number.isInteger(port)||port<1024||port>65535)throw Error('KUMA_CONSOLE_PORT must be 1024–65535');
const origin=`http://127.0.0.1:${port}`;
const workspace=path.resolve(__dirname,'../..');
const smokeRoot=app.isPackaged?path.join(app.getPath('temp'),'kuma-console-desktop-smoke'):path.resolve(__dirname,'../build');
let window, ownedBackend, ownedBuild, quitting=false, shutdownComplete=false;
if(smoke){app.setPath('userData',path.join(smokeRoot,'smoke-profile'));app.commandLine.appendSwitch('disable-gpu');}
if(!smoke&&!app.requestSingleInstanceLock()){app.quit();}else{
  app.on('second-instance',()=>{if(window){if(window.isMinimized())window.restore();window.show();window.focus();}});
  app.whenReady().then(start).catch(async error=>{dialog.showErrorBox('Kuma Console 启动失败',error.message);await Promise.all([stopChild(ownedBuild),stopChild(ownedBackend)]);app.exit(1);});
}
const pause=ms=>new Promise(resolve=>setTimeout(resolve,ms));
async function identity(){
  try{
    const response=await fetch(`${origin}/api/identity`,{signal:AbortSignal.timeout(1200)});
    if(!response.ok)throw Error('port-occupied');
    const body=await response.json();
    if(body.application!=='kuma-local-console')throw Error('port-occupied');
    return true;
  }catch(error){if(error.message==='port-occupied')throw Error(`端口 ${port} 已被其他程序占用，请设置 KUMA_CONSOLE_PORT。`);return false;}
}
async function ensureBackend(){
  if(await identity())return;
  if(quitting)throw Error('启动已取消');
  let child;
  if(app.isPackaged){
    child=spawn('java',['--enable-preview','--enable-native-access=ALL-UNNAMED','-jar',
      path.join(process.resourcesPath,'backend/kuma-console.jar'),'--console.open-browser=false'],
      {cwd:app.getPath('userData'),env:{...process.env,KUMA_CONSOLE_OPEN_BROWSER:'false'},windowsHide:true});
    ownedBackend=child;
  }else{
    await new Promise((resolve,reject)=>{
      const windows=process.platform==='win32';
      const build=spawn(windows?'powershell.exe':'sh',windows?
        ['-NoProfile','-ExecutionPolicy','Bypass','-File',path.join(workspace,'kuma-fronted-console/scripts/build-backend.ps1')]:
        [path.join(workspace,'gradlew'),':kuma-project:kuma-project-console:bootJar','--console=plain'],{cwd:workspace,windowsHide:true});
      ownedBuild=build;
      let output='';build.stdout.on('data',data=>{output=(output+data).slice(-6000);});build.stderr.on('data',data=>{output=(output+data).slice(-6000);});
      build.on('error',reject);build.on('exit',code=>{if(ownedBuild===build)ownedBuild=undefined;code===0?resolve():reject(Error(`后端构建失败 (${code})\n${output}`));});
    });
    if(quitting)throw Error('启动已取消');
    const runtimeRoot=path.join(workspace,'kuma-project/kuma-project-console/build/runtime');
    await fs.mkdir(runtimeRoot,{recursive:true});
    const runtimeJar=path.join(runtimeRoot,`kuma-console-${process.pid}-${Date.now()}.jar`);
    await fs.copyFile(path.join(workspace,'kuma-project/kuma-project-console/build/libs/kuma-console.jar'),runtimeJar);
    if(quitting)throw Error('启动已取消');
    child=spawn('java',['--enable-preview','--enable-native-access=ALL-UNNAMED','-jar',
      runtimeJar,'--console.open-browser=false'],
      {cwd:workspace,env:{...process.env,KUMA_CONSOLE_OPEN_BROWSER:'false'},windowsHide:true});
    ownedBackend=child;
  }
  let lastOutput='',failure;
  const receive=buffer=>{lastOutput=(lastOutput+buffer.toString()).slice(-6000);};
  child.stdout.on('data',receive);child.stderr.on('data',receive);
  child.on('error',error=>{failure=error.message;});
  child.on('exit',code=>{if(code!==0&&!quitting)failure=`后台进程退出 (${code})\n${lastOutput}`;});
  const deadline=Date.now()+180000;
  while(Date.now()<deadline){
    if(quitting)throw Error('启动已取消');
    if(failure)throw Error(failure);
    if(await identity())return;
    await pause(500);
  }
  throw Error(`后端未能启动。请确认已安装 JDK 25。\n${lastOutput}`);
}
function external(url){try{const target=new URL(url);if(['http:','https:'].includes(target.protocol)&&!target.username&&!target.password)shell.openExternal(target.href).catch(()=>{});}catch{}}
async function start(){
  if(process.platform==='win32')app.setAppUserModelId('io.github.kumaoji.console');
  Menu.setApplicationMenu(null);
  window=new BrowserWindow({title:'Kuma Console',width:1520,height:1040,minWidth:1060,minHeight:720,
    show:!smoke,backgroundColor:'#f5f7fb',titleBarStyle:'hidden',
    titleBarOverlay:{color:'#101b32',symbolColor:'#cbd5e1',height:36},
    webPreferences:{preload:path.join(__dirname,'preload.cjs'),nodeIntegration:false,contextIsolation:true,sandbox:true,backgroundThrottling:!smoke}});
  window.webContents.session.setPermissionRequestHandler((_contents,_permission,callback)=>callback(false));
  window.webContents.setWindowOpenHandler(({url})=>{external(url);return {action:'deny'};});
  window.webContents.on('will-navigate',(event,url)=>{if(new URL(url).origin!==origin){event.preventDefault();external(url);}});
  await window.loadFile(path.join(__dirname,'splash.html'));
  if(smoke)window.showInactive();
  try{
    await ensureBackend();
    await window.loadURL(origin);
    if(smoke)await verifyDesktop();
  }catch(error){
    if(quitting||window.isDestroyed())return;
    if(smoke){console.error(error);await Promise.all([stopChild(ownedBuild),stopChild(ownedBackend)]);app.exit(1);return;}
    await window.webContents.executeJavaScript(`document.getElementById('startup-message').textContent=${JSON.stringify(error.message)};document.body.classList.add('failed');`);
  }
}
async function verifyDesktop(){
  const output=path.join(smokeRoot,'screenshots');await fs.mkdir(output,{recursive:true});
  const evaluate=expression=>window.webContents.executeJavaScript(expression);
  for(let i=0;i<150;i++){
    if(await evaluate('typeof projectCatalog!=="undefined" && projectCatalog?.projects?.length>=17 && state.host?.cpu?.percent!=null && state.history.length>=2'))break;
    if(i===149)throw Error('Desktop data did not become ready');await pause(200);
  }
  if(!await evaluate('window.kumaDesktop?.isDesktop && typeof window.require==="undefined" && document.body.classList.contains("desktop")'))throw Error('Desktop bridge/isolation failed');
  await evaluate('setTheme("dark");');
  if(await evaluate('state.remote?.k3s?.ready')){
    if(!await evaluate('document.querySelectorAll("#server-k3s-table tbody tr").length===state.remote.k3s.items.filter(x=>x.kind==="Pod").length'))throw Error('Server k3s Pod rows did not render');
    await evaluate('document.querySelector("#server-k3s-tabs [data-kind=Node]").click();');
    if(!await evaluate('state.remote.k3s.items.filter(x=>x.kind==="Node").every(x=>document.getElementById("server-k3s-table").textContent.includes(x.name))'))throw Error('Server k3s nodes did not render');
    await evaluate('document.querySelector("#server-k3s-tabs [data-kind=Pod]").click();');
  }
  for(const page of ['overview','server','cluster','projects','lab']){
    await evaluate(`location.hash=${JSON.stringify(page)}`);await pause(600);
    if(!await evaluate(`!document.getElementById(${JSON.stringify(page)}).hidden`))throw Error(`Hidden page ${page}`);
    if(!await evaluate('document.documentElement.scrollWidth<=window.innerWidth'))throw Error(`Layout overflows on ${page}`);
    await fs.writeFile(path.join(output,page+'.png'),(await window.webContents.capturePage()).toPNG());
  }
  await evaluate('location.hash="projects";');await pause(150);
  await evaluate('document.querySelector("[data-dependencies]").click();');
  for(let i=0;i<100;i++){if(await evaluate('document.getElementById("drawer-content").textContent.includes("kuma-boot-starter-mq-kafka")'))break;if(i===99)throw Error('Starter drawer did not load');await pause(100);}
  await fs.writeFile(path.join(output,'dependencies.png'),(await window.webContents.capturePage()).toPNG());
  await evaluate('document.getElementById("close-drawer").click();setTheme("light");document.getElementById("theme-toggle").click();location.hash="overview";');await pause(600);
  if(!await evaluate('document.documentElement.dataset.theme==="dark"'))throw Error('Dark theme did not switch');
  await fs.writeFile(path.join(output,'overview-dark.png'),(await window.webContents.capturePage()).toPNG());
  await evaluate('document.getElementById("theme-toggle").click();');
  await fs.writeFile(path.join(smokeRoot,'desktop-smoke.json'),JSON.stringify({passed:true,pages:5,isolation:true,projects:17,starterDrawer:true},null,2));
  console.log('Electron smoke passed: five pages, real data, starter drawer, context isolation.');app.quit();
}
app.on('window-all-closed',()=>app.quit());
function stopChild(child){
  if(!child?.pid||child.exitCode!==null||child.signalCode!==null)return Promise.resolve();
  return new Promise(resolve=>{
    let timer;
    const done=()=>{clearTimeout(timer);resolve();};
    child.once('exit',done);
    if(process.platform==='win32'){
      // Stop the owned process and its monitoring helpers; never search for unrelated Java processes.
      const killer=spawn('taskkill.exe',['/PID',String(child.pid),'/T','/F'],{windowsHide:true,stdio:'ignore'});
      killer.once('error',()=>{child.kill();});
    }else child.kill('SIGTERM');
    timer=setTimeout(()=>{child.kill('SIGKILL');done();},5000);
  });
}
app.on('before-quit',event=>{
  if(shutdownComplete)return;
  event.preventDefault();
  if(quitting)return;
  quitting=true;
  Promise.all([stopChild(ownedBuild),stopChild(ownedBackend)]).finally(()=>{shutdownComplete=true;app.quit();});
});
for(const signal of ['SIGINT','SIGTERM'])process.on(signal,()=>app.quit());
