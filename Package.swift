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
            checksum: "c5fae14732a157438a4f08f73f1806dc8ea1794c956aa232889afd52681ebb9d"
        ),
    ]
)
