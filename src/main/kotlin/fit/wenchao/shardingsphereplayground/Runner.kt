package fit.wenchao.shardingsphereplayground

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import fit.wenchao.shardingsphereplayground.TestUnit
import fit.wenchao.shardingsphereplayground.dao.mapper.User1Mapper
import fit.wenchao.shardingsphereplayground.dao.mapper.User2Mapper
import fit.wenchao.shardingsphereplayground.dao.mapper.User3Mapper
import fit.wenchao.shardingsphereplayground.dao.mapper.UserMapper
import fit.wenchao.shardingsphereplayground.dao.po.User1PO
import fit.wenchao.shardingsphereplayground.dao.po.User2PO
import fit.wenchao.shardingsphereplayground.dao.po.User3PO
import fit.wenchao.shardingsphereplayground.dao.po.UserPO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.lang.reflect.Field
import java.util.*
import javax.annotation.PostConstruct
import fit.wenchao.shardingsphereplayground.TestUnit as TestUnit1

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

fun fillStringProperties(obj: Any) {
    val fields: List<Field> = obj.javaClass.declaredFields.toList()

    fields.filter { it.type == String::class.java && it.name != "id" }.forEach {
        it.isAccessible = true
        it.set(obj, randomString(16, charset))
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

fun main() {

    for (i in 1..10) {
        val user1PO = User1PO()
        fillStringProperties(user1PO)
        println(user1PO.password)
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

    fun printRecordCount(count: Int) {
        print("insert $count records\t\t")
    }

    fun testSpeedOfInsertRecordsWithDifferentDataLength() {
        cleanARound()

        var conutbase = 10

        // var multiplers = mutableListOf<Int>(1, 10, 100, 1000, 10000, 50000)
        var multiplers = mutableListOf<Int>(10000)
        // var multiplers = mutableListOf<Int>(1, 10)

        // test insert count through 10 base, 100 base, and 500 base
        var insertCountList = multiplers.map { it * conutbase }.toMutableList()

        val supportLengthList = RandomDataSet.getSupportLengthList()

        for (dataLength in supportLengthList) {
            println("data length: $dataLength")

            insertCountList.forEach {
                printRecordCount(it)
                stopwatch {
                    for (i in 0 until it) {
                        val userPO = UserPO()
                        userPO.password = RandomDataSet.getRandomData(dataLength)
                        userMapper.insert(userPO)
                    }
                }
            }
            println()
        }

    }

    fun aRound() {


        cleanARound()

        var conutbase = 10

        // var multiplers = mutableListOf<Int>(1, 10, 100, 1000, 10000, 50000)
        var multiplers = mutableListOf<Int>(100, 1000, 10000)
        // var multiplers = mutableListOf<Int>(100)
        // var multiplers = mutableListOf<Int>(1, 10)

        // test insert count through 10 base, 100 base, and 500 base
        var insertCountList = multiplers.map { it * conutbase }.toMutableList()

        val supportLengthList = RandomDataSet.getSupportLengthList()

        val userPO = UserPO()
        val user1PO = User1PO()
        val user2PO = User2PO()
        val user3PO = User3PO()

        for (dataLength in supportLengthList) {
            println("=======> data length: $dataLength")

            println(" compare group ")
            // for 0 encrypted entity
            insertCountList.forEach {
                printRecordCount(it)
                stopwatch {
                    for (i in 0 until it) {
                        userPO.password = RandomDataSet.getRandomData(dataLength)
                        userPO.phone = RandomDataSet.getRandomData(dataLength)
                        userPO.idNumber = RandomDataSet.getRandomData(dataLength)
                        userMapper.insert(userPO)
                        userPO.id=null
                        userPO.password=null
                        userPO.phone=null
                        userPO.idNumber=null
                    }
                }
            }

            println(" 1 field encryption group ")
            // for 1 encrypted entity
            insertCountList.forEach {
                printRecordCount(it)
                stopwatch {
                    for (i in 0 until it) {
                        user1PO.password = RandomDataSet.getRandomData(dataLength)
                        user1PO.phone = RandomDataSet.getRandomData(dataLength)
                        user1PO.idNumber = RandomDataSet.getRandomData(dataLength)
                        user1Mapper.insert(user1PO)
                        user1PO.id=null
                        user1PO.password=null
                        user1PO.phone=null
                        user1PO.idNumber=null
                    }
                }
            }
            println(" 2 field encryption group ")
            // for 2 encrypted entity
            insertCountList.forEach {
                printRecordCount(it)
                stopwatch {
                    for (i in 0 until it) {
                        user2PO.password = RandomDataSet.getRandomData(dataLength)
                        user2PO.phone = RandomDataSet.getRandomData(dataLength)
                        user2PO.idNumber = RandomDataSet.getRandomData(dataLength)
                        user2Mapper.insert(user2PO)
                        user2PO.id=null
                        user2PO.password=null
                        user2PO.phone=null
                        user2PO.idNumber=null
                    }
                }
            }
            println(" 3 field encryption group ")
            // for 3 encrypted entity
            insertCountList.forEach {
                printRecordCount(it)
                stopwatch {
                    for (i in 0 until it) {
                        user3PO.password = RandomDataSet.getRandomData(dataLength)
                        user3PO.phone = RandomDataSet.getRandomData(dataLength)
                        user3PO.idNumber = RandomDataSet.getRandomData(dataLength)
                        user3Mapper.insert(user3PO)
                        user3PO.id=null
                        user3PO.password=null
                        user3PO.phone=null
                        user3PO.idNumber=null
                    }
                }
            }
        }

    }

    private fun cleanARound() {
        userMapper.truncate()
        user1Mapper.truncate()
        user2Mapper.truncate()
        user3Mapper.truncate()
    }

    fun looptest() {

        var expTimes = 3

        for (i in 0 until expTimes) {
            println("=============================================== round $i ===============================================")
           aRound()
            println()
        }

    }


    @PostConstruct
    fun init() {
        looptest()
    }
}