package fit.wenchao.encryptservice.crypto

import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object AesUtils {
    @Throws(NoSuchAlgorithmException::class, InvalidAlgorithmParameterException::class, InvalidKeyException::class)
    fun encrypt(algorithm: String?, key: ByteArray?, iv: ByteArray?, message: ByteArray?): ByteArray {
        val cipher: Cipher = Cipher.getInstance(algorithm)
        val keySpec = SecretKeySpec(key, "AES")
        val ivSpec = IvParameterSpec(iv)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)
        return cipher.doFinal(message)
    }

    @Throws(NoSuchAlgorithmException::class, InvalidAlgorithmParameterException::class, InvalidKeyException::class)
    fun decrypt(algorithm: String?, key: ByteArray?, iv: ByteArray?, message: ByteArray?): ByteArray {
        val cipher: Cipher = Cipher.getInstance(algorithm)
        val keySpec = SecretKeySpec(key, "AES")
        val ivSpec = IvParameterSpec(iv)
        cipher.init(Cipher.DECRYPT_MODE, keySpec)
        return cipher.doFinal(message)
    }
}

fun main() {


    // Encrypt and decrypt a message using AES
    try {
        val aesKey = "my23secretaeskey".toByteArray()
        val iv = ByteArray(16)
        val message = "Hello, world!".toByteArray()
        val encrypted = AesUtils.encrypt("AES/CBC/PKCS5Padding", aesKey, iv, message)
        val decrypted = AesUtils.decrypt("AES/CBC/PKCS5Padding", aesKey, iv, encrypted)
        System.out.println("Encrypted message: " + Base64.getEncoder().encodeToString(encrypted))
        println("Decrypted message: " + String(decrypted))
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
}