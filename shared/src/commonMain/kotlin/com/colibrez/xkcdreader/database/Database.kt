package com.colibrez.xkcdreader.database

import app.cash.sqldelight.db.SqlDriver
import com.colibrez.xkcdreader.data.Database

expect class DriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DriverFactory): Database {
    val driver = driverFactory.createDriver()

    return Database(driver)
}