#!/bin/sh

if [ -z "$1" ]
then
  echo "Usage: install.sh <install dir>"
  exit
fi

LAUNCH_DIR=$(cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd)
INSTALL_DIR=$1/sapper

echo -n "Installing into $INSTALL_DIR... "
mkdir -p $INSTALL_DIR
rm -rf $INSTALL_DIR/*
cp -r $LAUNCH_DIR/../target/dist/Sapper/* $INSTALL_DIR
echo "done"

echo -n "Creating desktop entry... "
echo "[Desktop Entry]
Type=Application
Version=1.5
Name=Sapper
Name[ru_RU]=Сапёр
Comment=Sapper Game
Comment[ru_RU]=Игра Сапёр
Icon=$INSTALL_DIR/lib/Sapper.png
Exec=$INSTALL_DIR/bin/Sapper
Categories=Game;Java;
" > $HOME/.local/share/applications/sapper.desktop
echo "done"
