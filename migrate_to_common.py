#!/usr/bin/env python3
"""
Migrate Spring-free files from kuma-boot-starter-core to kuma-boot-starter-common.

Strategy:
  1. Collect all .java files under core's com/kuma/boot/common/
  2. For each file decide if it has Spring dependencies (imports or extends/implements)
  3. Build a kuma-internal import graph
  4. Iteratively move files whose only kuma deps are already in common
  5. Detect same-package references (no import needed in Java) and mark them as deps too
"""

import os, re, shutil, sys
from pathlib import Path

BASE   = Path(r"E:\IdeaProjects\framework\Kumaframework\kuma-boot-framework")
CORE   = BASE / "kuma-boot-starter-core"   / "src/main/java"
COMMON = BASE / "kuma-boot-starter-common" / "src/main/java"
PKG    = Path("com/kuma/boot/common")

CORE_PKG   = CORE   / PKG
COMMON_PKG = COMMON / PKG

SPRING_RE = re.compile(r'\bimport\s+(org\.springframework\.|jakarta\.annotation\.(Nullable|Nonnull|NonNull))')
KUMA_IMPORT_RE = re.compile(r'\bimport\s+(com\.kuma\.boot\.common\.[\w.]+);')
EXTENDS_SPRING_RE = re.compile(r'(?:extends|implements)\s+org\.springframework\.')

def all_java(root: Path):
    return list(root.rglob("*.java"))

def has_spring_dep(text: str) -> bool:
    if SPRING_RE.search(text):
        return True
    if EXTENDS_SPRING_RE.search(text):
        return True
    return False

def kuma_imports(text: str) -> set[str]:
    """Return set of com.kuma.boot.common.* class FQNs imported by this file."""
    hits = set()
    for m in KUMA_IMPORT_RE.finditer(text):
        fqn = m.group(1)
        # strip trailing field name if present (static field imports)
        parts = fqn.split('.')
        # walk back from end: if a segment starts with lowercase it might be field
        # The class portion ends where the segment starts with uppercase
        cls_parts = []
        for p in parts:
            cls_parts.append(p)
            if p[0].isupper():
                break
        hits.add('.'.join(cls_parts))
    return hits

def fqn_to_relpath(fqn: str) -> Path:
    """com.kuma.boot.common.utils.foo.Bar -> com/kuma/boot/common/utils/foo/Bar.java"""
    return Path(fqn.replace('.', '/') + '.java')

def collect_core_files():
    """Returns dict: relpath (relative to CORE) -> full Path"""
    result = {}
    for f in all_java(CORE_PKG):
        rel = f.relative_to(CORE)
        result[str(rel)] = f
    return result

def collect_common_files():
    """Returns set of relpaths already in common (relative to COMMON)."""
    result = set()
    for f in all_java(COMMON_PKG):
        rel = str(f.relative_to(COMMON))
        result.add(rel)
    return result

def same_package_siblings(f: Path, core_files: dict) -> set[str]:
    """
    Find other core files in the same directory that are referenced (by simple class name)
    inside the non-import body of this file, without an explicit import.
    Returns relpaths of those sibling core files.
    """
    text = f.read_text(encoding='utf-8', errors='replace')
    pkg_dir = f.parent
    siblings = set()
    for rel, sibling_path in core_files.items():
        if sibling_path.parent == pkg_dir and sibling_path != f:
            simple_name = sibling_path.stem
            # Search in non-import lines for the class name used as a type/constructor
            non_imports = re.sub(r'^import\s+.*?;\s*$', '', text, flags=re.MULTILINE)
            if re.search(r'\b' + re.escape(simple_name) + r'\b', non_imports):
                siblings.add(rel)
    return siblings

def migrate():
    core_files   = collect_core_files()   # rel -> Path
    common_files = collect_common_files() # set of rel strings

    # Build per-file metadata
    spring_deps  = {}  # rel -> bool
    kuma_dep_rels = {} # rel -> set of rel strings (kuma deps this file needs)

    for rel, path in core_files.items():
        text = path.read_text(encoding='utf-8', errors='replace')
        spring_deps[rel] = has_spring_dep(text)

        # Resolve kuma imports to relpaths
        deps = set()
        for fqn in kuma_imports(text):
            candidate = str(fqn_to_relpath(fqn))
            if candidate in core_files:
                deps.add(candidate)
            else:
                # check common
                pass  # already in common → not a blocking dep
        kuma_dep_rels[rel] = deps

    total_moved = 0
    iteration = 0

    while True:
        iteration += 1
        moved_this_round = []

        for rel, path in list(core_files.items()):
            if rel in common_files:
                continue  # already moved
            if spring_deps.get(rel, True):
                continue  # has Spring dep → stays in core

            # Check all kuma deps are in common
            blocking = set()
            for dep_rel in kuma_dep_rels.get(rel, set()):
                if dep_rel not in common_files and dep_rel in core_files:
                    blocking.add(dep_rel)

            # Check same-package siblings (references without imports)
            sibling_blocking = set()
            for sib_rel in same_package_siblings(path, core_files):
                if sib_rel not in common_files and sib_rel in core_files:
                    # only block if that sibling also has no Spring dep
                    # (if it does have Spring dep, they share a package and can't move anyway)
                    if not spring_deps.get(sib_rel, True):
                        sibling_blocking.add(sib_rel)

            if blocking or sibling_blocking:
                continue  # still blocked

            moved_this_round.append(rel)

        if not moved_this_round:
            break

        for rel in moved_this_round:
            src = core_files[rel]
            dst = COMMON / Path(rel)
            dst.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(src, dst)
            src.unlink()
            common_files.add(rel)
            total_moved += 1
            print(f"  MOVED  {rel}")

        print(f"--- iteration {iteration}: moved {len(moved_this_round)} files ---")

    print(f"\nDone. Total files moved: {total_moved}")

    # Report remaining core files with no Spring dep (still blocked by deps)
    remaining_clean = [
        rel for rel, path in core_files.items()
        if rel not in common_files and not spring_deps.get(rel, True)
    ]
    if remaining_clean:
        print(f"\n{len(remaining_clean)} Spring-free files still in core (blocked by remaining deps):")
        for r in sorted(remaining_clean):
            blockers = [d for d in kuma_dep_rels.get(r, set()) if d not in common_files and d in core_files]
            print(f"  {r}  blocked_by={blockers[:3]}")

if __name__ == '__main__':
    migrate()
