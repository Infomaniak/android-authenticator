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
            checksum: "31811095324c199650afebe2faebfa7226e57fc6db1f15847ed38623978d304e"
        ),
    ]
)
