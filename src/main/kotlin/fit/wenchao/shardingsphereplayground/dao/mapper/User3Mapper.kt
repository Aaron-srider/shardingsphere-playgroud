package fit.wenchao.shardingsphereplayground.dao.mapper
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import fit.wenchao.shardingsphereplayground.dao.po.User3PO
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Update

@Mapper
interface User3Mapper : BaseMapper<User3PO> {    @Update("TRUNCATE TABLE user3")
fun truncate()
}
