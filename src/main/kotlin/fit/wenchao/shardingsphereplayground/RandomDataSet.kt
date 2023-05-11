package fit.wenchao.shardingsphereplayground

fun main() {
    println(RandomDataSet)
    println("Hello World!")
}

object RandomDataSet {

    // supported data length
    enum class DataLength(val length: Long) {
        LENGTH_128(128),
        LENGTH_256(256),
        LENGTH_512(512),
        LENGTH_1k(1024),
        LENGTH_2k(2048),
        LENGTH_4k(4096)
    }


    var dataLength2Data = mutableMapOf<Long,List<String>>()

    init {
        generateRandomDataSet()
    }

    // get a random data from dataset of a specific data length
    fun getRandomData(dataLength: Long): String {
        var list = dataLength2Data[dataLength] ?: throw RuntimeException("data length $dataLength not found")
        var randomIndex = (Math.random() * list.size).toInt()
        return list[randomIndex]
    }

    fun getSupportLengthList(): List<Long> {
        return dataLength2Data.keys.toList()
    }

    fun generateRandomDataSet() {
        // try to generate 10 string of length 128b, 256b, 512b, 1k, 2k, 4k
        for (i in 1..10) {
            dataLength2Data.put(128, generateRandomData(128))
            dataLength2Data.put(256, generateRandomData(256))
            dataLength2Data.put(512, generateRandomData(512))
            dataLength2Data.put(1024, generateRandomData(1024))
            dataLength2Data.put(2048, generateRandomData(2048))
            dataLength2Data.put(4096, generateRandomData(4096))
        }
    }

    private fun generateRandomData(i: Int): List<String> {
        var set = mutableListOf<String>()
        for (j in 1..10) {
            set.add(generateRandomString(i))
        }
        return set
    }

    private fun generateRandomString(i: Int): String {
        var chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()
        var sb = StringBuilder()
        for (j in 1..i) {
            var randomInt = (Math.random() * chars.size).toInt()
            sb.append(chars[randomInt])
        }
        return sb.toString()
    }
}