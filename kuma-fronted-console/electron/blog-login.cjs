const {BrowserWindow}=require('electron');

// UAA forbids iframe embedding. Keep OAuth in a top-level window using the
// Console session so the callback cookies are also available to the preview.
function attachBlogLogin(mainWindow,{show=true}={}){
  let loginWindow=null;
  const navigate=event=>{
    if(event.isMainFrame||event.frame?.name!=='kuma-blog-preview')return;
    const frame=event.frame;
    let target,source;
    try{target=new URL(event.url);source=new URL(frame.url);}catch{return;}
    if(!['http:','https:'].includes(target.protocol)||target.origin!==source.origin||target.pathname!=='/api/auth/login')return;
    event.preventDefault();
    if(loginWindow&&!loginWindow.isDestroyed()){loginWindow.focus();return;}
    const popup=new BrowserWindow({parent:mainWindow,title:'Kuma 统一登录',width:1000,height:800,show,
      webPreferences:{session:mainWindow.webContents.session,nodeIntegration:false,contextIsolation:true,sandbox:true}});
    loginWindow=popup;
    popup.setMenuBarVisibility(false);
    popup.webContents.setWindowOpenHandler(()=>({action:'deny'}));
    popup.webContents.on('will-navigate',details=>{
      try{if(['http:','https:'].includes(new URL(details.url).protocol))return;}catch{}
      details.preventDefault();
    });
    popup.webContents.on('did-navigate',(_event,url)=>{
      const destination=new URL(url);
      if(destination.origin!==source.origin||destination.pathname==='/api'||destination.pathname.startsWith('/api/'))return;
      // Re-enter the Blog login page; it verifies the shared HttpOnly session
      // before forwarding to the admin page. Closing/cancelling never does this.
      frame.executeJavaScript('window.location.assign("/kuma")').catch(()=>{});
      popup.close();
    });
    popup.on('closed',()=>{if(loginWindow===popup)loginWindow=null;});
    popup.loadURL(target.href).catch(()=>{});
  };
  mainWindow.webContents.on('will-frame-navigate',navigate);
  mainWindow.once('closed',()=>{if(loginWindow&&!loginWindow.isDestroyed())loginWindow.destroy();});
  return {getWindow:()=>loginWindow};
}

module.exports={attachBlogLogin};
