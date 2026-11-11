'use strict';
(() => {
  const list=document.getElementById('credentials-list');
  const status=document.getElementById('credentials-status');
  const refresh=document.getElementById('credentials-refresh');
  const labels={available:'已读取（尚未验证登录）','manual-required':'需补充已知账号',unavailable:'来源暂不可用','no-stored-login':'未记录独立登录'};
  let entries=[],generation=0;
  const revealed=new Map(),timers=new Map(),pending=new Set();
  const element=(tag,text,className)=>{const node=document.createElement(tag);if(text!==undefined)node.textContent=text;if(className)node.className=className;return node;};
  async function read(service='all',reveal=false){
    const response=await fetch('/api/credentials',{method:'POST',cache:'no-store',headers:{'Content-Type':'application/json','X-Kuma-Credentials':'console'},body:JSON.stringify({service,reveal})});
    if(!response.ok)throw Error(response.status===403?'请使用本机 localhost / 127.0.0.1 地址访问 Console。':'凭据读取失败，请检查本机脚本和私有凭据文件。');
    const data=await response.json();
    if(!Array.isArray(data))throw Error('凭据返回格式不正确。');
    return data;
  }
  function hideAll(){
    generation++;revealed.clear();pending.clear();
    timers.forEach(clearTimeout);timers.clear();render();
  }
  async function toggle(entry){
    const id=entry.service;
    if(revealed.has(id)){revealed.delete(id);clearTimeout(timers.get(id));timers.delete(id);render();return;}
    const current=generation;pending.add(id);render();
    try{
      const data=await read(id,true);
      if(current!==generation)return;
      const value=data.find(item=>item.service===id);
      if(!value||value.status!=='available')throw Error('当前来源无法读取，请刷新后重试。');
      revealed.set(id,value);
      timers.set(id,setTimeout(()=>{revealed.delete(id);timers.delete(id);render();},60000));
      status.textContent='已显示 '+entry.name+'，60 秒后自动隐藏。';
    }catch(error){if(current===generation)status.textContent=error.message;}
    finally{if(current===generation){pending.delete(id);render();}}
  }
  function render(){
    list.replaceChildren();
    for(const entry of entries){
      const value=revealed.get(entry.service),card=element('div',undefined,'credential-card');
      const heading=element('div',undefined,'credential-heading');
      heading.append(element('h3',entry.name));
      if(entry.status==='available'){
        const show=element('button',pending.has(entry.service)?'读取中…':value?'隐藏':'显示凭据','button small');
        show.disabled=pending.has(entry.service);show.onclick=()=>toggle(entry);heading.append(show);
      }
      card.append(heading,element('p',labels[entry.status]||entry.status,'muted'));
      const fields=element('div',undefined,'credential-fields');
      for(const [key,label] of [['username','账号'],['password','密码'],['apiKey','API Key'],['loginUrl','登录链接']]){
        const text=(value||entry)[key];if(!text)continue;
        const row=element('div',undefined,'credential-field'),input=element('input');
        input.id='credential-'+entry.service+'-'+key;input.readOnly=true;input.value=text;input.autocomplete='off';input.spellcheck=false;
        const caption=element('label',label);caption.htmlFor=input.id;row.append(caption,input);
        if(key==='username'||value){
          const copy=element('button','复制','button small');copy.setAttribute('aria-label','复制'+entry.name+'的'+label);
          copy.onclick=async()=>{try{await navigator.clipboard.writeText(input.value);status.textContent=label+'已复制到剪贴板。';}catch{status.textContent='复制失败，可选中文本手动复制。';}};
          row.append(copy);
        }
        fields.append(row);
      }
      card.append(fields,element('p','来源：'+entry.source,'credential-source'));
      if(entry.note)card.append(element('p',entry.note,'muted'));
      list.append(card);
    }
  }
  refresh.onclick=async()=>{
    hideAll();const current=generation;refresh.disabled=true;status.textContent='正在读取本机与 WSL 凭据…';
    try{const data=await read();if(current!==generation)return;entries=data.filter(entry=>entry.status!=='no-stored-login');render();status.textContent='已读取 '+entries.length+' 个服务。账号从项目配置或本机保存的凭据获取；缺失账号可补充到用户目录 .kuma-console/credentials.json。';}
    catch(error){if(current===generation)status.textContent=error.message;}
    finally{refresh.disabled=false;}
  };
  document.getElementById('credentials-hide').onclick=()=>{hideAll();status.textContent='凭据已隐藏。';};
  window.addEventListener('hashchange',hideAll);
  window.addEventListener('blur',hideAll);
  document.addEventListener('visibilitychange',()=>{if(document.hidden)hideAll();});
})();
