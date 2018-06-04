# Config
PROJECT_NAME="Scandal"
PROJECT_VERSION="2.0"
MAIN_CLASS="language.ide.MainView"
ICON_SOURCE="./bundles/icon.png"
SIGNATURE="3rd Party Mac Developer Application: Luis Felipe Vieira Damiani (9TETT4M689)"

# Create executable jar
export M2_HOME=/Applications/apache-maven-3.5.3
export PATH=$PATH:$M2_HOME/bin
mvn package

# Create icns file
mkdir $PROJECT_NAME.iconset
sips -z 1024 1024 $ICON_SOURCE --out $PROJECT_NAME.iconset/icon_1024x1024.png
sips -z 512 512 $ICON_SOURCE --out $PROJECT_NAME.iconset/icon_512x512.png
sips -z 256 256 $ICON_SOURCE --out $PROJECT_NAME.iconset/icon_256x256.png
sips -z 128 128 $ICON_SOURCE --out $PROJECT_NAME.iconset/icon_128x128.png
sips -z 64 64 $ICON_SOURCE --out $PROJECT_NAME.iconset/icon_64x64.png
sips -z 32 32 $ICON_SOURCE --out $PROJECT_NAME.iconset/icon_32x32.png
sips -z 16 16 $ICON_SOURCE --out $PROJECT_NAME.iconset/icon_16x16.png
iconutil --convert icns $PROJECT_NAME.iconset

# Create bundle
javapackager -deploy -nosign \
	-native image \
	-srcdir . \
	-outdir . \
	-appclass $MAIN_CLASS \
	-name $PROJECT_NAME \
	-outfile $PROJECT_NAME \
	-srcfiles target/$PROJECT_NAME-$PROJECT_VERSION-jar-with-dependencies.jar \
	-Bicon=$PROJECT_NAME.icns \
	-BappVersion=$PROJECT_VERSION

# Copy resources
cp -r ./lib ./bundles/$PROJECT_NAME.app/Contents/Java

# Cleanup
rm -rf $PROJECT_NAME.iconset
rm $PROJECT_NAME.icns
rm $PROJECT_NAME.html
rm $PROJECT_NAME.jnlp

# Sign app
codesign -s $SIGNATURE ./bundles/$PROJECT_NAME.app/Contents/MacOS/libpackager.dylib
codesign -s $SIGNATURE ./bundles/$PROJECT_NAME.app/Contents/PlugIns/Java.runtime
codesign -s $SIGNATURE ./bundles/$PROJECT_NAME.app

# Launch app
open bundles/$PROJECT_NAME.app