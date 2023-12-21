require dart3-sdk-common.inc

DEPENDS:class-native += "\
    ca-certificates-native \
    curl-native \
    unzip-native \
    "

RDEPENDS:${PN}-native:class-native += "ca-certificates-native curl-native perl perl-modules unzip-native"
RDEPENDS_${PN}:class-target += "ca-certificates"

SRCREV = "${DART3_SDK_VERSION}"

COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE:aarch64 = "(.*)"
COMPATIBLE_MACHINE:x86 = "(.*)"
COMPATIBLE_MACHINE:x86-64 = "(.*)"

DART3_BIN_SDK_TARGET_ARCH:x86-64 = "x64"
DART3_BIN_SDK_TARGET_ARCH:aarch64 = "arm64"

SRC_URI:class-native = "https://storage.googleapis.com/dart-archive/channels/stable/release/${SRCREV}/sdk/dartsdk-linux-x64-release.zip;downloadfilename=${BPN}-${SRCREV}-x64-native-release.zip;subdir=extracted;name=host"
SRC_URI[host.sha256sum] = "${DART3_BIN_SDK_X64_SHA256SUM}"

SRC_URI:class-target = "https://storage.googleapis.com/dart-archive/channels/stable/release/${SRCREV}/sdk/dartsdk-linux-${DART3_BIN_SDK_TARGET_ARCH}-release.zip;downloadfilename=${BPN}-${SRCREV}-${DART3_BIN_SDK_TARGET_ARCH}-target-release.zip;subdir=extracted;name=target"
SRC_URI[target.sha256sum] = "${DART3_BIN_SDK_ARM64_SHA256SUM}"

S = "${WORKDIR}/extracted/dart-sdk"
DART3_BIN_SDK_DIR = "/opt/dart3-bin-sdk"

SYSROOT_PREPROCESS_FUNCS:class-native += "dart3_sdk_sysroot_preprocess"

dart3_sdk_sysroot_preprocess () {
   install -d ${SYSROOT_DESTDIR}/${STAGING_DIR_NATIVE}/${DART3_BIN_SDK_DIR}
   cp -R ${D}/${DART3_BIN_SDK_DIR}/* ${SYSROOT_DESTDIR}/${STAGING_DIR_NATIVE}/${DART3_BIN_SDK_DIR}
}

do_unpack[network] = "1"
do_patch[network] = "1"
do_compile[network] = "1"

do_compile:class-target[noexec] = "1"
do_package_qa:class-target[noexec] = "1"

do_compile:class-native () {
    export HOME=${WORKDIR}
    export CURL_CA_BUNDLE=${STAGING_DIR_NATIVE}/etc/ssl/certs/ca-certificates.crt
    export PATH=${S}/bin:$PATH
    export PUB_CACHE=${WORKDIR}/.pub-cache
    export http_proxy
    export https_proxy

    dart --disable-analytics
    dart --version
}

do_install:class-target () {
    chmod a+rw ${S} -R
    install -d ${D}${DART3_BIN_SDK_DIR}
    cp -R ${S}/* ${D}${DART3_BIN_SDK_DIR}
}

ALLOW_EMPTY:${PN}:class-native = "1"

FILES:${PN} += "${DART3_BIN_SDK_DIR}/*"

INSANE_SKIP:${PN}:class-native += "already-stripped file-rdeps"
INSANE_SKIP:${PN}:class-target += "ldflags already-stripped file-rdeps"

BBCLASSEXTEND = "native"
