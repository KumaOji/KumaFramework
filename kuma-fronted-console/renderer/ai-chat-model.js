(function(root){
  'use strict';
  function streamParser(onEvent){
    let pending='';
    return chunk=>{
      pending+=chunk;pending=pending.replace(/\r\n/g,'\n');
      let boundary;
      while((boundary=pending.indexOf('\n\n'))>=0){
        const frame=pending.slice(0,boundary);pending=pending.slice(boundary+2);
        let event='message';const data=[];
        for(const line of frame.split('\n')){
          if(line.startsWith('event:'))event=line.slice(6).trim();
          if(line.startsWith('data:'))data.push(line.slice(5).trimStart());
        }
        if(data.length)onEvent(event,JSON.parse(data.join('\n')));
      }
    };
  }
  function messages(chat){
    const items=chat.messages.filter(m=>m.role==='user'||(m.role==='assistant'&&m.content&&!m.error&&!m.interrupted))
      .map(({role,content})=>({role,content}));
    if(chat.system?.trim())items.unshift({role:'system',content:chat.system});return items;
  }
  function restore(raw){
    if(!Array.isArray(raw))return [];
    return raw.filter(c=>c&&typeof c.id==='string'&&typeof c.title==='string'&&Array.isArray(c.messages))
      .map(c=>({...c,model:['deepseek-chat','deepseek-reasoner'].includes(c.model)?c.model:'deepseek-chat',
        system:typeof c.system==='string'?c.system:'',draft:typeof c.draft==='string'?c.draft:'',
        messages:c.messages.filter(m=>m&&['user','assistant'].includes(m.role)&&typeof m.content==='string')
          .map(m=>({...m,reasoning:typeof m.reasoning==='string'?m.reasoning:'',interrupted:m.pending||m.interrupted,pending:false}))}));
  }
  const api={streamParser,messages,restore};
  if(typeof module!=='undefined'&&module.exports)module.exports=api;
  root.AiChatModel=api;
})(globalThis);
