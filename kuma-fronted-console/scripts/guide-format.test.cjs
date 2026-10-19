const {test}=require('node:test');
const assert=require('node:assert/strict');
const guide=require('../renderer/guide-format.js');

test('lesson view escapes HTML and code and refuses executable or credential URLs',()=>{
  const html=guide.render('<img src=x onerror=alert(1)>\n\n```html\n<script>alert(1)</script>\n```\n\n[bad](javascript:alert) [private](http://user:secret@example.com) [docs](https://example.com/docs)');
  assert.ok(!html.includes('<img')&&!html.includes('<script>'));
  assert.ok(html.includes('&lt;script&gt;'));
  assert.ok(!html.includes('href="javascript:')&&!html.includes('href="http://user:secret'));
  assert.ok(html.includes('rel="noopener noreferrer"'));
});

test('lesson headings, commands and tables remain readable without interpreting code Markdown',()=>{
  const html=guide.render('# ES\r\n\r\n## Run\r\n```powershell\r\nWrite-Output "**literal**"\r\n```\r\n\r\n| Type | Result |\r\n|---|---|\r\n| `keyword` | **exact** |\r\n\r\n1. First\r\n2. Second');
  assert.ok(html.includes('<h2>ES</h2>')&&html.includes('<h3>Run</h3>'));
  assert.ok(html.includes('&quot;**literal**&quot;'));
  assert.ok(html.includes('<table>')&&html.includes('<td><code>keyword</code></td>'));
  assert.ok(html.includes('<strong>exact</strong>')&&html.includes('<ol><li>First</li><li>Second</li></ol>'));
});
