const {test}=require('node:test');
const assert=require('node:assert/strict');
const fs=require('node:fs');
const path=require('node:path');
const model=require('../renderer/lab-catalog.js');
const renderer=path.resolve(__dirname,'../renderer');

test('audit lessons expose the runnable reports and all six HTTP entry points',()=>{
  const apis=require('../renderer/lab-catalog.json');
  const cards=require('../renderer/lab-learning.json');
  for(const lesson of ['scenario','success','failure','content','proxy','storage']){
    assert.ok(apis.some(entry=>entry.method==='POST'&&entry.path==='/lab/audit-log/'+lesson));
  }
  for(const lesson of ['all','content','proxy','storage']){
    const card=cards.find(entry=>entry.id==='audit-log-'+lesson);
    assert.ok(card);assert.equal(card.mainArgs,lesson);
    assert.ok(model.matches(card,'审计'));
  }
});

test('finder combines keywords and recognizes Chinese topics without matching ES inside unrelated words',()=>{
  const entries=[{group:'Redis',name:'写入字符串',path:'/lab/redis/string',method:'POST'},
    {group:'Elasticsearch',name:'全文搜索',path:'/lab/elasticsearch/search',method:'GET'},
    {kind:'learning',group:'Java',name:'Session lifecycle',sources:['src/session/Lesson.java']}];
  assert.equal(model.groups(entries,'Redis 写入').flatMap(group=>group.items).length,1);
  assert.equal(model.groups(entries,'缓存 写入').flatMap(group=>group.items).length,1);
  assert.deepEqual(model.groups(entries,'ES').map(group=>group.name),['Elasticsearch']);
  assert.equal(model.groups(entries,'Redis 搜索').length,0);
});

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

test('short English prefixes find real Alertmanager APIs and lessons while avoiding mid-word matches',()=>{
  const entries=[...require('../renderer/lab-catalog.json'),...require('../renderer/lab-learning.json')];
  for(const query of ['a','al','AL','alert','Alertmanager']){
    const matches=model.groups(entries,query).flatMap(group=>group.items.map(item=>item.entry));
    assert.ok(matches.some(entry=>entry.path==='/lab/monitoring/alertmanager'),query+' must find the API');
    assert.ok(matches.some(entry=>entry.id==='middleware-alertmanager'),query+' must find the lesson');
  }
  assert.equal(model.matches({name:'Session lifecycle',path:'/lab/session'},'es'),false);
  assert.equal(model.matches({name:'Elasticsearch',path:'/lab/elasticsearch'},'es'),true);
  assert.equal(model.matches({name:'Redis',path:'/lab/redis'},'re'),true);
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
    'middleware-loki','middleware-prometheus','middleware-alertmanager','middleware-otel','middleware-skywalking','middleware-grafana',
    'elasticsearch-all','elasticsearch-crud','elasticsearch-search','elasticsearch-aggregation',
    'socket-all','socket-lifecycle','session-all','session-isolation','session-lifecycle','session-expiry'])assert.ok(ids.has(id));
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
