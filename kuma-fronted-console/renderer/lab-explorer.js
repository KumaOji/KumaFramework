'use strict';
(() => {
  const byId=id=>document.getElementById(id);
  const read=(key)=>{try{const value=JSON.parse(localStorage.getItem(key)||'[]');return Array.isArray(value)?value.filter(item=>typeof item==='string').slice(0,200):[];}catch{return [];}};
  const favorites=new Set(read('kuma-lab-favorites'));
  let recent=read('kuma-lab-recent'),scope='all';
  const save=()=>{try{localStorage.setItem('kuma-lab-favorites',JSON.stringify([...favorites]));localStorage.setItem('kuma-lab-recent',JSON.stringify(recent));}catch{}};
  const search=byId('lab-search'),groupFilter=byId('lab-group-filter');
  search.closest('.lab-search-wrap').addEventListener('click',event=>{
    if(event.target===search||event.target.closest('button'))return;
    search.focus({preventScroll:true});
  });
  let composing=false;
  search.addEventListener('compositionstart',()=>{composing=true;});
  search.addEventListener('compositionend',()=>{composing=false;renderPresets();});
  function accepts(entry){
    const key=LabCatalog.key(entry);
    if(groupFilter.value&&entry.group!==groupFilter.value)return false;
    return scope==='all'||(scope==='favorites'?favorites.has(key):scope==='recent'?recent.includes(key):(entry.kind||'api')===scope);
  }
  function render(groups){
    const names=[...new Set(presets.map(entry=>entry.group))].sort((a,b)=>a.localeCompare(b,'zh-CN'));
    if(groupFilter.options.length!==names.length+1||names.some((name,index)=>groupFilter.options[index+1]?.value!==name)){
      const previous=groupFilter.value;
      groupFilter.replaceChildren(new Option('全部知识分组',''),...names.map(name=>new Option(name,name)));
      groupFilter.value=names.includes(previous)?previous:'';
    }
    const results=groups.flatMap(group=>group.items),filtered=Boolean(search.value.trim()||groupFilter.value||scope!=='all');
    byId('lab-finder-count').textContent=`${results.length} 个实验`;
    byId('lab-search-clear').hidden=!search.value;
    byId('lab-reset-filters').hidden=!filtered;
    byId('lab-quick-title').textContent=scope==='favorites'?'我的收藏':scope==='recent'?'最近访问':filtered?'匹配的实验':'快速开始';
    let shown=results;
    if(scope==='recent')shown=[...results].sort((a,b)=>recent.indexOf(LabCatalog.key(a.entry))-recent.indexOf(LabCatalog.key(b.entry)));
    else if(!filtered){
      const rank=entry=>favorites.has(LabCatalog.key(entry))?0:recent.includes(LabCatalog.key(entry))?1:/完整实验|完整读写|并发与锁|JMM|Socket.*总览/.test(entry.name)?2:3;
      shown=[...results].sort((a,b)=>rank(a.entry)-rank(b.entry));
    }
    const grid=byId('lab-quick-results');
    grid.innerHTML=shown.slice(0,filtered?9:6).map(({entry,index})=>{
      const starred=favorites.has(LabCatalog.key(entry)),learning=entry.kind==='learning';
      return `<article class="quick-experiment ${selected===index?'selected':''}"><div class="quick-meta"><span class="quick-kind ${learning?'lesson':'api'}">${learning?'终端学习':esc(entry.method)}</span><span>${esc(entry.group)}</span><button class="favorite-toggle" data-favorite="${index}" aria-label="${starred?'取消收藏':'收藏'}：${esc(entry.name)}" aria-pressed="${starred}" title="${starred?'取消收藏':'收藏实验'}">${starred?'★':'☆'}</button></div><button class="quick-select" data-quick-select="${index}"><strong>${esc(entry.name)}</strong><span class="quick-description">${esc(learning?entry.description:entry.path)}</span><span class="quick-action">${learning?'查看运行命令':'配置并运行'} <span aria-hidden="true">↗</span></span></button></article>`;
    }).join('');
    if(!shown.length)grid.innerHTML=`<div class="finder-empty"><strong>${scope==='favorites'?'还没有收藏的实验':scope==='recent'?'还没有访问记录':'没有找到匹配的实验'}</strong><p>${scope==='favorites'?'点击实验卡片上的 ☆，下次从这里快速打开。':scope==='recent'?'打开一个实验后，这里会保留最近访问的入口。':'换个关键词，或重置筛选后查看全部实验。'}</p></div>`;
    if(shown.length>9&&filtered)grid.insertAdjacentHTML('beforeend',`<p class="finder-more">显示前 9 项，其余 ${shown.length-9} 项可在下方实验目录中浏览。</p>`);
    document.querySelectorAll('[data-lab-scope]').forEach(button=>{const active=button.dataset.labScope===scope;button.classList.toggle('active',active);button.setAttribute('aria-pressed',String(active));});
    document.querySelectorAll('[data-lab-topic]').forEach(button=>button.setAttribute('aria-pressed',String(search.value===button.dataset.labTopic)));
  }
  function reset(){scope='all';search.value='';groupFilter.value='';renderPresets();}
  window.LabExplorer={accepts,render};
  byId('lab-reset-filters').onclick=reset;
  byId('lab-search-clear').onclick=()=>{search.value='';renderPresets();search.focus();};
  groupFilter.onchange=renderPresets;
  document.querySelectorAll('[data-lab-scope]').forEach(button=>button.onclick=()=>{scope=button.dataset.labScope;renderPresets();});
  const topics=['Redis','Kafka','Elasticsearch','Socket','Session','事务','JDK 25','内存'];
  byId('lab-topic-shortcuts').innerHTML='<span>直达知识点</span>'+topics.map(topic=>`<button data-lab-topic="${esc(topic)}" aria-pressed="false">${esc(topic)}</button>`).join('');
  byId('lab-topic-shortcuts').onclick=event=>{const button=event.target.closest('[data-lab-topic]');if(!button)return;scope='all';groupFilter.value='';search.value=search.value===button.dataset.labTopic?'':button.dataset.labTopic;renderPresets();};
  byId('lab-quick-results').onclick=event=>{
    const star=event.target.closest('[data-favorite]');
    if(star){const entry=presets[Number(star.dataset.favorite)];if(!entry)return;const key=LabCatalog.key(entry);if(favorites.has(key))favorites.delete(key);else favorites.add(key);save();renderPresets();return;}
    const button=event.target.closest('[data-quick-select]');
    if(button){const index=Number(button.dataset.quickSelect);selectPreset(index);const target=byId(presets[index].kind==='learning'?'lab-learning-workspace':'lab-api-workspace');target.setAttribute('tabindex','-1');target.focus({preventScroll:true});target.scrollIntoView({block:'start',behavior:matchMedia('(prefers-reduced-motion: reduce)').matches?'instant':'smooth'});}
  };
  window.addEventListener('lab:selected',event=>{const key=LabCatalog.key(event.detail);recent=[key,...recent.filter(item=>item!==key)].slice(0,12);save();renderPresets();});
  document.addEventListener('keydown',event=>{
    if(event.isComposing||composing||event.keyCode===229)return;
    if(event.key==='Escape'&&document.activeElement===search){reset();search.blur();}
    if(event.key==='Enter'&&document.activeElement===search){const first=byId('lab-quick-results').querySelector('[data-quick-select]');if(first){event.preventDefault();first.click();}}
  });
  renderPresets();
})();
