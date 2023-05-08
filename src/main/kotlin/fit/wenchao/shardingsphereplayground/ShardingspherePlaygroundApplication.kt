package fit.wenchao.shardingsphereplayground

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ShardingspherePlaygroundApplication

private val AES_ALGORITHM = "AES"
private val aesKey = "0123456789ABCDEF".toByteArray()
private val iv = ByteArray(16)

fun main(args: Array<String>) {

	runApplication<ShardingspherePlaygroundApplication>(*args)
	// var aes = AesEncryptRemote()
	//
	// // Encrypt a message
	// val message = "Hello, world!"
	// var cipher = aes.encrypt(AES_ALGORITHM, aesKey , iv, message.toByteArray())
	//
	// val decrypt = aes.decrypt(AES_ALGORITHM, aesKey, iv, cipher)
	//
	// println(String(Base64.encode(cipher)))
	// println(String(decrypt))

}
