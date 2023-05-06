package fit.wenchao.shardingsphereplayground.dao.repo.impl
import org.springframework.stereotype.Repository
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import fit.wenchao.shardingsphereplayground.dao.po.UserPO
import fit.wenchao.shardingsphereplayground.dao.mapper.UserMapper
import fit.wenchao.shardingsphereplayground.dao.repo.UserDao
@Repository
class UserDaoImpl : ServiceImpl<UserMapper,UserPO>() , UserDao {
}
