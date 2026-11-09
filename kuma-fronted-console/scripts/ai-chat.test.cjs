const {test}=require('node:test');
const assert=require('node:assert/strict');
const model=require('../renderer/ai-chat-model.js');
const format=require('../renderer/guide-format.js');
test('SSE buffers split JSON, Unicode and CRLF frames',()=>{
  const events=[],parse=model.streamParser((event,data)=>events.push({event,data}));
  const stream=': keepalive\r\n\r\nevent: delta\r\ndata: {"text":"你好🌸"}\r\n\r\nevent: done\ndata: {}\n\n';
  for(const character of stream)parse(character);
  assert.deepEqual(events,[{event:'delta',data:{text:'你好🌸'}},{event:'done',data:{}}]);
});
test('only successful assistant text is sent, with system prompt and prior turns',()=>{
  assert.deepEqual(model.messages({system:'中文',messages:[{role:'user',content:'a'},{role:'assistant',content:'b',reasoning:'private'},{role:'assistant',content:'partial',interrupted:true},{role:'assistant',content:'failed',error:'offline'},{role:'user',content:'c'}]}),[{role:'system',content:'中文'},{role:'user',content:'a'},{role:'assistant',content:'b'},{role:'user',content:'c'}]);
});
test('restoring an unfinished response marks it interrupted',()=>{
  const [chat]=model.restore([{id:'1',title:'test',messages:[{role:'assistant',content:'partial',pending:true},null]}]);
  assert.equal(chat.messages.length,1);assert.equal(chat.messages[0].interrupted,true);assert.equal(chat.messages[0].pending,false);
});
test('model Markdown cannot execute HTML or executable links',()=>{
  const html=format.render('<img src=x onerror=alert(1)>\n\n[x](javascript:alert)\n\n```html\n<script>alert(1)</script>\n```');
  assert.ok(!html.includes('<img'));assert.ok(!html.includes('<script>'));assert.ok(!html.includes('href="javascript:'));
});
