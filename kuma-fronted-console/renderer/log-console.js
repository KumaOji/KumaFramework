'use strict';
(function(root){
  const palette=['#1e2430','#f07178','#98c379','#e5c07b','#61afef','#c678dd','#56b6c2','#d8dee9','#7f848e','#ff8b92','#b3e59f','#ffe09e','#85c5ff','#e1a6f2','#80d4df','#ffffff'];
  function color(index){
    if(index<16)return palette[index];
    if(index>=232){const v=8+(index-232)*10;return `rgb(${v},${v},${v})`;}
    const n=index-16,levels=[0,95,135,175,215,255];
    return `rgb(${levels[Math.floor(n/36)]},${levels[Math.floor(n/6)%6]},${levels[n%6]})`;
  }
  function render(element,body){
    const doc=element.ownerDocument,fragment=doc.createDocumentFragment();
    let foreground='',background='',bold=false,dim=false;
    for(const line of String(body).split(/(?<=\n)/)){
      const row=doc.createElement('span');
      const plain=line.replace(/\x1b\[[0-?]*[ -/]*[@-~]/g,'');
      const level=plain.match(/^(?:\d{4}-\d{2}-\d{2}[ T]\S+\s+|\[)?(TRACE|DEBUG|INFO|WARN|ERROR|FATAL)\b/);
      if(level)row.className='log-level-'+level[1].toLowerCase();
      const pattern=/\x1b\[([0-9;]*)m|\x1b\[[0-?]*[ -/]*[@-~]|\x1b\][^\x07\x1b]*(?:\x07|\x1b\\)/g;
      let offset=0,match;
      function append(value){
        if(!value)return;
        const span=doc.createElement('span');span.textContent=value;
        if(foreground)span.style.color=foreground;
        if(background)span.style.backgroundColor=background;
        if(bold)span.style.fontWeight='700';
        if(dim)span.style.opacity='0.7';
        row.append(span);
      }
      while((match=pattern.exec(line))){
        append(line.slice(offset,match.index));offset=pattern.lastIndex;
        if(match[1]===undefined)continue;
        const codes=(match[1]||'0').split(';').map(Number);
        for(let i=0;i<codes.length;i++){
          const c=codes[i];
          if(c===0){foreground='';background='';bold=false;dim=false;}
          else if(c===1)bold=true;
          else if(c===2)dim=true;
          else if(c===22){bold=false;dim=false;}
          else if(c===39)foreground='';
          else if(c===49)background='';
          else if(c>=30&&c<=37)foreground=palette[c-30];
          else if(c>=90&&c<=97)foreground=palette[c-90+8];
          else if(c>=40&&c<=47)background=palette[c-40];
          else if(c>=100&&c<=107)background=palette[c-100+8];
          else if(c===38||c===48){
            let value;
            if(codes[i+1]===5&&codes[i+2]>=0&&codes[i+2]<=255){value=color(codes[i+2]);i+=2;}
            else if(codes[i+1]===2&&codes.slice(i+2,i+5).length===3&&codes.slice(i+2,i+5).every(v=>v>=0&&v<=255)){value=`rgb(${codes.slice(i+2,i+5).join(',')})`;i+=4;}
            if(value){if(c===38)foreground=value;else background=value;}
          }
        }
      }
      append(line.slice(offset));fragment.append(row);
    }
    element.replaceChildren(fragment);
  }
  function bind({button,element,load,isActive,onError}){
    let busy=false,previous=null,opened=false;
    button.setAttribute('aria-controls',element.id);
    button.setAttribute('aria-expanded',String(!element.hidden));
    element.classList.add('log-console');
    async function refresh(){
      if(busy||element.hidden||!isActive())return;
      busy=true;
      try{
        const data=await load();
        if(element.hidden||!isActive())return;
        if(data.body!==previous){
          const follow=!opened||element.scrollHeight-element.clientHeight-element.scrollTop<32;
          const top=element.scrollTop;
          render(element,data.body);previous=data.body;opened=true;
          element.scrollTop=follow?element.scrollHeight:top;
        }
      }catch(error){if(!element.hidden&&isActive())onError(error);}
      finally{busy=false;}
    }
    button.onclick=()=>{
      element.hidden=!element.hidden;
      button.setAttribute('aria-expanded',String(!element.hidden));
      if(!element.hidden)void refresh();
    };
    return refresh;
  }
  root.LogConsole={render,bind};
  if(typeof module==='object'&&module.exports)module.exports=root.LogConsole;
})(globalThis);
