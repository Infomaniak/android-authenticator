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
            url: "https://github.com/Infomaniak/android-authenticator/releases/download/0.0.9/CoreAuthenticator.xcframework.zip",
            checksum: "cb47bd70eef5981e086bc0131a3ef318b62d65e13f6cd553d8ad7a776d93b728"
        ),
    ]
)
