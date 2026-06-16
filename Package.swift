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
            checksum: "0a0524a8b0fcb9276143569b328fa9c750955af9e63a18be2f7e3a444377af17"
        ),
    ]
)
