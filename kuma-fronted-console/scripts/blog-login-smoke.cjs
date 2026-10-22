const {app,BrowserWindow}=require('electron');
const assert=require('node:assert/strict');
const http=require('node:http');
const path=require('node:path');
const {attachBlogLogin}=require('../electron/blog-login.cjs');
app.setPath('userData',path.resolve(__dirname,'../build/blog-login-smoke/profile'));
app.commandLine.appendSwitch('disable-gpu');
let server,main;
const pause=ms=>new Promise(resolve=>setTimeout(resolve,ms));
async function until(check,message){for(let i=0;i<150;i++){if(await check())return;await pause(50);}throw Error(message);}
app.whenReady().then(async()=>{
  let base;
  server=http.createServer((request,response)=>{
    response.setHeader('Content-Type','text/html; charset=utf-8');
    const redirect=url=>{response.writeHead(302,{Location:url});response.end();};
    if(request.url==='/console')return response.end(`<h1>Console</h1><iframe name="kuma-blog-preview" src="${base}/kuma"></iframe>`);
    if(request.url==='/console-live')return response.end('<h1>Console</h1><iframe name="kuma-blog-preview" src="http://localhost:4321/kuma"></iframe>');
    if(request.url==='/api/auth/login')return redirect(`${base.replace('127.0.0.1','localhost')}/uaa/login`);
    if(request.url==='/uaa/login'){
      response.setHeader('X-Frame-Options','SAMEORIGIN');
      return response.end(`<h1>UAA login</h1><a id="login" href="${base}/callback">Sign in</a>`);
    }
    if(request.url==='/callback'){
      response.setHeader('Set-Cookie','blog_session=authenticated; HttpOnly; Path=/; SameSite=Strict');
      return redirect('/');
    }
    if(request.url==='/')return response.end('<h1>Blog home</h1>');
    const authenticated=(request.headers.cookie||'').includes('blog_session=authenticated');
    response.end(authenticated?'<h1>Admin</h1>':'<h1>Blog login</h1><a id="login" href="/api/auth/login">Log in</a>');
  });
  await new Promise(resolve=>server.listen(0,'127.0.0.1',resolve));
  base=`http://127.0.0.1:${server.address().port}`;
  main=new BrowserWindow({show:false,webPreferences:{nodeIntegration:false,contextIsolation:true,sandbox:true}});
  const control=attachBlogLogin(main,{show:false});
  await main.loadURL(`${base}/console`);
  const preview=()=>main.webContents.mainFrame.frames.find(frame=>frame.name==='kuma-blog-preview');
  await until(()=>preview()?.url===`${base}/kuma`,'Preview did not load');
  const click=()=>preview().executeJavaScript('document.getElementById("login").click()',true);
  await click();
  await until(()=>control.getWindow()?.webContents.getURL()===`${base.replace('127.0.0.1','localhost')}/uaa/login`,'UAA did not open in a login window');
  assert.equal(preview().url,`${base}/kuma`,'Preview navigated into the blocked UAA frame');
  assert.equal(main.webContents.getURL(),`${base}/console`);
  assert.equal(await control.getWindow().webContents.executeJavaScript('document.querySelector("h1").textContent'),'UAA login');
  assert.equal(control.getWindow().webContents.session,main.webContents.session);
  control.getWindow().close();
  await until(()=>control.getWindow()===null,'Cancelled login window did not close');
  assert.equal(preview().url,`${base}/kuma`,'Cancelling changed the preview');
  await click();
  await until(()=>control.getWindow()?.webContents.getURL()===`${base.replace('127.0.0.1','localhost')}/uaa/login`,'Could not retry login');
  await control.getWindow().webContents.executeJavaScript('document.getElementById("login").click()',true);
  await until(async()=>!control.getWindow()&&await preview().executeJavaScript('document.querySelector("h1")?.textContent')==='Admin','Callback cookies did not authenticate the preview');
  assert.equal(main.webContents.getURL(),`${base}/console`);
  console.log('PASS: UAA displays outside iframe, Console stays open, cancel/retry works, callback cookies authenticate preview');
  if(process.argv.includes('--live')){
    await main.loadURL(`http://localhost:${server.address().port}/console-live`);
    await until(async()=>preview()?.url==='http://localhost:4321/kuma'&&await preview().executeJavaScript('!!document.querySelector(".login-btn")'),'Real Blog login button did not load');
    await preview().executeJavaScript('document.querySelector(".login-btn").click()',true);
    await until(()=>control.getWindow()?.webContents.getURL()==='http://localhost:33336/login','Real UAA login page did not open');
    await until(()=>control.getWindow().webContents.executeJavaScript('!!document.querySelector("input[type=password]")'),'Real UAA login form did not render');
    assert.equal(preview().url,'http://localhost:4321/kuma');
    console.log('PASS: actual Blog login opens actual UAA password form while Console preview stays on /kuma');
  }
  main.destroy();server.close();app.quit();
}).catch(error=>{console.error(error);main?.destroy();server?.close();app.exit(1);});
setTimeout(()=>{console.error('Blog login smoke timed out');app.exit(1);},25000).unref();
