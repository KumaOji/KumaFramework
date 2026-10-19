const {test}=require('node:test');
const assert=require('node:assert/strict');
const fs=require('node:fs');
const os=require('node:os');
const path=require('node:path');
const {spawnSync}=require('node:child_process');

function fixture(t){
  const root=fs.mkdtempSync(path.join(os.tmpdir(),'knowledge-cache-'));
  t.after(()=>{assert.equal(path.dirname(root),os.tmpdir());assert.ok(path.basename(root).startsWith('knowledge-cache-'));fs.rmSync(root,{recursive:true,force:true});});
  const blog=path.join(root,'KumaBlog'),knowledge=path.join(root,'KumaKnowledge');
  for(const dir of [path.join(blog,'scripts'),path.join(knowledge,'node_modules'),path.join(knowledge,'knowledge'),path.join(knowledge,'content')])fs.mkdirSync(dir,{recursive:true});
  fs.copyFileSync(path.resolve(__dirname,'../../../kuma-blog-fronted/KumaBlog/scripts/sync-knowledge.mjs'),path.join(blog,'scripts/sync-knowledge.mjs'));
  fs.writeFileSync(path.join(knowledge,'quartz.config.ts'),'export default {}');
  fs.writeFileSync(path.join(knowledge,'content/test.md'),'First version');
  fs.writeFileSync(path.join(knowledge,'knowledge/bootstrap-cli.mjs'),`
    import fs from 'node:fs';import path from 'node:path';
    if(process.env.FAIL_KNOWLEDGE_TEST==='1')process.exit(7);
    const counter='counter.txt';fs.writeFileSync(counter,String((fs.existsSync(counter)?Number(fs.readFileSync(counter)):0)+1));
    const output=process.argv[process.argv.indexOf('-o')+1];
    for(const file of ['index.html','index.css','static/contentIndex.json','lazy/graph.js']){const target=path.join(output,file);fs.mkdirSync(path.dirname(target),{recursive:true});fs.writeFileSync(target,'built');}
  `);
  return {blog,knowledge,run:env=>spawnSync(process.execPath,[path.join(blog,'scripts/sync-knowledge.mjs')],{cwd:blog,encoding:'utf8',env:{...process.env,KNOWLEDGE_ROOT:knowledge,KNOWLEDGE_FORCE_BUILD:'0',...env}}),count:()=>Number(fs.readFileSync(path.join(knowledge,'counter.txt')))};
}
test('unchanged knowledge reuses output; content changes and missing output rebuild',t=>{
  const f=fixture(t);assert.equal(f.run().status,0);assert.equal(f.count(),1);
  const cached=f.run();assert.equal(cached.status,0);assert.match(cached.stdout,/跳过构建/);assert.equal(f.count(),1);
  fs.writeFileSync(path.join(f.knowledge,'content/test.md'),'Second version');assert.equal(f.run().status,0);assert.equal(f.count(),2);
  fs.unlinkSync(path.join(f.blog,'public/knowledge/index.html'));assert.equal(f.run().status,0);assert.equal(f.count(),3);
});
test('failed build does not mark updated content as cached, and force build bypasses cache',t=>{
  const f=fixture(t);assert.equal(f.run().status,0);
  fs.writeFileSync(path.join(f.knowledge,'content/test.md'),'New content');
  assert.equal(f.run({FAIL_KNOWLEDGE_TEST:'1'}).status,7);
  assert.equal(f.run().status,0);assert.equal(f.count(),2);
  assert.equal(f.run({KNOWLEDGE_FORCE_BUILD:'1'}).status,0);assert.equal(f.count(),3);
});
