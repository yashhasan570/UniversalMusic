#!/usr/bin/env python3
"""Re-apply the Universal Music rebrand after each upstream sync (idempotent).

Protected anchors (kept intact on every run):
- LICENSE (BSD, untouched)
- .github/ and scripts/ are excluded from the sweep entirely
"""
from pathlib import Path
import re, shutil

SKIP_EXT = {".png",".jpg",".jpeg",".ico",".webp",".woff",".woff2",".ttf",".eot",".otf",".gif",".pdf",".gz",".zip",".mp4"}
KEEP = {"LICENSE", "migrate.py"}
DELETE = ["CHANGELOG.md", "CODE_OF_CONDUCT.md", "CONTRIBUTION.md", "PRIVACY_POLICY.md",
          "cliff.toml", "js_plugin_example", "metadata", "lefthook.yaml",
          ".github/FUNDING.yml", ".github/ISSUE_TEMPLATE", ".github/PULL_REQUEST_TEMPLATE"]


def sweep(t):
    t = t.replace("KRTirtho/spotube", "yashhasan570/UniversalMusic")
    t = t.replace("KRTirtho", "yashhasan570")
    t = t.replace('val bundleId = "org.spotube.spotube"', 'val bundleId = "com.universalmusic.app"')
    t = t.replace("SPOTUBE_APP_DIR", "UNIVERSALMUSIC_APP_DIR")
    t = re.sub(r"Spotube(?=[A-Za-z])", "UniversalMusic", t)
    t = re.sub(r"spotube(?=[a-z0-9])", "universalmusic", t)
    t = re.sub(r"\bSpotube\b", "Universal Music", t)
    t = re.sub(r"\bspotube\b", "universalmusic", t)
    return t


def display(t):
    t = re.sub(r"(<string name=\"[^\"]*\">)(.*?)(</string>)",
               lambda m: m.group(1) + m.group(2).replace("UniversalMusic", "Universal Music") + m.group(3), t, flags=re.S)
    return t


def main():
    for d in DELETE:
        p = Path(d)
        if p.is_dir():
            shutil.rmtree(p, ignore_errors=True)
        elif p.exists():
            p.unlink()
    for p in Path(".").rglob("*"):
        if ".git" in p.parts or not p.is_file() or p.name in KEEP or p.suffix in SKIP_EXT:
            continue
        if str(p).startswith((".github/", "scripts/")):
            continue
        try:
            t = p.read_text()
        except Exception:
            continue
        nt = sweep(t)
        if p.suffix == ".md" or p.name == "strings.xml":
            nt = display(nt)
        if nt != t:
            p.write_text(nt)
    for p in list(Path(".").rglob("*spotube*")) + list(Path(".").rglob("*Spotube*")):
        if ".git" in p.parts or p.is_dir():
            continue
        nb = re.sub(r"spotube", "universalmusic", p.name)
        nb = re.sub(r"Spotube", "UniversalMusic", nb)
        if nb != p.name:
            p.rename(p.with_name(nb))
    readme = Path("README.md")
    if readme.exists() and not readme.read_text().startswith("# Universal Music"):
        print("README drifted - rewrite needed")
    print("migrate done")


if __name__ == "__main__":
    main()
