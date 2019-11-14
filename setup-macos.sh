#!/usr/bin/env bash

function to-abs-path {
    local target="$1"

    if [ "$target" == "." ]; then
        echo "$(pwd)"
    elif [ "$target" == ".." ]; then
        echo "$(dirname "$(pwd)")"
    else
        echo "$(cd "$(dirname "$1")"; pwd)/$(basename "$1")"
    fi
}

echo 'installing brew...'
/usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"

echo 'installing git...'
brew install git

echo 'installing adopt open jdk 11...'
brew tap AdoptOpenJDK/openjdk
brew cask install adoptopenjdk11

echo 'install driver...'
echo 'no driver set yet'

echo 'setup power management...'
sudo systemsetup -setdisplaysleep Never
sudo systemsetup -setcomputersleep Never
sudo systemsetup -setharddisksleep Never
sudo systemsetup -setrestartpowerfailure on

echo 'auto login...'
brew tap xfreebird/utils
brew install kcpassword

echo 'setup autostart...'
AUTOSTART_SCRIPT=$(to-abs-path "../autostartDeepVision.command")
osascript -e "tell application \"System Events\" to make login item at end with properties {path:\"$AUTOSTART_SCRIPT\", hidden:false}"

echo "setup auto login..."
read -p "Enter username: " username
read -s "Enter password: " password

#enable_autologin "$username" "$password"

echo "every thing is setup and should work!"