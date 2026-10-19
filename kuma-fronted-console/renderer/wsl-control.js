'use strict';
let wslControlData=null,wslControlBusy=false,wslControlPolling=false,wslControlRevision=0;
function renderWslControl(){
  const items=wslControlData?.distributions||[],labels={RUNNING:'运行中',STOPPED:'已关闭',STARTING:'启动中',STOPPING:'关闭中'};
  updateOptions('wsl-control-select',items.map(item=>[item.name,`${item.name} · ${labels[item.state]||item.state}`]));
  const selected=items.find(item=>item.name===$('wsl-control-select').value);
  $('wsl-start').disabled=wslControlBusy||!selected?.canStart;$('wsl-stop').disabled=wslControlBusy||!selected?.canStop;
  $('wsl-shutdown').disabled=wslControlBusy||!wslControlData?.canShutdown;$('wsl-control-select').disabled=wslControlBusy||!items.length;
  $('wsl-control-status').textContent=selected?`${selected.name} · ${labels[selected.state]||selected.state}`:wslControlData?.error||'没有已安装的 WSL 发行版';
  $('wsl-control-status').className='status'+(selected?.state==='RUNNING'?' ok':['STARTING','STOPPING'].includes(selected?.state)?' warn':'');
  if(selected?.error||wslControlData?.error)notice('wsl-control-error',selected?.error||wslControlData.error);
}
async function pollWslControl(){
  if(wslControlPolling||wslControlBusy)return;wslControlPolling=true;const revision=wslControlRevision;
  try{const data=await api('/api/wsl/distributions');if(revision===wslControlRevision){wslControlData=data;renderWslControl();}}
  catch(e){$('wsl-start').disabled=true;$('wsl-stop').disabled=true;$('wsl-shutdown').disabled=true;notice('wsl-control-error',e.message);}
  finally{wslControlPolling=false;}
}
async function controlWsl(action){
  if(wslControlBusy)return;
  const distribution=$('wsl-control-select').value;
  if(action!=='shutdown'&&!distribution)return;
  wslControlBusy=true;wslControlRevision++;renderWslControl();notice('wsl-control-error','');
  try{const data=await api(action==='shutdown'?'/api/wsl/shutdown':'/api/wsl/distributions/'+action,{method:'POST',headers:{'Content-Type':'application/json'},...(action==='shutdown'?{}:{body:JSON.stringify({distribution})})});wslControlData=data;}
  catch(e){notice('wsl-control-error',e.message);}
  finally{wslControlBusy=false;renderWslControl();await pollWslControl();}
}
$('wsl-control-select').onchange=renderWslControl;
$('wsl-start').onclick=()=>controlWsl('start');$('wsl-stop').onclick=()=>controlWsl('stop');$('wsl-shutdown').onclick=()=>controlWsl('shutdown');
window.addEventListener('hashchange',()=>{if(state.page==='server'&&state.infrastructureTab==='wsl')pollWslControl();});
setInterval(()=>{if(!document.hidden&&state.page==='server'&&state.infrastructureTab==='wsl')pollWslControl();},3000);
pollWslControl();
