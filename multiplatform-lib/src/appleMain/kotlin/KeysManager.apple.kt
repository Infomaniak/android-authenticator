import cnames.structs.__CFError
import cnames.structs.__SecKey
import com.infomaniak.auth.lib.KeysManager
import com.infomaniak.auth.lib.PublicKey
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.CValuesRef
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.asStableRef
import kotlinx.cinterop.cValuesOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.readValues
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toCValues
import kotlinx.cinterop.value
import platform.CoreFoundation.CFDictionaryAddValue
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFDictionaryGetCount
import platform.CoreFoundation.CFErrorCreate
import platform.CoreFoundation.CFMutableDictionaryRef
import platform.CoreFoundation.CFNumberCreate
import platform.CoreFoundation.CFStringCreateWithCString
import platform.CoreFoundation.kCFAllocatorDefault
import platform.CoreFoundation.kCFBooleanTrue
import platform.CoreFoundation.kCFNumberIntType
import platform.CoreFoundation.kCFStringEncodingUTF8
import platform.CoreFoundation.kCFTypeDictionaryKeyCallBacks
import platform.CoreFoundation.kCFTypeDictionaryValueCallBacks
import platform.Foundation.NSError
import platform.Security.SecAccessControlCreateWithFlags
import platform.Security.SecKeyCreateRandomKey
import platform.Security.kSecAccessControlPrivateKeyUsage
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAlways
import platform.Security.kSecAttrAccessibleWhenUnlockedThisDeviceOnly
import platform.Security.kSecAttrCanSign
import platform.Security.kSecAttrCanVerify
import platform.Security.kSecAttrIsPermanent
import platform.Security.kSecAttrKeySizeInBits
import platform.posix.err

