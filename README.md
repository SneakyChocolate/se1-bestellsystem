
# Project: *se1-bestellsystem*

Project of a simple order processing system for the *Software Engineering-I*
course.

1. [Project Setup](#1-project-setup)
1. [Change Project to *"SE-1 Bestellsystem"*](#2-change-project-to-se-1-bestellsystem)
1. [Project Build](#3-project-build)
1. [Branch Clean Up](#4-branch-clean-up)
1. [Check Project into Own Repository](#5-check-project-into-own-repository)



&nbsp;

## 1. Project Setup

The project is based on two branches:
[*main*](https://github.com/sgra64/se1-play/tree/main) and
[*libs*](https://github.com/sgra64/se1-play/tree/libs)
of the
[*se1-play*](https://github.com/sgra64/se1-play)
repository.

Create a new project directory: *se1-bestellsystem* in your workspace
(directory where other projects are) and pull both branches.

```sh
mkdir se1-bestellsystem             # create new project directory
cd se1-bestellsystem                # cd into project directory

git init                            # initialize new local git repository for the project

# set link named 'se1-play' to remote repository
git remote add se1-play https://github.com/sgra64/se1-play.git

# pull the 'main' branch from the 'se1-play' repository
git pull se1-play main

# collapse all pulled commits into one
git reset $(git commit-tree HEAD^{tree} -m "se1-play main branch")

# clone the 'libs' branch to the 'branches' directory
mkdir branches
cd branches
git clone -b libs --single-branch https://github.com/sgra64/se1-play.git libs
cd ..

# source the project
source .env.sh

# verify the content of the project directory
ls -la
```
```
drwxr-xr-x 1     0 Nov  5 19:33 ./
drwxr-xr-x 1     0 Nov  5 18:55 ../
-rw-r--r-- 1  3518 Nov  5 19:33 .classpath
-rw-r--r-- 1 26135 Nov  5 19:28 .env.sh
drwxr-xr-x 1     0 Nov  5 19:28 .git/
-rw-r--r-- 1  1214 Nov  5 19:28 .gitignore
-rw-r--r-- 1   432 Nov  5 19:33 .project
drwxr-xr-x 1     0 Nov  5 19:33 .vscode/
-rw-r--r-- 1 25892 Nov  5 19:32 README.md
drwxr-xr-x 1     0 Nov  5 19:33 bin/
drwxr-xr-x 1     0 Nov  5 19:30 branches/
lrwxrwxrwx 1    15 Nov  5 19:33 libs -> ./branches/libs/
drwxr-xr-x 1     0 Nov  5 19:28 resources/
drwxr-xr-x 1     0 Nov  5 19:28 src/
drwxr-xr-x 1     0 Nov  5 19:28 tests/
```

Test-build the project:

```sh
mk compile compile-tests run-tests run A BB CCC
```
```
java application.Runtime A BB CCC
Hello, "SE-1 Play" (application.Application)
- arg: A
- arg: BB
- arg: CC
```


&nbsp;

## 2. Change Project to *"SE-1 Bestellsystem"*

Name *"SE-1 Play"* still appears. Changes need to be applied to fit the
project to the new name *"SE-1 Bestellsystem"*.

```sh
# fetch branch 'se1-bestellsystem-delta' from the remote repository 'se1-play'
git fetch se1-play se1-bestellsystem-delta
```
```
From github.com:sgra64/se1-play
 * branch            se1-bestellsystem-delta -> FETCH_HEAD
 * [new branch]      se1-bestellsystem-delta -> se1-play/se1-bestellsystem-delta
```

A new *remote*-branch: `se1-play/se1-bestellsystem-delta` was created in the
local repository with the content of the remote branch.

Content of the branch can be compared to the local `main` branch:

```sh
# compare content of fetched remote branch to local 'main' branch:
git diff --name-status se1-play/se1-bestellsystem-delta main
```

Files labeled with `M` represent modifications:

```
A       .env.sh
A       .vscode/launch.json
A       .vscode/launch_terminal.sh
A       .vscode/settings.json
A       README.md
A       resources/META-INF/MANIFEST.MF
M       resources/application.properties        <-- modified
A       resources/log4j2.properties
A       src/application/Application.java
A       src/application/Runtime.java
M       src/application/package-info.java       <-- modified
M       src/module-info.java                    <-- modified
A       tests/application/Application_0_always_pass_Tests.java
```

Modifications can be displayed, e.g. for file [src/module-info.java](src/module-info.java):

```sh
git diff main se1-play/se1-bestellsystem-delta -- src/module-info.java
```

Red lines labeled with `-` show current content of the file in the `main` branch,
green lines labeled with `+` show the content of the file from the fetched branch.

Changes in file [src/module-info.java](src/module-info.java) relate to renaming
the module from *"se1.play"* to *"se1.bestellsystem"*.

```
- * Module {@code se1.play} demonstrates Java project setup, sourcing the projec
t
- * and the project build process for the <i>Software Engineering-I</i> course.
+ * Module {@code se1.bestellsystem} implements a simple order processing system
+ * for the <i>Software Engineering-I</i> course.

...

-module se1.play {                  <-- current line (old module name)
+module se1.bestellsystem {         <-- incoming line (correct module name)
     opens application;     // open: package is accessible by JavaVM at runtime
     exports application;   // export: package is accessible to compile other mo
dules
```

See changes in all modified files:

```sh
git diff main se1-play/se1-bestellsystem-delta -- src/module-info.java

git diff main se1-play/se1-bestellsystem-delta -- src/application/package-info.java

git diff main se1-play/se1-bestellsystem-delta -- resources/application.properties
```

Changes fetched from the remote branch *se1-play/se1-bestellsystem-delta*
cam be applied by a `git merge`. In a merge, git integrates (merges) changes
from a *branch-to-merge* into the *current branch*.

```sh
# merge fetched branch 'se1-play/se1-bestellsystem-delta' into 'main' branch
git merge se1-play/se1-bestellsystem-delta --allow-unrelated-histories
```

This merge leads to conflicts, which means that git was not able to integrate
files properly:

```
CONFLICT (modify/delete): README.md deleted in se1-play/se1-bestellsystem-delta
and modified in HEAD.  Version HEAD of README.md left in tree.
Auto-merging resources/application.properties
CONFLICT (add/add): Merge conflict in resources/application.properties
Auto-merging src/application/package-info.java
CONFLICT (add/add): Merge conflict in src/application/package-info.java
Auto-merging src/module-info.java
CONFLICT (add/add): Merge conflict in src/module-info.java
Automatic merge failed; fix conflicts and then commit the result.
```

Open file: `src/module-info.java` to see the insertione git has made to
indicate conflicts:

```git
module se1.bestellsystem {
```

Lines between markers: `<<< HEAD` and `===` show the content of the file
from the `main` branch (*"current"*). Lines between markers `===` and `>>>`
show content of the file from the (*"incoming"*) branch.

Since *git* has inserted markers as text into files, the Java compiler will
report errors:

```sh
mk compile                          # fails with open merge conflicts
```
```
src\application\package-info.java:16: error: illegal start of type
<<<<<<< HEAD
^
src\application\package-info.java:16: error: > expected
<<<<<<< HEAD
            ^
```

For *git*, the project is in an state with: *unmerged paths*, which means
the merge has not been completed - or: "the merge is still open."

```sh
git status                          # show status of the 'open merge'
```
```
You have unmerged paths.            <-- "unmerged paths" (open merge)
  (fix conflicts and run "git commit")
  (use "git merge --abort" to abort the merge)

Unmerged paths:                     <-- list of files with open merge conflicts
  (use "git add/rm <file>..." as appropriate to mark resolution)
        deleted by them: README.md
        both added:      resources/application.properties
        both added:      src/application/package-info.java
        both added:      src/module-info.java
```

Merge conflicts must be resolved manually by opening files one after another
and inserting the correct text.

An *"open merge"* can always be reset restoring the status of the project
before the merge:

```sh
git merge --abort                   # abort merge and restore previous project state
git status                          # no more 'unmerged paths'

mk compile run A BB CCC             # the project compiles and runs again
```

```
java application.Runtime A BB CCC
Hello, "SE-1 Play" (application.Application)
- arg: A
- arg: BB
- arg: CC
```

Name *"SE-1 Play"* still appears.

Repeat the merge and resolve all conflicts. IDE provide support, usually by offering
selections: *Accept Current* or *Accept Incoming* changes.

Insert your name as `Author` in file
[src/application/package-info.java](src/application/package-info.java).

```sh
# merge fetched branch into 'main' branch (squash commits from merged branch)
git merge --squash se1-play/se1-bestellsystem-delta --allow-unrelated-histories
```

Resolve all conflicts using your IDE.

Make sure the project compiles and runs at the end.

```sh
mk compile run A BB CCC             # the project compiles and runs again
```

If the project compiles and runs, the still *open merge* can be committed:

```sh
git status                          # show files with modifications (red)

# prepare ("stage") commit
git add \
    README.md \
    resources/application.properties \
    src/application/package-info.java \
    src/module-info.java

git status                          # show staged files (green)

# commit ("record") staged files in the local repository
git commit -m "merge commit remote branch se1-play/se1-bestellsystem-delta"

git log --oneline                   # show new merge-commit on top of 'main'-branch ('HEAD')
```

The *git*-log shows two commits: one collapsed commit from the initial
pull of the *se1-play* branch and one *"merge commit"*:

```
c4a84ae (HEAD -> main) merge commit remote branch se1-play/se1-bestellsystem-delta
187123e se1-play main branch
```


&nbsp;

## 3. Project Build

Rebuild the project:

```sh
mk build                # compile compile-tests run-tests package
mk run-tests
java -jar bin/application-1.0.0-SNAPSHOT.jar A BB CCC DDD
```

The correct name of the project: *"SE-1 Bestellsystem"* defined in:
[application.properties](resources/application.properties)
appears.

```
Hello, "SE-1 Bestellsystem" (application.Application)
- arg: A
- arg: BB
- arg: CCC
- arg: DDD
```

```sh
mk javadoc              # create javadoc
```

Open documentation: `docs/index.html` and show that your name
appears on pages as `Author: your name`. The name is defined in
[application.package-info.java](src/application/package-info.java).


&nbsp;

## 4. Branch Clean Up

Remove remote branches from project:

```sh
git branch -avv                     # show all branches
```
```
* main                      31292ba merge commit remote branch se1-play/se1-bestellsystem-delta
  remotes/se1-play/main     30c6a74 update .env.sh, in wipe keep libs link
  remotes/se1-play/se1-bestellsystem-delta f06822b update module-info.java package-info.java...
```

Remove remote branches (`-dr`: delete, remote):

```sh
git branch -dr se1-play/main
git branch -dr se1-play/se1-bestellsystem-delta
git branch -avv                     # show all branches
```

Remote branches are gone. Only the *main*-branch remains in the local repository.

```
* main                      31292ba merge commit remote branch se1-play/se1-bestellsystem-delta
```


&nbsp;

## 5. Check Project into Own Repository

Create a new project with name: *"se1-bestellsystem"* in your
[BHT GitLab](https://gitlab.bht-berlin.de/)
(or other repository).

Make sure, your `public ssh key` is registered in your account.

Obtain the *ssh*-repository URL, e.g.
`git@gitlab.bht-berlin.de:<your-id>/se1.bestellsystem.git`

and register in the local repository under the name `origin` as new
remote repository:

```sh
# register the remote repository URL under the name 'origin'
# make sure to replace the '<...>' with your account id
git remote add origin git@gitlab.bht-berlin.de:<...>/se1.bestellsystem.git

# show the new remote URL
git remote -v
```

The new URL is registered under the name `origin`. The prior remote URL
is still registered under the name `se1-play`.

```
origin  git@gitlab.bht-berlin.de/se1-bestellsystem.git (fetch)
origin  git@gitlab.bht-berlin.de/se1-bestellsystem.git (push)
se1-play        https://github.com/sgra64/se1-play.git (fetch)
se1-play        https://github.com/sgra64/se1-play.git (push)
```

Show remote URL in file: [.git/config](.git/config)

```
[core]
        repositoryformatversion = 0
        filemode = false
        bare = false
        logallrefupdates = true
        ignorecase = true
[remote "origin"]
        url = git@gitlab.bht-berlin.de/se1-bestellsystem.git
        fetch = +refs/heads/*:refs/remotes/origin/*
[remote "se1-play"]
        url = https://github.com/sgra64/se1-play.git
        fetch = +refs/heads/*:refs/remotes/se1-play/*
```

Remove the prior URL `se1-play`:

```sh
git remote remove se1-play          # remove remote 'se1-play'

git remote -v                       # show remotes

cat .git/config
```

Next, the main branch can be pushed to remote `origin`:

```sh
git push -u origin main             # push branch 'main' to remote 'origin'
```
