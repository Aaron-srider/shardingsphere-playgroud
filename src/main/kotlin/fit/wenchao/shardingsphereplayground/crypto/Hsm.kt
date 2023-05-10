package fit.wenchao.shardingsphereplayground.crypto

import com.ndsec.jce.exception.RuntimeCryptoException
import com.ndsec.jce.provider.NDSecProvider
import java.security.Provider
import java.security.Security


// padding a string to a length of multiple of 16
fun padding2MultipleOf16(str: String): String {
    val padLength = (str.length / 16 + 1) * 16
    return str.padEnd(padLength, '-')
}

fun main() {
    HsmManagement.loadHsmProvider(
        "htls://172.27.128.209:5000",
        "lib/ndsec_sdf_client-generic_client-20230115.pfx",
        "gc-20230115-0x007d"
    )

    println("HSM provider loaded")

    var symm = SymmEncryptNdsecImpl()
    symm.provider = Security.getProvider(NDSecProvider.getProviderName())

    // fill the key
    var key = "1234567890123456".toByteArray()

    // fill the iv
    var iv = ByteArray(16)

    var message = "hellohellohello1"

    var alg = CipherName.SM4.toString()

    val encrypt = symm.encrypt(
        alg,
        Transformation.SM4_CBC_NoPadding.toString(),
        key,
        iv,
        message.toByteArray()
    )


    val decrypt = symm.decrypt(
        alg,
        Transformation.SM4_CBC_NoPadding.toString(),
        key,
        iv,
        encrypt
    )

    println(String(decrypt))
}

object HsmManagement {

    fun loadHsmProvider(socketUri: String, pfxPath: String, pfxPassword: String) {
        // provider exists?
        if (Security.getProvider(ProviderName.NDSecProvider.value) != null) {
            return
        }

        // try to connect to hsm
        try {
            NDSecProvider.getSettings().addServerNode(socketUri, pfxPath, pfxPassword)
        } catch (e: RuntimeCryptoException) {
            throw RuntimeException(e.message)
        }

        // load jce provider dynamically
        var provider: Provider = NDSecProvider()

        // add ndsec hsm provider
        Security.addProvider(provider)
    }
}
