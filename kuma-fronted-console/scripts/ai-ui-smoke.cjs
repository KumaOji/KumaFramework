// Uses an isolated Electron profile and a mock SSE backend; never calls a paid API.
const {app,BrowserWindow}=require('electron');
const http=require('node:http'),fs=require('node:fs/promises'),path=require('node:path'),assert=require('node:assert/strict');
const renderer=path.resolve(__dirname,'../renderer'),output=path.resolve(__dirname,'../build/ai-ui-smoke');
app.setPath('userData',path.join(output,'profile-'+Date.now()));app.commandLine.appendSwitch('disable-gpu');
let win,server,calls=[],cancelled=0;
const errors=[];
const delay=ms=>new Promise(resolve=>setTimeout(resolve,ms));
async function serve(req,res){
  const url=new URL(req.url,'http://localhost');
  if(url.pathname==='/api/ai/chat'){
    assert.match(url.searchParams.get('requestId'),/^[a-z0-9-]{16,80}$/);
    let raw='';for await(const chunk of req)raw+=chunk;calls.push(JSON.parse(raw));
    res.writeHead(200,{'Content-Type':'text/event-stream'});
    const event=(name,data)=>res.write('event: '+name+'\ndata: '+JSON.stringify(data)+'\n\n');
    if(calls.at(-1).messages.at(-1).content==='slow'){
      event('delta',{text:'部分回复'});const timer=setInterval(()=>event('delta',{text:'…'}),100);res.on('close',()=>clearInterval(timer));return;
    }
    event('reasoning',{text:'检查中文与代码'});await delay(40);
    event('delta',{text:'你好🌸\n\n**回答**\n\n```java\nSystem.out.println("你好");\n```\n\n<img src=x onerror=alert(1)>\n\n[x](javascript:alert)'});
    await delay(40);event('done',{});res.end();return;
  }
  if(url.pathname.startsWith('/api/ai/cancel/'))cancelled++;
  if(url.pathname.startsWith('/api/')){
    let data={ready:false};
    if(url.pathname==='/api/ai/status')data={configured:true,source:'local'};
    if(url.pathname==='/api/projects/catalog')data={projects:[]};
    if(url.pathname==='/api/projects/running')data=[];
    res.writeHead(200,{'Content-Type':'application/json'});res.end(JSON.stringify(data));return;
  }
  try{const target=path.resolve(renderer,'.'+(url.pathname==='/'?'/index.html':url.pathname));if(!target.startsWith(renderer+path.sep))throw Error();
    const content=await fs.readFile(target),ext=path.extname(target);
    res.writeHead(200,{'Content-Type':({'.html':'text/html','.js':'text/javascript','.css':'text/css','.png':'image/png','.json':'application/json'})[ext]||'application/octet-stream'});res.end(content);
  }catch{res.writeHead(404);res.end();}
}
app.whenReady().then(async()=>{
  await fs.mkdir(output,{recursive:true});server=http.createServer((req,res)=>serve(req,res).catch(e=>{console.error(e);res.end();}));await new Promise(resolve=>server.listen(0,'127.0.0.1',resolve));
  win=new BrowserWindow({show:false,width:1500,height:1050,webPreferences:{contextIsolation:true,nodeIntegration:false,backgroundThrottling:false}});
  win.webContents.on('console-message',event=>{if(event.level==='error'&&!event.message.includes('404'))errors.push(event.message);});
  const evaluate=code=>win.webContents.executeJavaScript(code,true);
  async function wait(code){for(let i=0;i<100;i++){if(await evaluate(code))return;await delay(50);}throw Error('Timed out: '+code);}
  const address='http://127.0.0.1:'+server.address().port;
  await win.loadURL(address);
  await evaluate(`localStorage.setItem('kuma-console-welcome','false');location.hash='ai';navigate();`);
  await wait(`!document.getElementById('ai-send').disabled`);
  await evaluate(`document.getElementById('ai-prompt').value='你好';document.getElementById('ai-composer').requestSubmit();`);
  await wait(`document.getElementById('ai-draft-status').textContent==='回复完成'`);
  assert.equal(calls.length,1);assert.equal(calls[0].messages[0].content,'你好');
  assert.ok(await evaluate(`document.querySelector('.ai-markdown strong').textContent==='回答' && document.querySelector('.ai-markdown pre code').textContent.includes('System.out')`));
  assert.ok(await evaluate(`!document.querySelector('#ai-messages img')&&!document.querySelector('#ai-messages a[href^="javascript:"]')`));
  await evaluate(`document.getElementById('ai-prompt').value='继续';document.getElementById('ai-composer').requestSubmit();`);await wait(`document.getElementById('ai-draft-status').textContent==='回复完成'`);
  assert.deepEqual(calls[1].messages.map(m=>m.role),['user','assistant','user']);
  await evaluate(`Array.from(document.querySelectorAll('.ai-message-tools button')).at(-1).click();`);await wait(`document.getElementById('ai-draft-status').textContent==='回复完成'`);
  assert.equal(calls.length,3);assert.equal(calls[2].messages.length,3);
  await evaluate(`document.getElementById('ai-rename').click();document.getElementById('ai-rename-input').value='中文实验';document.getElementById('ai-rename-form').requestSubmit();`);
  assert.equal(await evaluate(`document.getElementById('ai-title').textContent`),'中文实验');
  await evaluate(`document.getElementById('ai-new').click();document.getElementById('ai-prompt').value='slow';document.getElementById('ai-composer').requestSubmit();`);await wait(`document.querySelector('#ai-messages .assistant').textContent.includes('部分回复')`);
  await evaluate(`document.getElementById('ai-stop').click();`);await wait(`document.getElementById('ai-stop').hidden`);await delay(80);assert.equal(cancelled,1);
  assert.ok(await evaluate(`document.getElementById('ai-messages').textContent.includes('已停止生成')`));
  await new Promise(resolve=>{win.webContents.once('did-finish-load',resolve);win.reload();});
  await evaluate(`location.hash='ai';navigate();`);await wait(`!document.getElementById('ai-send').disabled`);
  assert.equal(await evaluate(`document.querySelectorAll('.ai-conversation').length`),2);
  await evaluate(`document.getElementById('ai-search').value='中文';document.getElementById('ai-search').dispatchEvent(new Event('input'));`);
  assert.equal(await evaluate(`document.querySelectorAll('.ai-conversation').length`),1);
  await evaluate(`document.querySelector('.ai-conversation').click();setTheme('dark');`);
  await wait(`document.getElementById('ai-title').textContent==='中文实验'`);await delay(500);
  assert.ok(await evaluate(`document.documentElement.scrollWidth<=innerWidth`));
  await fs.writeFile(path.join(output,'desktop.png'),(await win.webContents.capturePage()).toPNG());
  await evaluate(`document.getElementById('ai-settings').click();`);assert.ok(await evaluate(`document.getElementById('ai-settings-dialog').open`));await evaluate(`document.getElementById('ai-settings-close').click();`);
  win.setSize(480,900);await delay(200);assert.ok(await evaluate(`document.documentElement.scrollWidth<=innerWidth`));
  await fs.writeFile(path.join(output,'mobile.png'),(await win.webContents.capturePage()).toPNG());
  assert.deepEqual(errors,[]);console.log('AI UI smoke passed: streaming, Unicode, code, safe Markdown, context, retry, rename, cancellation, persistence, search, desktop/mobile.');
  win.destroy();server.close();app.exit(0);
}).catch(error=>{console.error(error);win?.destroy();server?.close();app.exit(1);});
