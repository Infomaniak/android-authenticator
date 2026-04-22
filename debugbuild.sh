./buildRelease 1.0.0 && unzip release/CoreAuthenticator.xcframework.zip -d release/CoreAuthenticator.xcframework
rm -rf $1/Debug-Frameworks/CoreAuthenticator.xcframework.bak
mv $1/Debug-Frameworks/CoreAuthenticator.xcframework $1/Debug-Frameworks/CoreAuthenticator.xcframework.bak
mv release/CoreAuthenticator.xcframework/multiplatform-lib/build/XCFrameworks/release/CoreAuthenticator.xcframework $1/Debug-Frameworks/CoreAuthenticator.xcframework

cd $1
tuist generate
