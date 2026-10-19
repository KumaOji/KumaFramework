'use strict';
(() => {
  const intro=document.createElement('dialog');intro.id='kuma-intro';intro.setAttribute('aria-label','欢迎来到 Kuma Console');
  intro.innerHTML='<div class="intro-art"></div><div class="intro-shimmer" aria-hidden="true"></div><button class="intro-skip button" autofocus>进入工作空间 ↗</button><div class="intro-copy"><span class="hero-kicker">YOUR LITTLE UNIVERSE OF CODE</span><h1>Kuma Console<span>让每一次探索，都闪闪发光。</span></h1><p>工作空间已就绪，开启今天的灵感之旅。</p><div class="intro-orbit" aria-hidden="true"><i></i><i></i><i></i></div></div>';
  document.body.append(intro);
  let timer;
  const close=()=>{clearTimeout(timer);if(intro.open)intro.close();};
  function play(){if(intro.open)return;intro.showModal();timer=setTimeout(close,2600);}
  intro.querySelector('button').onclick=close;
  intro.addEventListener('close',()=>clearTimeout(timer));
  window.addEventListener('console:preview-intro',play);
  let first=true;try{first=sessionStorage.getItem('kuma-intro-seen')!=='yes';sessionStorage.setItem('kuma-intro-seen','yes');}catch{}
  let welcomeEnabled=true;try{welcomeEnabled=localStorage.getItem('kuma-console-welcome')!=='false';}catch{}
  // The native desktop splash already accompanies the real backend startup.
  if(first&&welcomeEnabled&&!window.kumaDesktop?.isDesktop&&!matchMedia('(prefers-reduced-motion: reduce)').matches)play();
})();
