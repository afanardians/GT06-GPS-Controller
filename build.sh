#!/bin/bash
set -e

PROJECT_DIR="/Users/afanardianysah/WorkBuddy AI/Start Stop Engine GPS"
TOOLS_DIR="/Users/afanardianysah/.local/android-tools"
AAPT2="$TOOLS_DIR/aapt2"
ANDROID_JAR="$TOOLS_DIR/android.jar"
R8_JAR="$TOOLS_DIR/r8.jar"
SIGNER_JAR="$TOOLS_DIR/uber-apk-signer.jar"
KEYSTORE="$TOOLS_DIR/debug.keystore"

cd "$PROJECT_DIR"

echo "=== 1. Clean & Prepare directories ==="
rm -rf app/build
mkdir -p app/build/gen app/build/obj app/build/dex app/build/apk

echo "=== 2. Compiling resources with aapt2 ==="
$AAPT2 compile --dir app/src/main/res -o app/build/res.zip

echo "=== 3. Linking resources and generating R.java ==="
$AAPT2 link -o app/build/app-unaligned.apk \
    -I "$ANDROID_JAR" \
    app/build/res.zip \
    --manifest app/src/main/AndroidManifest.xml \
    --min-sdk-version 26 \
    --target-sdk-version 34 \
    --java app/build/gen \
    --auto-add-overlay

echo "=== 4. Compiling Java sources ==="
javac -cp "$ANDROID_JAR" -source 8 -target 8 -d app/build/obj \
    app/build/gen/com/gt06/gpscontroller/R.java \
    app/src/main/java/com/gt06/gpscontroller/*.java

echo "=== 5. Dexing classes with D8 ==="
java -cp "$R8_JAR" com.android.tools.r8.D8 \
    --lib "$ANDROID_JAR" \
    --min-api 26 \
    --output app/build/dex \
    app/build/obj/com/gt06/gpscontroller/*.class

echo "=== 6. Packaging DEX into APK ==="
cd app/build/dex
zip -u ../app-unaligned.apk classes.dex
cd "$PROJECT_DIR"

echo "=== 7. Signing and zipaligning APK ==="
java -jar "$SIGNER_JAR" \
    -a app/build/app-unaligned.apk \
    --ks "$KEYSTORE" \
    --ksAlias androiddebugkey \
    --ksPass android \
    --ksKeyPass android \
    --out app/build/apk

# Copy final signed APK
SIGNED_APK=$(ls app/build/apk/*-aligned-debugSigned.apk 2>/dev/null || ls app/build/apk/*.apk | head -n 1)
cp "$SIGNED_APK" "$PROJECT_DIR/GPS-Control-V1.0.2.apk"
cp "$SIGNED_APK" "$PROJECT_DIR/GT06-GPS-Controller.apk"
echo "=== Build Selesai! Output: $PROJECT_DIR/GPS-Control-V1.0.2.apk ==="
ls -lh "$PROJECT_DIR/GPS-Control-V1.0.2.apk"
