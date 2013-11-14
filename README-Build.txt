This directory only contains the code to build the Android front-end.  To build the complete system you also
need to build Poly/ML itself.  That isn't completely straightforward because the build process builds the run-time
system and then runs some Poly/ML code to compile in the basis library.

First install the Android Native Development kit.  Run the following in the Poly/ML source directory

export NDK_ROOT=<path to where you installed the NDK>

export PATH=$PATH:$NDK_ROOT/toolchains/arm-linux-androideabi-4.8/prebuilt/linux-x86_64/bin
export SYSROOT=$NDK_ROOT/platforms/android-3/arch-arm
export CXX_SYSROOT=${NDK_ROOT}/sources/cxx-stl/gnu-libstdc++/4.8
export CXX_BITS_INCLUDE=${CXX_SYSROOT}/libs/armeabi/include
export CFLAGS="--sysroot=${SYSROOT}"
export CXXFLAGS="--std=c++11 --sysroot=${SYSROOT} -I${CXX_SYSROOT}/include -I${CXX_BITS_INCLUDE} -fexceptions"
export LIBS="-lc"
export LDFLAGS="-Wl,-rpath-link=${SYSROOT}/usr/lib -L${SYSROOT}/usr/lib -L${CXX_SYSROOT}/libs/armeabi -lgnustl_static"

./configure --host=arm-linux-androideabi --target=arm-linux-androideabi

Now build Poly/ML with

make

This will fail when it gets to try to run polyimport.  You need to copy the following to an Android device
polyimport
polytemp.txt
exportPoly.sml
basis/
mlsource/

The polyimport file needs to be made executable and this doesn't seem to be possible if it is on the SD card.
That may require the device to be rooted so that it can be copied to the data directory.
Then run
./polyimport -H 50 polytemp.txt < exportPoly.sml

If this succeeds you should have
polyexport.o
Copy this back to your Poly/ML source directory, type "make" again and it should complete.

Now copy the "poly" file into the libs/armeabi directory and name it "libpolyexecutable.so".  Yes, it isn't really
a shared library but that is the simplest way to make sure it gets included in the APK and installed in the
target device with executable permission.

DCJM 14/11/13.
