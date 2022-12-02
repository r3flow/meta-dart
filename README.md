# meta-dart

Yocto Layer for Google Dart CLI/Service projects.

It's based on the [meta-flutter layer](https://github.com/meta-flutter/meta-flutter) (author Joel Winarske) and J-P Nurmi's [research on cross-compiling](https://medium.com/flutter-community/cross-compiling-dart-apps-f88e69824639).

## Layers dependencies

* meta-clang

## Recipes

* dart2-sdk
 * Compiles the Dart SDK for the target and installs this SDK on the target. The default installation directory is `/opt/dart2-sdk`.
* dart2-sdk-native
 * Compiles the Dart SDK for the Host to cross-compile your Dart project. You don't need to use it directly, `dart2-app.bbclas` will do it for you automatically.
* dart2-bin-sdk
 * Installs the offical Google binary ARM distribution on the target. The default installation directory is `/opt/dart2-bin-sdk`.
* cpumodel
 * Example project to create a package from a Dart project. You can create your own recipe based on this.

## Usage

There are two possible use cases:

* If you only want to have SDK available on the target, add the `dart2-bin-sdk` package to the image installation list.

* To create a package from your Dart project, use the example project to create your own recipe. The example recipe can be found in the `recipes-support/cpumodel` directory
 * In this case if you also want the SDK to be available on the target (not required to install and run your package on the target), add the `dart2-sdk` package to the image installation list.

## General

Only x64 host and aarch64 target architectures are supported.
