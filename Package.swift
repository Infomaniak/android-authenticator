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
            url: "https://github.com/Infomaniak/android-authenticator/releases/download/0.0.7/CoreAuthenticator.xcframework.zip",
            checksum: "2646b9d9480dd66520f537c852730d55ceeeb9d944fd4db63bab8e055292a6d1"
        ),
    ]
)
