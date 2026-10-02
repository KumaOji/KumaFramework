'use strict';
let projectCatalog;
function renderCatalog() {
  if (!projectCatalog) return;
  const search=$('catalog-search').value.trim().toLowerCase(), filter=$('catalog-filter').value;
  const rows=projectCatalog.projects.filter(p=>(filter==='all'||p.enabled===(filter==='enabled')) &&
    `${p.name} ${p.description} ${p.starters.map(s=>s.name).join(' ')}`.toLowerCase().includes(search));
  $('catalog-count').textContent=`${projectCatalog.projects.length} 个 · ${projectCatalog.projects.filter(p=>p.enabled).length} 个启用`;
  $('project-catalog').innerHTML=`<table class="catalog-table"><thead><tr><th>项目</th><th>构建状态</th><th>运行 / 服务</th><th>Starter 依赖</th><th></th></tr></thead><tbody>${rows.map(p=>{
    const processes=(state.running||[]).filter(r=>p.mainClasses.includes(r.name)||p.jarName===r.name||r.name===`${p.shortName}.jar`);
    const service=state.projects?.projects?.find(s=>s.name.toLowerCase()===p.shortName.toLowerCase());
    const direct=p.starters.filter(s=>s.direct).length, kuma=p.starters.filter(s=>!s.external).length;
    return `<tr><td><strong>${esc(p.name)}</strong><div class="catalog-description" title="${esc(p.description)}">${esc(p.description)}</div><div class="muted mono">${esc(p.directory)}</div></td><td>${badge(p.enabled?'已启用':'未启用',p.enabled?'good':'')}<div class="muted">${p.dependencySource==='gradle'?'Gradle 配置':'源码静态读取'}</div></td><td>${processes.length?processes.map(r=>`<div>${badge('进程已发现','good')} <span class="mono">PID ${r.pid}</span></div>`).join(''):'<span class="muted">未发现项目进程</span>'}${service?`<div>${projectBadge(service.status)}</div>`:''}</td><td><strong>${p.starters.length} <small>个</small></strong><div class="muted">直接 ${direct} · 间接 ${p.starters.length-direct}</div><div class="muted">Kuma ${kuma} · 第三方 ${p.starters.length-kuma}</div></td><td><button class="button small" data-dependencies="${esc(p.id)}">查看依赖 →</button></td></tr>`;
  }).join('')||'<tr><td colspan="5" class="empty">没有匹配的项目</td></tr>'}</tbody></table>`;
  document.querySelectorAll('[data-dependencies]').forEach(button=>button.onclick=()=>showDependencies(button.dataset.dependencies));
}
async function showDependencies(id) {
  const project=projectCatalog.projects.find(p=>p.id===id);
  $('resource-drawer').hidden=false;$('drawer-backdrop').hidden=false;
  $('drawer-kind').textContent='STARTER DEPENDENCIES';$('drawer-title').textContent=project.name;
  $('drawer-tools').replaceChildren();$('drawer-content').textContent='正在读取依赖关系…';
  try {
    const data=await api(`/api/projects/dependencies?project=${encodeURIComponent(id)}`);
    // Ignore a late response after the user navigates away or opens a different drawer.
    if($('resource-drawer').hidden||$('drawer-title').textContent!==project.name)return;
    const search=document.createElement('input');search.type='search';search.placeholder='搜索 starter';search.setAttribute('aria-label','搜索 starter 依赖');
    const mode=document.createElement('select');mode.setAttribute('aria-label','依赖展示方式');
    for(const [value,label] of [['starters','Starter 与引入路径'],['tree','完整模块依赖树']]){const option=document.createElement('option');option.value=value;option.textContent=label;mode.append(option);}
    const name=id=>data.nodes[id]?.name||id;
    const render=()=>{
      const q=search.value.trim().toLowerCase();
      let text=`${project.directory}\n${data.starters.length} 个 starter（去重） · ${data.starters.filter(s=>s.direct).length} 个直接引入\n\n`;
      text+=project.enabled?'数据来源：Gradle 加载后的依赖声明；包含 api、implementation、runtimeOnly、compileOnly，不包含测试依赖。\n':'未启用 Demo：仅识别源码中明确写出的 project 依赖，动态表达式及第三方声明可能不完整。\n';
      text+='第三方 starter 显示坐标，不展开其发布包内部依赖。compileOnly 表示仅编译使用。\n\n';
      if(mode.value==='starters'){
        const starters=data.starters.filter(s=>`${s.id} ${s.path.join(' ')}`.toLowerCase().includes(q));
        $('drawer-content').classList.add('rich-dependencies');
        $('drawer-content').innerHTML=`<div class="dependency-intro"><div class="dependency-total"><strong>${data.starters.length} <small>个 starter</small></strong></div>${esc(project.enabled?'来自 Gradle 依赖声明，展示每一层引入关系。':'未启用 Demo，仅静态读取明确声明的模块依赖，结果可能不完整。')}<br>第三方包内部依赖未展开；compileOnly 表示仅编译使用。</div>`+starters.map(s=>`<article class="dependency-card"><h3>${badge(s.direct?'[直接]':'[间接]',s.direct?'good':'')} ${esc(s.name)}</h3><div class="dependency-id">${esc(s.id)} · ${s.external?'第三方':'Kuma starter'}</div><div class="dependency-path">${s.path.map((id,i)=>`${i?`<span class="path-scope">${esc(s.scopes[i-1])} →</span>`:''}<span class="path-node">${esc(name(id))}</span>`).join('')}</div></article>`).join('');
        if(!starters.length)$('drawer-content').innerHTML+='<div class="empty">没有匹配的 starter</div>';
        if(data.missing.length)$('drawer-content').innerHTML+=`<div class="notice">未纳入当前构建的模块：${esc(data.missing.join(', '))}</div>`;
        return;
      }else{
        $('drawer-content').classList.remove('rich-dependencies');
        const expanded=new Set();
        function tree(id,depth,scope,rules=[]){
          const node=data.nodes[id];if(!node)return `${'  '.repeat(depth)}└─ ${id} [未纳入构建]\n`;
          const group=node.external?id.split(':')[0]:'io.github.kumaoji';
          if(rules.some(r=>(!r.group||r.group==='*'||r.group===group)&&(!r.module||r.module==='*'||r.module===node.name)))return '';
          const shared=expanded.has(id);let line=`${'  '.repeat(depth)}${depth?'└─ ':''}${node.name}${scope?' ['+scope+']':''}${shared?' ↩ 已在上方展开':''}\n`;
          if(shared)return line;expanded.add(id);
          for(const edge of node.edges){if(!q||`${edge.target} ${node.name}`.toLowerCase().includes(q)||!data.nodes[edge.target]?.external){const exclusions=[...rules,...(edge.excludes||[])];if(edge.transitive===false){line+=`${'  '.repeat(depth+1)}└─ ${name(edge.target)} [${edge.scope}, transitive=false]\n`;}else line+=tree(edge.target,depth+1,edge.scope,exclusions);}}
          return line;
        }
        text+=tree(id,0,'');
      }
      if(data.missing.length)text+='\n\n未纳入当前构建的模块：\n'+data.missing.join('\n');
      $('drawer-content').textContent=text;
    };
    search.oninput=render;mode.onchange=render;$('drawer-tools').append(search,mode);render();
  }catch(e){$('drawer-content').textContent=e.message;}
}
$('catalog-search').oninput=renderCatalog;$('catalog-filter').onchange=renderCatalog;
api('/api/projects/catalog').then(data=>{projectCatalog=data;renderCatalog();}).catch(e=>notice('catalog-warning',e.message));
