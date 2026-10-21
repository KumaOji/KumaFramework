const {spawn, spawnSync}=require('node:child_process');
const path=require('node:path');
const electron=require('electron');
const {stopChild}=require('../electron/owned-process.cjs');
const child=spawn(electron,['.',...process.argv.slice(2)],{
  cwd:path.resolve(__dirname,'..'),stdio:['inherit','inherit','inherit','ipc'],windowsHide:true
});
let closed=false,stopping=false,timer;
function stop(){
  if(closed||stopping||!child.pid)return;
  stopping=true;
  // Let Electron run its backend cleanup, retaining WSL keepalive sessions.
  if(child.connected)child.send({type:'kuma-console:quit'},error=>{if(error)void stopChild(child);});
  else void stopChild(child);
  timer=setTimeout(()=>{void stopChild(child);},5000);
}
child.on('error',error=>{console.error(error.message);process.exitCode=1;});
child.on('close',code=>{closed=true;clearTimeout(timer);process.exitCode=code??1;});
for(const signal of ['SIGINT','SIGTERM','SIGBREAK'])process.on(signal,stop);
process.on('exit',()=>{
  if(closed||!child.pid)return;
  if(process.platform==='win32')spawnSync('taskkill.exe',['/PID',String(child.pid),'/F'],{stdio:'ignore',windowsHide:true});
  else child.kill('SIGTERM');
});
