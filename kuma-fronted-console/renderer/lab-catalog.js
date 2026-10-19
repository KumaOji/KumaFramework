(function(root) {
  'use strict';
  const key = entry => entry.kind === 'learning' ? `learning:${entry.id}` : `${entry.method}:${entry.path.split('?')[0]}`;
  function merge(entries, incoming) {
    let added = 0;
    for (const item of incoming) {
      const entry = {...item,kind:item.kind || 'api'};
      const index = entries.findIndex(existing => key(existing) === key(entry));
      if (index < 0) { entries.push(entry); added++; }
      else {
        const previous = entries[index];
        // Keep curated request examples and query values while refreshing labels/schema metadata.
        entries[index] = {...previous,...entry,
          ...(entry.kind === 'api' ? {path:previous.path,body:previous.body || entry.body || ''} : {})};
      }
    }
    return added;
  }
  const aliases=[['redis','缓存','键值'],['kafka','卡夫卡','消息队列'],['elasticsearch','es','搜索引擎'],['mysql','sql','数据库'],['transaction','事务','回滚'],['socket','套接字','网络'],['session','会话'],['memory','内存','jmm','jvm'],['lock','锁','并发'],['spring','ioc','依赖注入'],['otel','opentelemetry','链路追踪'],['jdk','java版本']];
  function matches(entry,query=''){
    const text=[entry.group,entry.name,entry.method,entry.path,entry.runtime,entry.description,entry.mainClass,
      ...(entry.topics||[]),...(entry.sources||[]),...uiLinks(entry).flatMap(link=>[link.name,link.location])].join(' ').toLowerCase();
    const words=text.split(/[^a-z0-9]+/);
    const contains=word=>/^[a-z]{1,2}$/.test(word)?words.some(part=>part.startsWith(word)):text.includes(word);
    return query.toLowerCase().trim().split(/\s+/).filter(Boolean).every(token=>{
      if(contains(token))return true;
      return aliases.some(words=>words.includes(token)&&words.some(contains));
    });
  }
  function groups(entries, query = '', kind = 'all') {
    const grouped = new Map();
    entries.forEach((entry,index) => {
      if (kind !== 'all' && (entry.kind || 'api') !== kind) return;
      if (!matches(entry,query)) return;
      if (!grouped.has(entry.group)) grouped.set(entry.group,[]);
      grouped.get(entry.group).push({entry,index});
    });
    return [...grouped].map(([name,items]) => ({name,items}));
  }
  function uiLinks(entry) {
    return (Array.isArray(entry.uiLinks) ? entry.uiLinks : []).filter(link => {
      try {
        const url = new URL(link.url);
        return ['http:','https:'].includes(url.protocol) && !url.username && !url.password;
      } catch { return false; }
    });
  }
  const model = {key,merge,groups,uiLinks,matches};
  if (typeof module !== 'undefined' && module.exports) module.exports = model;
  root.LabCatalog = model;
})(typeof globalThis === 'undefined' ? this : globalThis);
