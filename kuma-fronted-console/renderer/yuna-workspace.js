'use strict';
(() => {
  const pages={
    overview:{title:'本机概览',label:'01 / SYSTEM OVERVIEW',caption:'看见系统的每一次脉动',description:'处理器、内存与进程，一眼掌握。',art:'银白发少女在系统监控工作台前查看数据'},
    server:{title:'服务器与 WSL',label:'02 / SERVER & WSL',caption:'远程与本机，在同一处连接',description:'远程服务器、本机 WSL 与 k3s，在同一工作空间管理。',art:'银白发少女在服务器机房检查运行状态'},
    projects:{title:'项目与组件',label:'03 / PROJECTS & COMPONENTS',caption:'让项目与公共能力，自然连接',description:'项目目录、服务监控与公共组件，在同一处管理。',art:'银白发少女整理项目蓝图与模块'},
    lab:{title:'Lab 实验室',label:'04 / EXPERIMENT ATELIER',caption:'找到实验，开始探索',description:'搜索、收藏、运行，从好奇走向理解。',art:'银白发少女在实验室观察发光的玻璃烧瓶'},
    blog:{title:'Blog',label:'05 / KNOWLEDGE GARDEN',caption:'把今天的发现，留给明天',description:'博客与知识仓库，记录每一步探索。',art:'银白发少女在数字图书馆阅读和记录'},
    settings:{title:'Console 配置',label:'CONSOLE / PREFERENCES',caption:'让工作空间，更合你的习惯',image:'overview',art:'银白发的控制台助手在工作台前'}
  };
  for(const [id,config] of Object.entries(pages)){
    const page=document.getElementById(id);if(!page)continue;
    page.querySelector(':scope > .anime-hero')?.remove();
    const old=page.querySelector(':scope > .page-heading');
    const hero=document.createElement('div');hero.className='workspace-hero';hero.dataset.workspace=id;
    const artwork=document.createElement('img');artwork.className='workspace-hero-art';artwork.src=`/assets/yuna/${config.image||id}.png`;artwork.alt=config.art;artwork.width=1536;artwork.height=1024;artwork.decoding='async';
    const copy=document.createElement('div');copy.className='workspace-hero-copy';
    const label=document.createElement('span');label.className='workspace-kicker';label.textContent=config.label;
    const caption=document.createElement('p');caption.className='workspace-caption';caption.textContent=config.caption;
    copy.append(label);
    let actions;
    if(old){
      for(const child of [...old.children]){
        if(child.classList.contains('controls')){actions=child;actions.classList.add('workspace-page-actions');}
        else copy.append(child);
      }
    }else{
      const title=document.createElement('h1');title.textContent=config.title;
      const description=document.createElement('p');description.className='workspace-description';description.textContent=config.description;copy.append(title,description);
    }
    copy.append(caption);hero.append(artwork,copy);
    const marker=document.createElement('span');marker.className='workspace-art-credit';marker.textContent='KUMA / WORKSPACE';hero.append(marker);
    if(old)old.replaceWith(hero);else page.prepend(hero);
    if(actions)hero.after(actions);
    const nav=document.querySelector(`nav a[data-page="${id}"]`);
    if(nav){
      nav.title=config.title;
      const icon=nav.querySelector('.nav-icon');
      const image=document.createElement('img');image.src=artwork.src;image.alt='';image.width=40;image.height=40;image.decoding='async';
      icon.replaceChildren(image);icon.classList.add('workspace-nav-art');
    }
  }
  const preparedArtwork=new Map();
  function prepareArtwork(name){
    if(!preparedArtwork.has(name)){
      const image=new Image(1536,1024);image.className='workspace-hero-art';image.decoding='async';image.src=`/assets/yuna/${name}.png`;
      preparedArtwork.set(name,image.decode().then(()=>image).catch(()=>{preparedArtwork.delete(name);return null;}));
    }
    return preparedArtwork.get(name);
  }
  function setWorkspaceArt(image,name,description){
    const hero=image.closest('.workspace-hero'),path=`/assets/yuna/${name}.png`;
    hero.dataset.artScene=name;
    if(image.getAttribute('src')===path){image.alt=description;return;}
    // Retain the current image until its replacement can be painted immediately.
    prepareArtwork(name).then(prepared=>{
      if(!prepared||hero.dataset.artScene!==name)return;
      const current=hero.querySelector('.workspace-hero-art');
      if(current.getAttribute('src')===path)return;
      prepared.alt=description;current.replaceWith(prepared);
    });
  }
  function updateWorkspaceArt(){
    const image=document.querySelector('#server .workspace-hero-art');if(!image)return;
    const local=state.infrastructureTab==='wsl';setWorkspaceArt(image,local?'cluster':'server',local?'银白发少女观察相互连接的集群立方体':'银白发少女在服务器机房检查运行状态');
    const projectImage=document.querySelector('#projects .workspace-hero-art');if(projectImage){const components=state.servicesTab==='components';setWorkspaceArt(projectImage,components?'components':'projects',components?'银白发少女在组件工作台检查认证与网关连接':'银白发少女整理项目蓝图与模块');}
  }
  window.addEventListener('hashchange',updateWorkspaceArt);updateWorkspaceArt();
  ['server','cluster','projects','components'].forEach(prepareArtwork);
  document.querySelectorAll('.hero-kicker').forEach(label=>label.textContent='KUMA CONSOLE / WORKSPACE');
})();
