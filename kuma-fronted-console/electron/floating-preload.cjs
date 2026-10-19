const {contextBridge,ipcRenderer}=require('electron');
contextBridge.exposeInMainWorld('kumaOverview',Object.freeze({
  expand:value=>ipcRenderer.invoke('overview-float:expand',value),
  restore:()=>ipcRenderer.invoke('overview-float:restore'),
  host:()=>ipcRenderer.invoke('overview-float:host'),
  drag:phase=>ipcRenderer.invoke('overview-float:drag',phase),
  onState:listener=>ipcRenderer.on('overview-float:state',(_event,state)=>listener(state))
}));
