const {app,BrowserWindow}=require('electron');
const assert=require('node:assert/strict');
const fs=require('node:fs');
const path=require('node:path');
const http=require('node:http');
app.commandLine.appendSwitch('disable-gpu');
app.setPath('userData',path.resolve(__dirname,'../build/network-ui-smoke/profile'));
let window,server;
app.whenReady().then(async()=>{
  window=new BrowserWindow({show:false,webPreferences:{sandbox:true,contextIsolation:true}});
  const html=fs.readFileSync(path.join(__dirname,'../renderer/index.html'),'utf8').replace(/<script\b[^>]*>[\s\S]*?<\/script>/g,'');
  server=http.createServer((request,response)=>{
    response.setHeader('Content-Type','text/html; charset=utf-8');response.end(html);
  });
  await new Promise(resolve=>server.listen(0,'127.0.0.1',resolve));
  await window.loadURL(`http://127.0.0.1:${server.address().port}/`);
  const source=fs.readFileSync(path.join(__dirname,'../renderer/console.js'),'utf8');
  const helpers=source.slice(source.indexOf('const $ ='),source.indexOf('const state ='));
  const functions=source.slice(source.indexOf('function renderNetwork()'),source.indexOf('function renderHost()'));
  const js=s=>window.webContents.executeJavaScript(s).catch(error=>{throw new Error(s,{cause:error});});
  const navigation=source.slice(source.indexOf('function navigate()'),source.indexOf("window.addEventListener('hashchange', navigate)"));
  await js(`${helpers}
    const state={errors:{},network:{ready:true,adapters:[{name:'<virtual>',virtual:true,status:'Up',ipv4:['192.0.2.1/24'],ipv6:['::1/128'],dns:['1.1.1.1'],gateways:['192.0.2.254']}],system:{enabled:true,server:'127.0.0.1:7890'},environment:[],proxyRoutes:['Windows static']}};
    function notice(id,text){$(id).hidden=!text;$(id).textContent=text;}
    let calls=0,fail=false;
    async function api(){calls++;await new Promise(r=>setTimeout(r,50));if(fail)throw Error('offline');return {testedAt:new Date().toISOString(),results:[{name:'GitHub',url:'https://github.com/',route:'direct',reachable:true,status:403,totalMs:25},{name:'Google',route:'proxy',reachable:false,error:'<timeout>'}]};}
    ${functions}
    renderNetwork();`);
  await js(`let pageScrollTimer;function closeDrawer(){};${navigation}
    location.hash='network';`);
  await js('navigate();');
  assert.ok(await js("state.page==='overview' && !$('overview').hidden && !$('network').hidden && $('overview-system').hidden && $('page-title').textContent==='本机概览' && document.querySelector('[data-page=overview]').getAttribute('aria-current')==='page' && !document.querySelector('[data-page=network]')"));
  assert.ok(await js("$('network').contains($('network-adapters')) && $('network').contains($('network-test')) && $('overview').contains($('network-test')) && $('overview-network-tab').getAttribute('aria-selected')==='true'"));
  await js("location.hash='overview';");
  await js('navigate();');
  assert.ok(await js("!$('overview').hidden && $('network').hidden"));
  await js("location.hash='overview/network';");
  await js('navigate();');
  assert.equal(await js('calls'),0,'Rendering must not trigger external probes');
  assert.ok(await js("$('network-details-panel').parentElement===$('network-proxy-panel').parentElement && !$('network-details-panel').contains($('network-proxies')) && $('network-proxy-panel').contains($('network-test')) && $('network-proxy-panel').contains($('network-test-results')) && $('network-test-title').tagName==='H3'"),'Network and proxy panels are siblings; connectivity belongs to proxy settings');
  assert.ok(await js("$('network-adapters').textContent.includes('虚拟') && $('network-adapters').textContent.includes('::1/128')"));
  assert.equal(await js("$('network-adapters').querySelector('virtual')"),null,'Adapter name must be escaped');
  assert.equal(await js("$('network-adapters').querySelector('details').open"),false,'Adapter details start collapsed');
  await js("$('network-adapters').querySelector('summary').click();window.__adapter=$('network-adapters').querySelector('details');renderNetwork();");
  assert.ok(await js("$('network-adapters').querySelector('details').open && $('network-adapters').querySelector('details')===window.__adapter"),'Refresh keeps expanded details and existing DOM');
  await js("state.network.adapters[0].dns=['9.9.9.9'];renderNetwork();");
  assert.ok(await js("$('network-adapters').querySelector('details').open && $('network-adapters').querySelector('details').textContent.includes('9.9.9.9')"),'New sample preserves expansion');
  await js("$('network-adapters').querySelector('summary').click();state.network.adapters[0].mtu=1400;renderNetwork();");
  assert.equal(await js("$('network-adapters').querySelector('details').open"),false,'New sample preserves collapsed state');
  await js("$('network-test').click()");
  assert.equal(await js("$('network-test').disabled"),true);
  await new Promise(r=>setTimeout(r,150));
  assert.equal(await js("$('network-test').disabled"),false);
  assert.ok(await js("$('network-test-results').textContent.includes('HTTP 403') && $('network-test-results').textContent.includes('<timeout>')"));
  await js("fail=true;$('network-test').click()");
  await new Promise(r=>setTimeout(r,150));
  assert.ok(await js("$('network-test-status').textContent.includes('offline') && $('network-test-results').textContent.includes('HTTP 403')"));
  console.log('PASS: adapter details, escaped output, manual probes, loading state, HTTP status, failure and retained results');
  window.destroy();server.close();app.quit();
}).catch(e=>{console.error(e);window?.destroy();server?.close();app.exit(1);});
setTimeout(()=>app.exit(1),15000).unref();
