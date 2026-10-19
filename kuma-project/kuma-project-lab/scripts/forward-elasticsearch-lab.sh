#!/usr/bin/env sh
set -u
# Invoked under setsid --wait; $$ is also the process group used for cleanup.
echo $$ > "$1"
while :; do
    kubectl -n base wait --for=condition=Ready pod/skywalking-storage-0 --timeout=45s &&
        kubectl -n base port-forward --address 127.0.0.1 service/skywalking-storage "$2:9200"
    sleep 1
done
