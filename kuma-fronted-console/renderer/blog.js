'use strict';
let blogControlBusy=false, blogPollBusy=false, blogRevision=0,blogStopAllBusy=false;
let blogBackendBusy=false,blogBackendPollBusy=false,blogFrontendStatus=null,blogBackendStatus=null,blogView='home';
function renderBlogPreview(){
  const ready=!!blogFrontendStatus?.canOpen;
  $('blog-preview').hidden=!ready;$('blog-preview-empty').hidden=ready;
  if(ready){const url=blogView==='knowledge'?blogFrontendStatus.knowledgeUrl:blogFrontendStatus.url;if($('blog-preview').dataset.url!==url){$('blog-preview').src=url;$('blog-preview').dataset.url=url;}}
  else if($('blog-preview').hasAttribute('src')){$('blog-preview').removeAttribute('src');delete $('blog-preview').dataset.url;}
  renderBlogControls();
  $('blog-settings-summary').textContent=`前端 ${blogFrontendStatus?.canOpen?'运行中':'未就绪'} · 后端 ${blogBackendStatus?.state==='RUNNING'?'运行中':'未就绪'}`;
}
function renderBlogControls(){
  $('blog-start').disabled=blogStopAllBusy||blogControlBusy||!blogFrontendStatus?.canStart;
  $('blog-stop').disabled=blogStopAllBusy||blogControlBusy||!blogFrontendStatus?.canStop;
  $('blog-sync').disabled=blogStopAllBusy||blogControlBusy||!blogFrontendStatus?.canSync;
  $('blog-backend-start').disabled=blogStopAllBusy||blogBackendBusy||!blogBackendStatus?.canStart;
  $('blog-backend-stop').disabled=blogStopAllBusy||blogBackendBusy||!blogBackendStatus?.canStop;
  $('blog-stop-all').disabled=blogStopAllBusy||blogControlBusy||blogBackendBusy||!(blogFrontendStatus?.canStop||blogBackendStatus?.canStop);
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
  if(blogPollBusy||blogControlBusy||blogStopAllBusy)return;
  blogPollBusy=true;const revision=blogRevision;
  try{const data=await api('/api/blog/process');if(revision===blogRevision)renderBlogProcess(data);}
  catch(e){if(revision===blogRevision){$('blog-start').disabled=true;$('blog-stop').disabled=true;$('blog-sync').disabled=true;notice('blog-process-error',e.message);}}
  finally{blogPollBusy=false;}
}
async function controlBlog(action){
  if(blogControlBusy||blogStopAllBusy)return;
  blogControlBusy=true;blogRevision++;renderBlogControls();notice('blog-process-error','');
  try{
    renderBlogProcess(await api('/api/blog/process/'+action,{method:'POST'}));
  }
  catch(e){notice('blog-process-error',e.message);}
  finally{blogControlBusy=false;await Promise.all([pollBlogProcess(),pollBlogBackend()]);renderBlogControls();}
}
$('blog-start').onclick=()=>controlBlog('start');$('blog-stop').onclick=()=>controlBlog('stop');
$('blog-stop-all').onclick=async()=>{
  if(blogStopAllBusy||blogControlBusy||blogBackendBusy)return;
  blogStopAllBusy=true;blogRevision++;renderBlogControls();notice('blog-process-error','');notice('blog-backend-error','');
  try{
    await Promise.allSettled([
      api('/api/blog/process/stop',{method:'POST'}).then(renderBlogProcess).catch(e=>notice('blog-process-error',e.message)),
      api('/api/blog/backend/stop',{method:'POST'}).then(renderBlogBackend).catch(e=>notice('blog-backend-error',e.message))
    ]);
  }finally{blogStopAllBusy=false;await Promise.all([pollBlogProcess(),pollBlogBackend()]);renderBlogControls();}
};
$('blog-sync').onclick=()=>controlBlog('sync');
const refreshBlogProcessLog=LogConsole.bind({button:$('blog-logs'),element:$('blog-process-log'),load:()=>api('/api/blog/process/logs'),isActive:()=>!document.hidden&&state.page==='blog',onError:e=>notice('blog-process-error',e.message)});
function renderBlogBackend(data){
  blogBackendStatus=data;
  const labels={STOPPED:'已停止',BUILDING:'构建中',STARTING:'启动中',RUNNING:'运行中',STOPPING:'停止中',FAILED:'失败',EXTERNAL:'端口已占用'};
  $('blog-backend-state').textContent=labels[data.state]||data.state;
  $('blog-backend-message').textContent=data.message+(data.pid?` · PID ${data.pid}`:'');
  $('blog-backend-url').textContent=data.url;$('blog-backend-distribution').textContent=data.distribution;
  renderBlogPreview();
}
async function pollBlogBackend(){if(blogBackendPollBusy||blogBackendBusy||blogStopAllBusy)return;blogBackendPollBusy=true;try{const data=await api('/api/blog/backend');if(!blogBackendBusy&&!blogStopAllBusy)renderBlogBackend(data);}catch(e){$('blog-backend-start').disabled=true;$('blog-backend-stop').disabled=true;notice('blog-backend-error',e.message);}finally{blogBackendPollBusy=false;}}
async function controlBlogBackend(action){if(blogBackendBusy||blogStopAllBusy)return;blogBackendBusy=true;renderBlogControls();notice('blog-backend-error','');try{renderBlogBackend(await api('/api/blog/backend/'+action,{method:'POST'}));}catch(e){notice('blog-backend-error',e.message);}finally{blogBackendBusy=false;await pollBlogBackend();renderBlogControls();}}
$('blog-backend-start').onclick=()=>controlBlogBackend('start');$('blog-backend-stop').onclick=()=>controlBlogBackend('stop');
const refreshBlogBackendLog=LogConsole.bind({button:$('blog-backend-logs'),element:$('blog-backend-log'),load:()=>api('/api/blog/backend/logs'),isActive:()=>!document.hidden&&state.page==='blog',onError:e=>notice('blog-backend-error',e.message)});
function refreshBlogLogs(){refreshBlogProcessLog();refreshBlogBackendLog();}
setInterval(refreshBlogLogs,1000);
document.addEventListener('visibilitychange',refreshBlogLogs);
window.addEventListener('hashchange',refreshBlogLogs);
$('blog-view-home').onclick=()=>{blogView='home';renderBlogPreview();};$('blog-view-knowledge').onclick=()=>{blogView='knowledge';renderBlogPreview();};
try{$('blog-settings').open=localStorage.getItem('blog-settings-open')==='true';}catch{}
$('blog-settings').addEventListener('toggle',()=>{try{localStorage.setItem('blog-settings-open',String($('blog-settings').open));}catch{}});
window.addEventListener('hashchange',()=>{if(state.page==='blog'){pollBlogProcess();pollBlogBackend();}});
setInterval(()=>{if(!document.hidden&&state.page==='blog'){pollBlogProcess();pollBlogBackend();}},2000);
pollBlogProcess();pollBlogBackend();
