package fit.wenchao.shardingsphereplayground.config
import org.apache.shardingsphere.driver.api.yaml.YamlShardingSphereDataSourceFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class MysqlConfig {

    @Bean
    fun dataSource(): DataSource {
        val ymlConfigStream = ClassLoader.getSystemResourceAsStream("encrypt-config.yml")

        // read yaml config to bytes
        val ymlConfigBytes = ymlConfigStream.readBytes()

        return YamlShardingSphereDataSourceFactory.createDataSource(ymlConfigBytes)
    }
}

