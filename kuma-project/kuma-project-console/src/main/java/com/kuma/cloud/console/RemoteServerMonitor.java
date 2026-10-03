package com.kuma.cloud.console;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.core.type.TypeReference;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Read-only Linux sampling over the local OpenSSH client. Credentials never reach the renderer. */
@Service
public class RemoteServerMonitor {
    private final CommandRunner runner;
    private final ObjectMapper mapper;
    private final String target;
    private final String key;
    private volatile Map<String, Object> snapshot = Map.of("ready", false, "status", "CONNECTING");

    public RemoteServerMonitor(CommandRunner runner, ObjectMapper mapper,
            @Value("${console.remote.target}") String target,
            @Value("${console.remote.key}") String key) {
        this.runner = runner; this.mapper = mapper; this.target = target; this.key = key;
    }

    public Map<String, Object> snapshot() { return snapshot; }

    @Scheduled(fixedDelay = 15000)
    public void sample() {
        String checkedAt = Instant.now().toString();
        try {
            if (!target.matches("[a-zA-Z0-9_.-]+@[a-zA-Z0-9.-]+"))
                throw new IllegalArgumentException("SSH 目标格式无效");
            String encoded = Base64.getEncoder().encodeToString(SCRIPT.getBytes(StandardCharsets.UTF_8));
            var result = runner.run(List.of("ssh", "-T", "-o", "BatchMode=yes", "-o", "ConnectTimeout=5",
                    "-o", "StrictHostKeyChecking=yes", "-o", "ServerAliveInterval=5", "-o", "ServerAliveCountMax=1",
                    "-i", key, target, "printf %s " + encoded + " | base64 -d | python3"), Duration.ofSeconds(20));
            if (!result.success()) throw new IllegalStateException(result.error().isBlank() ? "SSH 采样失败" : result.error());
            var data = new LinkedHashMap<>(mapper.readValue(result.output(), new TypeReference<Map<String, Object>>() {}));
            data.put("ready", true); data.put("status", "ONLINE"); data.put("target", target);
            data.put("sampledAt", checkedAt); data.put("checkedAt", checkedAt);
            snapshot = data;
        } catch (Exception e) {
            var data = new LinkedHashMap<>(snapshot);
            data.put("status", "OFFLINE"); data.put("target", target); data.put("checkedAt", checkedAt);
            data.put("error", e.getMessage() == null ? "服务器采样失败" : e.getMessage().replace(key, "[SSH key]"));
            snapshot = data;
        }
    }

