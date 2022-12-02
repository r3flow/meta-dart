include dart2-sdk-common.inc

inherit logging

DEPENDS:class-native += "\
    ca-certificates-native \
    curl-native \
    unzip-native \
    "

RDEPENDS:${PN}-native:class-native += "ca-certificates-native curl-native perl perl-modules unzip-native"
RDEPENDS_${PN}:class-target += "ca-certificates"

SRCREV = "${DART2_SDK_VERSION}"

SRC_URI:class-native = "https://storage.googleapis.com/dart-archive/channels/stable/release/${SRCREV}/sdk/dartsdk-linux-x64-release.zip;downloadfilename=${BPN}-${SRCREV}-x64-release.zip;subdir=extracted;name=host"
SRC_URI[host.sha256sum] = "945c3e29ac7386e00c9eeeb2a5ccc836acb0ce9883fbc29df82fd41c90eb3bd6"

SRC_URI:class-target = "https://storage.googleapis.com/dart-archive/channels/stable/release/${SRCREV}/sdk/dartsdk-linux-arm64-release.zip;downloadfilename=${BPN}-${SRCREV}-arm64-release.zip;subdir=extracted;name=target"
SRC_URI[target.sha256sum] = "b279454d8e2827800b18b736d745126c8d99ffffdcc752156145a6ed5a39cf62"

S = "${WORKDIR}/extracted/dart-sdk"
DART2_BIN_SDK_DIR = "/opt/dart2-bin-sdk"

SYSROOT_PREPROCESS_FUNCS:class-native += "dart2_sdk_sysroot_preprocess"

dart2_sdk_sysroot_preprocess () {
   install -d ${SYSROOT_DESTDIR}/${STAGING_DIR_NATIVE}/${DART2_BIN_SDK_DIR}
   cp -R ${D}/${DART2_BIN_SDK_DIR}/* ${SYSROOT_DESTDIR}/${STAGING_DIR_NATIVE}/${DART2_BIN_SDK_DIR}
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

do_install() {
    chmod a+rw ${S} -R
    install -d ${D}${DART2_BIN_SDK_DIR}
    cp -R ${S}/* ${D}${DART2_BIN_SDK_DIR}
}

ALLOW_EMPTY:${PN}:class-native = "1"

FILES:${PN} = "${DART2_BIN_SDK_DIR}/*"

INSANE_SKIP:${PN}:class-native += "already-stripped file-rdeps"
INSANE_SKIP:${PN}:class-target += "ldflags already-stripped file-rdeps"

BBCLASSEXTEND = "native"
