'use strict';
(() => {
  const prompt=document.getElementById('ai-prompt');
  const status=document.getElementById('ai-draft-status');
  const templates={
    code:'请解释以下代码的执行流程、关键设计和边界条件。\n\n代码：\n\n希望重点理解：\n',
    debug:'请根据以下信息分析问题，区分已知事实与推测，并给出可验证的排查步骤。\n\n预期行为：\n实际行为：\n复现步骤：\n相关代码或日志：\n',
    learning:'请围绕以下知识点设计一个可重复的学习实验，包含前置条件、操作步骤、预期结果和验证方法。\n\n知识点：\n当前理解：\n运行环境：\n'
  };
  const key='kuma-ai-prompt-draft';
  try{prompt.value=localStorage.getItem(key)||'';if(prompt.value)status.textContent='已恢复本机草稿';}catch{status.textContent='无法读取本机草稿，仍可编辑和复制';}
  function save(){
    try{if(prompt.value)localStorage.setItem(key,prompt.value);else localStorage.removeItem(key);status.textContent=prompt.value?'草稿已保存在当前客户端':'草稿已清空';}
    catch{status.textContent='草稿未能保存，请复制保留内容';}
  }
  prompt.addEventListener('input',save);
  document.querySelectorAll('[data-ai-template]').forEach(button=>button.onclick=()=>{
    const template=templates[button.dataset.aiTemplate];
    // Keep existing notes when selecting another starting point.
    prompt.value=prompt.value.trim()?prompt.value+'\n\n'+template:template;
    save();prompt.focus();
  });
  document.getElementById('ai-clear').onclick=()=>{prompt.value='';save();prompt.focus();};
  document.getElementById('ai-copy').onclick=async()=>{
    if(!prompt.value.trim()){status.textContent='请先输入问题或选择模板';prompt.focus();return;}
    try{await navigator.clipboard.writeText(prompt.value);status.textContent='提示词已复制';}
    catch{prompt.focus();prompt.select();status.textContent='无法自动复制，内容已选中，请按 Ctrl+C 复制';}
  };
  document.getElementById('ai-open-vector').onclick=()=>{
    const index=presets.findIndex(entry=>entry.path==='/lab/vector/scenario');
    if(index<0){status.textContent='实验目录尚未加载，请稍后重试';return;}
    selectPreset(index);location.hash='lab';
  };
})();
