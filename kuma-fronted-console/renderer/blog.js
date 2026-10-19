'use strict';
let blogControlBusy=false, blogPollBusy=false, blogRevision=0;
let blogBackendBusy=false,blogBackendPollBusy=false,blogFrontendStatus=null,blogBackendStatus=null,blogView='home';
function renderBlogPreview(){
  const ready=!!blogFrontendStatus?.canOpen;
  $('blog-preview').hidden=!ready;$('blog-preview-empty').hidden=ready;
  if(ready){const url=blogView==='knowledge'?blogFrontendStatus.knowledgeUrl:blogFrontendStatus.url;if($('blog-preview').dataset.url!==url){$('blog-preview').src=url;$('blog-preview').dataset.url=url;}}
  else if($('blog-preview').hasAttribute('src')){$('blog-preview').removeAttribute('src');delete $('blog-preview').dataset.url;}
  $('blog-stop').disabled=blogControlBusy||!(blogFrontendStatus?.canStop||blogBackendStatus?.canStop);
  $('blog-settings-summary').textContent=`前端 ${blogFrontendStatus?.canOpen?'运行中':'未就绪'} · 后端 ${blogBackendStatus?.state==='RUNNING'?'运行中':'未就绪'}`;
}
function renderBlogProcess(data){
  blogFrontendStatus=data;
  const labels={STOPPED:'已停止',SYNCING:'同步知识仓库中',STARTING:'启动中',RUNNING:'运行中',STOPPING:'停止中',FAILED:'失败',EXTERNAL:'端口已占用'};
  $('blog-process-state').textContent=labels[data.state]||data.state;
  $('blog-process-state').className='status '+(data.state==='RUNNING'?'ok':data.state==='FAILED'?'bad':'');
  $('blog-process-message').textContent=data.message+(data.pid?` · PID ${data.pid}`:'');
  $('blog-start').disabled=blogControlBusy||!data.canStart;
  $('blog-stop').disabled=blogControlBusy||!data.canStop;
  $('blog-sync').disabled=blogControlBusy||!data.canSync;
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
  blogControlBusy=true;blogRevision++;$('blog-start').disabled=true;$('blog-stop').disabled=true;$('blog-sync').disabled=true;notice('blog-process-error','');
  try{
    if(action==='start'&&blogBackendStatus?.canStart)await api('/api/blog/backend/start',{method:'POST'});
    if(action==='stop'){await Promise.all([api('/api/blog/process/stop',{method:'POST'}),api('/api/blog/backend/stop',{method:'POST'})]);}
    else await api('/api/blog/process/'+action,{method:'POST'});
  }
  catch(e){notice('blog-process-error',e.message);}
  finally{blogControlBusy=false;await Promise.all([pollBlogProcess(),pollBlogBackend()]);}
}
$('blog-start').onclick=()=>controlBlog('start');$('blog-stop').onclick=()=>controlBlog('stop');
$('blog-sync').onclick=()=>controlBlog('sync');
$('blog-logs').onclick=async()=>{try{const data=await api('/api/blog/process/logs');$('blog-process-log').hidden=false;$('blog-process-log').textContent=data.body;}catch(e){notice('blog-process-error',e.message);}};
function renderBlogBackend(data){
  blogBackendStatus=data;
  const labels={STOPPED:'已停止',BUILDING:'构建中',STARTING:'启动中',RUNNING:'运行中',STOPPING:'停止中',FAILED:'失败',EXTERNAL:'端口已占用'};
  $('blog-backend-state').textContent=labels[data.state]||data.state;
  $('blog-backend-message').textContent=data.message+(data.pid?` · PID ${data.pid}`:'');
  $('blog-backend-start').disabled=blogBackendBusy||blogControlBusy||!data.canStart;
  $('blog-backend-stop').disabled=blogBackendBusy||blogControlBusy||!data.canStop;
  $('blog-backend-url').textContent=data.url;$('blog-backend-distribution').textContent=data.distribution;
  renderBlogPreview();
}
async function pollBlogBackend(){if(blogBackendPollBusy||blogBackendBusy)return;blogBackendPollBusy=true;try{renderBlogBackend(await api('/api/blog/backend'));}catch(e){$('blog-backend-start').disabled=true;$('blog-backend-stop').disabled=true;notice('blog-backend-error',e.message);}finally{blogBackendPollBusy=false;}}
async function controlBlogBackend(action){if(blogBackendBusy||blogControlBusy)return;blogBackendBusy=true;$('blog-backend-start').disabled=true;$('blog-backend-stop').disabled=true;notice('blog-backend-error','');try{await api('/api/blog/backend/'+action,{method:'POST'});}catch(e){notice('blog-backend-error',e.message);}finally{blogBackendBusy=false;await pollBlogBackend();}}
$('blog-backend-start').onclick=()=>controlBlogBackend('start');$('blog-backend-stop').onclick=()=>controlBlogBackend('stop');
$('blog-backend-logs').onclick=async()=>{try{const data=await api('/api/blog/backend/logs');$('blog-backend-log').hidden=false;$('blog-backend-log').textContent=data.body;}catch(e){notice('blog-backend-error',e.message);}};
$('blog-view-home').onclick=()=>{blogView='home';renderBlogPreview();};$('blog-view-knowledge').onclick=()=>{blogView='knowledge';renderBlogPreview();};
try{$('blog-settings').open=localStorage.getItem('blog-settings-open')==='true';}catch{}
$('blog-settings').addEventListener('toggle',()=>{try{localStorage.setItem('blog-settings-open',String($('blog-settings').open));}catch{}});
window.addEventListener('hashchange',()=>{if(state.page==='blog'){pollBlogProcess();pollBlogBackend();}});
setInterval(()=>{if(!document.hidden&&state.page==='blog'){pollBlogProcess();pollBlogBackend();}},2000);
pollBlogProcess();pollBlogBackend();
