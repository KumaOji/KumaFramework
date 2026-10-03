'use strict';
if(window.kumaDesktop?.isDesktop){document.body.classList.add('desktop');document.getElementById('desktop-bar').hidden=false;}
function setTheme(theme){
  document.documentElement.dataset.theme=theme;
  document.getElementById('theme-toggle').textContent=theme==='dark'?'浅色模式':'深色模式';
  try{localStorage.setItem('kuma-console-theme',theme);}catch{}
  if(typeof renderChart==='function')renderChart();
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
