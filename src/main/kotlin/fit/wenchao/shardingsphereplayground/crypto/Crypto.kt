package fit.wenchao.shardingsphereplayground.crypto

import com.ndsec.jce.model.SymmetricParams
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.bouncycastle.util.encoders.Base64
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.Provider
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class Crypto {

}


object SymmUtils {
    @Throws(NoSuchAlgorithmException::class, InvalidAlgorithmParameterException::class, InvalidKeyException::class)
    fun encrypt(
        alg: String,
        transformation: String?,
        key: ByteArray?,
        iv: ByteArray?,
        message: ByteArray?,
        generateParams: (iv: ByteArray?) -> AlgorithmParameterSpec,
    ): ByteArray {
        val cipher: Cipher = Cipher.getInstance(transformation)
        val keySpec = SecretKeySpec(key, alg)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, generateParams(iv))
        return cipher.doFinal(message)
    }

    @Throws(NoSuchAlgorithmException::class, InvalidAlgorithmParameterException::class, InvalidKeyException::class)
    fun decrypt(
        alg: String,
        transformation: String?,
        key: ByteArray?,
        iv: ByteArray?,
        message: ByteArray?,
        generateParams: (iv: ByteArray?) -> AlgorithmParameterSpec,
    ): ByteArray {
        val cipher: Cipher = Cipher.getInstance(transformation)
        val keySpec = SecretKeySpec(key, alg)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, generateParams(iv))
        return cipher.doFinal(message)
    }
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

interface SymmEncrypt {

    /**
     * Encrypt a message using Symm
     *
     * @param alg The algorithm to use, SM4 or AES
     * @param transformation The transformation to use, e.g. AES/CBC/PKCS5Padding
     * @param key The key to use
     * @param iv The iv to use
     * @param message The message to encrypt
     * @return The encrypted message
     */
    fun encrypt(alg: String, transformation: String, key: ByteArray, iv: ByteArray, message: ByteArray): ByteArray

    /**
     * Decrypt a message using Symm
     *
     * @param alg The algorithm to use, SM4 or AES
     * @param transformation The transformation to use, e.g. AES/CBC/PKCS5Padding
     * @param key The key to use
     * @param iv The iv to use
     * @param message The message to decrypt
     * @return The decrypted message
     *
     */
    fun decrypt(alg: String, transformation: String, key: ByteArray, iv: ByteArray, message: ByteArray): ByteArray
}

class SymmEncryptLocal : SymmEncrypt {
    override fun encrypt(
        alg: String,
        transformation: String,
        key: ByteArray,
        iv: ByteArray,
        message: ByteArray,
    ): ByteArray {
        return SymmUtils.encrypt(alg, transformation, key, iv, message) { iv ->
            IvParameterSpec(iv)
        }
    }

    override fun decrypt(
        alg: String,
        transformation: String,
        key: ByteArray,
        iv: ByteArray,
        message: ByteArray,
    ): ByteArray {
        return SymmUtils.decrypt(alg, transformation, key, iv, message) { iv ->
            IvParameterSpec(iv)
        }
    }
}

class SymmEncryptRemote : SymmEncrypt {
    val address = "http://172.27.128.127:8080"
    val client = OkHttpClient()


    // alg and transformation in this impl is not used and depend on remote instead
    override fun encrypt(
        alg: String,
        transformation: String,
        key: ByteArray,
        iv: ByteArray,
        message: ByteArray,
    ): ByteArray {
        val requestEncrypt = Request.Builder()
            .url("${address}/encrypt")
            .post(String(message).toRequestBody("text/plain".toMediaType()))
            .build()

        var encrypted: String? = null
        val responseEncrypt = client.newCall(requestEncrypt).execute()
        if (responseEncrypt.isSuccessful) {
            encrypted = responseEncrypt.body?.string()
            return Base64.decode(encrypted)
        }

        throw Exception("Error encrypting message: ${responseEncrypt.code} ${responseEncrypt.message}")
    }

