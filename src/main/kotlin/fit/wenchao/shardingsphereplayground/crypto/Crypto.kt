package fit.wenchao.shardingsphereplayground.crypto

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.bouncycastle.util.encoders.Base64
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class Crypto {

}

object AesUtils {
    @Throws(NoSuchAlgorithmException::class, InvalidAlgorithmParameterException::class, InvalidKeyException::class)
    fun encrypt(transformation: String?, key: ByteArray?, iv: ByteArray?, message: ByteArray?): ByteArray {
        val cipher: Cipher = Cipher.getInstance(transformation)
        val keySpec = SecretKeySpec(key, "AES")
        val ivSpec = IvParameterSpec(iv)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
        return cipher.doFinal(message)
    }

    @Throws(NoSuchAlgorithmException::class, InvalidAlgorithmParameterException::class, InvalidKeyException::class)
    fun decrypt(transformation: String?, key: ByteArray?, iv: ByteArray?, message: ByteArray?): ByteArray {
        val cipher: Cipher = Cipher.getInstance(transformation)
        val keySpec = SecretKeySpec(key, "AES")
        val ivSpec = IvParameterSpec(iv)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
        return cipher.doFinal(message)
    }
}

interface AesEncrypt {
    fun encrypt(transformation: String, key: ByteArray, iv: ByteArray, message: ByteArray): ByteArray
    fun decrypt(transformation: String, key: ByteArray, iv: ByteArray, message: ByteArray): ByteArray
}


class AesEncryptLocal : AesEncrypt {
    override fun encrypt(transformation: String, key: ByteArray, iv: ByteArray, message: ByteArray): ByteArray {
        return AesUtils.encrypt(transformation, key, iv, message)
    }

    override fun decrypt(transformation: String, key: ByteArray, iv: ByteArray, message: ByteArray): ByteArray {
        return AesUtils.decrypt(transformation, key, iv, message)
    }
}

class AesEncryptRemote : AesEncrypt {
    val address = "http://172.27.128.127:8080"
    val client = OkHttpClient()
    override fun encrypt(transformation: String, key: ByteArray, iv: ByteArray, message: ByteArray): ByteArray {
        val requestEncrypt = Request.Builder()
            .url("${address}/encrypt")
            .post(String(message).toRequestBody("text/plain".toMediaType()))
            .build()

        var encrypted:String? = null
        val responseEncrypt = client.newCall(requestEncrypt).execute()
        if (responseEncrypt.isSuccessful) {
            encrypted = responseEncrypt.body?.string()
            return Base64.decode(encrypted)
        }

        throw Exception("Error encrypting message: ${responseEncrypt.code} ${responseEncrypt.message}")
    }

    override fun decrypt(transformation: String, key: ByteArray, iv: ByteArray, message: ByteArray): ByteArray {
        // Decrypt an encrypted message
        val requestDecrypt = Request.Builder()
            .url("${address}/decrypt")
            .post(String(Base64.encode(message)).toRequestBody("text/plain".toMediaType()))
            .build()

        val responseDecrypt = client.newCall(requestDecrypt).execute()
        if (responseDecrypt.isSuccessful) {
            val decrypted = responseDecrypt.body?.string()
            return decrypted!!.toByteArray()
        }

        throw Exception("Error decrypting message: ${responseDecrypt.code} ${responseDecrypt.message}")
    }

}