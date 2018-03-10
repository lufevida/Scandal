
# Config
PROJECT_NAME="Scandal"
PROJECT_VERSION="1.0"
MAIN_CLASS="Scandal"
ICON_SOURCE="ShowTime.png"

# Create executable jar
javac *.java -d .
echo -e "Main-Class: $MAIN_CLASS" > MANIFEST.MF
echo "Manifest-Version: $PROJECT_VERSION" >> MANIFEST.MF
jar -cmf MANIFEST.MF $PROJECT_NAME.jar *.class

# Create icns file
mkdir $PROJECT_NAME.iconset
sips -z 128 128 $ICON_SOURCE --out $PROJECT_NAME.iconset/icon_128x128.png
sips -z 64 64 $ICON_SOURCE --out $PROJECT_NAME.iconset/icon_64x64.png
iconutil --convert icns $PROJECT_NAME.iconset

# Create bundle
javapackager -deploy -nosign \
	-native image \
	-srcdir . \
	-outdir . \
	-appclass $MAIN_CLASS \
	-name $PROJECT_NAME \
	-outfile $PROJECT_NAME \
	-srcfiles $PROJECT_NAME.jar \
	-Bicon=$PROJECT_NAME.icns \
	-BappVersion=$PROJECT_VERSION

# Cleanup
rm Manifest.mf
rm *.class
rm -rf $PROJECT_NAME.iconset
rm $PROJECT_NAME.icns
rm $PROJECT_NAME.jar
rm $PROJECT_NAME.html
rm $PROJECT_NAME.jnlp

# Launch app
open bundles/$PROJECT_NAME.app
