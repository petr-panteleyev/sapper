#!/bin/sh

if [ -z "$1" ]
then
  echo "Usage: sudo -E install.sh <install dir>"
  exit
fi

LAUNCH_DIR=$(cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd)
INSTALL_DIR=$1/sapper

declare -a EMPTY_JARS=(
  "javafx-base-23.0.1.jar"
  "javafx-controls-23.0.1.jar"
  "javafx-graphics-23.0.1.jar"
  "javafx-media-23.0.1.jar"
)

# Remove JavaFX empty jars to enable jlink
for j in ${EMPTY_JARS[@]}
  do
    rm -f $LAUNCH_DIR/../target/jmods/$j
  done

# Make reduced runtime
echo -n "Creating reduced java runtime... "

$JAVA_HOME/bin/jlink --module-path $LAUNCH_DIR/../target/jmods \
  --add-modules ALL-MODULE-PATH \
  --no-header-files \
  --no-man-pages \
  --output $LAUNCH_DIR/../target/jlink

echo "done"

echo -n "Installing into $INSTALL_DIR... "
mkdir -p $INSTALL_DIR
rm -rf $INSTALL_DIR/*
cp $LAUNCH_DIR/../icons/icon.png $INSTALL_DIR
cp -r $LAUNCH_DIR/../target/jlink/* $INSTALL_DIR

echo "
#!/bin/sh
$INSTALL_DIR/bin/java --module sapper/org.panteleyev.sapper.SapperApplication
" > $INSTALL_DIR/sapper.sh

chmod +x $INSTALL_DIR/sapper.sh

echo "[Desktop Entry]
Type=Application
Version=1.5
Name=Sapper
Name[ru_RU]=Сапёр
Comment=Sapper Game
Comment[ru_RU]=Игра Сапёр
Icon=$INSTALL_DIR/icon.png
Exec=/bin/sh $INSTALL_DIR/sapper.sh
Categories=Game;Java;
" > $INSTALL_DIR/sapper.desktop

echo "done"

# Cleanup directory made by root
rm -rf $LAUNCH_DIR/../target/jlink