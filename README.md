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

## SDK version locking

The version of the SDK is based on the value of `DART2_SDK_VERSION`. The default value of `DART2_SDK_VERSION` is set in `conf/include/dart2-sdk-version.inc`. You can override `DART2_SDK_VERSION` in `local.conf` to choose a different SDK version:

```bash
DART2_SDK_VERSION = "2.18.4"
```
If you use the `dart2-bin-sdk` recipe, you must also provide the hashes of the binary archives:

```bash
DART2_BIN_SDK_X64_SHA256SUM = "945c3e29ac7386e00c9eeeb2a5ccc836acb0ce9883fbc29df82fd41c90eb3bd6"
DART2_BIN_SDK_ARM64_SHA256SUM = "b279454d8e2827800b18b736d745126c8d99ffffdcc752156145a6ed5a39cf62"
```

## General

Only x64 host and aarch64 target architectures are supported.
