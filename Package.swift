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
            url: "https://github.com/Infomaniak/android-authenticator/releases/download/0.0.3/CoreAuthenticator.xcframework.zip",
            checksum: "877d015d8d5d96cee4dacb13f5b26f7b82c04508ebc7fe71459ea0c4fdbd07be"
        ),
    ]
)
