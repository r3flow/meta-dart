# meta-dart

Yocto Layer for Google Dart CLI/Service projects.

It's based on the [meta-flutter layer](https://github.com/meta-flutter/meta-flutter) (author Joel Winarske) and J-P Nurmi's [research on cross-compiling](https://medium.com/flutter-community/cross-compiling-dart-apps-f88e69824639).

If you like this layer, help us get the Dart compiler to natively support cross-compilation, please thumbs up the following issue on GitHub! 👍 [Allow AOT executables to be cross-compiled](https://github.com/dart-lang/sdk/issues/28617)

## Layers dependencies

* meta-clang

## Recipes

* dart3-sdk
  * Compiles the Dart SDK for the target and installs this SDK on the target. The default installation directory is `/opt/dart3-sdk`.
* dart3-sdk-native
  * Compiles the Dart SDK for the Host to cross-compile your Dart project. You don't need to use it directly, `dart3-app.bbclas` will do it for you automatically.
* dart3-bin-sdk
  * Installs the offical Google binary ARM distribution on the target. The default installation directory is `/opt/dart3-bin-sdk`.
* cpumodel
  * Example project to create a package from a Dart project. You can create your own recipe based on this.

## Usage

There are two possible use cases:

* If you only want to have SDK available on the target, add the `dart3-bin-sdk` package to the image installation list.

* To create a package from your Dart project, use the example project to create your own recipe. The example recipe can be found in the `recipes-support/cpumodel` directory
  * In this case if you also want the SDK to be available on the target (not required to install and run your package on the target), add the `dart3-sdk` package to the image installation list.

## SDK version locking

The version of the SDK is based on the value of `DART3_SDK_VERSION`. The default value of `DART3_SDK_VERSION` is set in `conf/include/dart3-sdk-version.inc`. You can override `DART3_SDK_VERSION` in `local.conf` to choose a different SDK version:

```bash
DART3_SDK_VERSION = "3.1.5"
```
If you use the `dart3-bin-sdk` recipe, you must also provide the hashes of the binary archives:

```bash
DART3_BIN_SDK_X64_SHA256SUM = "4342ba274a4e9f8057079cf9de43b1c7bdb002016ad538313e8ebe942b61bba8"
DART3_BIN_SDK_ARM64_SHA256SUM = "0f0e19c276c99fa3efd6428ea4bef1502f742f2a1f9772959637eec775c10ba"
```

## General

Only x64 host and aarch64 target architectures are supported.
