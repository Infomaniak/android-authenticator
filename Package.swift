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
            checksum: "3da2578623878274cf167bab0bd07e71c9a0378b0baa4e0d9d3151954fea255c"
        ),
    ]
)
