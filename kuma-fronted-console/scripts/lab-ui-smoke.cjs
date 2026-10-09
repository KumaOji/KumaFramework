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
async function serve(request,response){
  const url=new URL(request.url,'http://localhost');
  if(url.pathname.startsWith('/api/')){
    let result={ready:false};
    if(url.pathname==='/api/projects/catalog')result={projects:[]};
    if(url.pathname==='/api/projects/running')result=[];
    if(url.pathname==='/api/lab/request'){
      let text='';for await(const chunk of request)text+=chunk;
      const body=JSON.parse(text);
      assert.equal(body.path,'/v3/api-docs');requests++;
      result={status:200,body:JSON.stringify({paths:{'/lab/kafka/status':{get:{tags:['Kafka 已同步'],summary:'更新后的状态名称'}}}})};
    }
    response.writeHead(200,{'Content-Type':'application/json'});response.end(JSON.stringify(result));return;
  }
  const target=path.resolve(renderer,'.'+(url.pathname==='/'?'/index.html':url.pathname));
  if(!target.startsWith(renderer+path.sep)){response.writeHead(403);response.end();return;}
  const content=await fs.readFile(target);
  const types={'.html':'text/html','.js':'text/javascript','.css':'text/css','.json':'application/json','.md':'text/plain'};
  response.writeHead(200,{'Content-Type':(types[path.extname(target)]||'application/octet-stream')+'; charset=utf-8'});
  response.end(content);
}
const evaluate=script=>window.webContents.executeJavaScript(script);
const paint=()=>evaluate('new Promise(resolve=>requestAnimationFrame(()=>requestAnimationFrame(resolve)))');
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
  await window.loadURL(`http://127.0.0.1:${server.address().port}/#lab`);
  await waitFor(`typeof presets!=='undefined' && presets.filter(p=>p.kind==='learning').length===${lessons.length}`);
  await evaluate(`location.hash='lab';navigate();setTheme('dark');`);
  assert.ok(await evaluate(`!document.getElementById('lab').hidden`));
  assert.equal(await evaluate('presets.length'),catalog.length+lessons.length);
  for(const id of ['memory','linux-memory','webhook','socket','jdk-8','jdk-21','jdk-25','jdk25-preview'])await check(id);
  for(const component of ['loki','prometheus','alertmanager','otel','skywalking','grafana']){
    await check('middleware-'+component);
    assert.ok(await evaluate(`!document.getElementById('lab-learning-links').hidden`));
    assert.ok(await evaluate(`document.querySelectorAll('#lab-learning-links a').length>=1`));
    assert.ok(await evaluate(`Array.from(document.querySelectorAll('#lab-learning-links a')).every(link=>link.target==='_blank' && link.rel.includes('noopener') && link.href.startsWith('http://localhost:'))`));
  }
  await paint();
  await fs.writeFile(path.join(output,'monitoring-grafana.png'),(await window.webContents.capturePage()).toPNG());
  await evaluate(`document.getElementById('lab-kind').value='learning';document.getElementById('lab-search').value='ScopedValue';renderPresets();`);
  assert.equal(await evaluate('document.querySelectorAll("#lab-presets [data-preset]").length'),1);
  assert.ok(await evaluate('document.getElementById("lab-presets").textContent.includes("JDK 25")'));
  await evaluate(`document.getElementById('lab-search').value='';renderPresets();document.getElementById('discover-lab').click();`);
  await waitFor(`!document.getElementById('discover-lab').disabled`);
  assert.ok(await evaluate(`presets.some(p=>p.name==='更新后的状态名称') && presets.filter(p=>p.kind==='learning').length===${lessons.length}`));
  await check('jdk-25');
  await paint();
  await fs.writeFile(path.join(output,'jdk25-dark.png'),(await window.webContents.capturePage()).toPNG());
  await evaluate(`setTheme('light');`);
  await paint();
  await fs.writeFile(path.join(output,'jdk25-light.png'),(await window.webContents.capturePage()).toPNG());
  window.setSize(480,900);
  await waitFor('window.innerWidth<=480');
  await paint();
  assert.ok(await evaluate('document.documentElement.scrollWidth<=window.innerWidth'));
  await fs.writeFile(path.join(output,'jdk25-mobile.png'),(await window.webContents.capturePage()).toPNG());
  await evaluate(`document.getElementById('lab-kind').value='api';renderPresets();selectPreset(presets.findIndex(p=>p.path==='/lab/javacore/socket/send'));`);
  assert.ok(await evaluate(`!document.getElementById('lab-api-workspace').hidden && document.getElementById('lab-learning-workspace').hidden && document.getElementById('lab-method').value==='POST'`));
  assert.equal(requests,1); // only the explicit discovery request; lesson selection must not execute experiments
  await fs.writeFile(path.join(output,'result.json'),JSON.stringify({passed:true,interfaces:catalog.length,lessons:lessons.length,middlewareUiLinks:true,discovery:true,narrowLayout:true},null,2));
  console.log(`Lab UI smoke passed: ${catalog.length} interfaces, ${lessons.length} lessons, middleware UI links, discovery, docs, mode switching and narrow layout.`);
  window.destroy();server.close();app.exit(0);
}).catch(error=>{console.error(error);window?.destroy();server?.close();app.exit(1);});