    override fun decrypt(
        alg: String,
        transformation: String,
        key: ByteArray,
        iv: ByteArray,
        message: ByteArray,
    ): ByteArray {
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


// provider name enum
enum class ProviderName(val value: String) {
    NDSecProvider("NDSecProvider"),
}

class CipherName(val value: String) {
    companion object {
        val AES = CipherName("AES")
        val SM4 = CipherName("SM4")
    }

    override fun toString(): String {
        return value
    }
}

enum class Mode(val value: String) {
    CBC("CBC"),
    ECB("ECB"),
}

enum class Padding(val value: String) {
    PKCS5Padding("PKCS5Padding"),
    NoPadding("NoPadding"),
}


class Transformation {
    companion object {
        val AES_CBC_PKCS5Padding = Transformation(CipherName.AES, Mode.CBC, Padding.PKCS5Padding)
        val AES_ECB_PKCS5Padding = Transformation(CipherName.AES, Mode.ECB, Padding.PKCS5Padding)
        val AES_CBC_NoPadding = Transformation(CipherName.AES, Mode.CBC, Padding.NoPadding)
        val AES_ECB_NoPadding = Transformation(CipherName.AES, Mode.ECB, Padding.NoPadding)

        val SM4_CBC_PKCS5Padding = Transformation(CipherName.SM4, Mode.CBC, Padding.PKCS5Padding)
        val SM4_ECB_PKCS5Padding = Transformation(CipherName.SM4, Mode.ECB, Padding.PKCS5Padding)
        val SM4_CBC_NoPadding = Transformation(CipherName.SM4, Mode.CBC, Padding.NoPadding)
        val SM4_ECB_NoPadding = Transformation(CipherName.SM4, Mode.ECB, Padding.NoPadding)
    }

    var alg: CipherName
    var mode: Mode
    var padding: Padding

    constructor(alg: CipherName, mode: Mode, padding: Padding) {
        this.alg = alg
        this.mode = mode
        this.padding = padding
    }

    override fun toString(): String {
        return "${alg}/${mode}/${padding}"
    }
}



abstract class SymmEncryptBase: SymmEncrypt {
    var provider: Provider? = null

   abstract fun getAlgorithmParameterSpec(iv: ByteArray): AlgorithmParameterSpec

    override fun encrypt(
        alg: String,
        transformation: String,
        key: ByteArray,
        iv: ByteArray,
        message: ByteArray,
    ): ByteArray {
        lateinit var cipher: Cipher
        provider?.let {
            cipher = Cipher.getInstance(transformation, provider)
        } ?: run {
             cipher =  Cipher.getInstance(transformation)
        }

        // external key
        val secretKeySpec = SecretKeySpec(key, alg)

        // init cipher
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, getAlgorithmParameterSpec(iv))

        // encrypt
        val encrypts = cipher.doFinal(message)

        return encrypts
    }

    override fun decrypt(
        alg: String,
        transformation: String,
        key: ByteArray,
        iv: ByteArray,
        message: ByteArray,
    ): ByteArray {
        // get cipher
        var cipher: Cipher =  Cipher.getInstance(transformation)

        provider?.let {
            cipher = Cipher.getInstance(transformation, provider)
        }

        // external key
        val secretKeySpec = SecretKeySpec(key, alg)

        // init cipher
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, getAlgorithmParameterSpec(iv))

        // encrypt
        val encrypts = cipher.doFinal(message)

        return encrypts
    }

}


/**
 * HSM provider. HSM must be connected before instantiate this class.
 */
class SymmEncryptNdsecImpl : SymmEncryptBase() {

    override fun getAlgorithmParameterSpec(iv: ByteArray): AlgorithmParameterSpec {
        val symmetricParams = SymmetricParams()
        symmetricParams.iv = iv
        return symmetricParams
    }

}



class SymmEncryptDefaultImpl : SymmEncryptBase() {

    override fun getAlgorithmParameterSpec(iv: ByteArray): AlgorithmParameterSpec {
        return IvParameterSpec(iv)
    }

}


fun main() {
    // fill the key
    var key = "1234567890123456".toByteArray(   )

    // fill the iv
    var iv = ByteArray(16)

    var message = "hello"

    var alg = CipherName.AES.toString()

    val symmEncryptLocal = SymmEncryptDefaultImpl()
    val encrypt = symmEncryptLocal.encrypt(
        alg,
        Transformation.AES_CBC_PKCS5Padding.toString(),
        key,
        iv,
        message.toByteArray()
    )




    val decrypt = symmEncryptLocal.decrypt(
        alg,
        Transformation.AES_CBC_PKCS5Padding.toString(),
        key,
        iv,
        encrypt
    )

    println(String(decrypt))

}
