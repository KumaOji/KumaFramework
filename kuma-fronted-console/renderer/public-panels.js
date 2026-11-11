'use strict';
(() => {
  const select=document.getElementById('public-panel-select');
  const frame=document.getElementById('public-panel-preview');
  const name=document.getElementById('public-panel-name');
  const address=document.getElementById('public-panel-url');
  const status=document.getElementById('public-panel-status');
  const storageKey='kuma-public-panels-v1';
  const catalogKey='kuma-public-panels-catalog-version';
  const grafanaHome={name:'Grafana',url:'http://localhost:3000/'};
  const blogPanels=[
    {name:'Blog',url:'http://localhost:4321'}
  ];
  function feedback(text){status.textContent=text;status.hidden=!text;}
  const additions=[
    {name:'n8n · 工作流自动化',url:'http://localhost:5678',externalOnly:true},
    {name:'Nacos · 配置与注册中心',url:'http://localhost:8848/nacos/',externalOnly:true},
    {name:'Qdrant · 向量数据库',url:'http://localhost:6333/dashboard',externalOnly:true}
  ];
  const defaults=[
    grafanaHome,
    {name:'Prometheus',url:'http://localhost:9090'},
    {name:'Alertmanager',url:'http://localhost:9093'},
    {name:'SkyWalking',url:'http://localhost:12880'},
    ...additions,...blogPanels
  ];
  let entries=[...defaults.map((entry,index)=>({...entry,id:['grafana','prometheus','alertmanager','skywalking','n8n','nacos','qdrant','blog-frontend'][index],group:index<4?'监控与告警':index<7?'WSL 应用':'本机项目'})),
    {id:'qinglong',name:'Life · 青龙',url:'http://localhost:5700',group:'WSL 应用',externalOnly:true},
    {id:'rsshub',name:'Life · RSSHub',url:'http://localhost:1200',group:'WSL 应用'},
    {id:'dsh',name:'DeepSeek Harness',url:'http://localhost:3080',group:'本机项目'},
    {id:'project-uaa',name:'UAA · API',url:'http://localhost:33336',group:'项目接口',externalOnly:true},
    {id:'project-gateway',name:'Gateway · API',url:'http://localhost:18080',group:'项目接口',externalOnly:true},
    {id:'project-lab',name:'Lab · API',url:'http://localhost:19090/api',group:'项目接口',externalOnly:true},
    {id:'project-blog',name:'Blog · API',url:'http://localhost:9000/api',group:'项目接口',externalOnly:true},
    {id:'loki',name:'Loki · API',url:'http://localhost:3100/ready',group:'采集接口',externalOnly:true},
    {id:'zipkin',name:'SkyWalking · Zipkin API',url:'http://localhost:9412/zipkin/api/v2/services',group:'采集接口',externalOnly:true},
    {id:'otlp',name:'OTLP · HTTP',url:'http://localhost:4318/v1/traces',group:'采集接口',externalOnly:true}];
  let dshStatus;
  function key(value){const url=new URL(value);if(['127.0.0.1','[::1]'].includes(url.hostname))url.hostname='localhost';return url.href.replace(/\/$/,'');}
  function panelUrl(panel,embedded=true){
    if(panel?.serviceId!=='dsh'||!dshStatus?.canOpen)return panel?.url;
    const url=new URL(dshStatus.url);if(url.protocol!=='http:'||!['localhost','127.0.0.1'].includes(url.hostname))throw Error('dsh 地址无效');
    // Embedded Strict cookies need the Console's site. External browsers keep
    // the launch host, where a previously opened dsh login session may exist.
    if(embedded&&location.hostname==='localhost')url.hostname='localhost';return url.href;
  }
  function renderDirectory(){
    const target=document.getElementById('service-entry-directory');if(!target)return;
    const search=document.getElementById('service-entry-search').value.trim().toLowerCase();
    const saved=panels.filter(panel=>!panel.temporary);
    const rows=entries.filter(entry=>!entry.readOnly&&!['项目接口','采集接口'].includes(entry.group)).map(entry=>({...entry,...saved.find(panel=>key(panel.url)===key(entry.url))}));
    const known=new Set(rows.map(entry=>key(entry.url)));
    rows.push(...saved.filter(panel=>!known.has(key(panel.url))).map(panel=>({...panel,group:'Custom'})));
    const filtered=rows.filter(entry=>[entry.name,entry.url,entry.group,entry.description].join(' ').toLowerCase().includes(search));
    target.replaceChildren();document.getElementById('service-entry-count').textContent=filtered.length+' / '+rows.length;
    for(const entry of filtered){
      const button=document.createElement('button');button.type='button';button.dataset.serviceEntry=entry.id||'custom';button.className='service-entry';
      button.textContent=entry.name.split(' \u00b7 ')[0];button.title=entry.name+'\n'+entry.url;
      button.setAttribute('aria-label',entry.name);button.setAttribute('aria-pressed',String(!!panels[selected]&&key(panels[selected].url)===key(entry.url)));
      button.onclick=()=>entry.id==='dsh'?openService(entry.id):selectUrl(entry.url,entry.name,entry.externalOnly,entry.id);
      target.append(button);
    }
    if(!filtered.length)target.textContent='\u6ca1\u6709\u5339\u914d\u7684\u4e66\u7b7e';
  }
  function validUrl(value){
    const url=new URL(value);
    if(!['http:','https:'].includes(url.protocol)||url.username||url.password)throw Error('请输入不含用户名密码的 HTTP 或 HTTPS 地址');
    if(url.origin===location.origin)throw Error('请使用其他服务的地址，避免嵌入控制台自身');
    return url.href;
  }
  let panels=defaults,selected=0;
  const history=[];let historyIndex=-1,restoringHistory=false;
  try{
    const saved=JSON.parse(localStorage.getItem(storageKey)||'null');
    if(Array.isArray(saved))panels=saved.filter(p=>{
      try{return p&&typeof p.name==='string'&&p.name.trim()&&typeof p.url==='string'&&validUrl(p.url);}catch{return false;}
    }).slice(0,100);
    const version=Number(localStorage.getItem(catalogKey)||0);
    if(version<7){
      if(Array.isArray(saved)){
        panels=panels.filter(panel=>{const url=new URL(panel.url);return !(panel.name==='知识仓库'&&['localhost','127.0.0.1'].includes(url.hostname)&&url.port==='4321'&&['/knowledge','/knowledge/'].includes(url.pathname)&&!url.search&&!url.hash);});
        for(const panel of [...(version<2?[grafanaHome,...additions]:[]),...(version<3?blogPanels:[])]){
          if(panels.length>=100)break;
          if(!panels.some(p=>new URL(p.url).href===new URL(panel.url).href||p.name===panel.name))panels.push(panel);
        }
        // Nacos 2.x serves relative assets; the context directory needs its slash.
        for(const panel of panels){
          const url=new URL(panel.url);
          if(version<4&&url.port==='8848'&&['/nacos','/nacos/'].includes(url.pathname)){url.pathname='/nacos/';panel.url=url.href;panel.externalOnly=true;}
          if(version<5&&url.port==='5678')panel.externalOnly=true;
        }
        const localGrafana=panel=>{const url=new URL(panel.url);return ['localhost','127.0.0.1','[::1]'].includes(url.hostname)&&url.port==='3000';};
        const grafanaIndex=panels.findIndex(localGrafana);
        if(grafanaIndex>=0){panels=panels.filter(panel=>!localGrafana(panel));panels.splice(Math.min(grafanaIndex,panels.length),0,{...grafanaHome});}
        localStorage.setItem(storageKey,JSON.stringify(panels));
      }
      localStorage.setItem(catalogKey,'7');
    }
  }catch{feedback('保存的配置无法读取，已加载默认入口');}
  function persist(){
    try{localStorage.setItem(storageKey,JSON.stringify(panels.filter(panel=>!panel.temporary)));feedback('面板地址已保存在当前客户端');}
    catch{feedback('当前面板可用，但地址未能保存');}
    renderDirectory();
  }
  function active(){return state.page==='projects'&&state.servicesTab==='panels';}
  function preview(force=false){
    const panel=panels[selected];
    const external=panel?.externalOnly===true;
    const unavailable=panel?.serviceId==='dsh'&&!dshStatus?.canOpen;
    const ready=active()&&!!panel&&!external&&!unavailable;
    frame.hidden=!ready;
    const empty=document.getElementById('public-panel-empty');
    empty.hidden=!!panel&&!external&&!unavailable;
    empty.textContent=unavailable?(dshStatus?.message||'请在「项目服务」启动 dsh，然后重新打开此入口。'):external?'此网页使用独立窗口，请点击地址栏旁的「新窗口 ↗」。':'点击书签或在地址栏输入网址。';
    const url=panelUrl(panel);
    if(active()&&panel&&!restoringHistory&&history[historyIndex]?.url!==panel.url){
      history.splice(historyIndex+1);history.push({...panel});historyIndex=history.length-1;
    }
    document.getElementById('public-panel-back').disabled=historyIndex<=0;
    document.getElementById('public-panel-forward').disabled=historyIndex>=history.length-1;
    if(ready&&(force||frame.dataset.url!==url)){
      frame.title=panel.name;frame.src=url;frame.dataset.url=url;
    }
    if((!ready)&&frame.hasAttribute('src')){frame.removeAttribute('src');delete frame.dataset.url;}
  }
  function render(){
    select.replaceChildren(...panels.map((p,i)=>new Option(p.name,String(i))));
    select.value=String(selected);
    const panel=panels[selected];name.value=panel?.name||'';address.value=panel?.url||'';
    document.getElementById('public-browser-address').value=panel?.url||'';
    document.getElementById('public-panel-bookmark').disabled=!panel;
    document.getElementById('public-panel-bookmark').textContent=panel&&!panel.temporary?'\u2605':'\u2606';
    document.getElementById('public-panel-mode').value=panel?.externalOnly?'external':'embed';
    const open=document.getElementById('public-panel-open');
    open.hidden=!panel||(panel.serviceId==='dsh'&&!dshStatus?.canOpen);if(!open.hidden)open.href=panelUrl(panel,false);else open.removeAttribute('href');
    document.getElementById('public-panel-delete').disabled=!panel;
    document.getElementById('public-panel-reload').disabled=!panel||panel?.externalOnly===true;
    if(!panel){frame.removeAttribute('src');delete frame.dataset.url;}
    preview();
    renderDirectory();
  }
  select.onchange=()=>{selected=Number(select.value);feedback('');render();};
  document.getElementById('public-panel-new').onclick=()=>{
    selected=-1;render();feedback('');document.getElementById('public-panel-manager').open=true;name.focus();
  };
  document.getElementById('public-panel-form').onsubmit=event=>{
    event.preventDefault();
    try{
      if(!name.value.trim())throw Error('请填写面板名称');
      const panel={name:name.value.trim().slice(0,100),url:validUrl(address.value.trim()),externalOnly:document.getElementById('public-panel-mode').value==='external'};
      panel.serviceId=entries.find(entry=>!entry.readOnly&&key(entry.url)===key(panel.url))?.id;
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
  function selectUrl(value,label,externalOnly=false,serviceId,temporary=false){
    const url=validUrl(value);
    let index=panels.findIndex(p=>new URL(p.url).href===url);
    if(index<0){panels=panels.filter(panel=>!panel.temporary);if(!temporary&&panels.length>=100)throw Error('最多保存 100 个书签');panels.push({name:label||new URL(url).host,url,externalOnly,serviceId,temporary});index=panels.length-1;persist();}
    else if(externalOnly&&!panels[index].externalOnly){panels[index].externalOnly=true;persist();}
    if(serviceId)panels[index].serviceId=serviceId;
    selected=index;feedback('');render();
  }
  document.getElementById('public-browser-form').onsubmit=event=>{
    event.preventDefault();
    try{
      let value=document.getElementById('public-browser-address').value.trim();
      if(!/^[a-z][a-z0-9+.-]*:\/\//i.test(value)&&!value.includes('://')&&!/^(javascript|data|file|about):/i.test(value))value='http://'+value;
      const url=validUrl(value),entry=entries.find(item=>!item.readOnly&&key(item.url)===key(url));
      selectUrl(url,entry?.name,entry?.externalOnly,entry?.id,true);
    }catch(error){feedback(error.message);}
  };
  document.getElementById('public-panel-bookmark').onclick=()=>{
    const panel=panels[selected];if(!panel)return;
    if(panel.temporary&&panels.filter(item=>!item.temporary).length>=100){feedback('最多保存 100 个书签');return;}
    panel.temporary=false;persist();render();
  };
  function travel(offset){
    const index=historyIndex+offset;if(index<0||index>=history.length)return;
    historyIndex=index;restoringHistory=true;
    try{const panel=history[index];selectUrl(panel.url,panel.name,panel.externalOnly,panel.serviceId,panel.temporary);}
    finally{restoringHistory=false;}
  }
  document.getElementById('public-panel-back').onclick=()=>travel(-1);
  document.getElementById('public-panel-forward').onclick=()=>travel(1);
  async function openService(id){
    const entry=entries.find(item=>item.id===id);if(!entry)return;
    try{
      if(id==='dsh'){const response=await fetch('/api/ai/status',{signal:AbortSignal.timeout(10000)});if(!response.ok)throw Error('无法读取 dsh 状态');dshStatus=await response.json();}
      selectUrl(entry.url,entry.name,entry.externalOnly,id);location.hash='projects/panels';preview();
      if(id==='dsh'&&dshStatus.state==='AUTH_REQUIRED')feedback(dshStatus.message);
      setTimeout(()=>document.querySelector('.browser-shell').scrollIntoView({block:'start',behavior:'smooth'}),50);
    }catch(e){feedback(e.message);}
  }
  window.PublicPanels={openService,setDshStatus(data){dshStatus=data;if(panels[selected]?.serviceId==='dsh')render();},open(url,label,externalOnly=false){try{selectUrl(url,label,externalOnly);location.hash='projects/panels';preview();}catch(e){feedback(e.message);}}};
  function route(){if(location.hash==='#ai'){openService('dsh');return;}if(['blog','projects/blog'].includes(location.hash.slice(1)))selectUrl(blogPanels[0].url,blogPanels[0].name);else preview();}
  window.addEventListener('hashchange',route);
  render();
  route();
  document.getElementById('service-entry-search').oninput=renderDirectory;
  fetch('/api/service-entries').then(response=>{if(!response.ok)throw Error();return response.json();}).then(data=>{
    if(!Array.isArray(data))return;
    entries=data.filter(entry=>{try{return typeof entry.id==='string'&&typeof entry.name==='string'&&typeof entry.group==='string'&&(entry.readOnly===true?['mysql:','postgresql:','redis:','kafka:','grpc:'].includes(new URL(entry.url).protocol):validUrl(entry.url));}catch{return false;}});
    renderDirectory();
  }).catch(()=>{});
})();
