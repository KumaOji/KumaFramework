"""Run inside WSL. Provision only the reference role, two schemas and its Kubernetes Secret."""
import base64
import json
import secrets
import subprocess

NAMESPACE, POD, DATABASE = "base", "postgresql-0", "base"
ROLE, SECRET = "kuma_reference_app", "kuma-reference-db"
SCHEMAS = ("kuma_reference_main", "kuma_reference_sandbox")


def execute(args, content=None):
    result = subprocess.run(args, input=content, text=True, capture_output=True)
    if result.returncode:
        # Credentials/SQL are never included in diagnostics.
        raise SystemExit(f"Operation failed: {args[0]} (exit {result.returncode}); no credentials were printed.")
    return result.stdout


def psql(sql):
    return execute(["kubectl", "exec", "-i", "-n", NAMESPACE, POD, "--", "psql", "-X", "-At",
                    "-v", "ON_ERROR_STOP=1", "-U", "postgres", "-d", DATABASE], sql)


if __name__ == "__main__":
    existing = psql("SELECT nspname || ':' || pg_get_userbyid(nspowner) FROM pg_namespace "
                    "WHERE nspname IN ('kuma_reference_main','kuma_reference_sandbox');")
    for line in existing.splitlines():
        name, owner = line.split(":", 1)
        if owner != ROLE:
            raise SystemExit(f"Schema {name} already belongs to another role; no changes made.")

    role_exists = psql("SELECT COUNT(*) FROM pg_roles WHERE rolname='kuma_reference_app';").strip() == "1"
    secret_text = execute(["kubectl", "get", "secret", SECRET, "-n", NAMESPACE, "-o", "json", "--ignore-not-found"])
    if secret_text.strip():
        data = json.loads(secret_text)["data"]
        decode = lambda key: base64.b64decode(data[key]).decode()
        if decode("username") != ROLE or decode("database") != DATABASE:
            raise SystemExit("Existing reference Secret does not match the expected application role/database.")
        password = decode("password")
    else:
        if role_exists:
            raise SystemExit("Reference role exists but its Secret is missing; refusing to reset an existing password.")
        password = secrets.token_urlsafe(32)
        document = {"apiVersion": "v1", "kind": "Secret", "metadata": {"name": SECRET, "namespace": NAMESPACE},
                    "type": "Opaque", "stringData": {"username": ROLE, "password": password, "database": DATABASE}}
        execute(["kubectl", "apply", "-f", "-"], json.dumps(document))

    sql = ["BEGIN;"]
    if not role_exists:
        literal = password.replace("'", "''")
        sql.append(f"CREATE ROLE {ROLE} LOGIN PASSWORD '{literal}';")
    sql.append(f"GRANT CONNECT ON DATABASE {DATABASE} TO {ROLE};")
    sql.extend(f"CREATE SCHEMA IF NOT EXISTS {name} AUTHORIZATION {ROLE};" for name in SCHEMAS)
    sql.append("COMMIT;")
    psql("\n".join(sql))
    print("Ready: database=base; role=kuma_reference_app; schemas=" + ",".join(SCHEMAS))
    print("Credentials stored in Kubernetes Secret base/kuma-reference-db; no existing business tables changed.")
