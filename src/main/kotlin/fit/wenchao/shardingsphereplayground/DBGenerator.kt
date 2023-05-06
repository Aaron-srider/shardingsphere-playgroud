package fit.wenchao.shardingsphereplayground

import fit.wenchao.db.generator.Generator


class DBGenerator {
}

fun main() {
    Generator().start(DBGenerator::class.java)
}