    static final String SCRIPT = """
            import json, os, platform, time, subprocess
            def read(path):
                with open(path) as f: return f.read()
            def command(args):
                try:
                    p = subprocess.run(args, capture_output=True, text=True, timeout=2)
                    return (p.stdout if p.returncode == 0 else p.stderr).strip()[:24000] or '暂无数据'
                except Exception as e: return '无法读取：' + str(e)
            def cpu():
                v = list(map(int, read('/proc/stat').splitlines()[0].split()[1:9]))
                return sum(v), v[3] + v[4]
            def cluster():
                try:
                    p = subprocess.run(['k3s','kubectl','--request-timeout=4s','get',
                        'nodes,pods,deployments,statefulsets,daemonsets,services,persistentvolumeclaims,events',
                        '-A','-o','json'], capture_output=True, text=True, timeout=5)
                    if p.returncode: return dict(ready=False, error=p.stderr.strip()[:2000] or 'k3s 查询失败')
                    items = []
                    for x in json.loads(p.stdout).get('items', []):
                        kind = x['kind']; m = x.get('metadata', {}); s = x.get('status', {}); spec = x.get('spec', {})
                        r = dict(kind=kind, name=m.get('name',''), namespace=m.get('namespace',''), createdAt=m.get('creationTimestamp'))
                        if kind == 'Pod':
                            cs = s.get('containerStatuses', []); init = s.get('initContainerStatuses', [])
                            reasons = [c.get('state',{}).get('waiting',{}).get('reason') for c in init+cs]
                            reasons += [c.get('state',{}).get('terminated',{}).get('reason') for c in init+cs if c.get('state',{}).get('terminated',{}).get('exitCode',0) != 0]
                            healthy = any(c.get('type') == 'Ready' and c.get('status') == 'True' for c in s.get('conditions',[]))
                            phase = s.get('phase','Unknown')
                            r.update(status=next((v for v in reasons if v), s.get('reason') or ('NotReady' if phase=='Running' and not healthy else phase)),
                                healthy=healthy, completed=s.get('phase')=='Succeeded', ready=sum(c.get('ready',False) for c in cs),
                                desired=len(spec.get('containers',[])), restarts=sum(c.get('restartCount',0) for c in init+cs),
                                node=spec.get('nodeName',''), ip=s.get('podIP',''),
                                message=next((c.get('message','') for c in s.get('conditions',[]) if c.get('type')=='Ready' and c.get('status')!='True'),'')[:500])
                        elif kind == 'Node':
                            healthy = any(c.get('type') == 'Ready' and c.get('status') == 'True' for c in s.get('conditions',[]))
                            r.update(status='Ready' if healthy else 'NotReady', healthy=healthy,
                                version=s.get('nodeInfo',{}).get('kubeletVersion',''), address=' / '.join(a['address'] for a in s.get('addresses',[]) if a['type']=='InternalIP'))
                        elif kind in ['Deployment','StatefulSet','DaemonSet']:
                            desired = s.get('desiredNumberScheduled',0) if kind=='DaemonSet' else spec.get('replicas',1)
                            ready = s.get('numberReady',0) if kind=='DaemonSet' else s.get('readyReplicas',0)
                            fresh = s.get('observedGeneration',0) >= m.get('generation',0)
                            updated = s.get('updatedNumberScheduled',0) if kind=='DaemonSet' else s.get('updatedReplicas',0)
                            healthy = fresh and ready >= desired and updated >= desired
                            r.update(ready=ready, desired=desired, status='Ready' if healthy else 'NotReady', healthy=healthy)
                        elif kind == 'Service':
                            r.update(status=spec.get('type',''), address=spec.get('clusterIP',''), ports=', '.join(str(v.get('port'))+'/'+v.get('protocol','TCP') for v in spec.get('ports',[])))
                        elif kind == 'PersistentVolumeClaim':
                            r.update(status=s.get('phase','Unknown'), healthy=s.get('phase')=='Bound', volume=spec.get('volumeName',''))
                        elif kind == 'Event':
                            if x.get('type') != 'Warning': continue
                            r.update(status=x.get('reason',''), message=x.get('message','')[:1000], count=x.get('count',1), time=x.get('lastTimestamp') or m.get('creationTimestamp'))
                        items.append(r)
                    return dict(ready=True, items=items)
                except Exception as e: return dict(ready=False, error=str(e)[:2000])
            a = cpu(); time.sleep(0.3); b = cpu()
            memory = {line.split(':')[0]: int(line.split()[1]) * 1024 for line in read('/proc/meminfo').splitlines()}
            disks = []
            seen = set()
            for line in read('/proc/mounts').splitlines():
                device, mount, kind = line.split()[:3]
                if not device.startswith('/dev/') or device in seen: continue
                try:
                    s = os.statvfs(mount); seen.add(device)
                    disks.append(dict(name=device, mount=mount, type=kind, total=s.f_blocks*s.f_frsize, free=s.f_bavail*s.f_frsize))
                except OSError: pass
            networks = []
            for line in read('/proc/net/dev').splitlines()[2:]:
                name, values = line.split(':'); v = values.split()
                networks.append(dict(name=name.strip(), received=int(v[0]), sent=int(v[8])))
            print(json.dumps(dict(hostname=platform.node(), os=platform.platform(), uptime=float(read('/proc/uptime').split()[0]),
                cpu=dict(percent=100*(1-(b[1]-a[1])/max(1,b[0]-a[0])), cores=os.cpu_count(), load=os.getloadavg()),
                memory=dict(total=memory['MemTotal'], available=memory['MemAvailable'], swapUsed=memory['SwapTotal']-memory['SwapFree']),
                disks=disks, networks=networks, k3s=cluster(),
                processes=command(['ps','-eo','pid,comm,pcpu,pmem,rss,etime','--sort=-rss']).splitlines()[:61],
                ports=command(['ss','-lntup']),
                services=command(['systemctl','list-units','--type=service','--all','--no-pager','--plain']),
                containers=command(['docker','ps','-a','--format','table {{.Names}}\\t{{.Status}}\\t{{.Ports}}']))))
            """;
}
