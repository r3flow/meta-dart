# Helper class for building native ARM64 Dart CLI/Service Application.
# Assumes that:
# - S is defined and points to source directory.
# - PUBSPEC_APPNAME is defined correctly.  This is the name value from pubspec.yml.
#

DEPENDS += " \
    ca-certificates-native \
    dart2-sdk-native \
    "

SRCREV ??= "1"
DART2_PREBUILD_CMD ??= ""
DART2_ENTRY_POINT ??= "bin/main.dart"
DART2_APP_BIN_NAME ??= "${PUBSPEC_APPNAME}"
DART2_APP_OUT_DIR ??= "${S}/build"
DART2_BUILD_ARGS ??= ""
DART2_APPLICATION_INSTALL_PREFIX ??= "/opt"
DART2_APP_INSTALL_DIR ??= "${DART2_APPLICATION_INSTALL_PREFIX}/${PUBSPEC_APPNAME}"

PUB_CACHE = "${WORKDIR}/pub_cache"
PUB_CACHE_ARCHIVE = "dart2-pub-cache-${PUBSPEC_APPNAME}-${SRCREV}.tar.bz2"
DART2_NATIVE_SDK_DIR = "${STAGING_DIR_NATIVE}/opt/dart2-sdk"

#
# Archive Pub Cache
#
addtask archive_pub_cache before do_patch after do_unpack
do_archive_pub_cache[network] = "1"
do_archive_pub_cache[dirs] = "${WORKDIR} ${DL_DIR}"
do_archive_pub_cache[depends] += " \
    dart2-sdk-native:do_populate_sysroot \
    pbzip2-native:do_populate_sysroot \
    tar-native:do_populate_sysroot \
    "
python do_archive_pub_cache() {
    import errno
    import multiprocessing
    from   bb.fetch2 import FetchError
    from   bb.fetch2 import runfetchcmd

    localfile = d.getVar("PUB_CACHE_ARCHIVE")
    localpath = os.path.join(d.getVar("DL_DIR"), localfile)
    
    if os.access(localpath, os.R_OK):
        return

    workdir = d.getVar("WORKDIR")
    pub_cache = d.getVar("PUB_CACHE")
    os.makedirs(pub_cache, exist_ok=True)

    DART2_NATIVE_SDK_DIR = d.getVar("DART2_NATIVE_SDK_DIR")
    app_src_root = d.getVar("S")

    pub_cache_cmd = \
        'export PUB_CACHE=%s; ' \
        '%s/bin/dart pub get;' \
        '%s/bin/dart pub get --offline' % \
        (pub_cache, DART2_NATIVE_SDK_DIR, DART2_NATIVE_SDK_DIR)

    bb.note("Running %s in %s" % (pub_cache_cmd, app_src_root))
    runfetchcmd('%s' % (pub_cache_cmd), d, quiet=False, workdir=app_src_root)

    cp_cmd = \
        'mkdir -p %s/.project | true; ' \
        'cp -r .dart_tool %s/.project/ | true; ' \
        'cp -r .packages %s/.project/ | true; ' \
        'cp -r .metadata %s/.project/ | true; ' \
        % (pub_cache, pub_cache, pub_cache, pub_cache)

    bb.note("Running %s in %s" % (cp_cmd, app_src_root))

    runfetchcmd('%s' % (cp_cmd), d, quiet=False, workdir=app_src_root)

    bb_number_threads = d.getVar("BB_NUMBER_THREADS", multiprocessing.cpu_count()).strip()
    pack_cmd = "tar -I \"pbzip2 -p%s\" -cf %s ./" % (bb_number_threads, localpath)

    bb.note("Running %s in %s" % (pack_cmd, pub_cache))
    runfetchcmd('%s' % (pack_cmd), d, quiet=False, workdir=pub_cache)

    if not os.path.exists(localpath):
        raise FetchError("The fetch command returned success for pub cache, but %s doesn't exist?!" % (localpath), localpath)

    if os.path.getsize(localpath) == 0:
        os.remove(localpath)
        raise FetchError("The fetch of %s resulted in a zero size file?! Deleting and failing since this isn't right." % (localpath), localpath)
}

