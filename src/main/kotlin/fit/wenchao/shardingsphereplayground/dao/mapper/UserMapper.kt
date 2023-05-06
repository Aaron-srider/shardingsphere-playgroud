package fit.wenchao.shardingsphereplayground.dao.mapper
import fit.wenchao.shardingsphereplayground.dao.po.UserPO
import org.apache.ibatis.annotations.Mapper
import com.baomidou.mybatisplus.core.mapper.BaseMapper
@Mapper
interface UserMapper : BaseMapper<UserPO> {
}
