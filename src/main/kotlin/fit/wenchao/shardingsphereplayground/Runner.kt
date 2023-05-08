package fit.wenchao.shardingsphereplayground

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.google.common.base.Preconditions
import fit.wenchao.shardingsphereplayground.TestUnit
import fit.wenchao.shardingsphereplayground.crypto.AesEncryptLocal
import fit.wenchao.shardingsphereplayground.crypto.AesEncryptRemote
import fit.wenchao.shardingsphereplayground.dao.mapper.User1Mapper
import fit.wenchao.shardingsphereplayground.dao.mapper.User2Mapper
import fit.wenchao.shardingsphereplayground.dao.mapper.User3Mapper
import fit.wenchao.shardingsphereplayground.dao.mapper.UserMapper
import fit.wenchao.shardingsphereplayground.dao.po.User1PO
import fit.wenchao.shardingsphereplayground.dao.po.User2PO
import fit.wenchao.shardingsphereplayground.dao.po.User3PO
import fit.wenchao.shardingsphereplayground.dao.po.UserPO
import mu.KotlinLogging
import org.apache.commons.codec.digest.DigestUtils
import org.apache.shardingsphere.encrypt.api.encrypt.standard.StandardEncryptAlgorithm
import org.apache.shardingsphere.encrypt.spi.context.EncryptContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.lang.reflect.Field
import java.nio.charset.StandardCharsets
import java.security.GeneralSecurityException
import java.util.*
import javax.annotation.PostConstruct
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import fit.wenchao.shardingsphereplayground.TestUnit as TestUnit1


class CustomLocalEncryptAlgorithm : StandardEncryptAlgorithm<Any?, String?> {
    private val log = KotlinLogging.logger {}

    private var props: Properties? = null

    private lateinit var secretKey: ByteArray

    var aes = AesEncryptLocal()

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

    var aes = AesEncryptRemote()

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

fun randomString(length: Int, charset: String): String {
    val random = Random()
    val sb = StringBuilder(length)
    for (i in 0 until length) {
        sb.append(charset[random.nextInt(charset.length)])
    }
    return sb.toString()
}

const val charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
fun stopwatch(func: () -> Unit) {
    val startTime = System.nanoTime()
    func()
    val endTime = System.nanoTime()
    val elapsedTime = (endTime - startTime) / 1_000_000_000.0
    println("Elapsed time: %.6f seconds".format(elapsedTime))
}


fun printOnOneLine(number: Int) {
    print("\r$number")
    System.out.flush()
}

fun fillStringProperties(obj: Any) {
    val fields: List<Field> = obj.javaClass.declaredFields.toList()

    fields.filter { it.type == String::class.java && it.name != "id" }.forEach {
        it.isAccessible = true
        it.set(obj, randomString(10, charset))
    }
}

// This class contains a kind of model and its mapper needed to access the database
// every unit is an abstract concept of a table and its model in the business code
class TestUnit<T>(var mapper: BaseMapper<T>, var modelClazz: Class<T>) {
    fun makeMeAInstancePlease(): T {
        return modelClazz.getDeclaredConstructor().newInstance()
    }
}

class TestUnits : Iterable<TestUnit1<*>> {

    var unitList: MutableList<TestUnit1<*>> = mutableListOf()

    override fun iterator(): Iterator<TestUnit<*>> {
        return unitList.iterator()
    }

}


@Component
class Runner {

    @Autowired
    lateinit var userMapper: UserMapper

    @Autowired
    lateinit var user1Mapper: User1Mapper

    @Autowired
    lateinit var user2Mapper: User2Mapper

    @Autowired
    lateinit var user3Mapper: User3Mapper

    fun insertAUserPO() {
        val userPO = UserPO()
        fillStringProperties(userPO)
        userMapper.insert(
            userPO
        )
    }

    fun insertAUser1PO() {
        val user1PO = User1PO()
        fillStringProperties(user1PO)
        user1Mapper.insert(
            user1PO
        )
    }

    fun insertAUser2PO() {
        val user2PO = User2PO()
        fillStringProperties(user2PO)
        user2Mapper.insert(
            user2PO
        )
    }

    fun insertAUser3PO() {
        val user3PO = User3PO()
        fillStringProperties(user3PO)
        user3Mapper.insert(
            user3PO
        )
    }


    fun looptest() {

        var conutbase = 1000

        // test insert count through 10 base, 100 base, and 500 base
        var insertCountList = mutableListOf<Int>(conutbase * 10, conutbase * 100, conutbase * 500)

        // // for 0 encrypted entity
        // insertCountList.forEach {
        //     stopwatch {
        //         for (i in 0 until it) {
        //             insertAUserPO()
        //         }
        //     }
        // }

        // for 1 encrypted entity
        insertCountList.forEach {
            stopwatch {
                for (i in 0 until it) {
                    insertAUser1PO()
                }
            }
        }


        // for 2 encrypted entity
        insertCountList.forEach {
            stopwatch {
                for (i in 0 until it) {
                    insertAUser2PO()
                }
            }
        }

        // for 3 encrypted entity
        insertCountList.forEach {
            stopwatch {
                for (i in 0 until it) {
                    insertAUser3PO()
                }
            }
        }
    }

    @PostConstruct
    fun init() {
        looptest()
    }
}