#
# Restore Pub Cache
#
addtask restore_pub_cache before do_patch after do_archive_pub_cache
do_restore_pub_cache[dirs] = "${WORKDIR} ${DL_DIR}"
do_restore_pub_cache[depends] += " \
    pbzip2-native:do_populate_sysroot \
    tar-native:do_populate_sysroot \
    "
python do_restore_pub_cache() {
    import multiprocessing
    import shutil
    import subprocess
    from   bb.fetch2 import subprocess_setup
    from   bb.fetch2 import UnpackError
    
    localfile = d.getVar("PUB_CACHE_ARCHIVE")
    localpath = os.path.join(d.getVar("DL_DIR"), localfile)

    bb_number_threads = d.getVar("BB_NUMBER_THREADS", multiprocessing.cpu_count()).strip()
    cmd = 'pbzip2 -dc -p%s %s | tar x --no-same-owner -f -' % (bb_number_threads, localpath)
    unpackdir = d.getVar("PUB_CACHE")
    shutil.rmtree(unpackdir, ignore_errors=True)
    bb.utils.mkdirhier(unpackdir)
    path = d.getVar('PATH')
    if path: cmd = 'PATH=\"%s\" %s' % (path, cmd)
    bb.note("Running %s in %s" % (cmd, unpackdir))
    ret = subprocess.call(cmd, preexec_fn=subprocess_setup, shell=True, cwd=unpackdir)

    if ret != 0:
        raise UnpackError("Unpack command %s failed with return value %s" % (cmd, ret), localpath)

    # restore dart pub get artifacts
    app_src_root = d.getVar("S")
    cmd = \
        'mv .project/.dart2_tool %s/ | true; ' \
        'mv .project/.packages %s/ | true; ' \
        'mv .project/.metadata %s/ | true; ' \
        'rm -rf .project' % (app_src_root, app_src_root, app_src_root)
    bb.note("Running %s in %s" % (cmd, unpackdir))
    ret = subprocess.call(cmd, preexec_fn=subprocess_setup, shell=True, cwd=unpackdir)

    if ret != 0:
        raise UnpackError("Restore .dart2_tool command %s failed with return value %s" % (cmd, ret), localpath)
}

#
# Cross compile dart application
#
do_compile[network] = "0"
do_compile() {
    export HOME=${WORKDIR}
    export PATH=${DART2_NATIVE_SDK_DIR}/bin:$PATH
    export PUB_CACHE=${PUB_CACHE}
    export PKG_CONFIG_PATH=${STAGING_DIR_TARGET}/usr/lib/pkgconfig:${STAGING_DIR_TARGET}/usr/share/pkgconfig:${PKG_CONFIG_PATH}
    export http_proxy
    export https_proxy

    bbnote `env`

    dart --disable-analytics
    install -d ${DART2_APP_OUT_DIR}
    cd ${S}
    dart pub get
    ${DART2_PREBUILD_CMD}
    cd ${S}

    bbnote "dart2 compile ${DART2_BUILD_ARGS}: Starting"
    dart compile exe ${DART2_BUILD_ARGS} -o ${DART2_APP_OUT_DIR}/${DART2_APP_BIN_NAME} ${DART2_ENTRY_POINT}
    bbnote "dart2 compile ${DART2_ENTRY_POINT}: Completed"
}

INSANE_SKIP:${PN} += " ldflags libdir already-stripped"
SOLIBS = ".so"
FILES:SOLIBSDEV = ""

do_install() {
    install -d ${D}${DART2_APP_INSTALL_DIR}
    cp ${DART2_APP_OUT_DIR}/${DART2_APP_BIN_NAME} ${D}${DART2_APP_INSTALL_DIR}/${DART2_APP_BIN_NAME}
    chmod +x ${D}${DART2_APP_INSTALL_DIR}/${DART2_APP_BIN_NAME}
}

FILES:${PN} += "\
    ${bindir} \
    ${libdir} \
    ${DART2_APP_INSTALL_DIR} \
    "

FILES:${PN}-dev = ""
