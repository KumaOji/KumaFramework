// Render the shared vector mark for Electron, Windows shortcuts and packaged EXEs.
const {app,BrowserWindow,nativeImage}=require('electron');
const fs=require('node:fs/promises');
const path=require('node:path');
const assets=path.resolve(__dirname,'../renderer/assets');
app.setPath('userData',path.resolve(__dirname,'../build/icon-profile'));
app.commandLine.appendSwitch('disable-gpu');
app.whenReady().then(async()=>{
  const window=new BrowserWindow({show:false,width:256,height:256,useContentSize:true,transparent:true,backgroundColor:'#00000000',webPreferences:{nodeIntegration:false,contextIsolation:true,sandbox:true,backgroundThrottling:false}});
  await window.loadFile(path.join(assets,'kuma-console.svg'));
  await window.webContents.executeJavaScript('new Promise(resolve=>requestAnimationFrame(()=>requestAnimationFrame(resolve)))');
  const image=(await window.webContents.capturePage()).resize({width:256,height:256,quality:'best'});
  await fs.writeFile(path.join(assets,'kuma-console.png'),image.toPNG());
  const sizes=[16,24,32,48,64,128,256];
  const frames=sizes.map(size=>image.resize({width:size,height:size,quality:'best'}).toPNG());
  const directory=Buffer.alloc(6+16*sizes.length);directory.writeUInt16LE(1,2);directory.writeUInt16LE(sizes.length,4);
  let offset=directory.length;
  sizes.forEach((size,index)=>{
    const entry=6+16*index;
    directory[entry]=directory[entry+1]=size===256?0:size;
    directory.writeUInt16LE(1,entry+4);directory.writeUInt16LE(32,entry+6);
    directory.writeUInt32LE(frames[index].length,entry+8);directory.writeUInt32LE(offset,entry+12);offset+=frames[index].length;
  });
  await fs.writeFile(path.join(assets,'kuma-console.ico'),Buffer.concat([directory,...frames]));
  if(nativeImage.createFromPath(path.join(assets,'kuma-console.ico')).isEmpty())throw Error('Generated Windows icon could not be loaded.');
  window.destroy();console.log('Generated Kuma Console PNG and seven-size Windows ICO.');app.quit();
}).catch(error=>{console.error(error);app.exit(1);});
