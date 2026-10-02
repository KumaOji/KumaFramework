const {spawnSync} = require('node:child_process');
const path = require('node:path');
const root=path.resolve(__dirname,'../..');
const script=path.join(__dirname,'build-backend.ps1');
const result=spawnSync('powershell.exe',['-NoProfile','-ExecutionPolicy','Bypass','-File',script],{cwd:root,stdio:'inherit',windowsHide:true});
if(result.error)throw result.error;
process.exit(result.status??1);
