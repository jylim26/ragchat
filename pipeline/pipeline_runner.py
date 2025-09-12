import argparse

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("pipeline", choices=["docx", "ppt"], help="which pipeline")
    args = parser.parse_args()

    if args.pipeline == "docx":
        from pipeline_docx import main as docx_main
        docx_main()

if __name__ == "__main__":
    main()