/*
 * Infomaniak Authenticator - Android
 * Copyright (C) 2026 Infomaniak Network SA
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
class KeysManagerImpl : KeysManager {
    override suspend fun supportsTrustedExecutionEnvironment(): Boolean {
        TODO()
    }

    override suspend fun hasEnrolledBiometrics(): Boolean {
        TODO()
    }

    override suspend fun generateSecurelyStoredKeyPairForSigning(securityParameters: KeysManager.SecurityParameters) {
        TODO()
    }

    override suspend fun updateSecurityParameters(newParameters: KeysManager.SecurityParameters) {
        TODO()
    }

    override suspend fun createAndStoreMigrationKeys() {
        TODO()
    }

    override suspend fun getPublicKey(): PublicKey {
        TODO()
    }

    override suspend fun getMostRecentMigrationPublicKey(): PublicKey {
        TODO()
    }
}

/**
 * Generates an EC key pair in the iOS Keychain for signing and verification.
 *
 * @param alias A unique tag (as bytes) to identify the key pair.
 * @return The private key's reference, or null on failure.
 */
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
fun generateKeyPair(alias: ByteArray): CPointer<__SecKey>? {
    // Use memScoped for memory management within this function scope
    return memScoped {
        println("1!")
        // Convert the alias ByteArray to a CPointer to UByte for the key tag
        val tag = alias.toCValues().ptr
        println("2!")

        // Create a CFDictionary with the key generation attributes.
        // This is equivalent to the KeyGenParameterSpec in Android.
        val attributes = CFDictionaryCreateMutable(
            allocator = kCFAllocatorDefault,
            capacity = 2, // We will add 8 key-value pairs
            keyCallBacks = kCFTypeDictionaryKeyCallBacks.ptr,
            valueCallBacks = kCFTypeDictionaryValueCallBacks.ptr
        )
        // val attributesEmpty = CFDictionaryCreateMutable(
        //     allocator = kCFAllocatorDefault,
        //     capacity = 0, // We will add 8 key-value pairs
        //     keyCallBacks = kCFTypeDictionaryKeyCallBacks.ptr,
        //     valueCallBacks = kCFTypeDictionaryValueCallBacks.ptr
        // )
        println("3!")

        // val a = alloc<Int>()
        // Add attributes to the dictionary
        // CFDictionaryAddValue(attributes, kSecAttrKeyType, kSecAttrKeyTypeECSECPrimeRandom)
        // val int = 256
        val n = CFNumberCreate(kCFAllocatorDefault, kCFNumberIntType, cValuesOf(-3))
        // CFDictionaryAddValue(attributes, kSecAttrKeySizeInBits, CFBridgingRetain(NSNumber(256)))
        attributes[kSecAttrKeySizeInBits] = n
        // attributes[kSecAttrKeySizeInBits] = cValuesOf(256)
        // attributes[kSecAttrIsPermanent] = kCFBooleanTrue
        attributes[kSecAttrIsPermanent] = kCFBooleanTrue
        // attributes[kSecAttrApplicationTag] = tag
        attributes[kSecAttrAccessible] = kSecAttrAccessibleWhenUnlockedThisDeviceOnly
        attributes[kSecAttrCanSign] = kCFBooleanTrue
        attributes[kSecAttrCanVerify] = kCFBooleanTrue
        // attributes[kSecAttrAccessibleWhenUnlocked] = kCFBooleanTrue

        val count = CFDictionaryGetCount(attributes)
        println("Count: $count")


        println("4!")
        // return null

        val domain = CFStringCreateWithCString(kCFAllocatorDefault, "com.infomaniak.auth.lib", kCFStringEncodingUTF8)

        val error = CFErrorCreate(
            allocator = kCFAllocatorDefault,
            domain = domain,
            code = 0L,
            userInfo = null
        )
        val errorValues = cValuesOf<__CFError>(error)
        println("5!")
        val a = alloc<ObjCObjectVar<__CFError>>()
        println("6!")
        // CFErrorRef

        val errorPtr = errorValues.ptr
        println("7!")


        // Call the native function to generate the key pair
        SecAccessControlCreateWithFlags(
            allocator = kCFAllocatorDefault,
            protection = kSecAttrAccessibleAlways,
            flags = kSecAccessControlPrivateKeyUsage,
            error = errorPtr
        )
        println("8!")
        println("${errorPtr.pointed.value}")
        // val b: __CFError = a.value
        println("9!")

        val aaa = errorPtr.pointed.value
        y(errorPtr)

        t(aaa)
        val e = error?.reinterpret<ObjCObjectVar<NSError>>()?.asStableRef<NSError>()?.get()
        println("Reinterpreted")
        println("${e?.localizedDescription}")
        // println("${e}")
        // errorPtr.pointed.value as NSError

        println("10!")
        return null
        val privateKey = SecKeyCreateRandomKey(attributes, errorPtr)

        errorPtr.pointed
        // val writtenErrorRef = errorPtr.pointed.value
        // writtenErrorRef?.let {
        //     errorValues
        //     writtenErrorRef.pointed
        //     it.reinterpret<NSError>()
        // }

        if (privateKey == null) {
            val errorValue = errorPtr.pointed.value
            if (errorValue != null) {
                val a = errorPtr.pointed.value


                // errorPtr.pointed.value.pointed

                // val errorDescription = CFBridgingRetain(errorValue.localizedDescription ?: "Unknown error")
                // println("Failed to generate key pair: ${errorDescription as? String}")
                // CFBridgingRelease(errorDescription)
            }
            return null
        }

        println("Key pair generated successfully.")
        return privateKey
    }
}

@ExperimentalForeignApi
private fun t(aaa: CPointer<__CFError>?) {
    // val a: __CFError = aaa.pointed<__CFError>
}

@ExperimentalForeignApi
private fun y(errorPtr: CPointer<CPointerVar<__CFError>>) {
    errorPtr.pointed.readValues(1)
}

@ExperimentalForeignApi
operator fun CFMutableDictionaryRef?.set(key: CValuesRef<*>?, value: CValuesRef<*>?) {
    CFDictionaryAddValue(this, key, value)
}
