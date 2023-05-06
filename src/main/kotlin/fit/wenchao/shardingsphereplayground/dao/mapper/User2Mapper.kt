package fit.wenchao.shardingsphereplayground.dao.mapper
import fit.wenchao.shardingsphereplayground.dao.po.User2PO
import org.apache.ibatis.annotations.Mapper
import com.baomidou.mybatisplus.core.mapper.BaseMapper
@Mapper
interface User2Mapper : BaseMapper<User2PO> {
}
