(function(root) {
  'use strict';
  // A deliberately small Markdown view for repository lessons. Raw HTML is always escaped.
  const escape = value => String(value).replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
  function inline(text) {
    const pattern = /`([^`]+)`|\[([^\]]+)\]\(([^)\s]+)\)|\*\*([^*]+)\*\*/g;
    let output='', position=0;
    for (const match of text.matchAll(pattern)) {
      output+=escape(text.slice(position,match.index));
      if (match[1] !== undefined) output+=`<code>${escape(match[1])}</code>`;
      else if (match[2] !== undefined) {
        let safe=false;
        try { const url=new URL(match[3]);safe=['http:','https:'].includes(url.protocol)&&!url.username&&!url.password; } catch {}
        output+=safe?`<a href="${escape(match[3])}" target="_blank" rel="noopener noreferrer">${escape(match[2])} ↗</a>`:escape(match[2]);
      } else output+=`<strong>${escape(match[4])}</strong>`;
      position=match.index+match[0].length;
    }
    return output+escape(text.slice(position));
  }
  const cells = line => line.trim().replace(/^\|/, '').replace(/\|$/, '').split('|').map(cell=>cell.trim());
  function render(markdown) {
    const lines=String(markdown).replace(/^\uFEFF/,'').replace(/\r\n?/g,'\n').split('\n');
    const output=[];
    const special = line => /^\s*$|^```|^#{1,6}\s|^\s*(?:[-*]|\d+\.)\s/.test(line);
    for(let i=0;i<lines.length;) {
      const line=lines[i];
      if(!line.trim()){i++;continue;}
      if(line.startsWith('```')) {
        const language=line.slice(3).trim(),code=[];i++;
        while(i<lines.length&&!lines[i].startsWith('```'))code.push(lines[i++]);
        if(i<lines.length)i++;
        output.push(`<div class="guide-code"><span class="guide-code-language">${escape(language||'命令 / 代码')}</span><pre><code>${escape(code.join('\n'))}</code></pre></div>`);continue;
      }
      const heading=line.match(/^(#{1,6})\s+(.*)$/);
      if(heading){const level=Math.min(6,heading[1].length+1);output.push(`<h${level}>${inline(heading[2])}</h${level}>`);i++;continue;}
      if(line.includes('|')&&i+1<lines.length&&/^\s*\|?[\s:|-]+\|[\s:|-]*$/.test(lines[i+1])) {
        const headings=cells(line);i+=2;const rows=[];
        while(i<lines.length&&lines[i].includes('|')&&lines[i].trim())rows.push(cells(lines[i++]));
        output.push(`<div class="guide-table"><table><thead><tr>${headings.map(cell=>`<th>${inline(cell)}</th>`).join('')}</tr></thead><tbody>${rows.map(row=>`<tr>${row.map(cell=>`<td>${inline(cell)}</td>`).join('')}</tr>`).join('')}</tbody></table></div>`);continue;
      }
      const list=line.match(/^\s*([-*]|\d+\.)\s+(.*)$/);
      if(list){const ordered=/\d/.test(list[1]),tag=ordered?'ol':'ul',items=[];
        while(i<lines.length){const item=lines[i].match(/^\s*([-*]|\d+\.)\s+(.*)$/);if(!item||/\d/.test(item[1])!==ordered)break;items.push(`<li>${inline(item[2])}</li>`);i++;}
        output.push(`<${tag}>${items.join('')}</${tag}>`);continue;
      }
      const paragraph=[line];i++;
      while(i<lines.length&&!special(lines[i])&&!(lines[i].includes('|')&&i+1<lines.length&&/^\s*\|?[\s:|-]+\|[\s:|-]*$/.test(lines[i+1])))paragraph.push(lines[i++]);
      output.push(`<p>${inline(paragraph.join(' '))}</p>`);
    }
    return output.join('\n');
  }
  const format={render};
  if(typeof module!=='undefined'&&module.exports)module.exports=format;
  root.GuideFormat=format;
})(typeof globalThis==='undefined'?this:globalThis);
