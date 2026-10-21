const {spawn}=require('node:child_process');

function spawnBackend(command,args,options={}){
  // A Windows Job Object can kill inherited WSL children when its handle closes.
  // Give the backend its own process group; stop it explicitly without its tree.
  return spawn(command,args,{...options,windowsHide:true,detached:process.platform==='win32'});
}

function stopChild(child,{tree=false,graceful=false}={}){
  if(!child?.pid||child.exitCode!==null||child.signalCode!==null)return Promise.resolve();
  return new Promise(resolve=>{
    let timer;
    const done=()=>{clearTimeout(timer);resolve();};
    child.once('exit',done);
    timer=setTimeout(()=>{child.kill('SIGKILL');done();},5000);
    const terminate=()=>{
      if(child.exitCode!==null||child.signalCode!==null)return;
      if(process.platform==='win32'){
        const killer=spawn('taskkill.exe',['/PID',String(child.pid),...(tree?['/T']:[]),'/F'],{windowsHide:true,stdio:'ignore'});
        killer.once('error',()=>child.kill());
      }else child.kill('SIGTERM');
    };
    if(graceful&&child.stdin&&!child.stdin.destroyed){
      child.stdin.once('error',terminate);
      child.stdin.end('kuma-console:quit\n');
    }else terminate();
  });
}

module.exports={spawnBackend,stopChild};
