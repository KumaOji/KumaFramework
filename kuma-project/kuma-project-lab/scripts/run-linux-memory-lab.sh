#!/usr/bin/env bash
set -euo pipefail
lab_root="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
if [[ "$(uname -s)" != Linux ]]; then
  echo 'This experiment needs Linux (server or WSL).' >&2
  exit 1
fi
command -v gcc >/dev/null || { echo 'gcc is required.' >&2; exit 1; }
mkdir -p "$lab_root/build/memory-lab"
binary="$lab_root/build/memory-lab/linux-memory-lab"
gcc -std=c11 -O2 -Wall -Wextra -Werror "$lab_root/src/main/c/linux_memory_lab.c" -o "$binary"
if [[ "${1:-all}" == trace ]]; then
  command -v strace >/dev/null || { echo 'strace is required for trace mode.' >&2; exit 1; }
  # Aggregate a million getpid calls rather than writing a million trace lines.
  strace -c -e trace=getpid "$binary" syscall
  strace -e trace=mmap,munmap,msync,mprotect,pread64 "$binary" memory
else
  "$binary" "${1:-all}"
fi
