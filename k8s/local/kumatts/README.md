# KumaTTS 本地部署

`kumatts` 命名空间运行 AstrBot 和 voice，使用 host 网络，默认入口分别为
http://localhost:6185 和 http://localhost:9880。

该部署依赖主机上的 KumaTTS 源码、Python 环境和模型，并非独立可移植镜像。
部署前调整 `astrbot.yaml`、`voice.yaml` 和相关脚本中的项目路径；
默认项目位于 `/mnt/d/KumaProject/KumaTTS`。
源码、Python 环境和模型以只读方式挂载，应用数据、运行目录和缓存可写。

## 准备与部署

需要 Buildah、NVIDIA Container Toolkit 及 k3s 的 `nvidia` RuntimeClass。
GPU 安装辅助入口为 `install-toolkit.sh`，执行前应检查其主机配置操作。
voice 使用 `NVIDIA_VISIBLE_DEVICES=all`，与主机进程共享 GPU，未配置独占资源预留。

在 WSL 的仓库根目录执行：

```bash
deploy=k8s/local/kumatts
image=$(python3 "$deploy/prepare.py" --image)
buildah bud --layers --isolation chroot --network host \
  -t "$image" -f "$deploy/Containerfile" "$deploy"
bash "$deploy/migrate.sh"
```

`migrate.sh` 导入镜像、检查前置条件，停止由旧启动器记录且命令行匹配的项目进程，
再部署 `kumatts`。两个 Deployment 的镜像标签需同步更新。
普通检查与更新不执行上述迁移操作：

```bash
python3 scripts/deploy-local-wsl.py --namespace kumatts
python3 scripts/deploy-local-wsl.py --namespace kumatts --apply
```

## 运维

```bash
kubectl -n kumatts get pods -o wide
kubectl -n kumatts logs deployment/voice --tail=100
kubectl -n kumatts logs deployment/astrbot --tail=100
kubectl -n kumatts scale deployment/voice deployment/astrbot --replicas=0
```

停止保留主机数据，重新部署可恢复副本；容器重启会中断正在执行的推理任务。
如需使用 `start-services-legacy.sh`，先缩容并等待两个 Pod 退出，避免端口和数据目录冲突。
