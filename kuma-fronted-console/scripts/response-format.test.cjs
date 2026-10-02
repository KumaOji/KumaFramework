const test = require('node:test');
const assert = require('node:assert/strict');
const {formatResponse} = require('../renderer/response-format.js');
test('renders nested SQL newlines and preserves raw JSON mode',()=>{
  const sql='CREATE TABLE t (\n    id BIGINT\n);\n';
  const body=JSON.stringify({payload:{data:[{syntax:sql}]}});
  assert.ok(formatResponse(body).includes(sql));
  assert.deepEqual(JSON.parse(formatResponse(body,false)),JSON.parse(body));
});
test('preserves literal escapes and never interprets HTML',()=>{
  const body=JSON.stringify({path:'C:\\new\\test',literal:'\\n',html:'<script>alert(1)</script>',tab:'a\tb'});
  const display=formatResponse(body);
  assert.ok(display.includes(JSON.stringify('C:\\new\\test')));
  assert.ok(display.includes(JSON.stringify('\\n')));
  assert.ok(display.includes('a\tb'));
  assert.equal(formatResponse('plain\ntext'),'plain\ntext');
});
