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
    # External published artifacts keep their upstream coordinates (resolvable
    # from Maven Central; nothing publishes under the rebranded ids):
    PROTECT = [
        ("org.spotube.gradle-plugin", "ORGSPOTUBE_PLUGIN_X"),
        ("org.spotube.vlcj-bundler", "ORGSPOTUBE_VLCJ_X"),
        ("org.spotube.millennium", "ORGSPOTUBE_MILL_X"),
    ]
    for a, b in PROTECT:
        t = t.replace(a, b)
    t = t.replace("KRTirtho/spotube", "yashhasan570/UniversalMusic")
    t = t.replace("KRTirtho", "yashhasan570")
    t = t.replace('val bundleId = "org.spotube.spotube"', 'val bundleId = "com.universalmusic.app"')
    t = t.replace("SPOTUBE_APP_DIR", "UNIVERSALMUSIC_APP_DIR")
    t = re.sub(r"Spotube(?=[A-Za-z])", "UniversalMusic", t)
    t = re.sub(r"spotube(?=[a-z0-9])", "universalmusic", t)
    t = re.sub(r"\bSpotube\b", "UniversalMusic", t)
    t = re.sub(r"\bspotube\b", "universalmusic", t)
    # The bundle id becomes com.universalmusic.app, but code imports the generated
    # VLC loader from the Kotlin package, so pin the vlcj-bundler output package:
    t = t.replace('packageName = "$bundleId.core.generated"',
                  'packageName = "org.universalmusic.universalmusic.core.generated"')
    # The rules above miss "spotube" inside identifiers (followed by _ or ") and
    # before non-ASCII text (Spotubeが, Spotube는). Catch the rest so resource
    # renames and the code that references them stay in sync:
    t = t.replace("Spotube", "UniversalMusic")
    t = re.sub(r"spotube(?=[^a-zA-Z0-9])", "universalmusic", t)
    for a, b in PROTECT:
        t = t.replace(b, a)
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
        if str(p) == "composeApp/build.gradle.kts":
            nt = nt.replace('appName = if (isNightly) "UniversalMusic Nightly" else "UniversalMusic"',
                            'appName = if (isNightly) "Universal Music Nightly" else "Universal Music"')
        if nt != t:
            p.write_text(nt)
    for p in list(Path(".").rglob("*spotube*")) + list(Path(".").rglob("*Spotube*")):
        if ".git" in p.parts or p.is_dir():
            continue
        nb = re.sub(r"spotube", "universalmusic", p.name)
        nb = re.sub(r"Spotube", "UniversalMusic", nb)
        if nb != p.name:
            p.rename(p.with_name(nb))
    settings = Path("settings.gradle.kts")
    if settings.exists():
        lines = [l for l in settings.read_text().splitlines(True)
                 if 'include(":js_plugin_example")' not in l]
        settings.write_text("".join(lines))
    readme = Path("README.md")
    if readme.exists() and not readme.read_text().startswith("# Universal Music"):
        print("README drifted - rewrite needed")
    print("migrate done")


if __name__ == "__main__":
    main()
