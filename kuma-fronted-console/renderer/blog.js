'use strict';
function isBlogViewActive(){return state.page==='projects'&&state.servicesTab==='blog';}
let blogControlBusy=false, blogPollBusy=false, blogRevision=0;
let blogBackendBusy=false,blogBackendPollBusy=false,blogFrontendStatus=null,blogBackendStatus=null,blogView='home';
let blogCustomUrl=null;
function renderBlogPreview(){
  const defaultUrl=blogView==='knowledge'?(blogFrontendStatus?.knowledgeUrl||'http://localhost:4321/knowledge'):(blogFrontendStatus?.url||'http://localhost:4321');
  const url=blogCustomUrl||defaultUrl,ready=!!blogCustomUrl||!!blogFrontendStatus?.canOpen;
  if($('blog-preview-url').dataset.url!==url){$('blog-preview-url').value=url;$('blog-preview-url').dataset.url=url;}
  $('blog-open').hidden=!ready;$('blog-open').href=url;
  $('blog-preview').hidden=!ready;$('blog-preview-empty').hidden=ready;
  if(ready){if($('blog-preview').dataset.url!==url){$('blog-preview').src=url;$('blog-preview').dataset.url=url;}}
  else if($('blog-preview').hasAttribute('src')){$('blog-preview').removeAttribute('src');delete $('blog-preview').dataset.url;}
  renderBlogControls();
  $('blog-settings-summary').textContent=`前端 ${blogFrontendStatus?.canOpen?'运行中':'未就绪'} · 后端 ${blogBackendStatus?.state==='RUNNING'?'运行中':'未就绪'}`;
}
function renderBlogControls(){
  $('blog-start').disabled=blogControlBusy||!blogFrontendStatus?.canStart;
  $('blog-stop').disabled=blogControlBusy||!blogFrontendStatus?.canStop;
  $('blog-sync').disabled=blogControlBusy||!blogFrontendStatus?.canSync;
  $('blog-backend-start').disabled=blogBackendBusy||!blogBackendStatus?.canStart;
  $('blog-backend-stop').disabled=blogBackendBusy||!blogBackendStatus?.canStop;
}
function renderBlogProcess(data){
  blogFrontendStatus=data;
  const labels={STOPPED:'已停止',SYNCING:'同步知识仓库中',STARTING:'启动中',RUNNING:'运行中',STOPPING:'停止中',FAILED:'失败',EXTERNAL:'端口已占用'};
  $('blog-process-state').textContent=labels[data.state]||data.state;
  $('blog-process-state').className='status '+(data.state==='RUNNING'?'ok':data.state==='FAILED'?'bad':'');
  $('blog-process-message').textContent=data.message+(data.pid?` · PID ${data.pid}`:'');
  $('blog-sync').textContent=data.knowledgeState==='SYNCING'?'同步中…':'同步知识仓库';
  $('blog-knowledge-message').textContent=data.knowledgeMessage||'启动 Blog 时自动同步知识仓库';
  $('blog-knowledge-open').hidden=!data.canOpen;$('blog-knowledge-open').href=data.knowledgeUrl||data.url+'/knowledge';
  $('blog-open').hidden=!data.canOpen;$('blog-open').href=data.url;
  $('blog-url').textContent=data.url;$('blog-directory').textContent=data.directory;
  renderBlogPreview();
}
async function pollBlogProcess(){
  if(blogPollBusy||blogControlBusy)return;
  blogPollBusy=true;const revision=blogRevision;
  try{const data=await api('/api/blog/process');if(revision===blogRevision)renderBlogProcess(data);}
  catch(e){if(revision===blogRevision){$('blog-start').disabled=true;$('blog-stop').disabled=true;$('blog-sync').disabled=true;notice('blog-process-error',e.message);}}
  finally{blogPollBusy=false;}
}
async function controlBlog(action){
  if(blogControlBusy)return;
  blogControlBusy=true;blogRevision++;renderBlogControls();notice('blog-process-error','');
  try{
    renderBlogProcess(await api('/api/blog/process/'+action,{method:'POST'}));
  }
  catch(e){notice('blog-process-error',e.message);}
  finally{blogControlBusy=false;await Promise.all([pollBlogProcess(),pollBlogBackend()]);renderBlogControls();}
}
$('blog-start').onclick=()=>controlBlog('start');$('blog-stop').onclick=()=>controlBlog('stop');
$('blog-sync').onclick=()=>controlBlog('sync');
const refreshBlogProcessLog=LogConsole.bind({button:$('blog-logs'),element:$('blog-process-log'),load:()=>api('/api/blog/process/logs'),isActive:()=>!document.hidden&&isBlogViewActive(),onError:e=>notice('blog-process-error',e.message)});
function renderBlogBackend(data){
  blogBackendStatus=data;
  const labels={STOPPED:'已停止',BUILDING:'构建中',STARTING:'启动中',RUNNING:'运行中',STOPPING:'停止中',FAILED:'失败',EXTERNAL:'端口已占用'};
  $('blog-backend-state').textContent=labels[data.state]||data.state;
  $('blog-backend-message').textContent=data.message+(data.pid?` · PID ${data.pid}`:'');
  $('blog-backend-url').textContent=data.url;$('blog-backend-distribution').textContent=data.distribution;
  renderBlogPreview();
}
async function pollBlogBackend(){if(blogBackendPollBusy||blogBackendBusy)return;blogBackendPollBusy=true;try{const data=await api('/api/blog/backend');if(!blogBackendBusy)renderBlogBackend(data);}catch(e){$('blog-backend-start').disabled=true;$('blog-backend-stop').disabled=true;notice('blog-backend-error',e.message);}finally{blogBackendPollBusy=false;}}
async function controlBlogBackend(action){if(blogBackendBusy)return;blogBackendBusy=true;renderBlogControls();notice('blog-backend-error','');try{renderBlogBackend(await api('/api/blog/backend/'+action,{method:'POST'}));}catch(e){notice('blog-backend-error',e.message);}finally{blogBackendBusy=false;await pollBlogBackend();renderBlogControls();}}
$('blog-backend-start').onclick=()=>controlBlogBackend('start');$('blog-backend-stop').onclick=()=>controlBlogBackend('stop');
const refreshBlogBackendLog=LogConsole.bind({
  button:$('blog-backend-logs'),element:$('blog-backend-log'),
  clearButton:$('blog-backend-log-clear'),load:()=>api('/api/blog/backend/logs'),
  isActive:()=>!document.hidden&&isBlogViewActive(),closeLabel:'关闭日志',onError:e=>notice('blog-backend-error',e.message)
});
function refreshBlogLogs(){refreshBlogProcessLog();refreshBlogBackendLog();}
setInterval(refreshBlogLogs,1000);
document.addEventListener('visibilitychange',refreshBlogLogs);
window.addEventListener('hashchange',refreshBlogLogs);
$('blog-preview-address').onsubmit=event=>{
  event.preventDefault();
  try{
    const url=new URL($('blog-preview-url').value.trim());
    if(!['http:','https:'].includes(url.protocol))throw new Error('请输入以 http:// 或 https:// 开头的 URL');
    blogCustomUrl=url.href;notice('blog-preview-error','');renderBlogPreview();
  }catch(error){notice('blog-preview-error','请输入有效的 HTTP 或 HTTPS 地址');}
};
$('blog-view-home').onclick=()=>{blogView='home';blogCustomUrl=null;notice('blog-preview-error','');renderBlogPreview();};$('blog-view-knowledge').onclick=()=>{blogView='knowledge';blogCustomUrl=null;notice('blog-preview-error','');renderBlogPreview();};
try{$('blog-settings').open=localStorage.getItem('blog-settings-open')==='true';}catch{}
$('blog-settings').addEventListener('toggle',()=>{try{localStorage.setItem('blog-settings-open',String($('blog-settings').open));}catch{}});
window.addEventListener('hashchange',()=>{if(isBlogViewActive()){pollBlogProcess();pollBlogBackend();}});
setInterval(()=>{if(!document.hidden&&isBlogViewActive()){pollBlogProcess();pollBlogBackend();}},2000);
pollBlogProcess();pollBlogBackend();
