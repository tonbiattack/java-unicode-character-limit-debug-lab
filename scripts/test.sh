#!/usr/bin/env bash
set -euo pipefail

project_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
out_dir="$project_root/out/test-classes"

rm -rf "$out_dir"
mkdir -p "$out_dir"

find "$project_root/src/main/java" "$project_root/src/test/java" -name '*.java' -print0 \
  | xargs -0 javac --release 21 -encoding UTF-8 -d "$out_dir"

java -ea -cp "$out_dir" com.tonbiattack.unicodevalidation.DisplayNameRegistryTest
