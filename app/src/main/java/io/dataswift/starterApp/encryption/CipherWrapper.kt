package com.hubofallthings.notables.encryption

import android.util.Base64
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec

/**
 * This class wraps [Cipher] class apis with some additional possibilities.
 */
class CipherWrapper(val transformation: String) {

    companion object {
        /**
         * For default created asymmetric keys
         */
        var TRANSFORMATION_ASYMMETRIC = "RSA/ECB/PKCS1Padding"

        /**
         * For default created symmetric keys
         */
        var TRANSFORMATION_SYMMETRIC = "AES/CBC/PKCS7Padding"

        val IV_SEPARATOR = "]"

    }
    val cipher: Cipher = Cipher.getInstance(transformation)

    /**
     * Encrypts data using the key.
     *
     * @param data the data to encrypt
     * @param key the key to encrypt data with
     * @param useInitializationVector encrypt data using initialization vector generated by system. Vector will be added
     * as a prefix to the result of encryption, separated with [IV_SEPARATOR]. Result example - "aaaaaa]bbbbbb", where `aaaaaa`
     * is an created Initialization Vector and `bbbbbb` the  encrypted to data. `false` by default.
     *
     */
    fun encrypt(data: String?, key: Key?, useInitializationVector: Boolean = false): String {
        cipher.init(Cipher.ENCRYPT_MODE, key)

        var result = ""
        if (useInitializationVector) {
            val iv = cipher.iv
            val ivString = Base64.encodeToString(iv, Base64.DEFAULT)
            result = ivString + IV_SEPARATOR
        }

        val bytes = cipher.doFinal(data?.toByteArray())
        result += Base64.encodeToString(bytes, Base64.DEFAULT)

        return result
    }

    /**
     * Decrypts data using the key.
     *
     * @param data the data to decrypt
     * @param key the key to decrypt data with
     * @param useInitializationVector decrypt data using initialization vector. Vector must be added
     * as a prefix to the encryption data, separated with [IV_SEPARATOR]. Data example - "aaaaaa]bbbbbb", where `aaaaaa`
     * is an Initialization Vector and `bbbbbb` the  data to decrypt. `false` by default.
     */
    fun decrypt(data: String, key: Key?, useInitializationVector: Boolean = false): String {
        val encodedString: String

        if (useInitializationVector) {
            val split = data.split(IV_SEPARATOR.toRegex())
            if (split.size != 2) throw IllegalArgumentException("Passed data is incorrect. There was no IV specified with it.")

            val ivString = split[0]
            encodedString = split[1]
            val ivSpec = IvParameterSpec(Base64.decode(ivString, Base64.DEFAULT))
            if(key != null){
                cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)
            } else{
                return ""
            }
        } else {
            encodedString = data
            cipher.init(Cipher.DECRYPT_MODE, key)
        }

        val encryptedData = Base64.decode(encodedString, Base64.DEFAULT)
        val decodedData = cipher.doFinal(encryptedData)
        return String(decodedData)
    }

    /**
     * Wraps(encrypts) a key with another key.
     */
    fun wrapKey(keyToBeWrapped: Key, keyToWrapWith: Key?): String {
        cipher.init(Cipher.WRAP_MODE, keyToWrapWith)
        val decodedData = cipher.wrap(keyToBeWrapped)
        return Base64.encodeToString(decodedData, Base64.DEFAULT)
    }

    /**
     * Unwraps(decrypts) a key with another key. Requires wrapped key algorithm and type.
     */
    fun unWrapKey(wrappedKeyData: String, algorithm: String, wrappedKeyType: Int, keyToUnWrapWith: Key?): Key {
        val encryptedKeyData = Base64.decode(wrappedKeyData, Base64.DEFAULT)
        cipher.init(Cipher.UNWRAP_MODE, keyToUnWrapWith)
        return cipher.unwrap(encryptedKeyData, algorithm, wrappedKeyType)
    }
}