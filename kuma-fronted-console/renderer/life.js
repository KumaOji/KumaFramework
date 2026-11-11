'use strict';
(() => {
  const get=id=>document.getElementById(id),card=document.querySelector('[data-managed-service="life"]');
  let busy=false,polling=false,revision=0,latest;
  const active=()=>!document.hidden&&state.page==='projects'&&state.servicesTab==='projects';
  function render(data){
    latest=data;
    get('life-state').textContent=({RUNNING:'运行中',STOPPED:'已停止',WSL_STOPPED:'WSL 未启动',STARTING:'启动中',STOPPING:'停止中',DEGRADED:'部分未就绪',UNKNOWN:'状态未知'})[data.state]||'状态未知';
    get('life-state').className='status '+(data.state==='RUNNING'?'ok':data.state==='DEGRADED'?'warn':'');
    get('life-message').textContent=data.message||'';
    get('life-start').disabled=busy||!data.canStart;get('life-stop').disabled=busy||!data.canStop;
    card.dataset.running=String(['RUNNING','DEGRADED','STARTING'].includes(data.state));
    card.dataset.address=(data.distribution||'WSL')+' · '+(data.namespace||'life-deals');
    get('life-location').textContent=card.dataset.address+' · '+(data.directory||'');
    get('life-deployments').replaceChildren(...(data.deployments||[]).map(item=>{
      const span=document.createElement('span');span.className='chip';span.textContent=item.name+' '+item.ready+'/'+item.replicas;return span;
    }));
    for(const name of ['qinglong','rsshub'])get('life-'+name).disabled=!data[name+'Url'];
    notice('life-error',data.error||'');renderManagedServices();
  }
  async function poll(){
    if(polling||busy)return;polling=true;const previous=revision;
    try{const data=await api('/api/life');if(previous===revision)render(data);}
    catch(error){notice('life-error',error.message);}
    finally{polling=false;}
  }
  async function action(name){
    if(busy)return;busy=true;revision++;get('life-start').disabled=true;get('life-stop').disabled=true;
    try{const data=await api('/api/life/'+name,{method:'POST'});busy=false;render(data);}
    catch(error){notice('life-error',error.message);}
    finally{busy=false;}
  }
  get('life-start').onclick=()=>action('start');get('life-stop').onclick=()=>action('stop');
  get('life-details').onclick=async()=>{
    get('life-detail-output').hidden=false;get('life-detail-output').textContent='正在读取部署…';
    try{get('life-detail-output').textContent=(await api('/api/life/details')).body;}catch(error){get('life-detail-output').textContent=error.message;}
  };
  for(const [name,title] of [['qinglong','Life · 青龙'],['rsshub','Life · RSSHub']])get('life-'+name).onclick=()=>{
    const value=latest?.[name+'Url'];if(!value)return;
    window.ServiceDialog?.close();window.PublicPanels?.open(value,title,name==='qinglong');
  };
  window.addEventListener('hashchange',()=>{if(active())poll();});
  setInterval(()=>{if(active())poll();},5000);
  poll();
})();
