'use strict';
const componentNames=['uaa','gateway'];
const componentBusy={uaa:false,gateway:false};
let componentPolling=false,componentRevision=0;
function renderComponents(data){
  const labels={STOPPED:'已停止',BUILDING:'构建中',STARTING:'启动中',RUNNING:'运行中',STOPPING:'停止中',FAILED:'失败',EXTERNAL:'端口已占用'};
  for(const name of componentNames){const item=data[name];if(!item)continue;
    $(`component-${name}-state`).textContent=labels[item.state]||item.state;
    $(`component-${name}-state`).className='status '+(item.state==='RUNNING'?'ok':item.state==='FAILED'?'bad':'');
    $(`component-${name}-message`).textContent=item.message+(item.pid?` · PID ${item.pid}`:'');
    $(`component-${name}-start`).disabled=componentBusy[name]||!item.canStart;
    $(`component-${name}-stop`).disabled=componentBusy[name]||!item.canStop;
    $(`component-${name}-url`).textContent=item.url;
  }
}
async function pollComponents(){
  if(componentPolling)return;componentPolling=true;const revision=componentRevision;
  try{const data=await api('/api/components');if(revision===componentRevision)renderComponents(data);}
  catch(e){for(const name of componentNames){$(`component-${name}-start`).disabled=true;$(`component-${name}-stop`).disabled=true;notice(`component-${name}-error`,e.message);}}
  finally{componentPolling=false;}
}
async function controlComponent(name,action){
  if(componentBusy[name])return;componentBusy[name]=true;componentRevision++;
  $(`component-${name}-start`).disabled=true;$(`component-${name}-stop`).disabled=true;notice(`component-${name}-error`,'');
  try{await api(`/api/components/${name}/${action}`,{method:'POST'});}
  catch(e){notice(`component-${name}-error`,e.message);}
  finally{componentBusy[name]=false;await pollComponents();}
}
for(const name of componentNames){
  $(`component-${name}-start`).onclick=()=>controlComponent(name,'start');
  $(`component-${name}-stop`).onclick=()=>controlComponent(name,'stop');
  $(`component-${name}-logs`).onclick=async()=>{try{const data=await api(`/api/components/${name}/logs`);$(`component-${name}-log`).hidden=false;$(`component-${name}-log`).textContent=data.body;}catch(e){notice(`component-${name}-error`,e.message);}};
}
window.addEventListener('hashchange',()=>{if(state.page==='projects'&&state.servicesTab==='components')pollComponents();});
setInterval(()=>{if(!document.hidden&&state.page==='projects'&&state.servicesTab==='components')pollComponents();},3000);
