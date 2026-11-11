'use strict';
(() => {
  const get=id=>document.getElementById(id),card=document.querySelector('[data-managed-service="dsh"]');
  let busy=false,refreshing=false,revision=0;
  const active=()=>!document.hidden&&state.page==='projects';
  async function request(path,options){
    const response=await fetch('/api/ai/'+path,{...options,signal:AbortSignal.timeout(10000)});
    const data=await response.json();if(!response.ok)throw Error(data.error||'dsh 请求失败');return data;
  }
  function render(data){
    get('dsh-state').textContent=({RUNNING:'运行中',STOPPED:'已停止',STARTING:'启动中',STOPPING:'停止中',EXTERNAL:'端口已占用',AUTH_REQUIRED:'运行中'})[data.state]||'未知';
    get('dsh-state').className='status '+(['RUNNING','AUTH_REQUIRED'].includes(data.state)?'ok':'');
    get('dsh-message').textContent=data.message||'';get('dsh-directory').textContent=data.directory||'未配置';
    get('dsh-start').disabled=busy||!data.canStart;get('dsh-stop').disabled=busy||!data.canStop;get('dsh-open').disabled=!data.canOpen;
    card.dataset.running=String(['RUNNING','STARTING','AUTH_REQUIRED'].includes(data.state));
    try{card.dataset.address=new URL(data.url).origin;}catch{card.dataset.address='';}
    window.PublicPanels?.setDshStatus(data);renderManagedServices();
  }
  async function refresh(){
    if(busy||refreshing)return;refreshing=true;const previous=revision;
    try{const data=await request('status');if(previous===revision)render(data);
      if(get('dsh-log-details').open)get('dsh-logs').textContent=(await request('logs')).body;
    }catch(error){get('dsh-message').textContent=error.message;}
    finally{refreshing=false;}
  }
  async function action(name){
    if(busy)return;busy=true;revision++;get('dsh-start').disabled=true;get('dsh-stop').disabled=true;
    try{const data=await request(name,{method:'POST'});busy=false;render(data);}
    catch(error){get('dsh-message').textContent=error.message;}
    finally{busy=false;refresh();}
  }
  get('dsh-start').onclick=()=>action('start');get('dsh-stop').onclick=()=>action('stop');
  get('dsh-open').onclick=()=>{window.ServiceDialog?.close();window.PublicPanels?.openService('dsh');};
  get('dsh-refresh').onclick=refresh;
  get('dsh-log-details').ontoggle=()=>{if(get('dsh-log-details').open)refresh();};
  window.addEventListener('hashchange',()=>{if(active())refresh();});
  setInterval(()=>{if(active())refresh();},2500);refresh();
})();
