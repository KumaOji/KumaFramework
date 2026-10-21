const {test}=require('node:test');
const assert=require('node:assert/strict');
const {once}=require('node:events');
const readline=require('node:readline');
const fs=require('node:fs/promises');
const os=require('node:os');
const path=require('node:path');
const {spawnBackend,stopChild}=require('../electron/owned-process.cjs');

test('stopping the Java console backend preserves its long-lived child sessions',async()=>{
  const directory=await fs.mkdtemp(path.join(os.tmpdir(),'kuma-console-process-'));
  const source=path.join(directory,'ConsoleProcessProbe.java');
  await fs.writeFile(source,`
    class ConsoleProcessProbe {
      public static void main(String[] args) throws Exception {
        Process session=new ProcessBuilder(args[0],"-e","setTimeout(()=>{},30000)")
          .redirectOutput(ProcessBuilder.Redirect.DISCARD)
          .redirectError(ProcessBuilder.Redirect.DISCARD).start();
        System.out.println(session.pid());
        System.out.flush();
        Thread.sleep(30000);
      }
    }
  `);
  const backend=spawnBackend('java',[source,process.execPath],{stdio:['ignore','pipe','ignore']});
  const output=readline.createInterface({input:backend.stdout});
  let sessionPid;
  try{
    const [line]=await once(output,'line',{signal:AbortSignal.timeout(10000)});
    sessionPid=Number(line);
    assert.ok(sessionPid>0);
    await stopChild(backend);
    assert.ok(backend.exitCode!==null||backend.signalCode!==null,'backend must exit');
    assert.doesNotThrow(()=>process.kill(sessionPid,0),'WSL-like child session must remain alive');
    await stopChild(backend);
    await stopChild(undefined);
  }finally{
    output.close();
    await stopChild(backend);
    if(sessionPid){try{process.kill(sessionPid);}catch{}}
    await fs.unlink(source);
    await fs.rmdir(directory);
  }
});
