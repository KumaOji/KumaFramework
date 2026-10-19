// Isolated browser verification: serves repository assets and mocks monitoring/API discovery only.
const {app,BrowserWindow}=require('electron');
const http=require('node:http');
const fs=require('node:fs/promises');
const path=require('node:path');
const assert=require('node:assert/strict');
const renderer=path.resolve(__dirname,'../renderer');
const catalog=require('../renderer/lab-catalog.json');
const lessons=require('../renderer/lab-learning.json');
const output=path.resolve(__dirname,'../build/lab-ui-smoke');
app.setPath('userData',path.join(output,'profile'));
app.commandLine.appendSwitch('disable-gpu');
let window,server;
let requests=0;
let discoveryInvalid=false;
let labState='STOPPED',labStarts=0,labStops=0;
let blogState='RUNNING',blogStarts=0,blogStops=0,knowledgeSyncs=0;
let backendState='STOPPED',backendStarts=0,backendStops=0;
const componentState={uaa:'STOPPED',gateway:'RUNNING'};
const componentActions=[];
const wslStates={'Ubuntu-24.04':'RUNNING','Debian':'STOPPED'},wslActions=[];
async function serve(request,response){
  const url=new URL(request.url,'http://localhost');
  if(url.pathname.startsWith('/api/')){
    let result={ready:false};
    if(url.pathname==='/api/projects/catalog')result={projects:[]};
    if(url.pathname==='/api/projects/running')result=[];
    if(url.pathname==='/api/projects')result={ready:true,sampledAt:new Date().toISOString(),projects:[{name:'Lab',url:'http://127.0.0.1:19090/api',status:'OFFLINE',httpStatus:0,latency:0,message:'服务未启动'}]};
    if(url.pathname.startsWith('/api/wsl/distributions')||url.pathname==='/api/wsl/shutdown'){
      if(request.method==='POST'){
        let text='';for await(const chunk of request)text+=chunk;
        const action=url.pathname.split('/').pop();
        if(action==='shutdown'){for(const name of Object.keys(wslStates))wslStates[name]='STOPPED';wslActions.push('shutdown');}
        else{const body=JSON.parse(text);wslStates[body.distribution]=action==='start'?'RUNNING':'STOPPED';wslActions.push(body.distribution+'/'+action);}
      }
      result={supported:true,error:'',canShutdown:Object.values(wslStates).includes('RUNNING'),distributions:Object.entries(wslStates).map(([name,value])=>({name,state:value,canStart:value==='STOPPED',canStop:value==='RUNNING',error:''}))};
    }
    if(url.pathname.startsWith('/api/components')){
      const parts=url.pathname.split('/'),name=parts[3],action=parts[4];
      if(action==='start'||action==='stop'){assert.equal(request.method,'POST');componentState[name]=action==='start'?'RUNNING':'STOPPED';componentActions.push(name+'/'+action);}
      result=action==='logs'?{body:'Component log'}:Object.fromEntries(Object.entries(componentState).map(([key,value])=>[key,{state:value,message:key,pid:value==='RUNNING'?123:0,canStart:value==='STOPPED',canStop:value==='RUNNING',url:key==='uaa'?'http://localhost:33336':'http://localhost:18080',distribution:'Ubuntu-24.04'}]));
    }
    if(url.pathname.startsWith('/api/blog/backend')){
      if(url.pathname.endsWith('/start')){backendState='RUNNING';backendStarts++;}
      if(url.pathname.endsWith('/stop')){backendState='STOPPED';backendStops++;}
      result=url.pathname.endsWith('/logs')?{body:'Backend log'}:{state:backendState,message:'Backend',pid:backendState==='RUNNING'?432:0,
        canStart:backendState==='STOPPED',canStop:backendState==='RUNNING',url:'http://localhost:9000/api',distribution:'Ubuntu-24.04'};
    }
    if(url.pathname.startsWith('/api/blog/process')){
      if(url.pathname.endsWith('/start')){assert.equal(request.method,'POST');blogState='RUNNING';blogStarts++;}
      if(url.pathname.endsWith('/stop')){assert.equal(request.method,'POST');blogState='STOPPED';blogStops++;}
      if(url.pathname.endsWith('/sync')){assert.equal(request.method,'POST');knowledgeSyncs++;}
      result=url.pathname.endsWith('/logs')?{body:'Blog startup log'}:{state:blogState,message:'Blog',pid:blogState==='RUNNING'?321:0,
        canStart:blogState==='STOPPED',canStop:blogState==='RUNNING',canOpen:blogState==='RUNNING',canSync:true,knowledgeState:'SUCCESS',knowledgeMessage:'知识仓库已同步',knowledgeUrl:'http://localhost:4321/knowledge',url:'http://localhost:4321',directory:'D:/KumaProject/kuma-blog-fronted/KumaBlog'};
    }
    if(url.pathname.startsWith('/api/lab/process')){
      if(url.pathname.endsWith('/start')){assert.equal(request.method,'POST');labState='RUNNING';labStarts++;}
      if(url.pathname.endsWith('/stop')){assert.equal(request.method,'POST');labState='STOPPED';labStops++;}
      result=url.pathname.endsWith('/logs')?{body:'Lab startup test log'}:
        {state:labState,message:labState==='RUNNING'?'Lab 已启动':'Lab 已停止',pid:labState==='RUNNING'?12345:0,canStart:labState==='STOPPED',canStop:labState==='RUNNING'};
    }
    if(url.pathname==='/api/lab/request'){
      let text='';for await(const chunk of request)text+=chunk;
      const body=JSON.parse(text);
      assert.equal(body.path,'/v3/api-docs');requests++;
      result={status:200,body:JSON.stringify({paths:{'/lab/kafka/status':{get:{tags:['Kafka 已同步'],summary:'更新后的状态名称'}}}})};
      if(discoveryInvalid)result={status:200,body:'{}'};
    }
    response.writeHead(200,{'Content-Type':'application/json'});response.end(JSON.stringify(result));return;
  }
  const target=path.resolve(renderer,'.'+(url.pathname==='/'?'/index.html':url.pathname));
  if(!target.startsWith(renderer+path.sep)){response.writeHead(403);response.end();return;}
  let content=await fs.readFile(target);
  if(url.pathname==='/yuna-workspace.js'){
    // Delay one decode promise without blocking the browser's load event.
    const delay=`(()=>{const decode=Image.prototype.decode;let release;const pending=new Promise(resolve=>{release=resolve;});Image.prototype.decode=function(){return decode.call(this).then(()=>this.src.endsWith('/cluster.png')?pending:undefined);};window.__releaseClusterArtwork=()=>{Image.prototype.decode=decode;release();};})();\n`;
    content=Buffer.concat([Buffer.from(delay),content]);
  }
  const types={'.html':'text/html','.js':'text/javascript','.css':'text/css','.json':'application/json','.md':'text/plain','.png':'image/png','.svg':'image/svg+xml'};
  response.writeHead(200,{'Content-Type':(types[path.extname(target)]||'application/octet-stream')+'; charset=utf-8'});
  response.end(content);
}
const evaluate=script=>window.webContents.executeJavaScript(script);
const paint=async()=>{
  await evaluate('document.getAnimations().forEach(animation=>{if(Number.isFinite(animation.effect.getTiming().iterations))animation.finish();})');
  await evaluate('new Promise(resolve=>requestAnimationFrame(()=>requestAnimationFrame(resolve)))');
  // Hidden Electron windows can finish DOM layout before committing the captured surface.
  await new Promise(resolve=>setTimeout(resolve,150));
};
async function waitFor(script){
  const deadline=Date.now()+10000;
  while(!await evaluate(script)){
    if(Date.now()>deadline)throw Error('Browser condition timed out: '+script);
    await new Promise(resolve=>setTimeout(resolve,50));
  }
}
async function check(id){
  await evaluate(`selectPreset(presets.findIndex(p=>p.id===${JSON.stringify(id)}));`);
  await waitFor(`!document.getElementById('lab-learning-guide').textContent.startsWith('正在加载')`);
  assert.ok(await evaluate(`!document.getElementById('lab-learning-workspace').hidden && document.getElementById('lab-api-workspace').hidden`));
  assert.ok(await evaluate(`document.getElementById('lab-learning-command').textContent.length>10 && !document.getElementById('lab-learning-guide').textContent.includes('说明加载失败')`));
}
app.whenReady().then(async()=>{
  await fs.mkdir(output,{recursive:true});
  server=http.createServer((req,res)=>serve(req,res).catch(error=>{res.writeHead(500);res.end(error.message);}));
  await new Promise(resolve=>server.listen(0,'127.0.0.1',resolve));
  window=new BrowserWindow({show:false,width:1440,height:1000,webPreferences:{nodeIntegration:false,contextIsolation:true,sandbox:true,backgroundThrottling:false}});
  await window.loadFile(path.resolve(__dirname,'../electron/splash.html'));
  await evaluate(`new Promise((resolve,reject)=>{const img=new Image();img.onload=resolve;img.onerror=reject;img.src='../renderer/assets/yuna/startup-jacket-v2.png';})`);
  await paint();
  await fs.writeFile(path.join(output,'sakura-startup.png'),(await window.webContents.capturePage()).toPNG());
  await window.webContents.session.clearStorageData({storages:['localstorage']});
  await window.loadURL(`http://127.0.0.1:${server.address().port}/#lab`);
  await waitFor(`typeof presets!=='undefined' && presets.filter(p=>p.kind==='learning').length===${lessons.length}`);
  await evaluate(`location.hash='lab';navigate();setTheme('dark');`);
  await waitFor(`window.LabExplorer && !document.getElementById('kuma-intro').open`);
  await evaluate(`document.body.classList.add('desktop');document.getElementById('desktop-bar').hidden=false;`);
  await waitFor(`(()=>{const image=document.querySelector('#desktop-bar img');return image.complete&&image.naturalWidth>0;})()`);
  assert.ok(await evaluate(`(()=>{const icon=document.querySelector('#desktop-bar img').getBoundingClientRect(),bar=document.getElementById('desktop-bar').getBoundingClientRect();return icon.width===18&&icon.height===18&&Math.abs(icon.y+icon.height/2-bar.y-bar.height/2)<1&&document.querySelector('link[rel="icon"]').href===document.querySelector('#desktop-bar img').src;})()`),'title-bar icon must be centered and match the favicon');
  await paint();
  await fs.writeFile(path.join(output,'kuma-titlebar.png'),(await window.webContents.capturePage()).toPNG());
  await evaluate(`document.body.classList.remove('desktop');document.getElementById('desktop-bar').hidden=true;`);
  await waitFor(`document.querySelectorAll('.workspace-hero-art').length===6 && Array.from(document.querySelectorAll('.workspace-hero-art')).every(img=>img.complete&&img.naturalWidth>0)`);
  assert.equal(await evaluate(`new Set(Array.from(document.querySelectorAll('.workspace-hero-art')).map(img=>img.src)).size`),5);
  assert.equal(await evaluate(`document.querySelectorAll('#workspace-navigation > a .workspace-nav-art img').length`),5);
  assert.ok(await evaluate(`!document.getElementById('open-lab-finder') && !document.querySelector('nav [data-page="cluster"]') && !document.querySelector('nav [data-page="components"]') && document.querySelectorAll('#workspace-navigation > a[data-page]').length===5`));
  await evaluate(`window.__sidebarBefore=document.querySelector('.sidebar').getBoundingClientRect().width;window.__contentBefore=document.querySelector('.workspace').getBoundingClientRect().x;document.getElementById('workspace-toggle').click();`);
  assert.ok(await evaluate(`!document.getElementById('workspace-navigation').hidden && document.documentElement.dataset.sidebar==='collapsed' && document.getElementById('workspace-toggle').getAttribute('aria-expanded')==='false' && !document.getElementById('lab').hidden && document.querySelector('.sidebar').getBoundingClientRect().width<window.__sidebarBefore && document.querySelector('.workspace').getBoundingClientRect().x<window.__contentBefore`));
  await evaluate(`document.getElementById('console-settings-link').click();`);
  await waitFor(`state.page==='settings' && !document.getElementById('settings').hidden`);
  assert.ok(await evaluate(`document.documentElement.dataset.sidebar==='collapsed' && !document.getElementById('workspace-navigation').hidden && !document.getElementById('workspace-navigation').contains(document.getElementById('console-settings-link')) && document.getElementById('console-settings-link').getAttribute('aria-current')==='page'`));
  await evaluate(`document.getElementById('console-theme').value='light';document.getElementById('console-theme').dispatchEvent(new Event('change'));`);
  assert.equal(await evaluate('document.documentElement.dataset.theme'),'light');
  await evaluate(`document.getElementById('theme-toggle').click();`);
  assert.equal(await evaluate(`document.getElementById('console-theme').value`),'dark');
  await evaluate(`document.getElementById('console-welcome').checked=false;document.getElementById('console-welcome').dispatchEvent(new Event('change'));document.getElementById('console-preview-intro').click();`);
  assert.ok(await evaluate(`document.getElementById('kuma-intro').open && localStorage.getItem('kuma-console-welcome')==='false'`));
  await evaluate(`document.querySelector('#kuma-intro .intro-skip').click();location.hash='lab';navigate();`);
  await evaluate(`document.getElementById('workspace-toggle').click();`);
  assert.ok(await evaluate(`!document.getElementById('workspace-navigation').hidden`));
  assert.ok(await evaluate(`!document.getElementById('replay-intro') && !document.getElementById('kuma-intro').open`));
  assert.ok(await evaluate(`document.getElementById('lab-presets').hidden && document.getElementById('toggle-lab-catalog').getAttribute('aria-expanded')==='false'`));
  await evaluate(`document.getElementById('toggle-lab-catalog').click()`);
  assert.ok(await evaluate(`!document.getElementById('lab-presets').hidden && document.getElementById('toggle-lab-catalog').getAttribute('aria-expanded')==='true'`));
  await evaluate(`document.getElementById('toggle-lab-catalog').click()`);
  assert.ok(await evaluate(`!document.getElementById('lab').hidden`));
  assert.ok(await evaluate(`document.querySelector('[data-page=lab]').nextElementSibling.dataset.page==='blog'`));
  assert.ok(await evaluate(`document.querySelector('[data-page=lab]').previousElementSibling.dataset.page==='projects'`));
  await evaluate(`location.hash='server';navigate();`);
  await waitFor(`state.page==='server' && state.infrastructureTab==='remote'`);
  await evaluate(`new Promise(resolve=>setTimeout(resolve,80))`);
  await evaluate(`window.scrollTo(0,120);window.__tabScrollY=window.scrollY;document.getElementById('infrastructure-wsl-tab').click();`);
  await waitFor(`state.infrastructureTab==='wsl'`);
  await evaluate(`new Promise(resolve=>requestAnimationFrame(()=>requestAnimationFrame(resolve)))`);
  assert.equal(await evaluate('window.scrollY'),await evaluate('window.__tabScrollY'),'switching environment tabs must preserve scroll position');
  assert.ok(await evaluate(`(()=>{const img=document.querySelector('#server .workspace-hero-art');return img.complete&&img.naturalWidth>0&&img.src.endsWith('/server.png');})()`),'keep decoded artwork visible while the next image is loading');
  await evaluate(`document.getElementById('infrastructure-remote-tab').click();`);
  await waitFor(`state.infrastructureTab==='remote'`);
  await evaluate('window.__releaseClusterArtwork()');
  await evaluate(`(async()=>{const image=new Image();image.src='/assets/yuna/cluster.png';await image.decode();await new Promise(resolve=>requestAnimationFrame(()=>requestAnimationFrame(resolve)));})()`);
  assert.ok(await evaluate(`document.querySelector('#server .workspace-hero-art').src.endsWith('/server.png')`),'late image decoding must not replace artwork after switching back');
  await evaluate(`document.getElementById('infrastructure-wsl-tab').click();`);
  await waitFor(`(()=>{const img=document.querySelector('#server .workspace-hero-art');return img.complete&&img.naturalWidth>0&&img.src.endsWith('/cluster.png');})()`);
  await evaluate('history.back()');
  await waitFor(`state.infrastructureTab==='remote' && !document.getElementById('infrastructure-remote').hidden`);
  await evaluate('history.forward()');
  await waitFor(`state.infrastructureTab==='wsl' && !document.getElementById('cluster').hidden`);
  await evaluate(`location.hash='cluster';navigate();pollWslControl();`);
  await waitFor(`!document.getElementById('wsl-stop').disabled`);
  assert.ok(await evaluate(`state.page==='server' && !document.getElementById('server').hidden && !document.getElementById('cluster').hidden && document.getElementById('infrastructure-remote').hidden && document.querySelector('nav [data-page="server"]').classList.contains('active')`));
  await evaluate(`document.getElementById('infrastructure-remote-tab').click();`);
  await waitFor(`state.infrastructureTab==='remote' && !document.getElementById('infrastructure-remote').hidden && document.getElementById('cluster').hidden`);
  await evaluate(`document.getElementById('infrastructure-remote-tab').dispatchEvent(new KeyboardEvent('keydown',{key:'ArrowRight',bubbles:true}));`);
  await waitFor(`location.hash==='#server/wsl' && state.infrastructureTab==='wsl' && document.getElementById('infrastructure-wsl-tab').getAttribute('aria-selected')==='true'`);
  assert.ok(await evaluate(`document.querySelector('#server .workspace-hero-art').src.endsWith('/cluster.png')`));
  await evaluate(`document.getElementById('wsl-control-select').value='Debian';renderWslControl();document.getElementById('wsl-start').click();`);
  await waitFor(`!document.getElementById('wsl-stop').disabled && !wslControlBusy`);
  await evaluate(`document.getElementById('wsl-stop').click();`);
  await waitFor(`!document.getElementById('wsl-start').disabled && !wslControlBusy`);
  await evaluate(`document.getElementById('wsl-shutdown').click();`);
  await waitFor(`document.getElementById('wsl-shutdown').disabled && !wslControlBusy`);
  assert.deepEqual(wslActions,['Debian/start','Debian/stop','shutdown']);
  await evaluate(`location.hash='components';navigate();pollComponents();`);
  assert.ok(await evaluate(`state.page==='projects' && !document.getElementById('projects').hidden && !document.getElementById('components').hidden && document.getElementById('project-services').hidden && document.querySelector('nav [data-page="projects"]').classList.contains('active')`));
  await evaluate(`document.getElementById('service-projects-tab').click();`);
  await waitFor(`state.servicesTab==='projects' && !document.getElementById('project-services').hidden && document.getElementById('components').hidden && document.getElementById('project-cards').textContent.includes('Lab')`);
  await evaluate(`document.getElementById('service-projects-tab').dispatchEvent(new KeyboardEvent('keydown',{key:'End',bubbles:true}));`);
  await waitFor(`location.hash==='#projects/components' && state.servicesTab==='components' && document.getElementById('service-components-tab').getAttribute('aria-selected')==='true'`);
  assert.ok(await evaluate(`document.querySelector('#projects .workspace-hero-art').src.endsWith('/components.png')`));
  await waitFor(`!document.getElementById('component-uaa-start').disabled && !document.getElementById('component-gateway-stop').disabled`);
  await evaluate(`document.getElementById('component-uaa-start').click();`);
  await waitFor(`!document.getElementById('component-uaa-stop').disabled`);
  await evaluate(`document.getElementById('component-uaa-stop').click();document.getElementById('component-gateway-stop').click();`);
  await waitFor(`!document.getElementById('component-uaa-start').disabled && !document.getElementById('component-gateway-start').disabled`);
  await evaluate(`document.getElementById('component-gateway-start').click();`);
  await waitFor(`!document.getElementById('component-gateway-stop').disabled`);
  assert.deepEqual(componentActions,['uaa/start','uaa/stop','gateway/stop','gateway/start']);
  await evaluate(`document.getElementById('component-gateway-logs').click();`);
  await waitFor(`document.getElementById('component-gateway-log').textContent==='Component log'`);
  await evaluate(`location.hash='blog';navigate();`);
  await waitFor(`!document.getElementById('blog-stop').disabled`);
  await waitFor(`!document.getElementById('blog-sync').disabled`);
  await evaluate(`document.getElementById('blog-sync').click();`);
  await waitFor(`!document.getElementById('blog-sync').disabled`);
  assert.equal(knowledgeSyncs,1);
  assert.ok(await evaluate(`!document.getElementById('blog-settings').open && !document.getElementById('blog-preview').hidden`));
  await evaluate(`document.getElementById('blog-view-knowledge').click();`);
  assert.equal(await evaluate(`document.getElementById('blog-preview').src`),'http://localhost:4321/knowledge');
  await evaluate(`document.getElementById('blog-view-home').click();document.getElementById('blog-settings').open=true;`);
  await waitFor(`!document.getElementById('blog-backend-start').disabled`);
  await evaluate(`document.getElementById('blog-backend-start').click();`);
  await waitFor(`document.getElementById('blog-backend-state').textContent==='运行中'`);
  assert.equal(backendStarts,1);
  assert.ok(await evaluate(`!document.getElementById('blog-knowledge-open').hidden && document.getElementById('blog-knowledge-open').href==='http://localhost:4321/knowledge'`));
  assert.ok(await evaluate(`!document.getElementById('blog').hidden && !document.getElementById('blog-open').hidden && document.getElementById('blog-open').href==='http://localhost:4321/'`));
  await evaluate(`document.getElementById('blog-stop').click();`);
  await waitFor(`!document.getElementById('blog-start').disabled`);
  assert.ok(await evaluate(`document.getElementById('blog-open').hidden`));
  assert.ok(await evaluate(`document.getElementById('blog-knowledge-open').hidden`));
  assert.ok(await evaluate(`document.getElementById('blog-preview').hidden`));
  assert.equal(backendStops,1);
  await evaluate(`document.getElementById('blog-start').click();`);
  await waitFor(`!document.getElementById('blog-stop').disabled`);
  assert.equal(blogStops,1);assert.equal(blogStarts,1);
  assert.equal(backendStarts,2);
  await evaluate(`document.getElementById('blog-logs').click();`);
  await waitFor(`document.getElementById('blog-process-log').textContent==='Blog startup log'`);
  await evaluate(`location.hash='lab';navigate();`);
  await waitFor(`!document.getElementById('lab-start').disabled`);
  assert.ok(await evaluate(`document.getElementById('lab-stop').disabled`));
  await evaluate(`document.getElementById('lab-start').click();`);
  await waitFor(`document.getElementById('lab-process-state').textContent==='运行中'`);
  assert.equal(labStarts,1);
  assert.ok(await evaluate(`document.getElementById('lab-start').disabled && !document.getElementById('lab-stop').disabled`));
  await evaluate(`document.getElementById('lab-logs').click();`);
  await waitFor(`document.getElementById('lab-process-log').textContent==='Lab startup test log'`);
  await evaluate(`document.getElementById('lab-stop').click();`);
  await waitFor(`document.getElementById('lab-process-state').textContent==='已停止'`);
  assert.equal(labStops,1);
  assert.ok(await evaluate(`!document.getElementById('lab-start').disabled && document.getElementById('lab-stop').disabled`));
  assert.equal(await evaluate('presets.length'),catalog.length+lessons.length);
  assert.equal(await evaluate('Number(document.getElementById("lab-total-count").textContent)'),catalog.length+lessons.length);
  assert.equal(await evaluate('Number(document.getElementById("lab-api-count").textContent)'),catalog.length);
  assert.equal(await evaluate('Number(document.getElementById("lab-learning-count").textContent)'),lessons.length);
  assert.ok(await evaluate('!document.getElementById("lab-kind") && !document.querySelector("[data-lab-kind]")'));
  assert.equal(await evaluate('document.querySelectorAll("#lab-presets [data-preset]").length'),catalog.length+lessons.length);
  await evaluate(`location.hash='lab';navigate();`);
  await waitFor(`state.page==='lab' && !document.getElementById('lab').hidden`);
  await evaluate(`document.querySelector('.lab-search-wrap>span').click();`);
  assert.equal(await evaluate('document.activeElement.id'),'lab-search','clicking the search icon must focus the input');
  await evaluate(`document.getElementById('lab-search').blur();document.querySelector('.lab-search-wrap').click();`);
  assert.equal(await evaluate('document.activeElement.id'),'lab-search','clicking the search container must focus the input');
  await evaluate(`document.getElementById('lab-search').value='Redis 写入';document.getElementById('lab-search').dispatchEvent(new Event('input',{bubbles:true}));`);
  assert.equal(await evaluate('document.activeElement.id'),'lab-search','filtering must preserve input focus');
  await evaluate(`window.__selectedBeforeComposition=selected;document.getElementById('lab-search').dispatchEvent(new CompositionEvent('compositionstart',{bubbles:true}));document.getElementById('lab-search').dispatchEvent(new KeyboardEvent('keydown',{key:'Enter',isComposing:true,bubbles:true}));`);
  assert.ok(await evaluate(`selected===window.__selectedBeforeComposition && document.activeElement.id==='lab-search'`),'IME confirmation must not select an experiment');
  await evaluate(`document.getElementById('lab-search').dispatchEvent(new CompositionEvent('compositionend',{bubbles:true}));`);
  assert.ok(await evaluate(`document.querySelectorAll('#lab-presets [data-preset]').length>0 && Array.from(document.querySelectorAll('#lab-quick-results .quick-select')).every(el=>el.textContent.includes('写入'))`));
  await evaluate(`document.querySelector('#lab-quick-results [data-favorite]').click();document.getElementById('lab-reset-filters').click();document.querySelector('[data-lab-scope="favorites"]').click();`);
  assert.equal(await evaluate(`document.querySelectorAll('#lab-quick-results .quick-select').length`),1);
  assert.equal(await evaluate(`JSON.parse(localStorage.getItem('kuma-lab-favorites')).length`),1);
  await evaluate(`document.querySelector('#lab-quick-results .quick-select').click();document.getElementById('lab-reset-filters').click();document.querySelector('[data-lab-scope="recent"]').click();`);
  assert.ok(await evaluate(`document.querySelectorAll('#lab-quick-results .quick-select').length>0`));
  await evaluate(`document.getElementById('lab-reset-filters').click();document.getElementById('lab-group-filter').value=presets[0].group;document.getElementById('lab-group-filter').dispatchEvent(new Event('change'));`);
  assert.ok(await evaluate(`document.querySelectorAll('#lab-presets details').length===1`));
  await evaluate(`document.getElementById('lab-reset-filters').click();document.querySelector('[data-lab-scope="learning"]').click();`);
  assert.equal(await evaluate('document.querySelectorAll("#lab-presets [data-preset]").length'),lessons.length);
  await evaluate(`document.querySelector('[data-lab-scope="api"]').click();`);
  assert.equal(await evaluate('document.querySelectorAll("#lab-presets [data-preset]").length'),catalog.length);
  await evaluate(`document.getElementById('lab-reset-filters').click();location.hash='overview';navigate();document.dispatchEvent(new KeyboardEvent('keydown',{key:'k',ctrlKey:true,bubbles:true}));`);
  assert.ok(await evaluate(`state.page==='overview' && !document.querySelector('.finder-heading')`));
  await evaluate(`location.hash='lab';navigate();document.activeElement.blur();document.dispatchEvent(new KeyboardEvent('keydown',{key:'/',bubbles:true}));`);
  assert.ok(await evaluate(`document.activeElement.id!=='lab-search'`));
  await evaluate(`document.getElementById('lab-search').value='不存在的实验-xyz';renderPresets();`);
  assert.ok(await evaluate(`document.getElementById('lab-quick-results').textContent.includes('没有找到')`));
  await evaluate(`document.getElementById('lab-reset-filters').click();document.getElementById('lab-logs').click();window.scrollTo(0,0);`);
  await paint();
  await fs.writeFile(path.join(output,'sakura-lab-dark.png'),(await window.webContents.capturePage()).toPNG());
  await evaluate(`setTheme('light');`);await paint();
  await fs.writeFile(path.join(output,'sakura-lab-light.png'),(await window.webContents.capturePage()).toPNG());
  await evaluate(`setTheme('dark');`);
  await evaluate(`document.querySelector('#lab-presets details').open=false;`);
  await paint();
  await evaluate('renderPresets();');
  assert.ok(await evaluate('!document.querySelector("#lab-presets details").open'));
  await check('elasticsearch-all');
  assert.ok(await evaluate('document.querySelectorAll("#lab-learning-guide table").length>=2 && document.querySelectorAll("#lab-learning-guide pre code").length>=1'));
  await paint();
  await fs.writeFile(path.join(output,'elasticsearch-dark.png'),(await window.webContents.capturePage()).toPNG());
  await evaluate(`setTheme('light');`);
  await paint();
  await fs.writeFile(path.join(output,'elasticsearch-light.png'),(await window.webContents.capturePage()).toPNG());
  await evaluate(`setTheme('dark');`);
  for(const id of ['memory','linux-memory','webhook','socket','jdk-8','jdk-21','jdk-25','jdk25-preview'])await check(id);
  for(const id of ['socket-all','socket-lifecycle','session-all','session-isolation','session-lifecycle','session-expiry']){
    await check(id);
    assert.ok(await evaluate('document.querySelectorAll("#lab-learning-guide table").length>=2'));
  }
  for(const component of ['loki','prometheus','alertmanager','otel','skywalking','grafana']){
    await check('middleware-'+component);
    assert.ok(await evaluate(`!document.getElementById('lab-learning-links').hidden`));
    assert.ok(await evaluate(`document.querySelectorAll('#lab-learning-links a').length>=1`));
    assert.ok(await evaluate(`Array.from(document.querySelectorAll('#lab-learning-links a')).every(link=>link.target==='_blank' && link.rel.includes('noopener') && link.href.startsWith('http://localhost:'))`));
  }
  await paint();
  await fs.writeFile(path.join(output,'monitoring-grafana.png'),(await window.webContents.capturePage()).toPNG());
  await evaluate(`document.getElementById('lab-search').value='ScopedValue';renderPresets();`);
  assert.equal(await evaluate('document.querySelectorAll("#lab-presets [data-preset]").length'),1);
  assert.ok(await evaluate('document.getElementById("lab-presets").textContent.includes("JDK 25")'));
  await evaluate(`document.getElementById('lab-search').value='';renderPresets();document.getElementById('discover-lab').click();`);
  await waitFor(`!document.getElementById('discover-lab').disabled`);
  assert.ok(await evaluate(`presets.some(p=>p.name==='更新后的状态名称') && presets.filter(p=>p.kind==='learning').length===${lessons.length}`));
  assert.equal(await evaluate(`document.getElementById('lab-discovery-status').dataset.state`),'success');
  discoveryInvalid=true;
  await evaluate(`document.getElementById('discover-lab').click();`);
  await waitFor(`!document.getElementById('discover-lab').disabled`);
  assert.ok(await evaluate(`document.getElementById('lab-discovery-status').dataset.state==='error' && document.getElementById('lab-discovery-status').textContent.includes('paths')`));
  assert.equal(await evaluate('presets.length'),catalog.length+lessons.length);
  discoveryInvalid=false;
  await check('jdk-25');
  await paint();
  await fs.writeFile(path.join(output,'jdk25-dark.png'),(await window.webContents.capturePage()).toPNG());
  await evaluate(`setTheme('light');`);
  await paint();
  await fs.writeFile(path.join(output,'jdk25-light.png'),(await window.webContents.capturePage()).toPNG());
  window.setSize(480,900);
  await waitFor('window.innerWidth<=480');
  assert.ok(await evaluate(`document.documentElement.dataset.sidebar==='collapsed'`));
  await evaluate(`document.getElementById('workspace-toggle').click();`);
  assert.ok(await evaluate(`document.documentElement.dataset.sidebar==='expanded' && !document.getElementById('sidebar-backdrop').hidden && document.querySelector('.sidebar').getBoundingClientRect().width===220`));
  await evaluate(`document.getElementById('sidebar-backdrop').click();`);
  assert.ok(await evaluate(`document.documentElement.dataset.sidebar==='collapsed' && document.getElementById('sidebar-backdrop').hidden`));
  await paint();
  assert.ok(await evaluate('document.documentElement.scrollWidth<=window.innerWidth'));
  assert.ok(await evaluate('document.querySelector(".lab-catalog").classList.contains("compact") && document.getElementById("toggle-lab-catalog").getAttribute("aria-expanded")==="false"'));
  await evaluate('document.getElementById("toggle-lab-catalog").click();');
  assert.ok(await evaluate('!document.querySelector(".lab-catalog").classList.contains("compact")'));
  await evaluate('document.getElementById("toggle-lab-catalog").click();');
  await fs.writeFile(path.join(output,'jdk25-mobile.png'),(await window.webContents.capturePage()).toPNG());
  await evaluate(`renderPresets();selectPreset(presets.findIndex(p=>p.path==='/lab/javacore/socket/send'));`);
  assert.ok(await evaluate(`!document.getElementById('lab-api-workspace').hidden && document.getElementById('lab-learning-workspace').hidden && document.getElementById('lab-method').value==='POST'`));
  assert.ok(await evaluate('document.documentElement.scrollWidth<=window.innerWidth'));
  window.setSize(1440,1000);
  await evaluate(`renderPresets();selectPreset(presets.findIndex(p=>p.path==='/lab/elasticsearch/scenario'));`);
  await paint();
  await fs.writeFile(path.join(output,'elasticsearch-api.png'),(await window.webContents.capturePage()).toPNG());
  assert.equal(requests,2); // explicit discovery requests only; lesson selection must not execute experiments
  for(const page of ['overview','server','cluster','projects','components','lab','blog','settings']){
    await evaluate(`location.hash=${JSON.stringify(page)};navigate();setTheme('dark');window.scrollTo(0,0);`);
    await waitFor(`Array.from(document.querySelectorAll('.page:not([hidden]) .workspace-hero-art')).every(img=>img.complete&&img.naturalWidth>0)`);
    await paint();
    await evaluate('window.scrollTo(0,0)');
    assert.ok(await evaluate(`document.documentElement.scrollWidth<=window.innerWidth`),page+' desktop overflow');
    await fs.writeFile(path.join(output,'yuna-'+page+'-dark.png'),(await window.webContents.capturePage()).toPNG());
  }
  await evaluate(`location.hash='lab';navigate();setTheme('light');`);await paint();
  await fs.writeFile(path.join(output,'yuna-lab-light.png'),(await window.webContents.capturePage()).toPNG());
  window.setSize(480,900);await waitFor('window.innerWidth<=480');
  for(const page of ['overview','server','cluster','projects','components','lab','blog','settings']){
    await evaluate(`location.hash=${JSON.stringify(page)};navigate();`);await paint();
    assert.ok(await evaluate('document.documentElement.scrollWidth<=window.innerWidth'),page+' mobile overflow');
  }
  await evaluate(`location.hash='lab';navigate();`);await paint();
  await fs.writeFile(path.join(output,'yuna-lab-mobile.png'),(await window.webContents.capturePage()).toPNG());
  window.setSize(1440,1000);
  await waitFor('window.innerWidth>850');
  await evaluate(`if(document.documentElement.dataset.sidebar!=='collapsed')document.getElementById('workspace-toggle').click();`);
  await new Promise(resolve=>{window.webContents.once('did-finish-load',resolve);window.reload();});
  await waitFor(`window.LabExplorer && presets.length===${catalog.length+lessons.length}`);
  assert.ok(await evaluate(`document.documentElement.dataset.sidebar==='collapsed' && !document.getElementById('workspace-navigation').hidden && document.querySelector('.sidebar').getBoundingClientRect().width===68`));
  assert.equal(await evaluate(`document.getElementById('console-welcome').checked`),false);
  await evaluate(`document.querySelector('[data-lab-scope="favorites"]').click();`);
  assert.equal(await evaluate(`document.querySelectorAll('#lab-quick-results .favorite-toggle[aria-pressed="true"]').length`),1);
  await fs.writeFile(path.join(output,'result.json'),JSON.stringify({passed:true,interfaces:catalog.length,lessons:lessons.length,middlewareUiLinks:true,discovery:true,narrowLayout:true,workspaceImages:7,workspacePages:5,mergedInfrastructure:true,mergedServices:true,consoleSettings:true},null,2));
  console.log(`Lab UI smoke passed: ${catalog.length} interfaces, ${lessons.length} lessons, middleware UI links, discovery, docs, mode switching and narrow layout.`);
  window.destroy();server.close();app.exit(0);
}).catch(error=>{console.error(error);window?.destroy();server?.close();app.exit(1);});
