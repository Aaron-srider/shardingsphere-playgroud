package fit.wenchao.shardingsphereplayground

import com.google.common.base.Preconditions
import com.ndsec.jce.provider.NDSecProvider
import fit.wenchao.shardingsphereplayground.crypto.*
import mu.KotlinLogging
import org.apache.commons.codec.digest.DigestUtils
import org.apache.shardingsphere.encrypt.api.encrypt.standard.StandardEncryptAlgorithm
import org.apache.shardingsphere.encrypt.spi.context.EncryptContext
import java.nio.charset.StandardCharsets
import java.security.GeneralSecurityException
import java.security.Security
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


class CustomLocalEncryptAlgorithm : StandardEncryptAlgorithm<Any?, String?> {
    private val log = KotlinLogging.logger {}

    private var props: Properties? = null

    private lateinit var secretKey: ByteArray

    var aes = SymmEncryptLocal()

    override fun init(props: Properties) {
        this.props = props
        secretKey = createSecretKey(props)
    }

    private fun createSecretKey(props: Properties): ByteArray {
        Preconditions.checkArgument(props.containsKey("aes-key-value"), "%s can not be null.", "aes-key-value")
        return Arrays.copyOf(DigestUtils.sha1(props.getProperty("aes-key-value")), 16)
    }

    override fun encrypt(plainValue: Any?, encryptContext: EncryptContext): String? {
        return try {
            if (null == plainValue) {
                null
            } else {
                val result =
                    aes.encrypt(
                        CipherName.AES.toString(),
                        Transformation.AES_CBC_PKCS5Padding.toString(),
                        secretKey,
                        ByteArray(16),
                        plainValue.toString().toByteArray(StandardCharsets.UTF_8)
                    )
                val encodeToString = Base64.getEncoder().encodeToString(result)
                // log.info { "encrypt ${plainValue} to ${encodeToString}" }
                encodeToString
            }
        } catch (var4: GeneralSecurityException) {
            throw var4
        }
    }

    override fun decrypt(cipherValue: String?, encryptContext: EncryptContext): Any? {
        return try {
            if (null == cipherValue) {
                null
            } else {
                val result = aes.decrypt(
                    CipherName.AES.toString(),
                    Transformation.AES_CBC_PKCS5Padding.toString(),
                    secretKey,
                    ByteArray(16),
                    Base64.getDecoder().decode(cipherValue)
                )

                val plaintext = String(result, StandardCharsets.UTF_8)
                // log.info { "decrypt ${cipherValue} to ${plaintext}" }
                plaintext
            }
        } catch (var4: GeneralSecurityException) {
            throw var4
        }
    }

    private fun getCipher(decryptMode: Int): Cipher {
        val result = Cipher.getInstance("AES")
        result.init(decryptMode, SecretKeySpec(secretKey, "AES"))
        return result
    }

    override fun getType(): String {
        return "custom_aes_local"
    }

    override fun getProps(): Properties {
        return props!!
    }

    companion object {
        private const val AES_KEY = "aes-key-value"
    }
}


class CustomRemoteEncryptAlgorithm : StandardEncryptAlgorithm<Any?, String?> {
    private val log = KotlinLogging.logger {}

    private var props: Properties? = null

    private lateinit var secretKey: ByteArray

    var aes = SymmEncryptRemote()

    override fun init(props: Properties) {
        this.props = props
        secretKey = createSecretKey(props)
    }

    private fun createSecretKey(props: Properties): ByteArray {
        Preconditions.checkArgument(props.containsKey("aes-key-value"), "%s can not be null.", "aes-key-value")
        return Arrays.copyOf(DigestUtils.sha1(props.getProperty("aes-key-value")), 16)
    }

