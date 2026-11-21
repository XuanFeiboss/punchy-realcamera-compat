#!/usr/bin/env bash
# Fallback build for networks where maven.neoforged.net is unreachable.
# The normal path is ./gradlew build (ModDevGradle + NeoForm).
set -euo pipefail
cd "$(dirname "$0")"

if [[ -z "${JAVA_HOME:-}" ]]; then
  echo "JAVA_HOME must point to a JDK 21 installation." >&2
  exit 1
fi

VERSION="$(grep '^mod_version=' gradle.properties | cut -d= -f2)"
DEPS="build/manual-deps"
CLASSES="build/manual-classes"
OUT="build/libs/prccompat-${VERSION}.jar"

mkdir -p "$DEPS" "$CLASSES" build/libs

fetch() {
  local url="$1" out="$2"
  if [[ ! -s "$out" ]]; then
    echo "Downloading $(basename "$out")"
    curl -fL --retry 3 --connect-timeout 20 "$url" -o "$out"
  fi
}

# NeoForge 21.1.267 runtime + FancyModLoader annotations + Mixin/ASM/slf4j.
fetch "https://mirrors.cernet.edu.cn/bmclapi/net/neoforged/neoforge/21.1.267/neoforge-21.1.267-universal.jar" \
      "$DEPS/neoforge-21.1.267-universal.jar"
fetch "https://mirrors.cernet.edu.cn/bmclapi/net/neoforged/fancymodloader/loader/4.0.45/loader-4.0.45.jar" \
      "$DEPS/fml-loader-4.0.45.jar"
fetch "https://mirrors.cernet.edu.cn/bmclapi/net/neoforged/mergetool/2.0.0/mergetool-2.0.0.jar" \
      "$DEPS/mergetool-2.0.0.jar"
fetch "https://mirrors.cernet.edu.cn/bmclapi/org/spongepowered/mixin/0.8.7/mixin-0.8.7.jar" \
      "$DEPS/mixin-0.8.7.jar"
fetch "https://repo1.maven.org/maven2/org/ow2/asm/asm-tree/9.7.1/asm-tree-9.7.1.jar" \
      "$DEPS/asm-tree-9.7.1.jar"
fetch "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.16/slf4j-api-2.0.16.jar" \
      "$DEPS/slf4j-api-2.0.16.jar"

CP="$DEPS/neoforge-21.1.267-universal.jar:$DEPS/fml-loader-4.0.45.jar:$DEPS/mergetool-2.0.0.jar:$DEPS/mixin-0.8.7.jar:$DEPS/asm-tree-9.7.1.jar:$DEPS/slf4j-api-2.0.16.jar"

rm -rf "$CLASSES"
mkdir -p "$CLASSES"
mapfile -t SOURCES < <(find src/main/java -name '*.java' -print | sort)

"$JAVA_HOME/bin/javac" -proc:none -encoding UTF-8 --release 21 \
  -cp "$CP" -d "$CLASSES" "${SOURCES[@]}"

cp -r src/main/resources/. "$CLASSES/"

cat > build/manual-manifest.mf <<EOF
Manifest-Version: 1.0
Specification-Title: prccompat
Specification-Vendor: XuanFei
Specification-Version: 1
Implementation-Title: prccompat
Implementation-Version: ${VERSION}
Implementation-Vendor: XuanFei
EOF

"$JAVA_HOME/bin/jar" --create --file "$OUT" --manifest build/manual-manifest.mf -C "$CLASSES" .
echo "Built $OUT"
