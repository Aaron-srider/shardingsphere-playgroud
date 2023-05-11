package fit.wenchao.shardingsphereplayground.dao.po

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.io.Serializable

@TableName("`user1`")
data class User1PO(
    @TableId(value = "id", type = IdType.AUTO)
    var id: Int?,
    var username: String?,
    var password: String?,
    var idNumber: String?,
    var phone: String?,
) : Serializable {
    // default constructor
    constructor() : this(null, null, null, null, null)
}
