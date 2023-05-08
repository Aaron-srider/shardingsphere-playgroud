package fit.wenchao.shardingsphereplayground.dao.mapper
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import fit.wenchao.shardingsphereplayground.dao.po.UserPO
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Update

@Mapper
interface UserMapper : BaseMapper<UserPO> {
    @Update("TRUNCATE TABLE user")
    fun truncate()
}
