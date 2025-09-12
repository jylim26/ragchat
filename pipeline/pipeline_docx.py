from markitdown import MarkItDown
from pathlib import Path
import mainfest_jsonl, common, traceback, os
from dotenv import load_dotenv

load_dotenv()

INPUT_DIR = Path(os.getenv("INPUT_DIR"))
OUTPUT_DIR = Path(os.getenv("OUTPUT_DIR"))
MANIFEST_PATH = OUTPUT_DIR / "_manifest.jsonl"


def convert_docx(src: Path, out_path: Path) -> Path:
    print(f'[convert_docx] output_path={out_path}')
    if not src.exists():
        raise FileNotFoundError(f"입력된 파일이 존재하지 않음. {src}")
    
    md_engine = MarkItDown(enable_plugins=False)
    result = md_engine.convert(src)
    md_text = common.normalize_md(result.text_content)

    return common.save_text(md_text, out_path)


def main():    
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)

    latest_map = mainfest_jsonl.read_manifest(MANIFEST_PATH)
    files = common.collect_files(INPUT_DIR, "docx")

    if not files:
        print("변환할 .docx 파일이 존재하지 않습니다.")
        return
    
    success = skipped = fail = 0
    for src in files:

        try:
            out_path = (OUTPUT_DIR / src.name).with_suffix(".md") 
            
            key = str(src.resolve())
            prev = latest_map.get(key)

            if not common.should_convert_with_prev(src, out_path, prev):
                print(f"[SKIP] {src}")
                skipped += 1
                continue

            convert_docx(src, out_path=out_path)

            cur_mtime = int(src.stat().st_mtime)
            cur_sha1 = common.file_sha1(src)
            mainfest_jsonl.append_manifest(
                MANIFEST_PATH, src=src, out=out_path, mtime=cur_mtime, sha1=cur_sha1
            )

            print(f"[변환 완료] {src} -> {out_path}")
            success += 1

        except Exception as e:
            print(f"[변환 실패] {src}: {e}")
            traceback.print_exc(limit=1)
            fail += 1

    print(f"\n[SUMMARY DOCX PIPELINE] 성공: {success} | 스킵: {skipped} | 실패: {fail}")


if __name__ == "__main__":
    main()