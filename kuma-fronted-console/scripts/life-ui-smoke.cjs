// Mock WSL only: this test never scales real deployments or executes Life tasks.
const {app,BrowserWindow}=require('electron');
const http=require('node:http'),fs=require('node:fs/promises'),path=require('node:path'),assert=require('node:assert/strict');
app.commandLine.appendSwitch('disable-gpu');
app.setPath('userData',path.resolve(__dirname,'../build/life-ui-smoke/profile-'+Date.now()));
let win,server,fixture,running=true,starts=0,stops=0,detailReads=0;
const renderer=path.resolve(__dirname,'../renderer');
const listen=s=>new Promise(resolve=>s.listen(0,'127.0.0.1',resolve));
async function serve(req,res){
  const url=new URL(req.url,'http://localhost');
  if(url.pathname.startsWith('/api/')){
    let data={ready:false};
    if(url.pathname==='/api/life/details'){detailReads++;data={body:'qinglong rsshub redis PVC retained'};}
    else if(url.pathname.startsWith('/api/life')){
      if(url.pathname.endsWith('/start')){assert.equal(req.method,'POST');running=true;starts++;}
      if(url.pathname.endsWith('/stop')){assert.equal(req.method,'POST');running=false;stops++;}
      data={state:running?'RUNNING':'STOPPED',message:'life-deals',directory:'D:/Vscode_project/life',distribution:'Ubuntu-24.04',namespace:'life-deals',canStart:!running,canStop:running,error:'',qinglongUrl:'http://127.0.0.1:'+fixture.address().port+'/qinglong',rsshubUrl:'http://127.0.0.1:'+fixture.address().port+'/rsshub',deployments:['qinglong','rsshub','redis'].map(name=>({name,ready:running?1:0,replicas:running?1:0}))};
    }
    if(url.pathname==='/api/projects/catalog')data={projects:[]};
    if(url.pathname==='/api/projects/running')data=[];
    if(url.pathname==='/api/projects')data={projects:[]};
    res.writeHead(200,{'Content-Type':'application/json'});res.end(JSON.stringify(data));return;
  }
  try{const target=path.resolve(renderer,'.'+(url.pathname==='/'?'/index.html':url.pathname));if(!target.startsWith(renderer+path.sep))throw Error();const body=await fs.readFile(target);res.writeHead(200,{'Content-Type':({'.html':'text/html','.js':'text/javascript','.css':'text/css','.png':'image/png'})[path.extname(target)]||'application/octet-stream'});res.end(body);}
  catch{res.writeHead(404);res.end();}
}
app.whenReady().then(async()=>{
  fixture=http.createServer((req,res)=>{res.writeHead(200,{'Content-Type':'text/html'});res.end('<h1>Life panel</h1>');});await listen(fixture);
  server=http.createServer((req,res)=>serve(req,res).catch(error=>{console.error(error);res.end();}));await listen(server);
  win=new BrowserWindow({show:false,width:1440,height:1000,webPreferences:{backgroundThrottling:false,contextIsolation:true}});
  const js=code=>win.webContents.executeJavaScript(code,true);
  async function wait(code){for(let i=0;i<150;i++){if(await js(code))return;await new Promise(resolve=>setTimeout(resolve,50));}throw Error('Timed out: '+code);}
  await win.loadURL('http://127.0.0.1:'+server.address().port+'/#projects');
  await wait(`!document.getElementById('life-stop').disabled`);
  assert.equal(await js(`document.querySelector('[data-managed-service=life]').dataset.running`),'true');
  await js(`document.querySelector('[data-service-open=life]').click()`);
  assert.ok(await js(`document.getElementById('service-dialog').open && document.getElementById('service-dialog-body').contains(document.getElementById('life-start'))`));
  assert.equal(await js(`document.querySelectorAll('#life-deployments .chip').length`),3);
  await js(`document.getElementById('life-details').click()`);await wait(`document.getElementById('life-detail-output').textContent.includes('PVC retained')`);assert.equal(detailReads,1);
  await js(`document.getElementById('life-stop').click()`);await wait(`!document.getElementById('life-start').disabled`);assert.equal(stops,1);
  await js(`document.getElementById('life-start').click()`);await wait(`!document.getElementById('life-stop').disabled`);assert.equal(starts,1);
  await js(`document.getElementById('life-qinglong').click()`);await wait(`state.servicesTab==='panels' && !document.getElementById('public-panel-open').hidden`);
  assert.ok(await js(`document.getElementById('public-panel-open').href.endsWith('/qinglong') && document.getElementById('public-panel-preview').hidden && document.getElementById('public-panel-mode').value==='external' && !document.getElementById('service-dialog').open`));
  await js(`location.hash='projects';navigate();document.querySelector('[data-service-open=life]').click();document.getElementById('life-rsshub').click()`);
  await wait(`document.getElementById('public-panel-preview').src.endsWith('/rsshub')`);
  assert.equal(starts,1);assert.equal(stops,1);
  await js(`location.hash='projects';navigate();document.getElementById('project-status-filter').value='started';renderManagedServices();`);
  assert.ok(await js(`!document.querySelector('[data-managed-service=life]').hidden`));
  win.setSize(480,900);await wait(`innerWidth<=480`);
  assert.ok(await js(`document.documentElement.scrollWidth<=innerWidth`),'mobile overflow');
  console.log('Life UI passed: status, service dialog, replicas, start/stop, details, QingLong/RSSHub panel routing, active filter, mobile.');
  win.destroy();server.close();fixture.close();app.exit(0);
}).catch(error=>{console.error(error);win?.destroy();server?.close();fixture?.close();app.exit(1);});
setTimeout(()=>{console.error('Life UI timeout');app.exit(1);},30000).unref();
