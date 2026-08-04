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
            url: "https://github.com/Infomaniak/android-authenticator/releases/download/0.0.6/CoreAuthenticator.xcframework.zip",
            checksum: "08c71d65be9a9817fd4c9c84e1f8fe2e3f6873035bdaac73fedd10e35c965fb2"
        ),
    ]
)
