// swift-tools-version:5.10
import PackageDescription

let package = Package(
    name: "Multiplatform-lib",
    platforms: [
        .iOS(.v14),
    ],
    products: [
        .library(name: "multiplatform-lib", targets: ["CoreAuthenticator"])
    ],
    targets: [
        .binaryTarget(
            name: "CoreAuthenticator",
            url: "https://github.com/Infomaniak/android-authenticator/releases/download/0.0.1/CoreAuthenticator.xcframework.zip",
            checksum: "031c0fb3c94546221b805e1df1f96247270fc991d39c2bb838e1a4c24259c6b2"
        ),
    ]
)
