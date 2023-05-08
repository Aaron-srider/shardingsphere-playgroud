package fit.wenchao.encryptservice.rest

import fit.wenchao.encryptservice.crypto.AesUtils
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class EncryptionController {

    private val AES_ALGORITHM = "AES"
    private val aesKey = "0123456789ABCDEF".toByteArray()
    private val iv = ByteArray(16)

    @PostMapping("/encrypt")
    fun encrypt(@RequestBody message: String): String? {
        return try {
            val encrypted: ByteArray = AesUtils.encrypt(AES_ALGORITHM, aesKey, iv, message.toByteArray())
            Base64.getEncoder().encodeToString(encrypted)
        } catch (e: Exception) {
            e.printStackTrace()
            "Error encrypting message: " + e.message
        }
    }

    @PostMapping("/decrypt")
    fun decrypt(@RequestBody encrypted: String?): String? {
        return try {
            val decoded: ByteArray = Base64.getDecoder().decode(encrypted)
            val decrypted: ByteArray = AesUtils.decrypt(AES_ALGORITHM, aesKey, iv, decoded)
            String(decrypted)
        } catch (e: Exception) {
            e.printStackTrace()
            "Error decrypting message: " + e.message
        }
    }
}
