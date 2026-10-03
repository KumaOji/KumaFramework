const {spawn, spawnSync}=require('node:child_process');
const path=require('node:path');
const electron=require('electron');
const child=spawn(electron,['.',...process.argv.slice(2)],{
  cwd:path.resolve(__dirname,'..'),stdio:'inherit',windowsHide:false
});
let closed=false;
function stop(signal='SIGTERM'){
  if(closed||!child.pid)return;
  if(process.platform==='win32'){
    // Electron's stock CLI kills only Electron on Ctrl+C, leaving its Java child behind.
    spawnSync('taskkill.exe',['/PID',String(child.pid),'/T','/F'],{stdio:'ignore',windowsHide:true});
  }else child.kill(signal);
}
child.on('error',error=>{console.error(error.message);process.exitCode=1;});
child.on('close',code=>{closed=true;process.exitCode=code??1;});
for(const signal of ['SIGINT','SIGTERM','SIGBREAK'])process.on(signal,()=>stop(signal==='SIGBREAK'?'SIGTERM':signal));
process.on('exit',()=>stop());
