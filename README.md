# Project: *se1-play*

Goal of this exercise is to demonstate a professionally engineered
Java project.

You will learn about project *"Setup"* and *"Build"* processes,
what *"Sourcing"* is and about the structure (*"scaffold"*) of a
Java project.

- Only basic tools are used: `git`, `java`, `javac`, `jar`, `javadoc`
(more advanced build tools are introduced later).

- Basic skills are trained such as the use of `git` and the `terminal shell`.

- *VSCode* is the preferred IDE for development, other IDE can be used
    as well, e.g. *eclipse*, *IntelliJ*.


Project *se1-play* was created in steps that are recorded as tagged commits
in the remote Git repository ([*"se1-play"*](https://github.com/sgra64/se1-play)).
Reviewing steps provides a first understanding:

<!-- relative paths work for tags and branches -->
<!-- - Step 1 (tag: [*t0*](https://github.com/sgra64/se1-play/tree/t0)) - -->
- Step 1 (tag: [*t0*](../../tree/t0)) -
    initial commit with [*.gitignore*](.gitignore) `README.md` files.

- Step 2 (tag: [*t1*](../../tree/t1)) -
    commit with the [*.vscode*](.vscode) settings folder for the *VSCode* IDE.

- Step 3 (tag: [*t2*](../../tree/t2)) -
    commit with `.env.sh`, the script to *source* the project
    (see: setup the project environment).

- Step 4 (tag: [*root*](../../tree/root)) -
    commit with `src`, `tests` and `resources` folders added.

- Step 5: a separate branch: [*libs*](../../tree/libs)
    containing *.jar* - libraries are added that are required by the project.


The following steps must be performed by a developer on a laptop
for *onboarding* the project.

- Step 6, section [*Getting the Project*](#getting-the-project-se1-play).

- Step 7, section [*Project Setup*](#project-setup).

- Step 8, section [*Project Build*](#project-build).

- Step 9, section [*Running the Application*](#running-the-application).

- Summary: [*Complete Project Content*](#complete-project-content)


&nbsp;

## Getting the Project: *se1-play*

Cloning the project from the Git repository yields the project
folder *`se1-play`* in the current directory (workspace).

A *workspace* is a directory where projects directories reside
as sub-folders.

You need to select a *workspace* directory on your laptop for
this and following projects.

```sh
cd <workspace>      # change to folder where the project should be located

# Cloning the entire repository (with all branches) is often not adviced
# in a real git-repository due to repository size:
#  - git clone git@github.com:sgra64/se1-play.git
# Smarter is to fetch only needed branches, here the 'main'-branch of
# the repository
# 
# Hint: if the ssh-link: git@github... does not work, use the https-link:
# https://github.com/sgra64/se1-play.git
# 
git clone -b main --single-branch git@github.com:sgra64/se1-play.git se1-play

ls -la              # show the new project folder: 'se1-play'
cd se1-play         # change into the project folder
ls -la              # list inital project content
```

The initial project content is:

```
drwxr-xr-x 1     0 Oct  4 14:26 ./
drwxr-xr-x 1     0 Oct  4 14:26 ../
-rw-r--r-- 1 25037 Oct  4 14:26 .env.sh
drwxr-xr-x 1     0 Oct  4 14:26 .git/
-rw-r--r-- 1  1214 Oct  4 14:26 .gitignore
drwxr-xr-x 1     0 Oct  4 14:26 .vscode/
-rw-r--r-- 1  1123 Oct  4 14:26 README.md
drwxr-xr-x 1     0 Oct  3 19:29 resources/
drwxr-xr-x 1     0 Oct  3 19:45 src/
drwxr-xr-x 1     0 Oct  3 19:29 tests/
```

The project structure (*"scaffold"*) with files and folders is:

```sh
<workspaces>        # workspace folder, select and know the location of
  |                 # this directory on your laptop
  +-<se1-play>          # project directory "se1-play" with files and
     |                  # folders for the project such as:
     +--.env.sh             # file to set/source the project entvironment
     +-<.git>               # the local git repository
     +--.gitignore          # file that tells git which files to ignore
     +-<.vscode>            # VSCode project files
     +--README.md           # this markup file
     +-<src>                # Java source code folder
     +-<resources>          # folder with none-Java sources, config files
     +-<tests>              # folder with source code for tests
 ```

The complete project content can be listed with:

```sh
find . | grep -v '.git'     # list project (omitting the .git folder)
```

Output shows folders and files that are included in the project:

```
./.env.sh
./.vscode
./.vscode/launch.json
./.vscode/launch_terminal.sh
./.vscode/settings.json
./README.md
./resources
./resources/application.properties
./resources/log4j2.properties
./resources/META-INF
./resources/META-INF/MANIFEST.MF
./src
./src/application
./src/application/Application.java
./src/application/package-info.java
./src/application/Runtime.java
./src/module-info.java
./tests
./tests/application
./tests/application/Application_0_always_pass_Tests.java
```


&nbsp;

## Project Setup

*Project Setup* describes steps to the stage that a project can be *"built"*,
which means, the IDE finds proper configurations and works, tools such as the
Java compiler *javac*, the Java VM *java*, the *javadoc* compiler work.

*Project Setup* includes these steps:

- step 1 - adding libraries that did not come with the git-checkout.

- step 2 - *"sourcing"* the project to set up the project environment with:

    - environment variables, e.g.
        `CLASSPATH`, `MODULEPATH`, `JDK_JAVAC_OPTIONS`, `JUNIT_OPTIONS` ... ,

    - configuration files created for the IDE and Java tools, e.g.
        `.classpath`, `.project`, `libs`-link ... and

    - functions defined to facilitate *build* commands, e.g.
        `show`, `mk`, `clean`, `wipe` ... .

*Libraries* required by a project are typically not stored inside the
project. In our case, a new sub-folder `branches` will hold the
[*libs*](https://github.com/sgra64/se1-play/tree/libs) - branch
from the project repository. The branch will be separately checked out.

Create folder `branches` in the project directory and *cd* into it:

```sh
mkdir branches          # create folder 'branches' (must be in 'se1-play')
cd branches             # cd into the new folder

# Fetch branch 'libs' from the repository (use the http-link, if the
# git@-link does not work)
# 
git clone -b libs --single-branch git@github.com:sgra64/se1-play.git libs
```
```
Cloning into 'libs'...
remote: Enumerating objects: 38, done.
remote: Counting objects: 100% (38/38), done.
remote: Compressing objects: 100% (32/32), done.
remote: Total 38 (delta 5), reused 38 (delta 5), pack-reused 0 (from 0)
Receiving objects: 100% (38/38), 6.91 MiB | 7.28 MiB/s, done.
Resolving deltas: 100% (5/5), done.
```

A new folder `libs` has been created in `branches`.

```sh
ls -la                      # show the new 'libs' folder
find libs | grep -v .git    # list content of 'libs' (omitting .git folder)
```

Output shows `.jar` files of various libraries:

```
libs
libs/jackson
libs/jackson/jackson-annotations-2.13.0.jar
libs/jackson/jackson-core-2.13.0.jar
libs/jackson/jackson-databind-2.13.0.jar
libs/jacoco
libs/jacoco/jacocoagent.jar
libs/jacoco/jacococli.jar
libs/junit
libs/junit/apiguardian-api-1.1.2.jar
libs/junit/junit-jupiter-api-5.9.3.jar
libs/junit/junit-platform-commons-1.9.3.jar
libs/junit/opentest4j-1.2.0.jar
libs/junit-platform-console-standalone-1.9.2.jar
libs/logging
libs/logging/log4j-api-2.23.1.jar
libs/logging/log4j-core-2.23.1.jar
libs/logging/log4j-slf4j2-impl-2.23.1.jar
libs/logging/slf4j-api-2.0.16.jar
libs/README.md
```

Learn about the libraries
[here](https://github.com/sgra64/se1-play/tree/libs):

- `jackson`: processing JSON data in Java

- `jacoco`: code coverage library for Java

- `junit`: libraries for JUnit tests

- `logging`: *log4j2* logging library for Java

- `junit-platform-console-standalone-1.9.2.jar`: JUnit test runner

The term *"sourcing"* comes from the *Shell* command `source` that
executes a script in context of the *Shell* process (not as a sub-process).

In this project, script [.env.sh](.env.sh) will:

- set environment variables, e.g.
    `CLASSPATH`, `MODULEPATH`, `JDK_JAVAC_OPTIONS`, `JUNIT_OPTIONS` ... ,

- create configuration files for the IDE and Java tools, e.g.
    e.g. `.classpath`, `.project`, `libs`-link ... and

- create functions to facilitate the *build* process, e.g.
    `show`, `mk`, `clean`, `wipe` ... .

This environment will be set for the *executing shell* and only
last for the existence of the *shell* process (terminal).

When the *shell* process ends, e.g. when the terminal is closed,
*environment variables* and *functions* will be lost (created
configuration files remain).

The *project must be sourced* with every new *shell* processes
and terminal.

*"Source"* the project in the project directory with:

```sh
source .env.sh          # source the project (execute script '.env.sh')
```

Output shows what was created for the environment:

```
setting the project environment with:
 - environment variables:
    - PROJECT_PATH
    - CLASSPATH
    - MODULEPATH
    - JDK_JAVAC_OPTIONS
    - JDK_JAVADOC_OPTIONS
    - JUNIT_CLASSPATH
    - JUNIT_OPTIONS
    - JACOCO_AGENT
 - files created:
    -  ln -s ./branches/libs libs
    - .classpath
    - .project
    - .vscode/.classpath
 - functions and aliases:
    - aliases: mk, build, wipe, clean
    - functions: make, show, cmd, copy, javac_version, coverage_report
\\
project environment is set (use 'wipe' to reset)
```

A new function: `show` shows the commands that are now available
for *building* the project.

```sh
show                    # show commands for build process
show --all              # show complete command list
```
```
source | project:
  source .env.sh

classpath | cp:
  echo $CLASSPATH | tr "[;:]" "\n"

compile:
  javac $(find src -name '*.java') -d bin/classes; \
  copy resources bin/resources

compile-tests:
  javac $(find tests -name '*.java') -d bin/test-classes; \
  copy resources bin/resources

resources:
  copy resources bin/resources

run:
  java application.Runtime

run-tests:
  java $(eval echo $JUNIT_CLASSPATH) org.junit.platform.console.ConsoleLauncher\
    $(eval echo $JUNIT_OPTIONS) \
    --scan-class-path

javadoc:
  javadoc -d docs $(eval echo $JDK_JAVADOC_OPTIONS) \
    $(builtin cd src; find . -type d | sed -e 's/\//./g' -e 's/^\.*//')

clean:
  rm -rf bin logs docs coverage

build:
  mk compile compile-tests run-tests package

wipe:
```

New content was added during *project setup* in the project directory is:

```sh
<se1-play>              # project directory
 |
 +--.env.sh             # file to set/source the project entvironment
 +-<.vscode>            # VSCode project files
 |  +--.classpath           # (generated) file used by VSCoderunner
 |
 +--.classpath          # VSCode, eclipse IDE configuration files,
 +--.project            # generated during "sourcing" the project
 |
 +-<libs> -> ./branches/libs    # link to 'libs'-branch (separately checked out)
 |
 +-<branches>           # home of separately checkout project branches
 |  +-<libs>            # 'libs'-branch containing project libraries
 |     +-<jackson>          # JSON library for Java
 |     +-<jacoco>           # code coverage library
 |     +-<logging>          # logging library
 |     +-<junit>            # library for JUnit5 tests
 |     +--junit-platform-console-standalone-1.9.2.jar   # JUnit5 test runner
```

Test the presence of environment variables:

```sh
echo $CLASSPATH
echo $MODULEPATH
echo $JDK_JAVAC_OPTIONS
```

Command `mk` (make) performs the shown commands.

```sh
mk classpath            # show $CLASSPATH with line-separated output
```

Content created during *project setup* can be removed and re-created
any time.

```sh
wipe                    # remove all content generated during setup
source .env.sh          # re-create setup
```


&nbsp;

## Project Build

*Project Build* is the process to create a final, executable artefact
from sources, typically a '.jar'-file for Java.

Steps include:

1. Compile sources from the `src` folder:
    ```sh
    mk compile
    ```

1. Run the program:
    ```sh
    mk run
    ```

1. Compile test sources from the `tests` folder:
    ```sh
    mk compile-tests
    ```

1. Run tests and perform code coverage analysis:
    ```sh
    mk run-tests
    mk coverage
    ```

1. Package classes to the final artefact, which is for this project
    `bin/application-1.0.0-SNAPSHOT.jar`:
    ```sh
    mk package
    ```

1. Run the final artefact:
    ```sh
    mk run-jar
    ```

1. Create *Javadoc*:
    ```sh
    mk javadoc
    ```

New content created during *project build* in the project directory is:

```sh
 |  # folders generated during project build by java, javac, javadoc
 |
 +-<bin>                        # compiled code (.class files, generated)
 |  +--application-1.0.0-SNAPSHOT.jar   # packaged application
 |  +-<classes>                 # folder with compiled files from 'src'
 |  |  +-<application>                  # package 'application' from 'src'
 |  |  |  +--Application.class
 |  |  |  +--Runtime.class
 |  |  |  +--Runtime$Run.class
 |  |  |  +--Runtime$Runnable.class
 |  |  |  +--Runtime$StartUpStatus.class
 |  |  |  +--Runtime$SupplierWithExceptions.class
 |  |  |  +--package-info.class
 |  |  |  +--package_info.class
 |  |  +--module-info.class
 |  |
 |  +-<resources>               # copy of 'resources' folder with files that
 |  |  +--application.properties    # are accessed at runtime and therefore
 |  |  +--log4j2.properties         # need to be on the CLASSPATH (bin)
 |  |
 |  +-<test-classes>            # folder with compiled tests from 'tests'
 |     +-<application>              # package 'application' from 'tests'
 |        +--Application_0_always_pass_Tests.class  # compiled test class
 |
 +-<docs>                       # folder with javadoc documentation (generated)
 |  +--index.html                   # javadoc landing page
 |  +-- ...                         # more .html and .css files
 |
 +-<coverage>                   # folder with code coverage files (generated)
 |  +--jacoco.exec                  # coverage recording
 |  +--index.html                   # coverage report
```

Content created during *project build* can always be removed and
re-created (re-built).

```sh
mk clean                        # remove 'bin', 'docs', 'coverage'

# re-buil project by performing all build steps
mk compile compile-tests run-tests coverage jar javadoc

# alternatively
mk clean build
```


&nbsp;

## Running the Application

Running the application means that a *configured entry point* is invoked
to start the application. Usually, this is a class with a `main(String[] args)`
function in Java. Multiple such classes may exist.

A more advanced project allows configuration of which class to launch.
In this project, this is achieved by a special class
[Runtime.java](src/application/Runtime.java)
that is included in the `application` package.

In general, a *Runtime* is a system that supports the execution of an application.
*Runtime* systems can be complex frameworks, such as powerful frameworks for
developing professional Java applications
[*Spring*](https://spring.io/),
[*Spring Boot*](https://spring.io/projects/spring-boot) or
[*Jakarta*](https://jakarta.ee/).

In this project, class [Runtime.java](src/application/Runtime.java) introduces to
the concept, which we will see again in *Spring Boot* development later.
*Runtime* here performs simple steps supporting the program execution:

- load [Properties](https://www.baeldung.com/java-properties) from file:
    [resources/application.properties](resources/application.properties),

- load [Logger](https://www.digitalocean.com/community/tutorials/log4j2-example-tutorial-configuration-levels-appenders)
    configuration from file:
    [resources/log4j2.properties](resources/log4j2.properties).

- Select a class for execution by

    - scanning all classes for implementing the `Runtime.Runnable` interface

    - select from those the class of the highest (run-)priority, create an
        instance of this class and invoke the interface method:
        ```java
        @Override
        void run(Properties properties, String[] args);
        ```

    - Run priorities are defined by

        - a `@Run(priority=n)` annotation on a *runnable* class and by

        - the order defined by the `runtime.run.priority` property
            in the `application.properties` file in the
            [*resources*](resources) folder.

        Property order supersedes the annotation priority for priorities:
        `1..999`. Annotation priority `>= 1000` supersedes order priority.

After build, the program can be launched in various ways.

In order to perform its tasks, class `Runtime.java` must be invoked before
the actual application can run:

```sh
java application.Runtime            # start Runtime directly
java application.Application        # start Runtime indirectly in Application.main()

mk run                              # also launches: application.Runtime
```

Starting the application directly will invoke the `main()` function in
[Application.java](src/application/Application.java),
which calls `Runtime` as the first step:

```java
public static void main(String[] args) {
    Runtime.getInstance().start(args);
}
```

The packaged application also has `application.Runtime` defined as
entry point in a file called: [*MANIFEST.MF*](resources/META-INF/MANIFEST.MF)
that defines the program launch for a `.jar` file.

*VSCode* also launches `application.Runtime`, see configuration files:
[*launch.json*](.vscode/launch.json) for *VSCode Run & Debug* or:
[*settings.json*](.vscode/settings.json) for the *Java Code Runner*.

After `Runtime` is finished performing its tasks, it will call the `run()` method
of the selected class passing references to `properties` and `args[]`:

```java
/**
 * Method of the {@link Runtime.Runnable} interface called on an instance
 * created by the {@link Runtime}. Program execution starts here.
 * @param properties from the {@code application.properties} file
 * @param args arguments passed from the command line
 */
@Override
public void run(Properties properties, String[] args) {
    /*
     * 1.) print application name fetched from 'application.name' property
     *     from the 'resources/application.properties' file:
     */
    String key = "application.name";
    String name = (String)properties.get(key);
    if(name==null) {
        name = "???";
        log.warn(String.format("property: \"%s\" not found, used \"%s\" instead", key, name));
    }
    System.out.println(String.format("Hello, \"%s\" (%s)", name, this.getClass().getName()));

    /*
     * 2.) print args[] from command line when called with:
     *     java application.Application AB BC CD
     */
    for(String arg : args) {
        String fmt = String.format("- arg: %s", arg);
        System.out.println(fmt);    // print formatted line
    }
}
```

The application prints the name obtained from the `application.name`
property in the `application.properties` file and then prints command
line arguments.

```sh
mk run 1 23 456                     # run application
java application.Runtime 1 23 456
java application.Application 1 23 456

mk run-jar 1 23 456                 # run packaged application
java -jar bin/application-1.0.0-SNAPSHOT.jar 1 23 456
```

Output is:

```
Hello, "SE-1 Play" (application.Application)
- arg: 1
- arg: 23
- arg: 456
done.
```

The *Logging* system has been configured by `Runtime` such that logfiles
are created in a directory called `logs`. File `runtime.log` contains logs
written by the logger for class `Runtime`.

Content created during *program execution* is:

```sh
 |
 +-<logs>               # log files generated during program execution
   +--runtime.log                  # log file for Runtime.java logger
   +--application.log              # log file for Application.java logger
```


&nbsp;

## Complete Project Content

The complete content of the project is shown below. All generated content
can be removed at any time and re-created by the *Build Process*.

The *Build Process* must always work in a software project.

Only *"source"* content belongs in the code repository, not generated
artefacts. Only *"working"* content must be checked-in, never broken files.

```sh
<se1-play>              # project directory
 |
 +--.env.sh             # file to set/source the project entvironment
 +-<.git>               # local git repository
 |  +--config               # git project settings
 +--.gitignore          # files for git to ignore
 +-- README.md          # this markup file
 |
 +--.classpath          # VSCode, eclipse IDE configuration files,
 +--.project            # generated during "sourcing" the project
 |
 +-<.vscode>                    # VSCode project files
 |  +--settings.json                # project-specific VSCode configuration
 |  +--launch.json                  # launch and debug configurations
 |  +--launch_terminal.sh           # script to launch terminals in VSCode
 |  +--.classpath                   # (generated) file used by VSCoderunner
 |
 +-<src>                        # Java source code
 |  +-<application>                 # package 'application'
 |  |  +--Application.java              # main application file
 |  |  +--Runtime.java                  # basic runtime
 |  |  +--package-info.java             # package documentation
 |  +--module-info.java             # module description (exports, requires)
 |
 +-<resources>                  # none-Java sources, configuration files
 |  +--application.properties       # application configuration file
 |  +--log4j2.properties            # logging configuration file
 |  +-<META-INF>                    # required to package executabe .jar
 |     +--<MANIFEST.MF>             # define main class and CLASSPATH for .jar
 |
 +-<tests>                      # source code for tests
 |  +-<application>                 # package 'application'
 |     +--Application_0_always_pass_Tests.java      # test class
 |
 +-<libs> -> ./branches/libs    # link to 'libs'-branch in folder 'branches'
 |
 +-<branches>                   # home of separately checkout project branches
 |  |
 |  +-<libs>                    # 'libs'-branch containing project libraries
 |     +-<jackson>                  # JSON library for Java
 |     |  +--jackson-annotations-2.13.0.jar
 |     |  +--jackson-core-2.13.0.jar
 |     |  +--jackson-databind-2.13.0.jar
 |     |
 |     +-<jacoco>                   # code coverage library
 |     |  +--jacocoagent.jar
 |     |  +--jacococli.jar
 |     |
 |     +-<logging>                  # logging library
 |     |  +--log4j-api-2.23.1.jar
 |     |  +--log4j-core-2.23.1.jar
 |     |  +--log4j-slf4j2-impl-2.23.1.jar
 |     |  +--slf4j-api-2.0.16.jar
 |     |
 |     +-<junit>                    # library for JUnit5 tests
 |     |  +--apiguardian-api-1.1.2.jar
 |     |  +--junit-jupiter-api-5.9.3.jar
 |     |  +--junit-platform-commons-1.9.3.jar
 |     |  +--opentest4j-1.2.0.jar
 |     |
 |     +--junit-platform-console-standalone-1.9.2.jar   # JUnit5 test runner
 |
 +-<bin>                        # compiled code (.class files, generated)
 |  +--application-1.0.0-SNAPSHOT.jar   # packaged application
 |  |
 |  +-<classes>                 # compiled files from 'src'
 |  |  +-<application>                  # package 'application' from 'src'
 |  |  |  +--Application.class
 |  |  |  +--Runtime.class
 |  |  |  +--Runtime$Run.class
 |  |  |  +--Runtime$Runnable.class
 |  |  |  +--Runtime$StartUpStatus.class
 |  |  |  +--Runtime$SupplierWithExceptions.class
 |  |  |  +--package-info.class
 |  |  |  +--package_info.class
 |  |  +--module-info.class
 |  |
 |  +-<resources>               # files copied from 'resources' folder that
 |  |  +--application.properties    # are accessed at runtime and therefore
 |  |  +--log4j2.properties         # need to be on the CLASSPATH (bin)
 |  |
 |  +-<test-classes>            # compiled tests from 'tests'
 |     +-<application>              # package 'application' in 'tests'
 |     +--Application_0_always_pass_Tests.class     # compiled test class
 |
 +-<docs>                       # javadoc documentation (generated)
 |  +--index.html                   # javadoc landing page
 |  +-- ...                         # more .html and .css files
 |
 +-<coverage>                   # code coverage files (generated)
 |  +--jacoco.exec                  # coverage recording
 |  +--index.html                   # coverage report
 |
 +-<logs>                       # log files (generated during program execution)
    +--runtime.log                  # log file for Runtime.java logger
    +--application.log              # log file for Application.java logger
```
