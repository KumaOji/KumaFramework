const {test}=require('node:test');
const assert=require('node:assert/strict');
const fs=require('node:fs');
const path=require('node:path');
const model=require('../renderer/lab-catalog.js');
const renderer=path.resolve(__dirname,'../renderer');

test('sync refreshes labels while preserving curated request parameters and learning entries',()=>{
  const entries=[{group:'old',name:'old',method:'GET',path:'/lab/test?limit=100',body:'curated'},
    {kind:'learning',id:'jdk-25',group:'JDK',name:'JDK25',runtime:'JDK25'}];
  assert.equal(model.merge(entries,[{method:'GET',path:'/lab/test',group:'new',name:'updated',body:'generated'}]),0);
  assert.equal(entries[0].name,'updated');
  assert.equal(entries[0].path,'/lab/test?limit=100');
  assert.equal(entries[0].body,'curated');
  assert.equal(entries[1].id,'jdk-25');
  assert.equal(model.merge(entries,[{method:'POST',path:'/lab/other',group:'new',name:'other',body:''}]),1);
  assert.equal(model.groups(entries,'','api').length,1);
});

test('all lesson cards have existing sources, mirrored docs, commands and searchable runtime/topics',()=>{
  const cards=JSON.parse(fs.readFileSync(path.join(renderer,'lab-learning.json'),'utf8'));
  const ids=new Set();
  for(const card of cards){
    assert.equal(card.kind,'learning');assert.ok(!ids.has(card.id));ids.add(card.id);
    assert.ok(card.commands && card.expected.length && card.topics.length);
    for(const source of card.sources)assert.ok(fs.existsSync(path.resolve(renderer,'../..',source)),source);
    const doc=fs.readFileSync(path.join(renderer,card.doc),'utf8');
    assert.equal(doc,fs.readFileSync(path.resolve(renderer,'../../kuma-project/kuma-project-lab/docs',path.basename(card.doc)),'utf8'));
  }
  for(const id of ['memory','webhook','socket','linux-memory','jdk-8','jdk-21','jdk-25','jdk25-preview',
    'otel-sdk','otel-skywalking','skywalking-agent','database-otel','database-skywalking',
    'middleware-loki','middleware-prometheus','middleware-alertmanager','middleware-otel','middleware-skywalking','middleware-grafana'])assert.ok(ids.has(id));
  assert.equal(model.groups(cards,'ScopedValue','learning').flatMap(group=>group.items).some(({entry})=>entry.id==='jdk-25'),true);
  assert.equal(model.groups(cards,'','api').length,0);
});

test('middleware UI links reject executable URLs and embedded credentials and support UI location search',()=>{
  const card={kind:'learning',id:'middleware',group:'monitoring',name:'Loki',uiLinks:[
    {name:'Explore',url:'http://localhost:3000/explore',location:'选择 Loki 数据源'},
    {url:'javascript:alert(1)'},{url:'http://admin:secret@localhost:3000'},{url:'/relative'}]};
  assert.equal(model.uiLinks(card).length,1);
  assert.equal(model.groups([card],'Explore','learning').length,1);
  assert.deepEqual(model.uiLinks({}),[]);
});
