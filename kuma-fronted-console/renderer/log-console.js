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
  function appended(previous,current){
    // The backend returns separate launcher/stdout/stderr tails in one snapshot.
    // Compare each source separately so an insertion before stderr is not a replay.
    function sources(body){
      const headers=[...body.matchAll(/^(?:build\/blog-runtime\/|logs\/)[^\r\n]+\.log\r?$/gm)];
      return new Map(headers.map((match,index)=>[match[0],body.slice(match.index+match[0].length+1,headers[index+1]?.index??body.length)]));
    }
    const before=sources(previous),after=sources(current);
    if(before.size&&after.size)return [...after].map(([name,body])=>{
      const added=before.has(name)?appendedText(before.get(name),body):body;
      return added?name+'\n'+added:'';
    }).join('');
    return appendedText(previous,current);
  }
  function appendedText(previous,current){
    if(current.startsWith(previous))return current.slice(previous.length);
    const before=previous.match(/[^\n]*\n|[^\n]+$/g)||[],after=current.match(/[^\n]*\n|[^\n]+$/g)||[];
    for(let count=Math.min(before.length,after.length);count>0;count--){
      if(before[before.length-count]===after[0]&&before.slice(-count).join('')===after.slice(0,count).join(''))return after.slice(count).join('');
    }
    return current;
  }
  function bind({button,element,load,isActive,onError,clearButton,closeLabel}){
    let busy=false,previous=null,opened=false,cleared=false,visible='',revision=0;
    const openLabel=button.textContent;
    button.setAttribute('aria-controls',element.id);
    button.setAttribute('aria-expanded',String(!element.hidden));
    element.classList.add('log-console');
    async function refresh(){
      if(busy||element.hidden||!isActive())return;
      busy=true;const requestedRevision=revision;
      try{
        const data=await load();
        const body=String(data.body??'');
        if(requestedRevision!==revision){previous=body;return;}
        if(element.hidden||!isActive())return;
        if(body!==previous){
          const follow=!opened||element.scrollHeight-element.clientHeight-element.scrollTop<32;
          const top=element.scrollTop;
          visible=cleared?(visible+(previous===null?'':appended(previous,body))).split('\n').slice(-1000).join('\n'):body;
          render(element,visible);previous=body;opened=true;
          element.scrollTop=follow?element.scrollHeight:top;
        }
      }catch(error){if(!element.hidden&&isActive())onError(error);}
      finally{busy=false;}
    }
    button.onclick=()=>{
      element.hidden=!element.hidden;
      button.setAttribute('aria-expanded',String(!element.hidden));
      if(closeLabel)button.textContent=element.hidden?openLabel:closeLabel;
      if(!element.hidden)void refresh();
    };
    if(clearButton){clearButton.setAttribute('aria-controls',element.id);clearButton.onclick=()=>{
      revision++;cleared=true;visible='';opened=false;
      element.replaceChildren();element.scrollTop=0;
    };}
    return refresh;
  }
  root.LogConsole={render,bind};
  if(typeof module==='object'&&module.exports)module.exports=root.LogConsole;
})(globalThis);
