'use strict';
(() => {
  const toggle=document.getElementById('workspace-toggle');
  const navigation=document.getElementById('workspace-navigation');
  if(!toggle||!navigation)return;
  const narrow=window.matchMedia('(max-width:850px)');
  const backdrop=document.getElementById('sidebar-backdrop');
  let desktopCollapsed=false,mobileExpanded=false;
  try{desktopCollapsed=(localStorage.getItem('kuma-sidebar-collapsed')??localStorage.getItem('kuma-workspace-collapsed'))==='true';}catch{}
  function renderSidebar(){
    const expanded=narrow.matches?mobileExpanded:!desktopCollapsed;
    document.documentElement.dataset.sidebar=expanded?'expanded':'collapsed';
    navigation.hidden=false;
    toggle.setAttribute('aria-expanded',String(expanded));
    toggle.setAttribute('aria-label',expanded?'收起左侧导航':'展开左侧导航');
    toggle.title=expanded?'收起左侧导航':'展开左侧导航';
    toggle.querySelector('.workspace-chevron').textContent=expanded?'‹':'›';
    if(backdrop)backdrop.hidden=!(narrow.matches&&expanded);
  }
  toggle.onclick=()=>{
    if(narrow.matches)mobileExpanded=!mobileExpanded;
    else{desktopCollapsed=!desktopCollapsed;try{localStorage.setItem('kuma-sidebar-collapsed',String(desktopCollapsed));}catch{}}
    renderSidebar();
  };
  const closeMobile=()=>{mobileExpanded=false;renderSidebar();};
  if(backdrop)backdrop.onclick=()=>{closeMobile();toggle.focus({preventScroll:true});};
  document.querySelector('.sidebar').addEventListener('click',event=>{if(narrow.matches&&event.target.closest('[data-page]'))closeMobile();});
  document.addEventListener('keydown',event=>{if(event.key==='Escape'&&mobileExpanded)closeMobile();});
  narrow.addEventListener('change',()=>{mobileExpanded=false;renderSidebar();});
  renderSidebar();
})();
if(window.kumaDesktop?.isDesktop){document.body.classList.add('desktop');document.getElementById('desktop-bar').hidden=false;}
if(window.kumaDesktop?.platform==='win32'&&window.kumaDesktop.getOpenAtLogin){
  const settings=document.createElement('article');
  settings.className='panel';
  settings.id='desktop-settings';
  settings.innerHTML='<div class="panel-heading"><div><h2>桌面设置</h2><p>登录 Windows 后自动打开 Kuma Console</p></div><label class="controls"><input id="desktop-startup-toggle" type="checkbox" role="switch" disabled>开机自动启动</label></div><p id="desktop-startup-status" class="muted" role="status" style="margin:0 23px 20px">正在读取设置…</p>';
  const settingsContainer=document.getElementById('console-desktop-settings');
  if(settingsContainer){settingsContainer.append(settings);const hint=document.getElementById('console-desktop-hint');if(hint)hint.hidden=true;}
  else document.querySelector('#overview .page-heading').after(settings);
  const toggle=settings.querySelector('input');
  const status=settings.querySelector('[role=status]');
  let busy=false;
  async function refreshStartup(){
    if(busy)return;
    busy=true;toggle.disabled=true;
    try{
      toggle.checked=await window.kumaDesktop.getOpenAtLogin();
      status.textContent=toggle.checked?'已开启，下次登录 Windows 时自动启动':'已关闭，可随时在这里开启';
    }catch{status.textContent='读取设置失败，请重新打开窗口重试';}
    finally{toggle.disabled=false;busy=false;}
  }
  toggle.onchange=async()=>{
    const previous=!toggle.checked;
    busy=true;toggle.disabled=true;
    try{
      toggle.checked=await window.kumaDesktop.setOpenAtLogin(toggle.checked);
      status.textContent=toggle.checked?'已开启，下次登录 Windows 时自动启动':'已关闭开机自动启动';
    }catch{toggle.checked=previous;status.textContent='保存失败，请重试';}
    finally{toggle.disabled=false;busy=false;}
  };
  window.addEventListener('focus',refreshStartup);
  refreshStartup();
}
function setTheme(theme){
  document.documentElement.dataset.theme=theme;
  document.getElementById('theme-toggle').textContent=theme==='dark'?'浅色模式':'深色模式';
  try{localStorage.setItem('kuma-console-theme',theme);}catch{}
  if(typeof renderChart==='function')renderChart();
  window.dispatchEvent(new CustomEvent('console:themechange',{detail:theme}));
}
let savedTheme;try{savedTheme=localStorage.getItem('kuma-console-theme');}catch{}
try{
  if(localStorage.getItem('kuma-console-theme-default')!=='dark-v1'){
    savedTheme='dark';localStorage.setItem('kuma-console-theme-default','dark-v1');
  }
}catch{}
setTheme(savedTheme==='light'?'light':'dark');
document.getElementById('theme-toggle').onclick=()=>setTheme(document.documentElement.dataset.theme==='dark'?'light':'dark');
const navigationIcons={overview:'<rect x="3" y="3" width="7" height="7" rx="2"/><rect x="14" y="3" width="7" height="7" rx="2"/><rect x="3" y="14" width="7" height="7" rx="2"/><rect x="14" y="14" width="7" height="7" rx="2"/>',server:'<rect x="3" y="3" width="18" height="7" rx="2"/><rect x="3" y="14" width="18" height="7" rx="2"/><path d="M7 6.5h.01M7 17.5h.01M11 6.5h6M11 17.5h6"/>',cluster:'<path d="m12 3 9 5v8l-9 5-9-5V8l9-5Z"/><path d="m3 8 9 5 9-5M12 13v8"/>',projects:'<rect x="3" y="5" width="18" height="15" rx="3"/><path d="M8 5V3h8v2M3 11h18M10 11v3h4v-3"/>',lab:'<path d="M9 3h6M10 3v7L4 19a1 1 0 0 0 1 2h14a1 1 0 0 0 1-2l-6-9V3M8 15h8"/>'};
document.querySelectorAll('nav a[data-page]').forEach(link=>{link.querySelector('.nav-icon').innerHTML=`<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">${navigationIcons[link.dataset.page] || navigationIcons.server}</svg>`;});
document.addEventListener('keydown',event=>{if(event.key==='Escape'&&typeof closeDrawer==='function')closeDrawer();});
