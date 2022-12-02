AUTHOR = "Example Ltd."
HOMEPAGE = "https://example.com"
SECTION = "console/utils"
SUMMARY = "commandline tool"
DESCRIPTION = " \
Reads and prints the CPU model from /proc/cpuinfo \
"

LICENSE = "MIT-Modern-Variant"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/MIT-Modern-Variant;md5=272dea2b67586002978254bc04648ab2"

SRC_URI = "file://cpumodel/"
SRCREV = "1"

# same as the "name" in your pubspec file
PUBSPEC_APPNAME = "cpumodel"
DART2_ENTRY_POINT = "bin/main.dart"
DART2_APP_INSTALL_DIR = "/opt/${PUBSPEC_APPNAME}"

# more options and examples

# optional, you can run a command before
# DART2_PREBUILD_CMD = "dart run build_runner build --delete-conflicting-outputs"

# by default, the name of the cmpiled binary is same as the PUBSPEC_APPNAME
# you can change this if you want to give a different name
# DART2_APP_BIN_NAME = "customBinName"

# there are two ways to specify where the binary should be installed on the target
# you can specify an exact path where the binary will be installed in the
# DART2_APP_INSTALL_DIR variable (see above)
#
# or you can specify a parent path in DART2_APPLICATION_INSTALL_PREFIX
# and your binary will be installed in the 
# DART2_APPLICATION_INSTALL_PREFIX/PUBSPEC_APPNAME directory. this can be useful
# if you have multiple projects and the parent path comes from common configuration.
# if both DART2_APP_INSTALL_DIR and DART2_APPLICATION_INSTALL_PREFIX are defined, the
# former will be used.

# S should points to the directory were the dart project is located
S = "${WORKDIR}/${PUBSPEC_APPNAME}"

inherit dart2-app

# here you can customize the do_compile() and/or do_install() if needed.
