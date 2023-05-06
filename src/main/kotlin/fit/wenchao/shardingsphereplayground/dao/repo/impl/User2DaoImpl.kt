package fit.wenchao.shardingsphereplayground.dao.repo.impl
import org.springframework.stereotype.Repository
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import fit.wenchao.shardingsphereplayground.dao.po.User2PO
import fit.wenchao.shardingsphereplayground.dao.mapper.User2Mapper
import fit.wenchao.shardingsphereplayground.dao.repo.User2Dao
@Repository
class User2DaoImpl : ServiceImpl<User2Mapper,User2PO>() , User2Dao {
}
