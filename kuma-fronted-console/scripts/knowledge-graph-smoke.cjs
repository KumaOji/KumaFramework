const {app,BrowserWindow}=require('electron');
const http=require('node:http');
const fs=require('node:fs/promises');
const path=require('node:path');
const assert=require('node:assert/strict');
const root=path.resolve(__dirname,'../../..','kuma-blog-fronted/KumaBlog/public/knowledge');
app.commandLine.appendSwitch('use-angle','swiftshader');
app.commandLine.appendSwitch('enable-unsafe-swiftshader');
app.setPath('userData',path.resolve(__dirname,'../build/knowledge-graph-smoke/profile'));
let window,server;
let graphFixture;
const pause=ms=>new Promise(resolve=>setTimeout(resolve,ms));
async function serve(req,res){
  let pathname=decodeURIComponent(new URL(req.url,'http://localhost').pathname).replace(/^\/knowledge\/?/,'/');
  if(pathname.endsWith('/contentIndex.json')){res.setHeader('Content-Type','application/json');res.end(JSON.stringify({index:{title:'Graph gesture test',links:[],tags:[],content:'',slug:'index'}}));return;}
  if(pathname==='/lazy/graph.js'){res.setHeader('Content-Type','text/javascript');res.end(graphFixture);return;}
  if(pathname==='/'||pathname==='')pathname='/index.html';
  const target=path.resolve(root,'.'+pathname);
  if(!target.startsWith(root+path.sep)){res.writeHead(403);res.end();return;}
  try{const data=await fs.readFile(target);res.setHeader('Content-Type',({'.html':'text/html','.js':'text/javascript','.css':'text/css','.json':'application/json'})[path.extname(target)]||'application/octet-stream');res.end(data);}
  catch{res.writeHead(404);res.end();}
}
async function waitFor(script){const deadline=Date.now()+20000;while(!await window.webContents.executeJavaScript(script)){if(Date.now()>deadline)throw Error('Timeout: '+script);await pause(50);}}
async function nodePoint(){
  await window.webContents.executeJavaScript(`document.querySelector('.graph-container canvas').scrollIntoView({block:'center'});`);
  const point=await window.webContents.executeJavaScript(`(()=>{const r=document.querySelector('.graph-container canvas').getBoundingClientRect();window.__graphTest.hoverFirst();return {x:Math.round(r.x+r.width/2),y:Math.round(r.y+r.height/2)};})()`);
  window.webContents.sendInputEvent({type:'mouseMove',...point});await pause(70);
  await window.webContents.executeJavaScript(`window.__graphTest.hoverFirst();`);
  return point;
}
app.whenReady().then(async()=>{
  const scripts=path.resolve(root,'../../../KumaKnowledge/knowledge/components/scripts');
  // Make node selection deterministic; drag handlers and navigation are the real source code.
  const source=(await fs.readFile(path.join(scripts,'graph.inline.ts'),'utf8')).replace('let currentTransform = zoomIdentity','window.__graphTest = { hoverFirst: () => updateHoverInfo(nodeRenderData[0].simulationData.id) }; let currentTransform = zoomIdentity');
  const esbuild=require(path.resolve(root,'../../../KumaKnowledge/node_modules/esbuild'));
  graphFixture=(await esbuild.build({stdin:{contents:source,resolveDir:scripts,loader:'ts'},bundle:true,write:false,format:'iife',platform:'browser',logLevel:'silent'})).outputFiles[0].text;
  server=http.createServer((req,res)=>serve(req,res).catch(error=>{res.writeHead(500);res.end(error.message);}));
  await new Promise(resolve=>server.listen(0,'127.0.0.1',resolve));
  window=new BrowserWindow({show:false,width:1440,height:1000,webPreferences:{backgroundThrottling:false,contextIsolation:true,sandbox:true}});
  await window.loadURL(`http://127.0.0.1:${server.address().port}/knowledge/`);
  await waitFor(`!!document.querySelector('.graph-container canvas') && !!window.__graphTest`);await pause(700);
  await window.webContents.executeJavaScript(`window.__graphNavigations=0;window.spaNavigate=()=>{window.__graphNavigations++;};void 0;`);
  let point=await nodePoint();
  window.webContents.sendInputEvent({type:'mouseDown',button:'left',clickCount:1,...point});
  await pause(25);
  window.webContents.sendInputEvent({type:'mouseMove',button:'left',x:point.x+35,y:point.y+15});
  await pause(25);
  window.webContents.sendInputEvent({type:'mouseUp',button:'left',clickCount:1,x:point.x+35,y:point.y+15});
  await pause(70);
  assert.equal(await window.webContents.executeJavaScript('window.__graphNavigations'),0,'quick drag must not navigate');
  await pause(1200);point=await nodePoint();
  window.webContents.sendInputEvent({type:'mouseDown',button:'left',clickCount:1,...point});await pause(25);
  await window.webContents.executeJavaScript(`window.dispatchEvent(new MouseEvent('mousemove',{view:window,bubbles:true,buttons:1,clientX:${point.x+35},clientY:${point.y}}));void 0;`);await pause(25);
  await window.webContents.executeJavaScript(`window.dispatchEvent(new MouseEvent('mousemove',{view:window,bubbles:true,buttons:1,clientX:${point.x},clientY:${point.y}}));void 0;`);await pause(25);
  window.webContents.sendInputEvent({type:'mouseUp',button:'left',clickCount:1,...point});await pause(70);
  assert.equal(await window.webContents.executeJavaScript('window.__graphNavigations'),0,'drag returning to its origin must not navigate');
  await pause(1200);point=await nodePoint();
  window.webContents.sendInputEvent({type:'mouseDown',button:'left',clickCount:1,...point});await pause(25);
  window.webContents.sendInputEvent({type:'mouseUp',button:'left',clickCount:1,...point});await pause(70);
  assert.equal(await window.webContents.executeJavaScript('window.__graphNavigations'),1,'a real click must still navigate');
  console.log('Knowledge graph passed: quick drag, drag returning to origin, normal click.');
  window.destroy();server.close();app.exit(0);
}).catch(error=>{console.error(error);window?.destroy();server?.close();app.exit(1);});
