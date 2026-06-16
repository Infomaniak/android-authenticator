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
            checksum: "109eaffca1bdd653c93352bc5f7483bb94ef800d924b43de1416874b5df42bf7"
        ),
    ]
)
