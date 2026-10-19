const bridge=window.kumaOverview;
const $=id=>document.getElementById(id);
let visible=false,expanded=false,busy=false,timer,revision=0,drag,moved=false;
const bytes=value=>{if(!Number.isFinite(value))return '—';const units=['B','KiB','MiB','GiB','TiB'];let i=0;while(value>=1024&&i<units.length-1){value/=1024;i++;}return `${value.toFixed(i?1:0)} ${units[i]}`;};
const percent=value=>Number.isFinite(value)?`${Math.max(0,Math.min(100,value)).toFixed(1)}%`:'—';
function render(h){
  if(!h?.ready||!h.cpu)throw Error('正在等待本机采样');
  const cpu=h.cpu.percent,total=h.memory?.total,used=total-h.memory?.available;
  const memory=total>0?used/total*100:null;
  $('cpu').textContent=percent(cpu);$('orb-value').textContent=Number.isFinite(cpu)?`CPU ${Math.round(cpu)}%`:'采样中';
  $('orb').style.setProperty('--load',percent(cpu)==='—'?'0%':percent(cpu));
  $('cpu-track').style.width=Number.isFinite(cpu)?percent(cpu):'0%';
  $('cores').textContent=`${h.cpu.logicalCores??'—'} 个逻辑处理器`;
  $('memory').textContent=percent(memory);$('memory-detail').textContent=`${bytes(used)} / ${bytes(total)}`;
  $('memory-track').style.width=Number.isFinite(memory)?percent(memory):'0%';
  const diskTotal=h.disks.reduce((n,d)=>n+d.total,0),diskUsed=h.disks.reduce((n,d)=>n+d.total-d.free,0);
  $('disk').textContent=bytes(diskUsed);$('disk-detail').textContent=`总计 ${bytes(diskTotal)}`;
  $('processes').textContent=h.processCount??'—';
  const hours=Math.floor((h.uptime||0)/3600),minutes=Math.floor((h.uptime||0)%3600/60);
  $('uptime').textContent=`已运行 ${hours} 小时 ${minutes} 分`;
  $('hostname').textContent=[h.hostname,h.os].filter(Boolean).join(' · ');
  $('hostname').title=$('hostname').textContent;
  $('download').textContent=bytes(h.networks.reduce((n,v)=>n+(v.download||0),0))+'/s';
  $('upload').textContent=bytes(h.networks.reduce((n,v)=>n+(v.upload||0),0))+'/s';
  const sampled=new Date(h.sampledAt),stale=!Number.isFinite(sampled.getTime())||Date.now()-sampled.getTime()>15000||!!h.error;
  document.body.dataset.stale=String(stale);
  $('status').textContent=stale?'采样暂未更新 · 保留最近数据':`更新于 ${sampled.toLocaleTimeString('zh-CN',{hour12:false})} · 每 2 秒刷新`;
  if(stale)$('orb-value').textContent='数据过期';
}
async function refresh(){
  if(!visible||busy)return;
  busy=true;const current=revision;
  try{const data=await bridge.host();if(visible&&current===revision)render(data);}
  catch{if(visible&&current===revision){document.body.dataset.stale='true';$('status').textContent='连接暂时不可用 · 正在自动重试';$('orb-value').textContent='未连接';}}
  finally{busy=false;if(visible&&current!==revision)refresh();}
}
bridge.onState(state=>{
  const wasVisible=visible;visible=state.visible;expanded=state.expanded;
  $('overview').hidden=!expanded;$('orb').setAttribute('aria-expanded',String(expanded));
  clearInterval(timer);
  if(visible){if(!wasVisible)revision++;refresh();timer=setInterval(refresh,2000);}
  else{revision++;drag=null;}
});
const safely=promise=>promise.catch(()=>{});
$('orb').addEventListener('pointerdown',event=>{
  if(event.button!==0)return;drag={x:event.screenX,y:event.screenY};moved=false;
  $('orb').setPointerCapture(event.pointerId);safely(bridge.drag('start'));
});
$('orb').addEventListener('pointermove',event=>{
  if(!drag)return;
  if(Math.hypot(event.screenX-drag.x,event.screenY-drag.y)>4)moved=true;
  if(moved)safely(bridge.drag('move'));
});
function endDrag(){if(!drag)return;drag=null;safely(bridge.drag('end'));}
$('orb').addEventListener('pointerup',endDrag);
$('orb').addEventListener('pointercancel',()=>{moved=true;endDrag();});
$('orb').addEventListener('lostpointercapture',endDrag);
$('orb').onclick=event=>{if(event.detail===0||!moved)safely(bridge.expand(!expanded));moved=false;};
$('collapse').onclick=()=>{safely(bridge.expand(false));$('orb').focus();};
$('restore').onclick=()=>safely(bridge.restore());
document.addEventListener('keydown',event=>{if(event.key==='Escape'){safely(bridge.expand(false));$('orb').focus();}});
