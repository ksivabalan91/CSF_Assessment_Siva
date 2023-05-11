#!/bin/bash

echo ------------- GIT INIT ------------
git init
echo ------------- GIT ADD -------------
git add .
echo Enter commit message:
read message
echo ------------ GIT COMMIT -----------
git commit -m "$message"
echo Enter repo url:
read url
echo ----- GIT ADD REMOTE AND PUSH -----
git remote add origin $url
git push origin master
echo -----------------------------------
echo --------------- DONE --------------
echo -----------------------------------