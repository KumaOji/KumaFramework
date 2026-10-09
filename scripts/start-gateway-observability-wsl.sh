#!/usr/bin/env bash
# Run the built Gateway with WSL Nacos/Redis, tracing, Loki and Prometheus.
set -euo pipefail
[[ ${EUID} -eq 0 ]] || { echo 'Run as root in WSL.' >&2; exit 1; }
repo_root="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
gateway_jar="${1:-${repo_root}/kuma-project/kuma-project-gateway/build/libs/gateway-2026.10.jar}"
[[ -f "${gateway_jar}" ]] || { echo 'Build Gateway bootJar first or pass the jar path.' >&2; exit 1; }
# Keep a running JVM independent of Gradle replacing the build artifact.
jar_hash="$(sha256sum "${gateway_jar}" | cut -d' ' -f1)"
mkdir -p /var/lib/kuma/gateway
runtime_jar="/var/lib/kuma/gateway/gateway-${jar_hash}.jar"
if [[ ! -f "${runtime_jar}" ]]; then
  install -m 644 "${gateway_jar}" "${runtime_jar}"
fi
[[ "$(sha256sum "${runtime_jar}" | cut -d' ' -f1)" == "${jar_hash}" ]] || { echo 'Jar changed during copy; finish the build and retry.' >&2; exit 1; }
nacos_ip="$(kubectl -n base get svc nacos -o 'jsonpath={.spec.clusterIP}')"
redis_ip="$(kubectl -n base get svc redis -o 'jsonpath={.spec.clusterIP}')"
mkdir -p /etc/kuma
if [[ ! -f /etc/kuma/gateway-local.env ]]; then
  cat > /etc/kuma/gateway-local.env <<EOF
SPRING_PROFILES_ACTIVE=dev,observability
KUMA_CONFIG_PROFILE=dev
SPRING_CLOUD_NACOS_SERVER_ADDR=${nacos_ip}:8848
SPRING_DATA_REDIS_HOST=${redis_ip}
SERVER_ADDRESS=0.0.0.0
GATEWAY_MANAGEMENT_ADDRESS=0.0.0.0
GATEWAY_TRACING_SAMPLING=0.1
GATEWAY_ENVIRONMENT=wsl
KUMA_BOOT_MONITOR_ENABLED=false
KUMA_BOOT_IP2REGION_ENABLED=false
KUMA_BOOT_LOGGER_LOGGING_FILES_ENABLED=false
MANAGEMENT_HEALTH_REDIS_ENABLED=false
EOF
  chmod 600 /etc/kuma/gateway-local.env
fi
# Environment has higher precedence than shared Nacos defaults (which disable tracing).
for setting in \
  'MANAGEMENT_SERVER_PORT=18081' \
  'MANAGEMENT_SERVER_ADDRESS=0.0.0.0' \
  'MANAGEMENT_TRACING_ENABLED=true' \
  'MANAGEMENT_TRACING_SAMPLING_PROBABILITY=0.1' \
  'MANAGEMENT_OPENTELEMETRY_TRACING_EXPORT_OTLP_ENDPOINT=http://localhost:4318/v1/traces' \
  'MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,prometheus' \
  'SPRING_REACTOR_CONTEXT_PROPAGATION=auto'; do
  key="${setting%%=*}"
  if ! grep -q "^${key}=" /etc/kuma/gateway-local.env; then
    printf '%s\n' "${setting}" >> /etc/kuma/gateway-local.env
  fi
done
kubectl apply -f "${repo_root}/k8s/local/monitoring/gateway-metrics.yaml"
cat > /etc/systemd/system/kuma-gateway-local.service <<EOF
[Unit]
Description=Kuma Gateway with local observability
After=k3s.service
Wants=k3s.service
StartLimitIntervalSec=0

[Service]
User=kuma
WorkingDirectory=${repo_root}
Environment=KUBECONFIG=/etc/rancher/k3s/k3s.yaml
EnvironmentFile=/etc/kuma/gateway-local.env
ExecStartPre=+/usr/bin/python3 ${repo_root}/scripts/register-gateway-metrics-wsl.py
ExecStart=/usr/bin/java --enable-preview --enable-native-access=ALL-UNNAMED -Xmx512m -Dlogging.config=classpath:logback-gateway.xml -jar ${runtime_jar}
Restart=on-failure
RestartSec=5
TimeoutStopSec=45

[Install]
WantedBy=multi-user.target
EOF
systemctl daemon-reload
systemctl enable kuma-gateway-local
systemctl restart kuma-gateway-local
echo 'Gateway: http://localhost:18080; management: http://localhost:18081; environment: /etc/kuma/gateway-local.env'
