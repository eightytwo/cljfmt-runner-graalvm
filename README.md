# cljfmt-runner-graalvm

A pre-built binary for [cljfmt-runner](https://github.com/JamesLaverack/cljfmt-runner), created with [GraalVM](https://www.graalvm.org/).

## Quick Start

1. Download the [latest binary](https://github.com/eightytwo/cljfmt-runner-graalvm/releases/latest) from the releases page.

2. Move the downloaded binary to a directory that's on your path, e.g. `~/.local/bin`.

3. Check the formatting of some Clojure code.
    ```shell script
    $ pwd
    ~/projects/hello

    $ cljfmt --check
    Checked 6 file(s)
    1 file(s) were incorrectly formatted
    --- a/src/clj/hello/core.clj
    +++ b/src/clj/hello/core.clj
    @@ -5,5 +5,5 @@
       (str "Hello, " text "?"))

     (defn -main
    -     ([] (println (say-hello "world")))
    +  ([] (println (say-hello "world")))
       ([text] (println (say-hello text))))
    ```

4. Fix the formatting errors.
    ```shell script
    $ cljfmt --fix
    Checked 6 file(s)
    Fixing 1 file(s)
    ```

It is also possible to include additional directories for checking:
```shell script
$ cljfmt --check -- -d dev
```

## What's This About?

There's a lot of nice Clojure tooling for developers, such as [`cljfmt`](https://github.com/weavejester/cljfmt), which formats Clojure code idiomatically. These tools are typically run via [`clj`](https://clojure.org/guides/deps_and_cli) (or [`lein`](https://github.com/technomancy/leiningen)) and can therefore take seconds to complete. This is because the Clojure code needs to be compiled to Java bytecode which is then executed in a Java virtual machine.

This isn't a huge problem if you are only running `cljfmt` once or twice. However, if you are going to run `cljfmt` frequently, such as in a [pre-commit](https://pre-commit.com/) hook, then execution time becomes really important.

## How to Create a Native Binary

This project uses the following to create native binaries:
* [clj.native-image](https://github.com/taylorwood/clj.native-image) - a Clojure program for building GraalVM native images using Clojure Deps and CLI tools.
* [oracle/graalvm-ce](https://hub.docker.com/r/oracle/graalvm-ce) - A Docker image with GraalVM installed.

1. Clone this repository.
    ```shell script
    $ git clone https://github.com/eightytwo/cljfmt-runner-graalvm.git
    ```

2. Change to the project directory.
    ```shell script
    $ cd cljfmt-runner-graalvm
    ```

3. Build the Docker image.
    ```shell script
    $ docker-compose build
    Building graalvm-builder

    Step 1/7 : FROM oracle/graalvm-cd:20.1.0
    ...
    Successfully tagged clj-native-graalvm-builder:1.0
    ```

4. Build a native binary of `cljfmt-runner`.
    ```shell script
    $ docker-compose run --rm graalvm-builder
    ```

The native binary will be placed in the project directory and will be called `cljfmt`. Feel free to move this to a directory on your path so you can easily format Clojure code.

## Reducing the Size of the Binary

You might have noticed the binary size is almost 30MB! Using the [Ultimate Packer for eXecutables](https://github.com/upx/upx) (UPX) you can drastically reduce the size.

1. Download the [latest release](https://github.com/upx/upx/releases/latest) of UPX.

2. Run UPX on the `cljfmt` binary.
```shell script
$ ./upx cljfmt
                       Ultimate Packer for eXecutables
                          Copyright (C) 1996 - 2020
UPX 3.96        Markus Oberhumer, Laszlo Molnar & John Reiser   Jan 23rd 2020

        File size         Ratio      Format      Name
   --------------------   ------   -----------   -----------
  30211888 ->   7122948   23.58%   linux/amd64   cljfmt

Packed 1 file.
```

The packed binary is now 7MB. That's still large, but a 75% reduction in size is certainly a nice improvement!

## Credits

* [cljfmt](https://github.com/weavejester/cljfmt): a great tool for developers that helps avoid formatting discussions on pull requests. This project was also a great resource for learning how to deal with command line arguments in Clojure.

* [cljfmt-runner](https://github.com/JamesLaverack/cljfmt-runner): making it possible to use `cljfmt` in `tools.deps` projects.

* [clj.native-image](https://github.com/taylorwood/clj.native-image): providing functionality to build GraalVM native images with Clojure Deps and CLI tools. This was also a valuable resource for [learning about GraalVM's reflection config](https://github.com/taylorwood/clj.native-image/issues/3#issuecomment-434137936) which was necessary to resolve a build error. Coincidentally, this discussion was related to [a fork of cljfmt-runner](https://github.com/aviflax/cljfmt-runner/tree/native-image#building-a-native-imageexecutable) for building a native executable.

* [UPX project](https://github.com/upx/upx): making it possible to not have to lug around a 30MB executable.
