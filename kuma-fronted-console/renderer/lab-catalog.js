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
  function groups(entries, query = '', kind = 'all') {
    const grouped = new Map();
    const search = query.trim().toLowerCase();
    entries.forEach((entry,index) => {
      if (kind !== 'all' && (entry.kind || 'api') !== kind) return;
      const text = [entry.group,entry.name,entry.path,entry.runtime,entry.description,entry.mainClass,
        ...(entry.topics || []),...(entry.sources || []),
        ...uiLinks(entry).flatMap(link => [link.name,link.location])].join(' ').toLowerCase();
      if (!text.includes(search)) return;
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
  const model = {key,merge,groups,uiLinks};
  if (typeof module !== 'undefined' && module.exports) module.exports = model;
  root.LabCatalog = model;
})(typeof globalThis === 'undefined' ? this : globalThis);
