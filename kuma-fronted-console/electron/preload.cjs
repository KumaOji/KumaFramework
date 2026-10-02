const {contextBridge} = require('electron');
contextBridge.exposeInMainWorld('kumaDesktop', Object.freeze({isDesktop:true, platform:process.platform}));
