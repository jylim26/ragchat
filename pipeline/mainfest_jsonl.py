from __future__ import annotations
from pathlib import Path
from typing import Dict, Any
import json, time


def read_manifest(path: Path) -> Dict[str, Dict[str, Any]]:
    latest: Dict[str, Dict[str, Any]] = {}
    if not path.exists():
        return latest
    with path.open("r", encoding="utf-8") as f:
        for line in f:
            line = line.strip()
            if not line:
                continue
            try:
                rec = json.loads(line)
            except json.JSONDecodeError:
                continue
            if not isinstance(rec, dict):
                continue
            src = rec.get("src")
            if isinstance(src, str):
                latest[src] = rec
    return latest


def append_manifest(path: Path, *, src: Path, out: Path, mtime: int, sha1: str) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    rec = {
        "src": str(src.resolve()),
        "out": str(out.resolve()),
        "mtime": int(mtime),
        "sha1": sha1,
        "ts": int(time.time()),
    }
    with path.open("a", encoding="utf-8") as f:
        f.write(json.dumps(rec, ensure_ascii=False) + "\n")