    override fun encrypt(plainValue: Any?, encryptContext: EncryptContext): String? {
        return try {
            if (null == plainValue) {
                null
            } else {
                val result =
                    aes.encrypt(
                        "AES",
                        "AES/CBC/PKCS5Padding",
                        secretKey,
                        ByteArray(16),
                        plainValue.toString().toByteArray(StandardCharsets.UTF_8)
                    )
                val encodeToString = Base64.getEncoder().encodeToString(result)
                // log.info { "encrypt ${plainValue} to ${encodeToString}" }
                encodeToString
            }
        } catch (var4: GeneralSecurityException) {
            throw var4
        }
    }

    override fun decrypt(cipherValue: String?, encryptContext: EncryptContext): Any? {
        return try {
            if (null == cipherValue) {
                null
            } else {
                val result = getCipher(Cipher.DECRYPT_MODE).doFinal(Base64.getDecoder().decode(cipherValue))
                val plaintext = String(result, StandardCharsets.UTF_8)
                // log.info { "decrypt ${cipherValue} to ${plaintext}" }
                plaintext
            }
        } catch (var4: GeneralSecurityException) {
            throw var4
        }
    }

    private fun getCipher(decryptMode: Int): Cipher {
        val result = Cipher.getInstance("AES")
        result.init(decryptMode, SecretKeySpec(secretKey, "AES"))
        return result
    }

    override fun getType(): String {
        return "custom_aes_remote"
    }

    override fun getProps(): Properties {
        return props!!
    }

    companion object {
        private const val AES_KEY = "aes-key-value"
    }
}


class HsmEncryptAlgorithm : StandardEncryptAlgorithm<Any?, String?> {
    private val log = KotlinLogging.logger {}

    private var props: Properties? = null

    private lateinit var secretKey: ByteArray

    private lateinit var alg: String

    var aes: SymmEncrypt

    init {
        HsmManagement.loadHsmProvider(
            "htls://172.27.128.209:5000",
            "lib/ndsec_sdf_client-generic_client-20230115.pfx",
            "gc-20230115-0x007d"
        )

        println("HSM provider loaded")

        var symm = SymmEncryptNdsecImpl()
        symm.provider = Security.getProvider(NDSecProvider.getProviderName())
        this.aes = symm
    }

    override fun init(props: Properties) {
        this.props = props

        Preconditions.checkArgument(props.containsKey("key-value"), "%s can not be null.", "key-value")
        Preconditions.checkArgument(props.containsKey("alg"), "%s can not be null.", "alg")
        secretKey = Arrays.copyOf(DigestUtils.sha1(props.getProperty("key-value")), 16)
        alg = props.getProperty("alg")
    }

    override fun encrypt(plainValue: Any?, encryptContext: EncryptContext): String? {
        return try {
            if (null == plainValue) {
                null
            } else {
                val result =
                    aes.encrypt(
                        alg,
                        "${alg}/${Mode.CBC}/${Padding.NoPadding}",
                        secretKey,
                        ByteArray(16),
                        plainValue.toString().toByteArray(StandardCharsets.UTF_8)
                    )
                val encodeToString = Base64.getEncoder().encodeToString(result)
                // log.info { "encrypt ${plainValue} to ${encodeToString}" }
                encodeToString
            }
        } catch (var4: GeneralSecurityException) {
            throw var4
        }
    }

    override fun decrypt(cipherValue: String?, encryptContext: EncryptContext): Any? {
        return try {
            if (null == cipherValue) {
                null
            } else {
                val result =
                    aes.encrypt(
                        alg,
                        "${alg}/${Mode.CBC}/${Padding.NoPadding}",
                        secretKey,
                        ByteArray(16),
                        Base64.getDecoder().decode(cipherValue)
                    )
                val plaintext = String(result, StandardCharsets.UTF_8)
                // log.info { "decrypt ${cipherValue} to ${plaintext}" }
                plaintext
            }
        } catch (var4: GeneralSecurityException) {
            throw var4
        }
    }

    override fun getType(): String {
        return "hsm_symm_remote"
    }

    override fun getProps(): Properties {
        return props!!
    }

    companion object {
        private const val AES_KEY = "aes-key-value"
    }
}
