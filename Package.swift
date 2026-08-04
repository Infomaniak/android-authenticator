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
            url: "https://github.com/Infomaniak/android-authenticator/releases/download/0.0.5/CoreAuthenticator.xcframework.zip",
            checksum: "c9dc4dd63cec4bc9de69a1e92f2212ce99d10d4dc8d902ab6a4fd8432d74a8f8"
        ),
    ]
)
