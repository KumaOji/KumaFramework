const {app,BrowserWindow}=require('electron');
const assert=require('node:assert/strict');
const http=require('node:http');
const fs=require('node:fs');
const path=require('node:path');
app.setPath('userData',path.resolve(__dirname,'../build/blog-preview-smoke/profile'));
app.commandLine.appendSwitch('disable-gpu');
let server,window,homeRequests=0,knowledgeRequests=0;
const pause=ms=>new Promise(resolve=>setTimeout(resolve,ms));
async function until(check,message){for(let i=0;i<150;i++){if(await check())return;await pause(50);}throw Error(message);}
app.whenReady().then(async()=>{
  let base;
  const renderer=path.resolve(__dirname,'../renderer');
  server=http.createServer((req,res)=>{
    const pathname=new URL(req.url,'http://localhost').pathname;
    if(pathname==='/public-panels.js'){res.setHeader('Content-Type','text/javascript');return res.end(fs.readFileSync(path.join(renderer,'public-panels.js')));}
    res.setHeader('Content-Type','text/html; charset=utf-8');
    if(pathname==='/preview'){homeRequests++;return res.end('<h1>Blog home</h1>');}
    if(pathname==='/knowledge'){knowledgeRequests++;return res.end('<h1>Knowledge</h1>');}
    if(pathname!=='/console'){res.writeHead(404);return res.end();}
    const panels=[{name:'Blog',url:base.replace('127.0.0.1','localhost')+'/preview'},{name:'知识仓库',url:base.replace('127.0.0.1','localhost')+'/knowledge'}];
    const scaffold=`<script>
      const $=id=>document.getElementById(id);
      const state={page:'overview',servicesTab:'projects'};
      localStorage.setItem('kuma-public-panels-v1',${JSON.stringify(JSON.stringify(panels))});
      localStorage.setItem('kuma-public-panels-catalog-version','3');
    </script><script src='/public-panels.js' defer></script>`;
    const html=fs.readFileSync(path.join(renderer,'index.html'),'utf8').replace(/<script\b[^>]*>[\s\S]*?<\/script>/g,'');
    res.end(html.replace('</head>',scaffold+'</head>'));
  });
  await new Promise(resolve=>server.listen(0,'127.0.0.1',resolve));
  base=`http://127.0.0.1:${server.address().port}`;
  window=new BrowserWindow({show:false,webPreferences:{sandbox:true,contextIsolation:true,backgroundThrottling:false}});
  const js=source=>window.webContents.executeJavaScript(source);
  const preview=()=>window.webContents.mainFrame.frames.find(frame=>frame.name==='kuma-public-panel-preview');
  await window.loadURL(base+'/console');
  assert.equal(homeRequests,0,'Hidden panel must not preload');
  await js(`state.page='projects';state.servicesTab='panels';$('projects').hidden=false;$('public-panels').hidden=false;PublicPanels.open(${JSON.stringify(base.replace('127.0.0.1','localhost')+'/preview')},'Blog')`);
  await until(async()=>preview()&&await preview().executeJavaScript('document.querySelector("h1")?.textContent')==='Blog home','Common preview did not render Blog');
  assert.ok(await js("!$('public-panel-name').disabled && !$('public-panel-url').disabled && !$('public-panel-mode').disabled && !$('blog-panel') && !$('blog-preview')"));
  assert.ok(await js("$('public-panel-status').hidden && !$('public-panel-status').textContent"));
  const loaded=homeRequests;
  await js("window.dispatchEvent(new Event('hashchange'));");await pause(100);
  assert.equal(homeRequests,loaded,'Common routing should preserve a working preview');
  await js("$('public-panel-reload').click()");
  await until(()=>homeRequests>loaded,'Common refresh did not reload');
  await js("$('public-panel-select').value='1';$('public-panel-select').dispatchEvent(new Event('change'))");
  await until(async()=>knowledgeRequests>0&&await preview().executeJavaScript('document.querySelector("h1")?.textContent')==='Knowledge','Common selector did not render knowledge');
  await js("$('public-panel-name').value='Custom knowledge';$('public-panel-form').requestSubmit()");
  assert.equal(await js("JSON.parse(localStorage.getItem('kuma-public-panels-v1'))[1].name"),'Custom knowledge');
  await js("$('public-panel-delete').click()");
  assert.equal(await js("JSON.parse(localStorage.getItem('kuma-public-panels-v1')).length"),1);
  console.log('PASS: Blog and knowledge use editable ordinary panels, shared iframe/refresh, no lifecycle gating or automatic load messages');
  window.destroy();server.close();app.quit();
}).catch(error=>{console.error(error);window?.destroy();server?.close();app.exit(1);});
setTimeout(()=>app.exit(1),25000).unref();
