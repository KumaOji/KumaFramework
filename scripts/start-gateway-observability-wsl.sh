#!/usr/bin/env bash
# Application JVMs run on Windows; WSL hosts middleware and monitoring only.
printf '%s\n' 'Gateway runs on Windows. Use scripts/start-services-local.ps1 -Services gateway (or the console). WSL startup is no longer supported.' >&2
exit 1
