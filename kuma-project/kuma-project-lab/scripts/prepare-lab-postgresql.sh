#!/bin/sh
# Run inside the existing PostgreSQL pod; credentials never leave shell variables/stdin.
set -eu
export PGUSER="$(cat "$POSTGRES_USER_FILE")"
export PGPASSWORD="$(cat "$POSTGRES_PASSWORD_FILE")"
export PGHOST=127.0.0.1 PGDATABASE=postgres PGCONNECT_TIMEOUT=5
exists=$(psql -X -v ON_ERROR_STOP=1 -Atc "SELECT 1 FROM pg_database WHERE datname='kuma_lab'")
if [ "$exists" != "1" ]; then
    createdb --template=template0 --encoding=UTF8 --owner="$PGUSER" kuma_lab
    psql -X -v ON_ERROR_STOP=1 -c "COMMENT ON DATABASE kuma_lab IS 'KumaFramework Lab: isolated experiment schemas and retained evidence'"
    echo 'Created PostgreSQL database kuma_lab'
else
    echo 'Using existing PostgreSQL database kuma_lab'
fi
export PGDATABASE=kuma_lab
psql -X -v ON_ERROR_STOP=1 -Atc "SELECT current_database(), current_setting('server_version')"
