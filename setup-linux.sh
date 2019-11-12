#!/usr/bin/env bash

echo 'installing adopt open jdk 11...'
sudo add-apt-repository ppa:rpardini/adoptopenjdk
sudo apt-get update
sudo apt install adoptopenjdk-11-jdk-hotspot-installer
sudo update-java-alternatives -s adoptopenjdk-11-jdk-hotspot