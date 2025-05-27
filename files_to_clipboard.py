import pyperclip
from pathlib import Path

project_root = Path('.')

def is_excluded(file_path: Path) -> bool:
    return any(part in {'.idea', 'target'} for part in file_path.parts)

def read_file_safely(file_path: Path) -> str:
    try:
        return file_path.read_text(encoding='utf-8')
    except UnicodeDecodeError:
        return f"\n# [Skipped binary or non-UTF8 file: {file_path}]\n"
    except Exception as unexpected_error:
        return f"\n# [Error reading {file_path}: {unexpected_error}]\n"

all_content = '\n'.join(
    read_file_safely(file_path)
    for file_path in project_root.rglob('*')
    if file_path.is_file() and not is_excluded(file_path)
)

pyperclip.copy(all_content)
print('✅ Successfully copied all file contents to clipboard (excluding .idea and target, and skipping non-UTF8 files).')
