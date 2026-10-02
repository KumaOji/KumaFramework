// Optional UI verification with an installed Chrome; no npm dependencies needed (Node 22+).
import {spawn} from 'node:child_process';
import {mkdtemp,readFile,mkdir,writeFile,rm} from 'node:fs/promises';
import {tmpdir} from 'node:os';
import {join,resolve,sep} from 'node:path';
import assert from 'node:assert/strict';
const base=process.env.CONSOLE_URL||'http://127.0.0.1:18090';
const chrome=process.env.CHROME_BIN||'C:/Program Files/Google/Chrome/Application/chrome.exe';
const profile=await mkdtemp(join(tmpdir(),'kuma-console-chrome-'));
const output=resolve('kuma-project/kuma-project-console/build/screenshots');
await mkdir(output,{recursive:true});
const browser=spawn(chrome,['--headless=new','--disable-gpu','--no-first-run','--no-default-browser-check',
  '--remote-debugging-port=0',`--user-data-dir=${profile}`,'about:blank'],{windowsHide:true,stdio:'ignore'});
const pause=ms=>new Promise(r=>setTimeout(r,ms));let socket;
try{
  let port;
  for(let i=0;i<100;i++){try{port=Number((await readFile(join(profile,'DevToolsActivePort'),'utf8')).split('\n')[0]);break;}catch{await pause(100);}}
  assert(port,'Chrome did not start');
  const tab=await (await fetch(`http://127.0.0.1:${port}/json/new?${encodeURIComponent(base)}`,{method:'PUT'})).json();
  socket=new WebSocket(tab.webSocketDebuggerUrl);await new Promise((r,j)=>{socket.onopen=r;socket.onerror=j;});
  let sequence=0;const pending=new Map(),errors=[];
  socket.onmessage=event=>{const msg=JSON.parse(event.data);if(msg.id){const promise=pending.get(msg.id);pending.delete(msg.id);msg.error?promise?.reject(Error(JSON.stringify(msg.error))):promise?.resolve(msg.result);}if(msg.method==='Runtime.exceptionThrown')errors.push(msg.params.exceptionDetails.text+' '+JSON.stringify(msg.params.exceptionDetails.exception));};
  const send=(method,params={})=>new Promise((resolve,reject)=>{const id=++sequence;pending.set(id,{resolve,reject});socket.send(JSON.stringify({id,method,params}));});
  const evaluate=async(expression)=>{const r=await send('Runtime.evaluate',{expression,awaitPromise:true,returnByValue:true});if(r.exceptionDetails)throw Error(JSON.stringify(r.exceptionDetails));return r.result.value;};
  async function waitFor(expression){for(let i=0;i<100;i++){if(await evaluate(expression))return;await pause(200);}throw Error(`Timed out: ${expression}`);}
  await send('Runtime.enable');await send('Page.enable');
  await send('Emulation.setDeviceMetricsOverride',{width:1440,height:1100,deviceScaleFactor:1,mobile:false});
  await send('Page.navigate',{url:base});
  await waitFor('document.querySelectorAll("#process-rows tr").length > 5 && document.querySelectorAll("#cluster-table tbody tr").length > 0');
  await waitFor('state.host?.cpu?.percent != null');
  await waitFor('presets.length >= 64');
  await waitFor('projectCatalog?.projects?.length >= 17');
  for(const page of ['overview','cluster','projects','lab']){
    await evaluate(`location.hash=${JSON.stringify(page)}`);await pause(300);
    assert.equal(await evaluate(`!document.getElementById(${JSON.stringify(page)}).hidden`),true);
    const screenshot=await send('Page.captureScreenshot',{format:'png',captureBeyondViewport:false});
    await writeFile(join(output,page+'.png'),Buffer.from(screenshot.data,'base64'));
  }
  await evaluate('location.hash="overview"');await pause(100);
  await evaluate('document.getElementById("process-search").value="java";document.getElementById("process-search").dispatchEvent(new Event("input"));');
  assert(await evaluate('document.querySelectorAll("#process-rows tr").length>0'));
  await evaluate('location.hash="cluster"');await pause(100);
  await evaluate('document.querySelector("#cluster-table [data-resource]").click()');
  assert.equal(await evaluate('document.getElementById("resource-drawer").hidden'),false);
  await evaluate('document.getElementById("close-drawer").click()');
  await evaluate('document.getElementById("inspect-wsl").click()');
  await waitFor('!document.getElementById("inspect-wsl").disabled');
  assert(await evaluate('wslData?.success && Object.keys(wslData.sections).length===8'),'WSL details unavailable');
  await evaluate('document.querySelector("#wsl-tabs [data-section=PROCESSES]").click()');
  assert(await evaluate('document.getElementById("wsl-detail").textContent.includes("PID")'));
  await evaluate('location.hash="projects"');await pause(100);
  assert.equal(await evaluate('projectCatalog.projects.filter(p=>p.enabled).length'),7);
  await evaluate('document.getElementById("catalog-search").value="blog";document.getElementById("catalog-search").dispatchEvent(new Event("input"));');
  assert.equal(await evaluate('document.querySelectorAll("[data-dependencies]").length'),1);
  await evaluate('document.querySelector("[data-dependencies]").click()');
  await waitFor('document.getElementById("drawer-content").textContent.includes("kuma-boot-starter-mq-kafka")');
  assert(await evaluate('document.getElementById("drawer-content").textContent.includes("[间接] kuma-boot-starter-mq-kafka")'));
  await evaluate('const mode=document.querySelector("#drawer-tools select");mode.value="tree";mode.dispatchEvent(new Event("change"));');
  assert(await evaluate('document.getElementById("drawer-content").textContent.includes("kuma-boot-starter-web [api]")'));
  await evaluate('document.getElementById("close-drawer").click();document.getElementById("catalog-search").value="";document.getElementById("catalog-filter").value="disabled";document.getElementById("catalog-filter").dispatchEvent(new Event("change"));');
  assert.equal(await evaluate('document.querySelectorAll("[data-dependencies]").length'),10);
  await evaluate('document.getElementById("catalog-filter").value="all";document.getElementById("catalog-filter").dispatchEvent(new Event("change"));');
  await waitFor('state.running?.length>0');
  await evaluate('document.querySelector(".project-card button").click()');
  await waitFor('document.getElementById("drawer-content").textContent.includes("HTTP")');
  await evaluate('document.getElementById("close-drawer").click()');
  await evaluate('location.hash="lab"');await pause(100);
  const sqlSample='CREATE TABLE t (\n    id BIGINT\n);';
  await evaluate(`lastLabResponse=${JSON.stringify(JSON.stringify({payload:{data:[{syntax:sqlSample}]}}))};renderLabResponse();`);
  assert(await evaluate(`document.getElementById("lab-response").textContent.includes(${JSON.stringify(sqlSample)})`),'Response string newlines are not rendered');
  await evaluate('document.getElementById("response-view").value="json";document.getElementById("response-view").dispatchEvent(new Event("change"));');
  assert(await evaluate(`JSON.parse(document.getElementById("lab-response").textContent).payload.data[0].syntax.includes(${JSON.stringify('\n')})`));
  await evaluate('document.getElementById("response-view").value="multiline";document.getElementById("response-view").dispatchEvent(new Event("change"));');
  await evaluate('document.getElementById("lab-body").value="{invalid";document.getElementById("lab-send").click()');
  assert.equal(await evaluate('document.getElementById("lab-request-error").hidden'),false);
  await evaluate('document.getElementById("lab-method").value="GET";document.getElementById("lab-path").value="/lab/kafka/status";document.getElementById("lab-body").value="";document.getElementById("lab-send").click()');
  await waitFor('!document.getElementById("lab-send").disabled');
  assert(await evaluate('document.getElementById("lab-response-meta").textContent.includes("HTTP")'));
  await send('Emulation.setDeviceMetricsOverride',{width:390,height:844,deviceScaleFactor:1,mobile:true});
  await pause(300);
  assert(await evaluate('document.documentElement.scrollWidth<=window.innerWidth'),'Mobile page overflows');
  const mobile=await send('Page.captureScreenshot',{format:'png'});await writeFile(join(output,'lab-mobile.png'),Buffer.from(mobile.data,'base64'));
  assert.deepEqual(errors,[],'Browser JavaScript errors');
  console.log('Browser smoke passed: four pages, project catalog/filter, nested starter paths/tree, process filter, resource details, JSON validation, Lab request, mobile layout.');
  console.log('Screenshots: '+output);
}finally{
  socket?.close();browser.kill();
  // Remove only this temporary directory created by mkdtemp, never a supplied profile.
  await pause(500);if(!resolve(profile).startsWith(resolve(tmpdir())+sep))throw Error('Unexpected temporary profile path');
  try{await rm(profile,{recursive:true,force:true,maxRetries:3});}catch{}
}
