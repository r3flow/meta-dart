include dart2-sdk-common.inc

inherit logging

DEPENDS:class-native += "\
    ca-certificates-native \
    dart-depot-tools-native \
    git-native \
    bzip2-replacement-native \
    xz-native \
    zlib-native \
    curl-native \
    ncurses-native \
    zstd-native \
    tar-native \
    unzip-native \
    python3-native \
    cmake-native \
    compiler-rt \
    libcxx \
    ninja-native \
    cmake-native \
    "
RUNTIME = "llvm"
TOOLCHAIN = "clang"
PREFERRED_PROVIDER:libgcc = "compiler-rt"

DART2_SDK_DIR = "/opt/dart2-sdk"

S = "${WORKDIR}/src/sdk"

do_unpack[network] = "1"
do_patch[network] = "1"
do_compile[network] = "0"

do_patch[noexec] = "1"
do_package_qa[noexec] = "1"

do_unpack[depends] += " \
    ca-certificates-native:do_populate_sysroot \
    dart-depot-tools-native:do_populate_sysroot \
    git-native:do_populate_sysroot \
    bzip2-replacement-native:do_populate_sysroot \
    xz-native:do_populate_sysroot \
    zlib-native:do_populate_sysroot \
    curl-native:do_populate_sysroot \
    ncurses-native:do_populate_sysroot \
    zstd-native:do_populate_sysroot \
    tar-native:do_populate_sysroot \
    unzip-native:do_populate_sysroot \
    python3-native:do_populate_sysroot \
    ninja-native:do_populate_sysroot \
    cmake-native:do_populate_sysroot \
    "
do_unpack() {
    export PATH=${STAGING_DIR_NATIVE}/usr/share/depot_tools:$PATH 
    export CURL_CA_BUNDLE=${STAGING_DIR_NATIVE}/etc/ssl/certs/ca-certificates.crt
    export PUB_CACHE=${WORKDIR}/.pub-cache
    export http_proxy=${http_proxy}
    export https_proxy=${https_proxy}
    export DEPOT_TOOLS_UPDATE=0
    export GCLIENT_PY3=1

    install -d ${WORKDIR}/src

    bbnote "dart2-sdk fetch: starting"
    cd ${WORKDIR}/src
    fetch --force dart

    bbnote "dart2-sdk checkout tags/${DART2_SDK_VERSION}: starting"
    cd ${WORKDIR}/src/sdk
    git checkout tags/${DART2_SDK_VERSION}
    gclient sync -D
}

do_compile[depends] += " \
    dart-depot-tools-native:do_populate_sysroot \
    git-native:do_populate_sysroot \
    bzip2-replacement-native:do_populate_sysroot \
    xz-native:do_populate_sysroot \
    zlib-native:do_populate_sysroot \
    curl-native:do_populate_sysroot \
    ncurses-native:do_populate_sysroot \
    zstd-native:do_populate_sysroot \
    tar-native:do_populate_sysroot \
    python3-native:do_populate_sysroot \
    ninja-native:do_populate_sysroot \
    cmake-native:do_populate_sysroot \
    "
do_compile[progress] = "outof:^\[(\d+)/(\d+)\]\s+"
do_compile:class-native () {
    export HOME=${WORKDIR}
    export PATH=${STAGING_DIR_NATIVE}/usr/share/depot_tools:${WORKDIR}/src/sdk/out/ReleaseX64/dart-sdk/bin:${WORKDIR}/src/sdk/out/ReleaseX64/dart-sdk/bin/utils:$PATH
    export CURL_CA_BUNDLE=${STAGING_DIR_NATIVE}/etc/ssl/certs/ca-certificates.crt
    export PUB_CACHE=${WORKDIR}/.pub-cache
    export DEPOT_TOOLS_UPDATE=0
    export GCLIENT_PY3=1
    export http_proxy
    export https_proxy

    cd ${S}
    bbnote "dart2-sdk x64 compile: starting"
    ${S}/tools/build.py --no-goma --mode release --arch x64 create_sdk
    bbnote "dart2-sdk simarm64 gen_snapshot compile: starting"
    ${S}/tools/build.py --no-goma --mode release -a simarm64 -m product copy_gen_snapshot
    bbnote "dart2-sdk arm64 dartaotruntime compile: starting"
    ${S}/tools/build.py --no-goma --mode release -a arm64 -m product copy_dartaotruntime
    cp -f ${S}/out/ProductSIMARM64/dart-sdk/bin/utils/gen_snapshot ${S}/out/ReleaseX64/dart-sdk/bin/utils/gen_snapshot
    cp -f ${S}/out/ProductXARM64/dart-sdk/bin/dartaotruntime ${S}/out/ReleaseX64/dart-sdk/bin/dartaotruntime

    dart --disable-analytics
    dart --version
    gen_snapshot --version
}

do_compile:class-target () {
    export HOME=${WORKDIR}
    export CURL_CA_BUNDLE=${STAGING_DIR_NATIVE}/etc/ssl/certs/ca-certificates.crt
    export PUB_CACHE=${WORKDIR}/.pub-cache
    export DEPOT_TOOLS_UPDATE=0
    export GCLIENT_PY3=1
    export http_proxy
    export https_proxy

    cd ${S}
    bbnote "dart2-sdk arm64 compile: starting"
    ${S}/tools/build.py --no-goma --mode release --arch arm64 create_sdk
}

SYSROOT_PREPROCESS_FUNCS:class-native += "dart2_compiled_sdk_sysroot_preprocess"

dart2_compiled_sdk_sysroot_preprocess () {
   install -d ${SYSROOT_DESTDIR}/${STAGING_DIR_NATIVE}/${DART2_SDK_DIR}
   cp -R ${D}/${DART2_SDK_DIR}/* ${SYSROOT_DESTDIR}/${STAGING_DIR_NATIVE}/${DART2_SDK_DIR}
}

do_install:class-native () {
    chmod -R a+rw ${S}/out/ReleaseX64/dart-sdk
    install -d ${D}${DART2_SDK_DIR}
    cp -R ${S}/out/ReleaseX64/dart-sdk/* ${D}${DART2_SDK_DIR}
}

do_install:class-target () {
    chmod -R a+rw ${S}/out/ReleaseXARM64/dart-sdk
    install -d ${D}${DART2_SDK_DIR}
    cp -R ${S}/out/ReleaseXARM64/dart-sdk/* ${D}${DART2_SDK_DIR}
}

ALLOW_EMPTY:${PN} = "1"

FILES:${PN} = "${DART2_SDK_DIR}/*"

INSANE_SKIP:${PN} += "ldflags already-stripped file-rdeps"

BBCLASSEXTEND = "native"
