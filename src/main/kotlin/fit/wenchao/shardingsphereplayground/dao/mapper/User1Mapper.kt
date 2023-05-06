package fit.wenchao.shardingsphereplayground.dao.mapper
import fit.wenchao.shardingsphereplayground.dao.po.User1PO
import org.apache.ibatis.annotations.Mapper
import com.baomidou.mybatisplus.core.mapper.BaseMapper
@Mapper
interface User1Mapper : BaseMapper<User1PO> {
}
