'use strict';
(() => {
  const dialog=document.getElementById('service-dialog');
  const body=document.getElementById('service-dialog-body');
  let active=null,trigger=null;
  function close(){if(dialog.open)dialog.close();}
  function restore(){
    if(!active)return;
    const content=body.querySelector('[data-service-detail]');
    const card=document.querySelector(`[data-managed-service="${active}"]`);
    if(content){content.hidden=true;card.append(content);}
    active=null;
    if(trigger?.isConnected&&!trigger.closest('[hidden]'))trigger.focus({preventScroll:true});
  }
  function open(name){
    const content=document.querySelector(`[data-service-detail="${name}"]`);
    if(!content)return;
    if(dialog.open){close();restore();}
    active=name;trigger=document.querySelector(`[data-service-open="${name}"]`);
    document.getElementById('service-dialog-title').textContent={uaa:'UAA · 认证服务',gateway:'Gateway · 网关服务',blog:'Blog · 前后端与知识仓库',lab:'Lab · 实验服务'}[name];
    content.hidden=false;body.append(content);dialog.showModal();
    document.getElementById('service-dialog-close').focus({preventScroll:true});
  }
  dialog.addEventListener('close',()=>{if(!dialog.open)restore();});
  document.getElementById('service-dialog-close').onclick=close;
  dialog.addEventListener('click',event=>{
    if(event.target!==dialog)return;
    const rect=dialog.getBoundingClientRect();
    if(event.clientX<rect.left||event.clientX>rect.right||event.clientY<rect.top||event.clientY>rect.bottom)close();
  });
  document.querySelectorAll('[data-service-open]').forEach(button=>button.onclick=()=>open(button.dataset.serviceOpen));
  window.addEventListener('hashchange',close);
  window.ServiceDialog={open,close,isOpen:name=>dialog.open&&active===name};
})();
