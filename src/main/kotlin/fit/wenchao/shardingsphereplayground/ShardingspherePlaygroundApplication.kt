package fit.wenchao.shardingsphereplayground

import org.apache.shardingsphere.driver.api.yaml.YamlShardingSphereDataSourceFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import javax.sql.DataSource

@SpringBootApplication
class ShardingspherePlaygroundApplication

fun main(args: Array<String>) {
	runApplication<ShardingspherePlaygroundApplication>(*args)
}
