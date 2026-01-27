// swift-tools-version:5.10
import PackageDescription

let package = Package(
    name: "Multiplatform-lib",
    platforms: [
        .iOS(.v14),
        .macOS(.v11)
    ],
    products: [
        .library(name: "multiplatform-lib", targets: ["MultiplatformLib"])
    ],
    targets: [
        .binaryTarget(
            name: "MultiplatformLib",
            url: "https://github.com/Infomaniak/android-authenticator/multiplatform-lib/releases/download/0.0.2/MultiplatformLib.xcframework.zip",
            checksum: "6f828b0d757f1a47914c8587cb11821aca755fa2b2b163e60cdd2b7ac5987927"
        ),
    ]
)
