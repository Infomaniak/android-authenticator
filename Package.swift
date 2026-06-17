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
            url: "https://github.com/Infomaniak/android-authenticator/releases/download/0.0.2/CoreAuthenticator.xcframework.zip",
            checksum: "31c86b3b872eac548de98a4aa69c214f6d06a2b95f43a47353bb9545fa37b83e"
        ),
    ]
)
