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
            url: "https://github.com/Infomaniak/android-authenticator/multiplatform-lib/releases/download/0.0.2/CoreAuthenticator.xcframework.zip",
            checksum: "7de3aec67a8188dd6f5919c8f9678cb04563c4ecf3a45c555fc01758bff9d8f0"
        ),
    ]
)
