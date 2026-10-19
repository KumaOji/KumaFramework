const {contextBridge, ipcRenderer} = require('electron');
contextBridge.exposeInMainWorld('kumaDesktop', Object.freeze({
  isDesktop:true, platform:process.platform,
  getOpenAtLogin:()=>ipcRenderer.invoke('desktop:startup:get'),
  setOpenAtLogin:enabled=>ipcRenderer.invoke('desktop:startup:set',enabled)
}));
