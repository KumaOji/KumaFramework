'use strict';
const $ = id => document.getElementById(id);
const esc = value => String(value ?? '').replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
const bytes = value => { const n = Number(value) || 0; if(n < 1024) return `${n.toFixed(0)} B`; const i = Math.min(4, Math.floor(Math.log(n) / Math.log(1024))); return `${(n / 1024 ** i).toFixed(i > 2 ? 1 : 0)} ${['B','KiB','MiB','GiB','TiB'][i]}`; };
const pct = value => value == null ? '—' : Number(value).toFixed(1);
const time = value => value ? new Date(value).toLocaleTimeString('zh-CN', {hour12:false}) : '—';
const uptime = value => { const n = Number(value) || 0; return n >= 86400 ? `${Math.floor(n/86400)}天 ${Math.floor(n%86400/3600)}时` : n >= 3600 ? `${Math.floor(n/3600)}时 ${Math.floor(n%3600/60)}分` : `${Math.floor(n/60)}分`; };
const badge = (text, kind = '') => `<span class="status ${kind}">${esc(text)}</span>`;
const state = {host:null,cluster:null,projects:null,history:[],lastSample:null,page:'overview',kind:'Pod',rows:[],paused:false,busy:false,errors:{}};
async function api(path, options) {
  const controller = new AbortController(); const timer = setTimeout(() => controller.abort(), 35000);
  try { const response = await fetch(path, {...options,signal:controller.signal}); const data = await response.json(); if(!response.ok) throw Error(data.error || `HTTP ${response.status}`); return data; }
  finally { clearTimeout(timer); }
}
function navigate() {
  const page = location.hash.slice(1); state.page = ['overview','cluster','projects','lab'].includes(page) ? page : 'overview';
  document.querySelectorAll('.page').forEach(el => el.hidden = el.id !== state.page);
  document.querySelectorAll('nav a').forEach(el => el.classList.toggle('active',el.dataset.page === state.page));
  $('page-title').textContent = {overview:'本机概览',cluster:'WSL / k3s',projects:'项目服务',lab:'Lab 实验室'}[state.page];
  closeDrawer(); window.scrollTo(0,0); setTimeout(()=>window.scrollTo(0,0),0);
}
window.addEventListener('hashchange', navigate); navigate();
function metric(label,value,unit,note,percent,icon='◈') {
  const paths={'⌁':'<path d="M3 13h5l3-8 4 14 3-7h3"/>','▦':'<rect x="4" y="6" width="16" height="12" rx="2"/><path d="M8 10v4M12 10v4M16 10v4M8 3v3M16 3v3M8 18v3M16 18v3"/>','▱':'<rect x="3" y="5" width="18" height="14" rx="3"/><path d="M3 13h18M16 16h2"/>','▤':'<rect x="4" y="3" width="16" height="18" rx="3"/><path d="M8 8h8M8 12h8M8 16h4"/>'};
  icon=`<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">${paths[icon]||paths['▤']}</svg>`;
  return `<article class="metric"><div class="metric-top"><span>${esc(label)}</span><span class="metric-icon">${icon}</span></div><div class="metric-value">${esc(value)}<small>${esc(unit)}</small></div><div class="metric-note" title="${esc(note)}">${esc(note)}</div>${percent == null ? '' : `<div class="mini-track"><i style="width:${Math.max(0,Math.min(100,percent))}%"></i></div>`}</article>`;
}
function notice(id,text) { $(id).hidden = !text; $(id).textContent = text || ''; }
function renderHost() {
  const h = state.host; notice('host-warning',state.errors.host || h?.error);
  if(!h?.ready || !h.cpu) return;
  $('sidebar-host').textContent = h.hostname;
  $('host-subtitle').textContent = `${h.hostname} · ${h.os} · 已运行 ${uptime(h.uptime)}`;
  $('host-updated').textContent = time(h.sampledAt);
  const used = h.memory.total - h.memory.available; const memoryPct = used / h.memory.total * 100;
  const totalDisk = h.disks.reduce((n,d) => n + d.total,0); const freeDisk = h.disks.reduce((n,d) => n + d.free,0);
  $('host-metrics').innerHTML = metric('CPU 使用率',pct(h.cpu.percent),'%',`${h.cpu.physicalCores} 核心 / ${h.cpu.logicalCores} 逻辑处理器`,h.cpu.percent,'⌁')
    + metric('物理内存',pct(memoryPct),'%',`${bytes(used)} / ${bytes(h.memory.total)} · 交换 ${bytes(h.memory.swapUsed)}`,memoryPct,'▦')
    + metric('存储已用',bytes(totalDisk-freeDisk),'',`总计 ${bytes(totalDisk)} · 可用 ${bytes(freeDisk)}`,totalDisk ? (totalDisk-freeDisk)/totalDisk*100 : 0,'▱')
    + metric('运行进程',h.processCount,'个',`${h.threadCount} 线程 · Java ${h.java}`,null,'▤');
  if(state.lastSample !== h.sampledAt && h.cpu.percent != null) {
    state.history.push({cpu:h.cpu.percent,memory:memoryPct}); if(state.history.length > 60) state.history.shift(); state.lastSample = h.sampledAt;
  }
  renderChart();
  $('disk-list').innerHTML = h.disks.map(d => { const usedPct = d.total ? (d.total-d.free)/d.total*100 : 0; return `<div class="stack-row"><div class="row-between"><span class="mono">${esc(d.mount)}</span><small>${bytes(d.total-d.free)} / ${bytes(d.total)}</small></div><div class="track"><i style="width:${usedPct}%${usedPct>90?';background:var(--amber)':''}"></i></div><div class="subtle">${esc(d.name)} · ${esc(d.type)} · 可用 ${bytes(d.free)}</div></div>`; }).join('') || '<div class="empty">暂无挂载卷</div>';
  $('network-list').innerHTML = [...h.networks].sort((a,b)=>b.ipv4.length-a.ipv4.length || (b.download+b.upload)-(a.download+a.upload)).map(n => `<div class="stack-row"><div class="row-between"><span>${esc(n.displayName || n.name)}</span><small class="mono">${esc(n.ipv4.join(' / ') || '无 IPv4')}</small></div><div class="subtle"><span class="download">↓ ${bytes(n.download)}/s</span> &nbsp; <span class="upload">↑ ${bytes(n.upload)}/s</span> &nbsp; 累计接收 ${bytes(n.received)} · 发送 ${bytes(n.sent)}</div></div>`).join('') || '<div class="empty">暂无网络接口</div>';
  const hardware=h.hardware||{};const entries=[['设备',hardware.computer],['处理器',hardware.cpu],['主板',hardware.board],['固件',hardware.firmware],['CPU 温度',hardware.cpuTemperature?hardware.cpuTemperature.toFixed(1)+' °C':'传感器未提供'],['风扇',(hardware.fans||[]).length?hardware.fans.join(' / ')+' RPM':'传感器未提供']];
  (hardware.graphics||[]).forEach(g=>entries.push(['显卡',`${g.name} · ${bytes(g.vram)} 显存`]));(hardware.memoryModules||[]).forEach(m=>entries.push(['内存条',`${m.bank} · ${bytes(m.capacity)} · ${m.type} · ${(m.speed/1e6).toFixed(0)} MHz`]));(hardware.batteries||[]).forEach(b=>entries.push(['电池',`${pct(b.percent)}% · ${b.charging?'充电中':b.powerOnline?'外接电源':'电池供电'}`]));
  $('hardware-list').innerHTML=entries.map(([label,value])=>`<div class="hardware-item"><span>${esc(label)}</span><strong>${esc(value||'正在读取…')}</strong></div>`).join('');$('host-ports').textContent=h.ports||'当前平台尚未提供端口数据';
  if(h.dns?.length)$('hardware-list').innerHTML+=`<div class="hardware-item"><span>DNS</span><strong>${esc(h.dns.join(' / '))}</strong></div>`;
  $('cpu-core-load').innerHTML=(h.cpu.cores||[]).map((load,i)=>`<div><span class="muted">CPU ${i}</span> <strong>${pct(load)}%</strong><div class="track"><i style="width:${load}%"></i></div></div>`).join('')||'<span class="muted">等待第二次 CPU 采样</span>';
  $('gpu-metrics').textContent=h.gpuMetrics||'正在读取…';renderWindowsServices();
  renderProcesses(); renderRuntimeProcesses(); renderEnvironment();
}
function renderChart() {
  if(!state.history.length) return;
  const points = state.history; const w = 700,h = 175; const colors=getComputedStyle(document.documentElement);const cpuColor=colors.getPropertyValue('--mint').trim(),memoryColor=colors.getPropertyValue('--blue').trim(),gridColor=colors.getPropertyValue('--border').trim(),labelColor=colors.getPropertyValue('--muted').trim();
  const path = key => points.map((p,i) => `${i?'L':'M'}${(i/(Math.max(1,points.length-1))*w).toFixed(1)},${(h-p[key]/100*h).toFixed(1)}`).join(' ');
  const grid = [0,25,50,75,100].map(n => `<line x1="0" y1="${h-n/100*h}" x2="${w}" y2="${h-n/100*h}" stroke="${gridColor}" stroke-dasharray="3 5"/><text x="4" y="${Math.max(11,h-n/100*h-5)}" fill="${labelColor}" font-size="9">${n}%</text>`).join('');
  $('resource-chart').innerHTML = `<svg viewBox="0 0 ${w} ${h+5}" preserveAspectRatio="none" role="img" aria-label="CPU 和内存使用率趋势"><defs><linearGradient id="cpu-fill" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="${cpuColor}" stop-opacity=".2"/><stop offset="1" stop-color="${cpuColor}" stop-opacity="0"/></linearGradient></defs>${grid}<path d="${path('cpu')} L${w},${h} L0,${h} Z" fill="url(#cpu-fill)"/><path d="${path('memory')}" fill="none" stroke="${memoryColor}" stroke-width="2" vector-effect="non-scaling-stroke"/><path d="${path('cpu')}" fill="none" stroke="${cpuColor}" stroke-width="2" vector-effect="non-scaling-stroke"/></svg>`;
}
function processTable(processes) {
  return processes.map(p => `<tr><td>${esc(p.name)}</td><td class="mono muted">${p.pid}</td><td class="mono">${pct(p.cpu)}${p.cpu==null?'':'%'}</td><td class="mono">${bytes(p.memory)}</td><td>${p.threads}</td><td>${badge(p.state,p.state==='RUNNING'?'ok':'')}</td><td class="muted">${uptime(p.uptime)}</td></tr>`).join('');
}
function renderProcesses() {
  const source = state.host?.processes || []; const query = $('process-search').value.toLowerCase(); const sort = $('process-sort').value;
  const rows = source.filter(p => `${p.name} ${p.pid}`.toLowerCase().includes(query)).sort((a,b) => sort==='pid' ? a.pid-b.pid : (b[sort]??-1)-(a[sort]??-1));
  $('process-count').textContent = source.length; $('process-rows').innerHTML = processTable(rows) || '<tr><td colspan="7" class="empty">没有匹配的进程</td></tr>';
  $('process-footer').textContent = `显示 ${rows.length} / ${source.length} 个可读取进程 · 系统进程总数可能包含当前账户不可读取的进程`;
}
$('process-search').addEventListener('input',renderProcesses); $('process-sort').addEventListener('change',renderProcesses);
function renderWindowsServices(){const source=Array.isArray(state.host?.services)?state.host.services:[];const q=$('service-search').value.toLowerCase();const rows=source.filter(s=>`${s.Name} ${s.DisplayName} ${s.State}`.toLowerCase().includes(q));$('service-count').textContent=`${rows.length} / ${source.length} 个服务`;$('windows-services').innerHTML=`<table><thead><tr><th>服务</th><th>显示名称</th><th>状态</th><th>启动类型</th><th>PID</th></tr></thead><tbody>${rows.map(s=>`<tr><td>${esc(s.Name)}</td><td>${esc(s.DisplayName)}</td><td>${badge(s.State,s.State==='Running'?'ok':'')}</td><td>${esc(s.StartMode)}</td><td class="mono">${s.ProcessId||'—'}</td></tr>`).join('')||'<tr><td colspan="5" class="empty">当前平台暂无服务数据</td></tr>'}</tbody></table>`;}
$('service-search').addEventListener('input',renderWindowsServices);
function renderEnvironment() {
  const c = state.cluster; const projects = state.projects?.projects || []; const readyPods = (c?.items||[]).filter(x => x.kind==='Pod' && podReady(x));
  $('environment-summary').className = 'environment-items';
  $('environment-summary').innerHTML = `<div class="environment-row"><span>WSL · ${esc(c?.distribution||'Ubuntu')}</span>${badge(c?.error?'未连接':c?.ready?'运行中':'采集中',c?.ready&&!c?.error?'ok':'warn')}</div><div class="environment-row"><span>k3s 工作负载</span><span>${readyPods.length} 个 Pod 就绪</span></div>`
    + projects.slice(0,4).map(p => `<div class="environment-row"><span>${esc(p.name)}</span>${projectBadge(p.status)}</div>`).join('');
}
const podReady = pod => { const statuses = pod.status?.containerStatuses || []; return pod.status?.phase==='Running' && statuses.length>0 && statuses.every(s=>s.ready); };
function renderCluster() {
  const c = state.cluster; notice('cluster-warning',state.errors.cluster || c?.error); if(!c?.ready) return;
  $('cluster-updated').textContent = time(c.sampledAt); $('wsl-title').textContent = `WSL · ${c.distribution}`;
  $('wsl-distributions').textContent = c.distributions || '暂无发行版'; $('wsl-system').textContent = c.system || '暂无 Linux 采样';
  $('k3s-metrics').textContent = `NODES\n${c.nodeMetrics||'尚无数据'}\n\nPODS\n${c.podMetrics||'尚无数据'}`;
  const items = c.items||[]; const nodes = items.filter(x=>x.kind==='Node'),pods = items.filter(x=>x.kind==='Pod');
  const ready = pods.filter(podReady).length, issues = items.filter(x=>x.kind==='Event'&&x.type==='Warning').length;
  $('cluster-badge').textContent = nodes.length || '—';
  $('cluster-metrics').innerHTML = metric('集群节点',nodes.length,'个',`${nodes.filter(n=>n.status?.conditions?.some(x=>x.type==='Ready'&&x.status==='True')).length} 个 Ready`,null,'⬡')
    + metric('Pods 就绪',ready,`/ ${pods.length}`,`包含所有命名空间 · 完成的 Job 不计入就绪`,pods.length?ready/pods.length*100:0,'◈')
    + metric('服务发现',items.filter(x=>x.kind==='Service').length,'个','ClusterIP / NodePort / LoadBalancer',null,'⇄')
    + metric('警告事件',issues,'条','当前 Kubernetes Event 对象，不是累计次数',null,'!');
  const namespaces = [...new Set(items.map(x=>x.metadata?.namespace).filter(Boolean))].sort();
  updateOptions('namespace-filter', [['','全部命名空间'], ...namespaces.map(n=>[n,n])]);
  updateOptions('resource-type',(c.resourceTypes||[]).map(n=>[n,n]));updateOptions('wsl-select',(c.runningDistributions||[]).map(n=>[n,n])); renderClusterTable(); renderEnvironment();
}
function updateOptions(id,options) { const el=$(id), before=el.value, signature=JSON.stringify(options); if(el.dataset.signature===signature) return; el.dataset.signature=signature; el.innerHTML=options.map(([v,t])=>`<option value="${esc(v)}">${esc(t)}</option>`).join(''); if(options.some(x=>x[0]===before)) el.value=before; }
let wslData=null,wslSection='SYSTEM';
function renderWslDetails(){$('wsl-detail').textContent=wslData?.sections?.[wslSection]||wslData?.error||'此分类尚无数据';$('wsl-detail-meta').textContent=wslData?`${wslData.distribution} · ${time(wslData.sampledAt)} · 15 秒缓存`:'选择发行版后读取详情';}
$('inspect-wsl').onclick=async()=>{const button=$('inspect-wsl');button.disabled=true;$('wsl-detail').textContent='正在读取系统、CPU、全部进程和服务…';try{wslData=await api('/api/wsl?distribution='+encodeURIComponent($('wsl-select').value));renderWslDetails();}catch(e){$('wsl-detail').textContent=e.message;}finally{button.disabled=false;}};
$('wsl-tabs').onclick=e=>{const b=e.target.closest('[data-section]');if(!b)return;wslSection=b.dataset.section;document.querySelectorAll('#wsl-tabs button').forEach(x=>x.classList.toggle('active',x===b));renderWslDetails();};
function resourceSummary(item) {
  const s=item.status||{},spec=item.spec||{};
  if(item.kind==='Pod') { const statuses=s.containerStatuses||[],ready=statuses.filter(x=>x.ready).length; const waiting=statuses.find(x=>x.state?.waiting)?.state.waiting.reason; return [waiting || (podReady(item)?'Ready':s.phase||'Unknown'),`${ready}/${spec.containers?.length||0} 就绪 · 重启 ${statuses.reduce((n,x)=>n+x.restartCount,0)}`,s.podIP||'—']; }
  if(item.kind==='Service') return [spec.type||'ClusterIP',(spec.ports||[]).map(p=>`${p.port}${p.nodePort?':'+p.nodePort:''}/${p.protocol}`).join(', '),[spec.clusterIP,...(s.loadBalancer?.ingress||[]).map(x=>x.ip||x.hostname)].filter(Boolean).join(' / ')];
  if(item.kind==='Node') return [s.conditions?.some(x=>x.type==='Ready'&&x.status==='True')?'Ready':'NotReady',s.nodeInfo?.kubeletVersion||'—',s.addresses?.find(x=>x.type==='InternalIP')?.address||'—'];
  if(item.kind==='Event') return [item.type||'Normal',item.reason||'',item.message||''];
  if(item.kind==='PersistentVolume'||item.kind==='PersistentVolumeClaim') return [s.phase||'Unknown',spec.storageClassName||'默认存储',s.capacity?.storage||spec.capacity?.storage||'—'];
  if(item.kind==='Ingress') return ['Ingress',(spec.rules||[]).map(x=>x.host||'*').join(', '), (s.loadBalancer?.ingress||[]).map(x=>x.ip||x.hostname).join(', ')||'—'];
  if(item.kind==='Job') return [s.succeeded?'Completed':s.failed?'Failed':'Running',`成功 ${s.succeeded||0} · 失败 ${s.failed||0}`,s.startTime?time(s.startTime):'—'];
  if(item.kind==='CronJob') return [spec.suspend?'Suspended':'Scheduled',spec.schedule||'',s.lastScheduleTime?time(s.lastScheduleTime):'—'];
  const desired=spec.replicas??s.desiredNumberScheduled??0, current=s.readyReplicas??s.numberReady??0;
  return [current===desired?'Ready':'Pending',`${current}/${desired} 就绪`,item.kind];
}
function renderClusterTable() {
  const explorer=state.kind==='explorer'; $('resource-explorer').hidden=!explorer; $('cluster-table').hidden=explorer; if(explorer)return;
  const ns=$('namespace-filter').value,q=$('resource-search').value.toLowerCase();
  const kinds=state.kind==='workload'?['Deployment','StatefulSet','DaemonSet','ReplicaSet','Job','CronJob','Ingress']:state.kind==='storage'?['PersistentVolume','PersistentVolumeClaim']:[state.kind];
  state.rows=(state.cluster?.items||[]).filter(x=>kinds.includes(x.kind)&&(!ns||x.metadata?.namespace===ns)&&`${x.metadata?.name} ${x.metadata?.namespace} ${JSON.stringify(resourceSummary(x))}`.toLowerCase().includes(q));
  $('cluster-table').innerHTML = `<table><thead><tr><th>名称 / 类型</th><th>命名空间</th><th>状态</th><th>详情</th><th>${state.kind==='Event'?'事件内容':'地址 / 信息'}</th></tr></thead><tbody>${state.rows.map((x,i)=>{const [status,detail,extra]=resourceSummary(x),kind=['Ready','Running','Completed','Bound','Normal'].includes(status)?'ok':['Warning','Failed','CrashLoopBackOff','NotReady','Error','ImagePullBackOff'].includes(status)?'bad':'';return `<tr class="clickable" tabindex="0" role="button" data-resource="${i}" aria-label="查看 ${esc(x.metadata?.name)} 详情"><td>${esc(x.metadata?.name)}<div class="subtle">${esc(x.kind)}</div></td><td class="muted">${esc(x.metadata?.namespace||'集群范围')}</td><td>${badge(status,kind)}</td><td>${esc(detail)}</td><td title="${esc(extra)}">${esc(String(extra).slice(0,110))}</td></tr>`;}).join('')||'<tr><td colspan="5" class="empty">暂无资源，或当前筛选没有匹配项</td></tr>'}</tbody></table>`;
}
$('namespace-filter').addEventListener('change',renderClusterTable);$('resource-search').addEventListener('input',renderClusterTable);
$('resource-tabs').addEventListener('click',e=>{const b=e.target.closest('[data-kind]');if(!b)return;state.kind=b.dataset.kind;document.querySelectorAll('#resource-tabs button').forEach(x=>x.classList.toggle('active',x===b));renderClusterTable();});
$('load-resource').addEventListener('click',async()=>{const button=$('load-resource');button.disabled=true;$('explorer-output').textContent='正在查询…';try{$('explorer-output').textContent=JSON.stringify(await api(`/api/cluster/resources?type=${encodeURIComponent($('resource-type').value)}&namespace=${encodeURIComponent($('namespace-filter').value)}`),null,2);}catch(e){$('explorer-output').textContent=e.message;}finally{button.disabled=false;}});
function showResource(index) {
  const item=state.rows[index]; if(!item)return;$('resource-drawer').hidden=false;$('drawer-backdrop').hidden=false;$('drawer-kind').textContent=item.kind;$('drawer-title').textContent=item.metadata?.name;$('drawer-content').textContent=JSON.stringify(item,null,2);$('drawer-tools').replaceChildren();
  if(item.kind==='Pod') {
    const select=document.createElement('select');select.setAttribute('aria-label','容器');for(const c of [...(item.spec?.containers||[]),...(item.spec?.initContainers||[])]){const option=document.createElement('option');option.value=c.name;option.textContent=c.name;select.append(option);}
    const logs=document.createElement('button');logs.className='button primary';logs.textContent='查看容器日志';logs.onclick=async()=>{logs.disabled=true;try{const result=await api(`/api/cluster/logs?namespace=${encodeURIComponent(item.metadata.namespace)}&pod=${encodeURIComponent(item.metadata.name)}&container=${encodeURIComponent(select.value)}`);$('drawer-content').textContent=result.text||'暂无日志';}catch(e){$('drawer-content').textContent=e.message;}finally{logs.disabled=false;}};
    const detail=document.createElement('button');detail.className='button';detail.textContent='资源详情';detail.onclick=()=>{$('drawer-content').textContent=JSON.stringify(item,null,2);};$('drawer-tools').append(select,logs,detail);
  }
}
$('cluster-table').addEventListener('click',e=>{const row=e.target.closest('[data-resource]');if(row)showResource(Number(row.dataset.resource));});
$('cluster-table').addEventListener('keydown',e=>{if((e.key==='Enter'||e.key===' ')&&e.target.dataset.resource){e.preventDefault();showResource(Number(e.target.dataset.resource));}});
function closeDrawer(){$('drawer-content').classList.remove('rich-dependencies');$('resource-drawer').hidden=true;$('drawer-backdrop').hidden=true;}
$('close-drawer').onclick=closeDrawer;$('drawer-backdrop').onclick=closeDrawer;document.addEventListener('keydown',e=>{if(e.key==='Escape')closeDrawer();});
function projectBadge(status) {return badge({HEALTHY:'健康',REACHABLE:'HTTP 可达',UNHEALTHY:'健康异常',OFFLINE:'未连接'}[status]||'探测中',status==='HEALTHY'?'ok':status==='REACHABLE'?'warn':status==='OFFLINE'||status==='UNHEALTHY'?'bad':'');}
function renderProjects() {
  if (typeof renderCatalog === "function") renderCatalog();
  const data=state.projects;notice('projects-warning',state.errors.projects||data?.error);if(!data?.ready)return;$('projects-updated').textContent=time(data.sampledAt);
  $('project-cards').innerHTML=(data.projects||[]).map(p=>`<article class="panel project-card"><div class="project-top"><div class="project-name"><span class="project-symbol">${esc(p.name[0])}</span><h2>${esc(p.name)}</h2></div>${projectBadge(p.status)}</div><div class="project-url">${esc(p.url)}</div><div class="project-facts"><span>HTTP<strong>${p.httpStatus||'—'}</strong></span><span>响应<strong>${p.latency} ms</strong></span></div><div class="project-message">${esc(p.message)}</div><div class="project-links"><a href="${esc(p.url)}" target="_blank" rel="noreferrer" class="button small">打开服务 ↗</a><a href="${esc(p.url)}/swagger-ui.html" target="_blank" rel="noreferrer" class="button small">Swagger ↗</a><a href="${esc(p.url)}/actuator/health" target="_blank" rel="noreferrer" class="button small">健康检查 ↗</a>${p.name==='Lab'?'<a href="#lab" class="button small">进入实验室 →</a>':''}</div></article>`).join('')||'<div class="empty">未配置项目服务</div>';
  document.querySelectorAll('.project-card').forEach((card,i)=>{const button=document.createElement('button');button.className='button small';button.textContent='运行诊断';button.onclick=()=>showProjectDetails(data.projects[i]);card.querySelector('.project-links').append(button);});
  const lab=(data.projects||[]).find(p=>p.name==='Lab');if(lab){$('lab-target-url').textContent=lab.url;$('lab-target-status').textContent={HEALTHY:'服务健康',REACHABLE:'服务可达',UNHEALTHY:'服务健康异常',OFFLINE:'服务未启动或无法连接'}[lab.status];$('lab-docs-link').href=lab.url+'/swagger-ui.html';}
  $('discovered-projects').innerHTML=`<table><thead><tr><th>主类 / JAR</th><th>PID</th><th>实际监听端口</th><th>进程启动时间</th><th>累计 CPU 时间</th></tr></thead><tbody>${(state.running||[]).map(p=>`<tr><td>${esc(p.name)}</td><td class="mono">${p.pid}</td><td class="mono">${esc(p.ports.join(', ')||'无监听端口')}</td><td>${esc(p.startTime?new Date(p.startTime).toLocaleString('zh-CN'):'—')}</td><td>${p.cpuTime} s</td></tr>`).join('')||'<tr><td colspan="5" class="empty">当前账户未发现运行中的 Kuma 项目 JVM</td></tr>'}</tbody></table>`;
  renderEnvironment();
}
function renderRuntimeProcesses(){const rows=(state.host?.processes||[]).filter(p=>/java|node|python/i.test(p.name)).sort((a,b)=>b.memory-a.memory);$('runtime-processes').innerHTML=`<table><thead><tr><th>进程</th><th>PID</th><th>CPU</th><th>内存</th><th>线程</th><th>状态</th><th>运行时间</th></tr></thead><tbody>${processTable(rows)||'<tr><td colspan="7" class="empty">未发现运行时进程</td></tr>'}</tbody></table>`;}
function showProjectDetails(project){$('resource-drawer').hidden=false;$('drawer-backdrop').hidden=false;$('drawer-kind').textContent='PROJECT DIAGNOSTICS';$('drawer-title').textContent=project.name;$('drawer-tools').replaceChildren();const select=document.createElement('select');select.setAttribute('aria-label','项目诊断端点');for(const endpoint of ['health','info','metrics','metrics/jvm.memory.used','metrics/jvm.threads.live','metrics/process.uptime','metrics/system.cpu.usage','threaddump','httpexchanges']){const option=document.createElement('option');option.value=endpoint;option.textContent=endpoint;select.append(option);}const button=document.createElement('button');button.className='button primary';button.textContent='读取诊断';const read=async()=>{button.disabled=true;$('drawer-content').textContent='正在读取项目运行信息…';try{const r=await api(`/api/projects/details?name=${encodeURIComponent(project.name)}&endpoint=${encodeURIComponent(select.value)}`);let body=r.body;try{body=JSON.stringify(JSON.parse(body),null,2);}catch{}$('drawer-content').textContent=`HTTP ${r.status||'未连接'} · ${r.url}\n${r.truncated?'响应已截断为 1 MiB\n':''}\n${body}\n\n401/403 表示需要项目授权；404 表示该端点未开放。`;}catch(e){$('drawer-content').textContent=e.message;}finally{button.disabled=false;}};button.onclick=read;select.onchange=read;$('drawer-tools').append(select,button);read();}
const presets=[];
function preset(group,name,method,path,body=''){presets.push({group,name,method,path,body:typeof body==='string'?body:JSON.stringify(body,null,2)});}
preset('Kafka','查看状态','GET','/lab/kafka/status');preset('Kafka','发送测试消息','POST','/lab/kafka/send',{key:'console-demo',message:'Hello from Kuma Console'});preset('Kafka','最近消费消息','GET','/lab/kafka/messages?limit=100&direction=CONSUMED');
preset('Redis','完整读写实验','POST','/lab/redis/scenario');preset('Redis','写入字符串','POST','/lab/redis/string',{key:'console-demo',value:'hello',ttlSeconds:60});
preset('向量库','查看状态','GET','/lab/vector/status');preset('向量库','完整检索实验','POST','/lab/vector/scenario');preset('向量库','相似度检索','POST','/lab/vector/search',{query:'KumaFramework',topK:5,minScore:0});
preset('MySQL','SQL 语法目录','GET','/lab/mysql/syntax');preset('MySQL','GROUP BY','GET','/lab/mysql/group-by');preset('MySQL','窗口函数','GET','/lab/mysql/window');
preset('事务','转账与回滚实验','POST','/lab/transaction/transfer',{fromAccountId:1,toAccountId:2,amount:100,failAfterDebit:false,rollbackAfterExecution:true});
preset('Binlog','查看监听状态','GET','/lab/binlog/status');preset('Binlog','查看事件','GET','/lab/binlog/events');
preset('Spring','IOC 信息','GET','/lab/spring/ioc');preset('Spring','上下文信息','GET','/lab/spring/context');preset('Spring','完整实验','POST','/lab/spring/scenario');
preset('Java Core','类加载器','GET','/lab/javacore/classloader');preset('Java Core','Mark Word','GET','/lab/javacore/markword');preset('Java Core','HashMap 碰撞','GET','/lab/javacore/hashmap/collision');
preset('JNI','完整实验','POST','/lab/jni/scenario');preset('布隆过滤器','完整实验','POST','/lab/bloom/scenario');preset('雪花算法','完整实验','POST','/lab/snowflake/scenario');
preset('LeetCode','题目列表','GET','/lab/leetcode/problems');preset('Starter','查看模块目录','GET','/lab/starter/catalog');preset('Starter','查看探测结果','GET','/lab/starter/probes');
let selected=0;
function renderPresets(){const q=$('lab-search').value.toLowerCase();let last='';$('lab-presets').innerHTML=presets.map((p,i)=>{if(!`${p.group} ${p.name} ${p.path}`.toLowerCase().includes(q))return '';let title='';if(last!==p.group){last=p.group;title=`<div class="preset-group">${esc(p.group)}</div>`;}return `${title}<button class="preset ${selected===i?'active':''}" data-preset="${i}" title="${esc(p.path)}"><span class="method-tag ${p.method.toLowerCase()}">${esc(p.method)}</span><span>${esc(p.name)}</span></button>`;}).join('')||'<div class="empty">没有匹配的实验</div>';}
function selectPreset(index){const p=presets[index];selected=index;$('lab-request-title').textContent=`${p.group} · ${p.name}`;$('lab-method').value=p.method;$('lab-path').value=p.path;$('lab-body').value=p.body;notice('lab-request-error','');renderPresets();}
$('lab-presets').onclick=e=>{const button=e.target.closest('[data-preset]');if(button)selectPreset(Number(button.dataset.preset));};$('lab-search').oninput=renderPresets;selectPreset(0);
api('/lab-catalog.json').then(catalog=>{for(const entry of catalog){if(!presets.some(p=>p.method===entry.method&&p.path.split('?')[0]===entry.path))preset(entry.group,entry.name,entry.method,entry.path,entry.body??'');}renderPresets();$('lab-discovery-status').textContent=`已内置仓库全部 ${catalog.length} 个实验接口。启动 Lab 后可同步最新 OpenAPI。自动生成的正文是示例，请检查参数。`;}).catch(e=>{$('lab-discovery-status').textContent='完整接口目录未加载：'+e.message;});
let requestHistory=[];try{requestHistory=JSON.parse(localStorage.getItem('kuma-console-history')||'[]');if(!Array.isArray(requestHistory))requestHistory=[];}catch{}
function renderHistory(){$('lab-history').classList.toggle('empty',!requestHistory.length);$('lab-history').innerHTML=requestHistory.map((r,i)=>`<button class="history-row" data-history="${i}"><span>${esc(r.method)}</span><span class="path">${esc(r.path)}</span>${badge(r.status||'失败',r.status>=200&&r.status<300?'ok':'bad')}<span>${r.duration} ms</span><span>${esc(r.time)}</span></button>`).join('')||'暂无请求记录';}
function saveHistory(){try{localStorage.setItem('kuma-console-history',JSON.stringify(requestHistory));}catch{}renderHistory();}
$('clear-history').onclick=()=>{requestHistory=[];saveHistory();};$('lab-history').onclick=e=>{const row=e.target.closest('[data-history]');if(!row)return;const r=requestHistory[Number(row.dataset.history)];$('lab-method').value=r.method;$('lab-path').value=r.path;$('lab-body').value='';$('lab-request-title').textContent='历史请求 · 请重新填写正文';};renderHistory();
$('lab-send').onclick=async()=>{
  const button=$('lab-send');notice('lab-request-error','');const method=$('lab-method').value,path=$('lab-path').value.trim(),body=$('lab-body').value.trim();
  try {if(body)JSON.parse(body);if(path.includes('{')||path.includes('}'))throw Error('请先把路径中的 {参数} 替换为实际值。');}catch(e){notice('lab-request-error',`请求未发送：${e.message}`);return;}
  button.disabled=true;button.textContent='请求中…';$('lab-response-meta').textContent='正在等待 Lab 响应…';
  try {const r=await api('/api/lab/request',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({method,path,body:method==='GET'?'':body,authorization:$('lab-auth').value})});let text=r.body;try{text=JSON.stringify(JSON.parse(text),null,2);}catch{}$('lab-response').textContent=text||'(空响应)';$('lab-response-meta').textContent=`HTTP ${r.status||'连接失败'} · ${r.duration} ms · ${time(Date.now())}${r.truncated?' · 响应超过 1 MiB，已截断':''}`;requestHistory.unshift({method,path,status:r.status,duration:r.duration,time:time(Date.now())});requestHistory=requestHistory.slice(0,20);saveHistory();}
  catch(e){notice('lab-request-error',e.message);$('lab-response-meta').textContent='请求失败';}finally{button.disabled=false;button.textContent='发送请求 ↗';}
};
$('copy-response').onclick=async()=>{try{await navigator.clipboard.writeText($('lab-response').textContent);$('copy-response').textContent='已复制';setTimeout(()=>$('copy-response').textContent='复制结果',1500);}catch{$('copy-response').textContent='复制失败';}};
function sampleValue(schema,schemas,depth=0){if(!schema||depth>4)return null;if(schema.$ref)return sampleValue(schemas[schema.$ref.split('/').pop()],schemas,depth+1);if(schema.example!==undefined)return schema.example;if(schema.default!==undefined)return schema.default;if(schema.enum)return schema.enum[0];if(schema.type==='object'||schema.properties)return Object.fromEntries(Object.entries(schema.properties||{}).map(([k,v])=>[k,sampleValue(v,schemas,depth+1)]));if(schema.type==='array')return [sampleValue(schema.items,schemas,depth+1)];if(schema.type==='boolean')return false;if(schema.type==='integer'||schema.type==='number')return 1;return '';}
$('discover-lab').onclick=async()=>{const button=$('discover-lab');button.disabled=true;$('lab-discovery-status').textContent='正在加载 Lab OpenAPI…';try{const r=await api('/api/lab/request',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({method:'GET',path:'/v3/api-docs',authorization:$('lab-auth').value})});if(r.status!==200)throw Error(`Lab OpenAPI 返回 ${r.status||'连接失败'}`);const doc=JSON.parse(r.body);let count=0;for(const [path,methods] of Object.entries(doc.paths||{})){if(!path.startsWith('/lab/'))continue;for(const [method,op] of Object.entries(methods)){if(!['get','post','put','delete','patch'].includes(method))continue;const upper=method.toUpperCase();if(presets.some(p=>p.method===upper&&p.path.split('?')[0]===path))continue;const schema=op.requestBody?.content?.['application/json']?.schema;const body=schema?sampleValue(schema,doc.components?.schemas||{}):'';preset(op.tags?.[0]||'其他实验',op.summary||path,upper,path,body);count++;}}renderPresets();$('lab-discovery-status').textContent=`已同步 ${count} 个新接口。自动生成的 JSON 是示例，请按实际业务填写。`;}catch(e){$('lab-discovery-status').textContent=e.message+'，可继续使用内置实验和手动请求。';}finally{button.disabled=false;}};
async function refresh(){if(state.busy)return;state.busy=true;$('refresh').disabled=true;const keys=['host','cluster','projects'];if(!state.runningAt||Date.now()-state.runningAt>10000){keys.push('running');state.runningAt=Date.now();}const results=await Promise.allSettled(keys.map(async key=>{state[key]=await api(key==='running'?'/api/projects/running':'/api/'+key);delete state.errors[key];}));results.forEach((r,i)=>{if(r.status==='rejected')state.errors[keys[i]]=r.reason.message;});renderHost();renderCluster();renderProjects();const failed=Object.keys(state.errors).length;$('connection').innerHTML=`<i class="live-dot" style="${failed?'background:var(--amber)':''}"></i>${failed?'部分连接异常':'本机已连接'}`;state.busy=false;$('refresh').disabled=false;}
$('refresh').onclick=refresh;$('pause').onclick=()=>{state.paused=!state.paused;$('pause').textContent=state.paused?'恢复刷新':'暂停刷新';$('footer-status').textContent=state.paused?'面板刷新已暂停 · 后台仍在采样':'本机 2s / 项目 10s / 集群 15s';};
setInterval(()=>{$('clock').textContent=new Date().toLocaleString('zh-CN',{hour12:false});},1000);setInterval(()=>{if(!state.paused&&!document.hidden)refresh();},2000);document.addEventListener('visibilitychange',()=>{if(!document.hidden&&!state.paused)refresh();});refresh();
