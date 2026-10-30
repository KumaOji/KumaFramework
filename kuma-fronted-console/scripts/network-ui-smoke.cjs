const {app,BrowserWindow}=require('electron');
const assert=require('node:assert/strict');
const fs=require('node:fs');
const path=require('node:path');
app.commandLine.appendSwitch('disable-gpu');
app.setPath('userData',path.resolve(__dirname,'../build/network-ui-smoke/profile'));
let window;
app.whenReady().then(async()=>{
  window=new BrowserWindow({show:false,webPreferences:{sandbox:true,contextIsolation:true}});
  const html=fs.readFileSync(path.join(__dirname,'../renderer/index.html'),'utf8').replace(/<script\b[^>]*>[\s\S]*?<\/script>/g,'');
  await window.loadURL('data:text/html;charset=utf-8,'+encodeURIComponent(html));
  const source=fs.readFileSync(path.join(__dirname,'../renderer/console.js'),'utf8');
  const helpers=source.slice(source.indexOf('const $ ='),source.indexOf('const state ='));
  const functions=source.slice(source.indexOf('function renderNetwork()'),source.indexOf('function renderHost()'));
  const js=s=>window.webContents.executeJavaScript(s);
  await js(`${helpers}
    const state={errors:{},network:{ready:true,adapters:[{name:'<virtual>',virtual:true,status:'Up',ipv4:['192.0.2.1/24'],ipv6:['::1/128'],dns:['1.1.1.1'],gateways:['192.0.2.254']}],system:{enabled:true,server:'127.0.0.1:7890'},environment:[],proxyRoutes:['Windows static']}};
    function notice(id,text){$(id).hidden=!text;$(id).textContent=text;}
    let calls=0,fail=false;
    async function api(){calls++;await new Promise(r=>setTimeout(r,50));if(fail)throw Error('offline');return {testedAt:new Date().toISOString(),results:[{name:'GitHub',url:'https://github.com/',route:'direct',reachable:true,status:403,totalMs:25},{name:'Google',route:'proxy',reachable:false,error:'<timeout>'}]};}
    ${functions}
    renderNetwork();`);
  assert.equal(await js('calls'),0,'Rendering must not trigger external probes');
  assert.ok(await js("$('network-adapters').textContent.includes('虚拟') && $('network-adapters').textContent.includes('::1/128')"));
  assert.equal(await js("$('network-adapters').querySelector('virtual')"),null,'Adapter name must be escaped');
  await js("$('network-test').click()");
  assert.equal(await js("$('network-test').disabled"),true);
  await new Promise(r=>setTimeout(r,150));
  assert.equal(await js("$('network-test').disabled"),false);
  assert.ok(await js("$('network-test-results').textContent.includes('HTTP 403') && $('network-test-results').textContent.includes('<timeout>')"));
  await js("fail=true;$('network-test').click()");
  await new Promise(r=>setTimeout(r,150));
  assert.ok(await js("$('network-test-status').textContent.includes('offline') && $('network-test-results').textContent.includes('HTTP 403')"));
  console.log('PASS: adapter details, escaped output, manual probes, loading state, HTTP status, failure and retained results');
  window.destroy();app.quit();
}).catch(e=>{console.error(e);window?.destroy();app.exit(1);});
setTimeout(()=>app.exit(1),15000).unref();
