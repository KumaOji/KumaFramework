'use strict';
(() => {
  const theme=document.getElementById('console-theme');
  const welcome=document.getElementById('console-welcome');
  const status=document.getElementById('console-settings-status');
  theme.value=document.documentElement.dataset.theme;
  try{welcome.checked=localStorage.getItem('kuma-console-welcome')!=='false';}catch{welcome.checked=true;}
  theme.onchange=()=>{setTheme(theme.value);status.textContent='界面主题已更新。';};
  window.addEventListener('console:themechange',event=>{theme.value=event.detail;});
  welcome.onchange=()=>{
    try{localStorage.setItem('kuma-console-welcome',String(welcome.checked));status.textContent=welcome.checked?'已开启浏览器启动欢迎动画。':'已关闭浏览器启动欢迎动画。';}
    catch{welcome.checked=!welcome.checked;status.textContent='设置未能保存，请重试。';}
  };
  document.getElementById('console-preview-intro').onclick=()=>window.dispatchEvent(new Event('console:preview-intro'));
  document.getElementById('console-client-mode').textContent=window.kumaDesktop?.isDesktop?'桌面版'+(window.kumaDesktop.platform==='win32'?' · Windows':''):'浏览器';
  document.getElementById('console-client-address').textContent=location.origin;
})();
