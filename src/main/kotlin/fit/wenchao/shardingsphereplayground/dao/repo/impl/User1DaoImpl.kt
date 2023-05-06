package fit.wenchao.shardingsphereplayground.dao.repo.impl
import org.springframework.stereotype.Repository
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import fit.wenchao.shardingsphereplayground.dao.po.User1PO
import fit.wenchao.shardingsphereplayground.dao.mapper.User1Mapper
import fit.wenchao.shardingsphereplayground.dao.repo.User1Dao
@Repository
class User1DaoImpl : ServiceImpl<User1Mapper,User1PO>() , User1Dao {
}
