// Isolated mock dsh; no model API calls or existing user data.
const {app,BrowserWindow}=require('electron');
const http=require('node:http'),fs=require('node:fs/promises'),path=require('node:path'),assert=require('node:assert/strict');
const renderer=path.resolve(__dirname,'../renderer'),output=path.resolve(__dirname,'../build/dsh-ui-smoke');
app.setPath('userData',path.join(output,'profile-'+Date.now()));app.commandLine.appendSwitch('disable-gpu');
let win,server,running=false,authRequired=false,starts=0,stops=0,lifeReads=0;
async function serve(req,res){
  const url=new URL(req.url,'http://localhost');
  if(url.pathname==='/mock-dsh'){
    if(url.searchParams.get('token')==='mock-token'){res.writeHead(303,{'Set-Cookie':'dsh-session=fixture; HttpOnly; SameSite=Strict; Path=/','Location':'/mock-dsh'});res.end();return;}
    if(!req.headers.cookie?.includes('dsh-session=fixture')){res.writeHead(401);res.end('dsh web authentication required');return;}
    res.writeHead(200,{'Content-Type':'text/html'});res.end('<h1>DeepSeek Harness</h1>');return;
  }
  if(url.pathname.startsWith('/api/')){
    if(url.pathname.startsWith('/api/life'))lifeReads++;
    let data={ready:false};
    if(url.pathname.startsWith('/api/ai/')){
      if(url.pathname.endsWith('/start')){assert.equal(req.method,'POST');running=true;starts++;}
      if(url.pathname.endsWith('/stop')){assert.equal(req.method,'POST');running=false;stops++;}
      data=url.pathname.endsWith('/logs')?{body:'dsh startup log'}:{state:running?(authRequired?'AUTH_REQUIRED':'RUNNING'):'STOPPED',message:'dsh status',directory:'D:/Vscode_project/deepseek-harness',canStart:!running,canStop:running,canOpen:running,url:'http://127.0.0.1:'+server.address().port+'/mock-dsh'+(authRequired?'':'?token=mock-token')};
    }
    if(url.pathname==='/api/projects/catalog')data={projects:[]};
    if(url.pathname==='/api/projects/running')data=[];
    res.writeHead(200,{'Content-Type':'application/json'});res.end(JSON.stringify(data));return;
  }
  try{const target=path.resolve(renderer,'.'+(url.pathname==='/'?'/index.html':url.pathname));if(!target.startsWith(renderer+path.sep))throw Error();
    const body=await fs.readFile(target),ext=path.extname(target);res.writeHead(200,{'Content-Type':({'.html':'text/html','.js':'text/javascript','.css':'text/css','.png':'image/png'})[ext]||'application/octet-stream'});res.end(body);
  }catch{res.writeHead(404);res.end();}
}
app.whenReady().then(async()=>{
  await fs.mkdir(output,{recursive:true});server=http.createServer((req,res)=>serve(req,res).catch(error=>{console.error(error);res.end();}));await new Promise(resolve=>server.listen(0,'127.0.0.1',resolve));
  win=new BrowserWindow({show:false,width:1500,height:1050,webPreferences:{contextIsolation:true,nodeIntegration:false,backgroundThrottling:false}});
  const evaluate=code=>win.webContents.executeJavaScript(code,true);
  async function wait(code){for(let i=0;i<150;i++){if(await evaluate(code))return;await new Promise(resolve=>setTimeout(resolve,50));}throw Error('Timed out: '+code+'; starts='+starts+'; '+await evaluate(`document.getElementById('project-services').textContent`));}
  await win.loadURL('http://127.0.0.1:'+server.address().port);
  await evaluate(`localStorage.setItem('kuma-console-welcome','false');location.hash='projects';navigate();`);
  await wait(`!document.getElementById('dsh-start').disabled`);
  assert.deepEqual(await evaluate(`Array.from(document.querySelectorAll('[data-managed-service]'),card=>card.dataset.managedService).sort()`),['blog','dsh','gateway','lab','uaa']);
  assert.ok(await evaluate(`!document.querySelector('script[src="/life.js"]') && !document.querySelector('[data-service-open=life]')`));
  assert.ok(await evaluate(`!document.getElementById('ai-composer')&&!document.getElementById('ai-api-key')`));
  assert.equal(await evaluate(`document.getElementById('dsh-directory').textContent`),'D:/Vscode_project/deepseek-harness');
  await evaluate(`document.getElementById('kuma-intro')?.close();void 0`);
  await new Promise(resolve=>setTimeout(resolve,350));
  await evaluate(`document.querySelector('[data-service-open=dsh]').click()`);
  for(const [width,height,zoom] of [[1500,1050,1],[1500,1050,1.25],[1060,720,1.5],[480,900,1],[480,900,1.25],[480,900,2]]){
    win.setSize(width,height);win.webContents.setZoomFactor(zoom);
    await new Promise(resolve=>setTimeout(resolve,200));
    const bounds=await evaluate(`(()=>{const dialog=document.getElementById('service-dialog'),r=dialog.getBoundingClientRect(),button=document.getElementById('dsh-start').getBoundingClientRect();return {inside:r.left>=0&&r.top>=0&&r.right<=innerWidth&&r.bottom<=innerHeight,centered:Math.abs(r.left+r.width/2-document.documentElement.clientWidth/2)<2&&Math.abs(r.top+r.height/2-innerHeight/2)<2,noOverflow:dialog.scrollWidth<=dialog.clientWidth,buttonInset:button.left-r.left>=16&&button.right<r.right};})()`);
    if(!Object.values(bounds).every(Boolean)){
      console.log(await evaluate(`(()=>{const d=document.getElementById('service-dialog'),s=getComputedStyle(d),b=document.getElementById('dsh-start');return {open:d.open,service:d.dataset.service,dialog:d.getBoundingClientRect().toJSON(),button:b.getBoundingClientRect().toJSON(),innerWidth,innerHeight,style:{margin:s.margin,position:s.position,inset:s.inset,width:s.width},parent:d.parentElement.className};})()`));
      await fs.writeFile(path.join(output,'dialog-failure.png'),(await win.webContents.capturePage()).toPNG());
    }
    assert.ok(Object.values(bounds).every(Boolean),'Dialog bounds '+JSON.stringify({width,height,zoom,...bounds}));
  }
  win.webContents.setZoomFactor(1);win.setSize(1500,1050);
  await new Promise(resolve=>setTimeout(resolve,150));
  await fs.writeFile(path.join(output,'dsh-dialog.png'),(await win.webContents.capturePage()).toPNG());
  await evaluate(`document.getElementById('dsh-start').click()`);
  await wait(`!document.getElementById('dsh-open').disabled`);
  await evaluate(`document.getElementById('dsh-open').click()`);
  await wait(`state.servicesTab==='panels' && !document.getElementById('public-panel-preview').hidden`);
  assert.equal(starts,1);
  assert.ok(await evaluate(`document.getElementById('public-panel-open').href.includes('mock-dsh?token=mock-token')`));
  await wait(`document.getElementById('public-panel-preview').contentDocument?.body.textContent.includes('DeepSeek Harness')`);
  authRequired=true;
  await evaluate(`location.hash='projects';navigate();document.getElementById('dsh-refresh').click()`);
  await wait(`document.getElementById('public-panel-open').href.endsWith('/mock-dsh')`);
  assert.equal(await evaluate(`document.getElementById('dsh-state').textContent`),'运行中');
  assert.ok(await evaluate(`!document.getElementById('dsh-open').disabled`),'A backend without browser cookies cannot disable an authenticated client');
  await evaluate(`PublicPanels.openService('dsh')`);
  await wait(`state.servicesTab==='panels'&&!document.getElementById('public-panel-preview').hidden&&document.getElementById('public-panel-preview').contentDocument?.body.textContent.includes('DeepSeek Harness')`);
  await evaluate(`document.getElementById('kuma-intro')?.close();void 0`);
  assert.ok(await evaluate(`!document.getElementById('public-panel-manager').open && !document.getElementById('public-browser-address').value.includes('token=')`),'Browser chrome stays compact and never exposes the runtime token in its address field');
  await new Promise(resolve=>setTimeout(resolve,150));
  win.setSize(480,900);await wait(`innerWidth<=480`);
  assert.ok(await evaluate(`document.documentElement.scrollWidth<=innerWidth`),'bookmark browser mobile overflow');
  await new Promise(resolve=>setTimeout(resolve,150));
  await fs.writeFile(path.join(output,'bookmark-browser-mobile.png'),(await win.webContents.capturePage()).toPNG());
  win.setSize(1500,1050);await wait(`innerWidth>480`);
  await new Promise(resolve=>setTimeout(resolve,150));
  await fs.writeFile(path.join(output,'bookmark-browser.png'),(await win.webContents.capturePage()).toPNG());
  assert.ok(await evaluate(`!document.querySelector('[data-page=ai]') && document.querySelector('[data-managed-service=dsh]').parentElement===document.querySelector('[data-managed-service=uaa]').parentElement`));
  assert.ok(await evaluate(`!localStorage.getItem('kuma-public-panels-v1').includes('mock-token')`),'Runtime token must not be persisted');
  await evaluate(`location.hash='projects';navigate();document.querySelector('[data-service-open=dsh]').click();document.getElementById('dsh-log-details').open=true`);await wait(`document.getElementById('dsh-logs').textContent==='dsh startup log'`);
  win.setSize(480,900);await wait(`window.innerWidth<=480`);
  assert.ok(await evaluate(`document.documentElement.scrollWidth<=innerWidth`),'mobile overflow');
  await evaluate(`document.getElementById('dsh-stop').click()`);await wait(`document.getElementById('dsh-open').disabled`);assert.equal(stops,1);
  assert.ok(await evaluate(`!document.getElementById('public-panel-preview').hasAttribute('src')&&document.getElementById('dsh-open').disabled`));
  assert.equal(lifeReads,0,'Local project services must not poll WSL Life');
  console.log('DSH UI smoke passed: peer service card, start/stop, centralized authenticated panel, no persisted token, logs, mobile.');
  win.destroy();server.close();app.exit(0);
}).catch(error=>{console.error(error);win?.destroy();server?.close();app.exit(1);});
