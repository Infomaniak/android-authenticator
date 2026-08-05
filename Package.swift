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
            url: "https://github.com/Infomaniak/android-authenticator/releases/download/0.0.8/CoreAuthenticator.xcframework.zip",
            checksum: "96666a08ffc833972ad987cced1e3ff5684b09cf3bbb57ccd5a42e2544ac0bd1"
        ),
    ]
)
