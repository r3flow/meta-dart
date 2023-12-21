SUMMARY = "Dart makes it easy and fast to build beautiful apps for mobile and beyond."
DESCRIPTION = "Dart is Google's SDK, is used by developers and organizations around the world, \
               and is free and open source."
AUTHOR = "Google"
HOMEPAGE = "https://dart.dev/"
BUGTRACKER = "https://github.com/dart-lang/sdk/issues"
SECTION = "devtools"
CVE_PRODUCT = ""

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=29b4ad63b1f1509efea6629404336393"

require conf/include/dart3-sdk-version.inc

do_install:class-target:append () {
    install -d ${D}${sysconfdir}/skel/.dart
    echo "" > ${D}${sysconfdir}/skel/.dart/analytics.json
    echo "{ \"disclosureShown\": true, \"enabled\": false }" > ${D}${sysconfdir}/skel/.dart/dartdev.json
    install -d ${D}${sysconfdir}/skel/.dart-tool
    echo "reporting=0" > ${D}${sysconfdir}/skel/.dart-tool/dart-flutter-telemetry.config
}

FILES:${PN} = "\
    ${sysconfdir}/* \
    "
