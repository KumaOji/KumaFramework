'use strict';
(() => {
  const select=document.getElementById('public-panel-select');
  const frame=document.getElementById('public-panel-preview');
  const name=document.getElementById('public-panel-name');
  const address=document.getElementById('public-panel-url');
  const status=document.getElementById('public-panel-status');
  const storageKey='kuma-public-panels-v1';
  const catalogKey='kuma-public-panels-catalog-version';
  const blogPanels=[
    {name:'Blog',url:'http://localhost:4321'},
    {name:'知识仓库',url:'http://localhost:4321/knowledge'}
  ];
  function feedback(text){status.textContent=text;status.hidden=!text;}
  const additions=[
    {name:'n8n · 工作流自动化',url:'http://localhost:5678',externalOnly:true},
    {name:'Nacos · 配置与注册中心',url:'http://localhost:8848/nacos/',externalOnly:true},
    {name:'Qdrant · 向量数据库',url:'http://localhost:6333/dashboard',externalOnly:true},
    {name:'Grafana · 总览',url:'http://localhost:3000/d/kuma-overview'},
    {name:'Grafana · 监控组件',url:'http://localhost:3000/d/kuma-monitoring'},
    {name:'Grafana · 日志',url:'http://localhost:3000/d/kuma-logs'},
    {name:'Grafana · 调用链',url:'http://localhost:3000/d/kuma-traces'},
    {name:'Grafana · Gateway',url:'http://localhost:3000/d/kuma-gateway'},
    {name:'Grafana · Reference',url:'http://localhost:3000/d/kuma-reference'}
  ];
  const defaults=[
    {name:'Grafana · Kuma Services',url:'http://localhost:3000/d/kuma-services'},
    {name:'Grafana · 日志与链路 Explore',url:'http://localhost:3000/explore'},
    {name:'Prometheus',url:'http://localhost:9090'},
    {name:'Alertmanager',url:'http://localhost:9093'},
    {name:'SkyWalking',url:'http://localhost:12880'},
    ...additions,...blogPanels
  ];
  function validUrl(value){
    const url=new URL(value);
    if(!['http:','https:'].includes(url.protocol)||url.username||url.password)throw Error('请输入不含用户名密码的 HTTP 或 HTTPS 地址');
    if(url.origin===location.origin)throw Error('请使用其他服务的地址，避免嵌入控制台自身');
    return url.href;
  }
  let panels=defaults,selected=0;
  try{
    const saved=JSON.parse(localStorage.getItem(storageKey)||'null');
    if(Array.isArray(saved))panels=saved.filter(p=>{
      try{return p&&typeof p.name==='string'&&p.name.trim()&&typeof p.url==='string'&&validUrl(p.url);}catch{return false;}
    }).slice(0,100);
    const version=Number(localStorage.getItem(catalogKey)||0);
    if(version<5){
      if(Array.isArray(saved)){
        for(const panel of [...(version<2?additions:[]),...(version<3?blogPanels:[])]){
          if(panels.length>=100)break;
          if(!panels.some(p=>new URL(p.url).href===new URL(panel.url).href||p.name===panel.name))panels.push(panel);
        }
        // Nacos 2.x serves relative assets; the context directory needs its slash.
        for(const panel of panels){
          const url=new URL(panel.url);
          if(version<4&&url.port==='8848'&&['/nacos','/nacos/'].includes(url.pathname)){url.pathname='/nacos/';panel.url=url.href;panel.externalOnly=true;}
          if(url.port==='5678')panel.externalOnly=true;
        }
        localStorage.setItem(storageKey,JSON.stringify(panels));
      }
      localStorage.setItem(catalogKey,'5');
    }
  }catch{feedback('保存的配置无法读取，已加载默认入口');}
  function persist(){
    try{localStorage.setItem(storageKey,JSON.stringify(panels));feedback('面板地址已保存在当前客户端');}
    catch{feedback('当前面板可用，但地址未能保存');}
  }
  function active(){return state.page==='projects'&&state.servicesTab==='panels';}
  function preview(force=false){
    const panel=panels[selected];
    const external=panel?.externalOnly===true;
    const ready=active()&&!!panel&&!external;
    frame.hidden=!ready;
    const empty=document.getElementById('public-panel-empty');
    empty.hidden=!!panel&&!external;
    empty.textContent=external?'此面板使用新窗口展示，请点击右上角「新窗口打开」访问。':'填写名称和 URL，保存后在这里查看面板。';
    if(ready&&(force||frame.dataset.url!==panel.url)){
      frame.title=panel.name;frame.src=panel.url;frame.dataset.url=panel.url;
    }
    if((!ready)&&frame.hasAttribute('src')){frame.removeAttribute('src');delete frame.dataset.url;}
  }
  function render(){
    select.replaceChildren(...panels.map((p,i)=>new Option(p.name,String(i))));
    select.value=String(selected);
    const panel=panels[selected];name.value=panel?.name||'';address.value=panel?.url||'';
    document.getElementById('public-panel-mode').value=panel?.externalOnly?'external':'embed';
    const open=document.getElementById('public-panel-open');
    open.hidden=!panel;if(panel)open.href=panel.url;else open.removeAttribute('href');
    document.getElementById('public-panel-delete').disabled=!panel;
    document.getElementById('public-panel-reload').disabled=!panel||panel?.externalOnly===true;
    if(!panel){frame.removeAttribute('src');delete frame.dataset.url;}
    preview();
  }
  select.onchange=()=>{selected=Number(select.value);feedback('');render();};
  document.getElementById('public-panel-new').onclick=()=>{
    selected=-1;render();feedback('');name.focus();
  };
  document.getElementById('public-panel-form').onsubmit=event=>{
    event.preventDefault();
    try{
      if(!name.value.trim())throw Error('请填写面板名称');
      const panel={name:name.value.trim().slice(0,100),url:validUrl(address.value.trim()),externalOnly:document.getElementById('public-panel-mode').value==='external'};
      if(selected<0){if(panels.length>=100)throw Error('最多保存 100 个面板');panels.push(panel);selected=panels.length-1;}
      else panels[selected]=panel;
      render();persist();
    }catch(e){feedback(e.message);}
  };
  document.getElementById('public-panel-delete').onclick=()=>{
    if(!panels[selected])return;
    panels.splice(selected,1);selected=panels.length?Math.max(0,selected-1):-1;render();persist();
  };
  document.getElementById('public-panel-reload').onclick=()=>{feedback('');preview(true);};
  function selectUrl(value,label){
    const url=validUrl(value);
    let index=panels.findIndex(p=>new URL(p.url).href===url);
    if(index<0){if(panels.length>=100)throw Error('最多保存 100 个面板');panels.push({name:label||new URL(url).host,url});index=panels.length-1;persist();}
    selected=index;feedback('');render();
  }
  window.PublicPanels={open(url,label){try{selectUrl(url,label);location.hash='projects/panels';preview();}catch(e){feedback(e.message);}}};
  function route(){if(['blog','projects/blog'].includes(location.hash.slice(1)))selectUrl(blogPanels[0].url,blogPanels[0].name);else preview();}
  window.addEventListener('hashchange',route);
  render();
  route();
})();
