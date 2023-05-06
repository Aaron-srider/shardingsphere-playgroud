package fit.wenchao.shardingsphereplayground.dao.mapper
import fit.wenchao.shardingsphereplayground.dao.po.User3PO
import org.apache.ibatis.annotations.Mapper
import com.baomidou.mybatisplus.core.mapper.BaseMapper
@Mapper
interface User3Mapper : BaseMapper<User3PO> {
}
