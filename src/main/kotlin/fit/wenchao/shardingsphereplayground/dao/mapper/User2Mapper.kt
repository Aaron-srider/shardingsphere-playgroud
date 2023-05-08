package fit.wenchao.shardingsphereplayground.dao.mapper
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import fit.wenchao.shardingsphereplayground.dao.po.User2PO
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Update

@Mapper
interface User2Mapper : BaseMapper<User2PO> {    @Update("TRUNCATE TABLE user2")
fun truncate()
}
