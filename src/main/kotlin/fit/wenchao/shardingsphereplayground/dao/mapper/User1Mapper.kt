package fit.wenchao.shardingsphereplayground.dao.mapper
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import fit.wenchao.shardingsphereplayground.dao.po.User1PO
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Update

@Mapper
interface User1Mapper : BaseMapper<User1PO> {    @Update("TRUNCATE TABLE user1")
fun truncate()
}
