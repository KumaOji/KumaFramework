const {spawnSync} = require('node:child_process');
const path = require('node:path');
const root=path.resolve(__dirname,'../..');
const script=path.join(__dirname,'build-backend.ps1');
const windows=process.platform==='win32';
const result=spawnSync(windows?'powershell.exe':'sh',windows?['-NoProfile','-ExecutionPolicy','Bypass','-File',script]:
  [path.join(root,'gradlew'),':kuma-project:kuma-project-console:bootJar','--console=plain'],{cwd:root,stdio:'inherit',windowsHide:true});
if(result.error)throw result.error;
process.exit(result.status??1);
