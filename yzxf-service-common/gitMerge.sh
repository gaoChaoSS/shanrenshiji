#!/usr/bin/env bash

git add .
git commit -m 'submit'
git checkout master
git pull
git checkout $1
git rebase master
git checkout master
git merge $1
git push
