package fit.wenchao.shardingsphereplayground.dao.repo.impl
import org.springframework.stereotype.Repository
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import fit.wenchao.shardingsphereplayground.dao.po.User3PO
import fit.wenchao.shardingsphereplayground.dao.mapper.User3Mapper
import fit.wenchao.shardingsphereplayground.dao.repo.User3Dao
@Repository
class User3DaoImpl : ServiceImpl<User3Mapper,User3PO>() , User3Dao {
}
