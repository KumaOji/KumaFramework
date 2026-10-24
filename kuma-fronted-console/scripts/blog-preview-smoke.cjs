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
    if(req.url==='/blog.js'){
      res.setHeader('Content-Type','text/javascript');return res.end(fs.readFileSync(path.join(renderer,'blog.js')));
    }
    res.setHeader('Content-Type','text/html; charset=utf-8');
    if(req.url==='/preview'){homeRequests++;return res.end('<h1>Blog home</h1>');}
    if(req.url==='/knowledge'){knowledgeRequests++;return res.end('<h1>Knowledge</h1>');}
    if(req.url!=='/console'){res.writeHead(404);return res.end();}
    const scaffold=`<script>
      const $=id=>document.getElementById(id);
      const state={page:'overview',servicesTab:'projects'};
      const frontend={state:'RUNNING',canOpen:true,url:${JSON.stringify(base.replace('127.0.0.1','localhost')+'/preview')},knowledgeUrl:${JSON.stringify(base.replace('127.0.0.1','localhost')+'/knowledge')}};
      async function api(url){return url==='/api/blog/process'?frontend:{state:'STOPPED'};}
      function notice(){}
      const LogConsole={bind:()=>()=>{}};
    </script><script src='/blog.js' defer></script>`;
    const html=fs.readFileSync(path.join(renderer,'index.html'),'utf8').replace(/<script\b[^>]*>[\s\S]*?<\/script>/g,'');
    res.end(html.replace('</head>',scaffold+'</head>'));
  });
  await new Promise(resolve=>server.listen(0,'127.0.0.1',resolve));
  base=`http://127.0.0.1:${server.address().port}`;
  window=new BrowserWindow({show:false,webPreferences:{sandbox:true,contextIsolation:true,backgroundThrottling:false}});
  const js=source=>window.webContents.executeJavaScript(source);
  const preview=()=>window.webContents.mainFrame.frames.find(frame=>frame.name==='kuma-blog-preview');
  await window.loadURL(base+'/console');
  assert.equal(await js("document.getElementById('blog-preview').hasAttribute('src')"),false,'Hidden tab loaded Blog prematurely');
  assert.equal(homeRequests,0);
  await js("state.page='projects';state.servicesTab='blog';document.getElementById('projects').hidden=false;document.getElementById('blog').hidden=false;window.dispatchEvent(new Event('hashchange'))");
  await until(async()=>preview()&&await preview().executeJavaScript('document.querySelector("h1")?.textContent')==='Blog home','Blog did not render on opening tab');
  const loaded=homeRequests;
  await js('renderBlogPreview();renderBlogPreview()');
  await pause(100);
  assert.equal(homeRequests,loaded,'Status polling reloaded a working preview');
  await js("document.getElementById('blog-preview-reload').click()");
  await until(()=>homeRequests>loaded,'Refresh did not reload the same URL');
  await js("document.getElementById('blog-view-knowledge').click()");
  await until(async()=>knowledgeRequests>0&&await preview().executeJavaScript('document.querySelector("h1")?.textContent')==='Knowledge','Knowledge did not render');
  await js('frontend.canOpen=false;renderBlogProcess(frontend)');
  assert.equal(await js("document.getElementById('blog-preview').hasAttribute('src')"),false);
  const previous=knowledgeRequests;
  await js('frontend.canOpen=true;renderBlogProcess(frontend)');
  await until(()=>knowledgeRequests>previous,'Preview did not recover after service became ready again');
  console.log('PASS: no hidden preload; Blog and Knowledge render without an external browser; polling preserves navigation; same-URL refresh and service recovery work');
  window.destroy();server.close();app.quit();
}).catch(error=>{console.error(error);window?.destroy();server?.close();app.exit(1);});
setTimeout(()=>{console.error('Blog preview smoke timed out');app.exit(1);},25000).unref();
