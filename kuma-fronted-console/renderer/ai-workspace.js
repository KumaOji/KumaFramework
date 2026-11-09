'use strict';
(() => {
  const $=id=>document.getElementById(id),model=globalThis.AiChatModel;
  const storageKey='kuma-ai-conversations-v1',settingsKey='kuma-ai-preferences-v1';
  let chats=[],preferences={},activeId,aborter=null,connected=false,storageWarning='',renderQueued=false,requestId=null;
  const status=text=>{$('ai-draft-status').textContent=text;};
  try{chats=model.restore(JSON.parse(localStorage.getItem(storageKey)||'[]'));preferences=JSON.parse(localStorage.getItem(settingsKey)||'{}')||{};}
  catch{storageWarning='无法恢复本机历史；新消息仍可发送和导出';}
  function save(){try{localStorage.setItem(storageKey,JSON.stringify(chats));}catch{storageWarning='本机存储空间不足，当前对话尚未保存，请导出保留';status(storageWarning);}}
  function current(){return chats.find(chat=>chat.id===activeId);}
  function node(tag,className,text){const el=document.createElement(tag);if(className)el.className=className;if(text!==undefined)el.textContent=text;return el;}
  function action(label,handler){const el=node('button','button small',label);el.type='button';el.onclick=handler;return el;}
  function newChat(){
    if(aborter)return;
    const chat={id:crypto.randomUUID(),title:'新对话',model:preferences.model||'deepseek-chat',system:preferences.system||'',messages:[],draft:'',updated:Date.now()};
    chats.unshift(chat);activeId=chat.id;save();render();$('ai-prompt').focus();
  }
  function sidebar(){
    const target=$('ai-conversations');target.replaceChildren();const query=$('ai-search').value.toLocaleLowerCase();
    for(const chat of chats.filter(c=>(c.title+' '+c.messages.map(m=>m.content).join(' ')).toLocaleLowerCase().includes(query))){
      const button=node('button','ai-conversation'+(chat.id===activeId?' selected':''),chat.title);
      button.type='button';button.title=chat.title;button.setAttribute('aria-current',String(chat.id===activeId));button.disabled=!!aborter;
      button.onclick=()=>{activeId=chat.id;render();};target.append(button);
    }
  }
  async function copy(text){try{await navigator.clipboard.writeText(text);status('已复制');}catch{status('复制失败，请选择文字后复制');}}
  function renderMessages(){
    const target=$('ai-messages'),chat=current(),atBottom=target.scrollHeight-target.scrollTop-target.clientHeight<100,previousScroll=target.scrollTop;
    const expanded=new Set([...target.querySelectorAll('.ai-reasoning[open]')].map(el=>el.dataset.index));
    target.replaceChildren();
    if(!chat.messages.length){
      const welcome=node('div','ai-welcome');welcome.append(node('div','ai-welcome-mark','✧'),node('h2','','今天想聊些什么？'),node('p','muted','从一个问题开始，把想法一步步展开。'));
      const suggestions=node('div','ai-suggestions');
      for(const text of ['解释一段代码的执行流程','根据日志设计排查步骤','设计一个可重复的技术实验'])suggestions.append(action(text,()=>{$('ai-prompt').value=text;chat.draft=text;save();$('ai-prompt').focus();}));
      welcome.append(suggestions);target.append(welcome);return;
    }
    chat.messages.forEach((message,index)=>{
      const row=node('article','ai-message '+message.role);row.append(node('div','ai-avatar',message.role==='user'?'你':'✧'));
      const body=node('div','ai-message-body');body.append(node('strong','ai-message-name',message.role==='user'?'你':'DeepSeek'));
      if(message.reasoning){const details=node('details','ai-reasoning');details.dataset.index=String(index);details.open=expanded.has(String(index));details.append(node('summary','',message.pending?'正在思考…':'思考过程'),node('div','',message.reasoning));body.append(details);}
      const content=node('div','ai-markdown');
      if(message.role==='user')content.textContent=message.content;
      else content.innerHTML=GuideFormat.render(message.content||(message.pending?'正在等待回复…':''));
      content.querySelectorAll('.guide-code').forEach(block=>block.prepend(action('复制代码',()=>copy(block.querySelector('code').textContent))));body.append(content);
      if(message.error)body.append(node('p','ai-message-error',message.error));
      if(message.interrupted)body.append(node('p','muted','已停止生成'));
      if(message.notice)body.append(node('p','muted',message.notice));
      if(!message.pending){
        const tools=node('div','ai-message-tools');tools.append(action('复制',()=>copy(message.content)));
        if(index===chat.messages.length-1&&message.role==='assistant')tools.append(action('重新生成',()=>generate(true)));
        if(message.role==='user'&&(index===chat.messages.length-1||index===chat.messages.length-2))tools.append(action('编辑',()=>{
          if(aborter)return;chat.draft=message.content;chat.messages.splice(index);save();render();$('ai-prompt').focus();
        }));
        tools.querySelectorAll('button').forEach(b=>b.disabled=!!aborter);body.append(tools);
      }
      row.append(body);target.append(row);
    });
    target.scrollTop=atBottom?target.scrollHeight:previousScroll;
  }
  function queueRender(){if(renderQueued)return;renderQueued=true;requestAnimationFrame(()=>{renderQueued=false;renderMessages();});}
  function render(){
    const chat=current();if(!chat)return;
    $('ai-title').textContent=chat.title;$('ai-model').value=chat.model;$('ai-prompt').value=chat.draft||'';
    for(const id of ['ai-new','ai-model','ai-delete','ai-rename'])$(id).disabled=!!aborter;
    $('ai-send').disabled=!!aborter||!connected;$('ai-stop').hidden=!aborter;$('ai-prompt').disabled=!!aborter;sidebar();renderMessages();
  }
  async function checkConnection(){
    try{const response=await fetch('/api/ai/status');if(!response.ok)throw Error();const data=await response.json();connected=data.configured===true;
      $('ai-connection').textContent=connected?'已配置 · DeepSeek API':'未配置 · 打开连接设置';
      $('ai-api-key').disabled=['environment','gradle'].includes(data.source);
      $('ai-api-key').placeholder=data.source==='environment'?'由 DEEPSEEK_API_KEY 环境变量提供':data.source==='gradle'?'由 gradle.properties 的 deepseek.api.key 提供':connected?'已配置，输入新密钥可替换':'sk-…';
    }catch{connected=false;$('ai-connection').textContent='连接状态读取失败';}render();
  }
  async function generate(retry=false){
    if(aborter)return;const chat=current();
    if(!connected){$('ai-system-prompt').value=preferences.system||'';$('ai-settings-dialog').showModal();return;}
    if(retry){if(chat.messages.at(-1)?.role!=='assistant')return;chat.messages.pop();}
    else{const text=$('ai-prompt').value.trim();if(!text)return;chat.messages.push({role:'user',content:text});chat.draft='';
      if(chat.messages.filter(m=>m.role==='user').length===1)chat.title=text.replace(/\s+/g,' ').slice(0,32);}
    const outgoing=model.messages(chat),reply={role:'assistant',content:'',reasoning:'',pending:true};chat.messages.push(reply);
    const controller=new AbortController();aborter=controller;requestId=crypto.randomUUID();chat.updated=Date.now();save();render();status('正在生成…');let completed=false;
    try{
      const response=await fetch('/api/ai/chat?requestId='+requestId,{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({model:chat.model,messages:outgoing}),signal:controller.signal});
      if(!response.ok){const body=await response.json().catch(()=>({}));throw Error(body.error||'请求失败（HTTP '+response.status+'）');}
      if(!response.headers.get('content-type')?.includes('text/event-stream'))throw Error('服务器未返回流式回复');
      const reader=response.body.getReader(),decoder=new TextDecoder();
      const parse=model.streamParser((event,data)=>{
        if(event==='delta')reply.content+=data.text;if(event==='reasoning')reply.reasoning+=data.text;
        if(event==='error')throw Error(data.message);if(event==='notice')reply.notice=data.message;if(event==='done')completed=true;queueRender();
      });
      try{while(true){const {value,done}=await reader.read();if(done)break;parse(decoder.decode(value,{stream:true}));}parse(decoder.decode());}
      finally{await reader.cancel().catch(()=>{});reader.releaseLock();}
      if(!completed)throw Error('连接提前结束，请重新生成');if(!reply.content)throw Error('模型没有返回正文，请重新生成');status(reply.notice||'回复完成');
    }catch(error){if(error.name==='AbortError'){reply.interrupted=true;status('已停止生成');}else{reply.error=error.message;status(error.message);}}
    finally{controller.abort();reply.pending=false;aborter=null;requestId=null;save();render();if(storageWarning)status(storageWarning);}
  }
  $('ai-composer').onsubmit=event=>{event.preventDefault();generate();};
  $('ai-prompt').onkeydown=event=>{if(event.key==='Enter'&&!event.shiftKey&&!event.isComposing){event.preventDefault();generate();}};
  $('ai-prompt').oninput=()=>{current().draft=$('ai-prompt').value;save();};$('ai-stop').onclick=()=>{if(requestId)fetch('/api/ai/cancel/'+requestId,{method:'POST'}).catch(()=>{});aborter?.abort();};$('ai-new').onclick=newChat;$('ai-search').oninput=sidebar;
  $('ai-model').onchange=()=>{current().model=$('ai-model').value;preferences.model=current().model;save();savePreferences();};
  $('ai-rename').onclick=()=>{$('ai-rename-input').value=current().title;$('ai-rename-dialog').showModal();};
  $('ai-rename-form').onsubmit=event=>{event.preventDefault();const title=$('ai-rename-input').value.trim();if(title){current().title=title;save();render();$('ai-rename-dialog').close();}};
  $('ai-rename-cancel').onclick=()=>$('ai-rename-dialog').close();
  $('ai-delete').onclick=()=>{if(!window.confirm('删除这段本机对话？'))return;chats=chats.filter(c=>c.id!==activeId);if(chats.length){activeId=chats[0].id;save();render();}else newChat();};
  $('ai-export').onclick=()=>{const url=URL.createObjectURL(new Blob([JSON.stringify(current(),null,2)],{type:'application/json'}));const link=node('a');link.href=url;link.download='deepseek-chat-'+current().id+'.json';link.click();setTimeout(()=>URL.revokeObjectURL(url),1000);};
  function savePreferences(){try{localStorage.setItem(settingsKey,JSON.stringify(preferences));}catch{status('偏好设置未能保存');}}
  $('ai-settings').onclick=()=>{$('ai-system-prompt').value=preferences.system||'';$('ai-settings-status').textContent='';$('ai-settings-dialog').showModal();};
  $('ai-settings-close').onclick=()=>{$('ai-api-key').value='';$('ai-settings-dialog').close();};$('ai-settings-dialog').addEventListener('close',()=>{$('ai-api-key').value='';});
  $('ai-connection-form').onsubmit=async event=>{
    event.preventDefault();const button=$('ai-save-settings');button.disabled=true;
    try{const key=$('ai-api-key').value.trim();
      if(key){const response=await fetch('/api/ai/connection',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({apiKey:key})});const data=await response.json();if(!response.ok)throw Error(data.error||'密钥保存失败');}
      preferences.system=$('ai-system-prompt').value;savePreferences();if(!current().messages.length){current().system=preferences.system;save();}
      $('ai-api-key').value='';$('ai-settings-status').textContent='设置已保存';await checkConnection();
    }catch(error){$('ai-settings-status').textContent=error.message;}finally{button.disabled=false;}
  };
  if(chats.length){activeId=chats[0].id;render();}else{newChat();try{current().draft=localStorage.getItem('kuma-ai-prompt-draft')||'';save();render();}catch{}}
  if(storageWarning)status(storageWarning);checkConnection();
})();
