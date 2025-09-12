from pathlib import Path
import re, hashlib
from typing import Dict, Any


def collect_files(input_dir: Path, ext: str) -> list[Path]:
    if not input_dir.is_absolute():
        raise ValueError(f"절대 경로가 아닙니다. {input_dir}")
    if not input_dir.exists() or not input_dir.is_dir():
        raise NotADirectoryError(f"폴더가 없거나 디렉터리가 아닙니다. {input_dir}")

    pattern = f"*.{ext.lstrip('.')}"
    return [p for p in input_dir.glob(pattern) if p.is_file()]


def save_text(text: str, out_path: Path) -> Path:
    out_path.parent.mkdir(parents=True, exist_ok=True)
    out_path.write_text(text, encoding="utf-8")
    return out_path


def normalize_md(md: str) -> str:
    md = md.strip()
    md = re.sub(r"\n{3,}", "\n\n", md)
    return md


def file_sha1(p: Path) -> str:
    h = hashlib.sha1()
    with p.open("rb") as f:
        for chunk in iter(lambda: f.read(1024 * 1024), b""):
            h.update(chunk)
    return h.hexdigest()


def should_convert_with_prev(src: Path, out: Path, prev: Dict[str, Any]) -> bool:
    if not out.exists():
        return True
    if prev is None:
        return True
    try:
        return(
            prev.get("sha1") != file_sha1(src)
            or prev.get("mtime") != int(src.stat().st_mtime)
        )
    except FileNotFoundError:
        return True