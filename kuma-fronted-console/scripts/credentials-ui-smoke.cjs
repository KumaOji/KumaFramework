// Only fixture credentials are used. No PowerShell, WSL, or real secrets.
const {app,BrowserWindow}=require('electron');
const http=require('node:http'),fs=require('node:fs/promises'),path=require('node:path'),assert=require('node:assert/strict');
app.commandLine.appendSwitch('disable-gpu');
app.setPath('userData',path.resolve(__dirname,'../build/credentials-ui-smoke/profile-'+Date.now()));
let win,server,reads=0,reveals=0,delayReveal=false;
const renderer=path.resolve(__dirname,'../renderer');
const fixture={service:'grafana',name:'Grafana <img src=x onerror=alert(1)>',status:'available',username:'fixture-admin',password:'********',apiKey:'',loginUrl:'',source:'wsl-text',note:'fixture only'};
async function serve(req,res){
  const url=new URL(req.url,'http://localhost');
  if(url.pathname==='/api/credentials'){
    assert.equal(req.method,'POST');assert.equal(req.headers['x-kuma-credentials'],'console');
    let body='';for await(const chunk of req)body+=chunk;
    const query=JSON.parse(body);reads++;
    if(query.reveal){assert.equal(query.service,'grafana');reveals++;if(delayReveal)await new Promise(resolve=>setTimeout(resolve,250));}
    res.writeHead(200,{'Content-Type':'application/json','Cache-Control':'no-store'});
    res.end(JSON.stringify([{...fixture,password:query.reveal?'fixture-password':fixture.password},...(query.reveal?[]:[{service:'otlp',name:'OTLP',status:'no-stored-login'}])]));return;
  }
  if(url.pathname.startsWith('/api/')){res.writeHead(200,{'Content-Type':'application/json'});res.end('{}');return;}
  try{
    const file=path.resolve(renderer,'.'+(url.pathname==='/'?'/index.html':url.pathname));
    if(!file.startsWith(renderer+path.sep))throw Error();
    res.writeHead(200,{'Content-Type':({'.html':'text/html','.js':'text/javascript','.css':'text/css','.png':'image/png'})[path.extname(file)]||'application/octet-stream'});res.end(await fs.readFile(file));
  }catch{res.writeHead(404);res.end();}
}
app.whenReady().then(async()=>{
  server=http.createServer((req,res)=>serve(req,res).catch(error=>{console.error(error);app.exit(1);}));
  await new Promise(resolve=>server.listen(0,'127.0.0.1',resolve));
  win=new BrowserWindow({show:false,width:1200,height:900,webPreferences:{backgroundThrottling:false,contextIsolation:true}});
  const js=code=>win.webContents.executeJavaScript(code,true);
  async function wait(code){for(let i=0;i<100;i++){if(await js(code))return;await new Promise(resolve=>setTimeout(resolve,50));}throw Error('Timed out: '+code);}
  await win.loadURL('http://127.0.0.1:'+server.address().port+'/#settings');
  assert.equal(reads,0,'must not read credentials automatically');
  await js(`document.getElementById('credentials-refresh').click()`);
  await wait(`document.querySelectorAll('.credential-card').length===1`);
  assert.equal(await js(`document.getElementById('credential-grafana-password').value`),'********');
  assert.equal(await js(`document.querySelectorAll('.credential-card img').length`),0,'source values must be text');
  await js(`document.querySelector('.credential-heading button').click()`);
  await wait(`document.getElementById('credential-grafana-password').value==='fixture-password'`);
  assert.equal(reveals,1);
  await js(`window.fixtureClipboard='';Object.defineProperty(navigator,'clipboard',{configurable:true,value:{writeText:async text=>{window.fixtureClipboard=text;}}});document.querySelector('[aria-label$="的密码"]').click()`);
  await wait(`window.fixtureClipboard==='fixture-password'`);
  await js(`window.dispatchEvent(new Event('blur'))`);
  assert.equal(await js(`document.getElementById('credential-grafana-password').value`),'********');
  await js(`window.realTimeout=window.setTimeout;window.setTimeout=(callback,delay,...args)=>{if(delay===60000)window.fixtureExpiry=callback;return window.realTimeout(callback,delay,...args);};document.querySelector('.credential-heading button').click()`);
  await wait(`document.getElementById('credential-grafana-password').value==='fixture-password'`);
  await js(`window.fixtureExpiry();window.setTimeout=window.realTimeout;void 0`);
  assert.equal(await js(`document.getElementById('credential-grafana-password').value`),'********','60 second expiry must hide credentials');
  delayReveal=true;
  await js(`document.querySelector('.credential-heading button').click();window.dispatchEvent(new Event('blur'))`);
  await new Promise(resolve=>setTimeout(resolve,400));
  assert.equal(await js(`document.getElementById('credential-grafana-password').value`),'********','late responses must not reveal after blur');
  await js(`document.querySelector('.credential-heading button').click()`);
  await wait(`document.getElementById('credential-grafana-password').value==='fixture-password'`);
  await js(`location.hash='overview'`);
  await wait(`document.getElementById('credential-grafana-password').value==='********'`);
  await js(`location.hash='settings'`);win.setSize(480,900);await wait(`innerWidth<=480`);
  assert.ok(await js(`document.documentElement.scrollWidth<=innerWidth`),'mobile overflow');
  console.log('Credentials UI passed: explicit read, masking, per-service reveal, copy, escaping, blur/navigation hiding, stale-response protection, mobile.');
  win.destroy();server.close();app.exit(0);
}).catch(error=>{console.error(error);win?.destroy();server?.close();app.exit(1);});
setTimeout(()=>{console.error('Credentials UI timeout');app.exit(1);},30000).unref();
