// swift-tools-version:5.10
import PackageDescription

let package = Package(
    name: "Authenticator-Core",
    platforms: [
        .iOS(.v14),
    ],
    products: [
        .library(name: "CoreAuthenticator", targets: ["CoreAuthenticator"])
    ],
    targets: [
        .binaryTarget(
            name: "CoreAuthenticator",
            url: "https://github.com/Infomaniak/android-authenticator/releases/download/0.0.4/CoreAuthenticator.xcframework.zip",
            checksum: "1285be60ce37248ec84690716d5599fd14bffd86a035d89b3f05d3d4a6acecd8"
        ),
    ]
)
