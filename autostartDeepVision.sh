#!/bin/sh

# add github to the list of known_hosts
if ! ssh-keygen -F github.com; then
  echo "Add github.com to known_hosts"
  ssh -o StrictHostKeyChecking=no github.com
fi

function checkOnline {
	if ping -c 1 github.com &> /dev/null
	then
	  online=1
	else
	  online=0
	fi
}

checkOnline
repoUrl="https://github.com/cansik/three-scan-exhibition"
repoName=$(basename $repoUrl)
sshKeyFile="deploymentKey"

echo "Deep Vision Autostart Script"
echo "----------------------------"
echo
echo "Repository: $repoName"

# check if repository is installed
if [ ! -d "./$repoName/.git" ]; then
  if [ ! $online ]; then
  	echo "Not online! Can not install repository!"
  	exit
  fi

  # download git
  echo "installing repository..."
  ssh-agent $(ssh-add "$sshKeyFile"; git clone --branch latest --depth 1 "$repoUrl")
  # git clone --branch latest --depth 1 "$repoUrl"
fi

# change directory
cd "./$repoName"

# if online -> update to latest
if [ $online ]; then
	echo "updating $repoName..."
	ssh-agent $(ssh-add "$sshKeyFile"; git fetch)
	# git fetch
	git reset --hard latest

	echo "updated!"

	# start gradle build
	./gradlew build
fi

# run application
./gradlew run