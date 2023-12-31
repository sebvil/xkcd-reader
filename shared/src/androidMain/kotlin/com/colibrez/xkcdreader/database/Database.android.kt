package com.colibrez.xkcdreader.database

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.colibrez.xkcdreader.data.Database

actual class DriverFactory(private val context: Context) {
    var driver: SqlDriver? = null
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = Database.Schema,
            context = context,
            name = "app.db",
            callback = object : AndroidSqliteDriver.Callback(Database.Schema) {
                override fun onOpen(db: SupportSQLiteDatabase)  {
                    super.onOpen(db)
                    db.setForeignKeyConstraintsEnabled(true)
                }
            }).also { driver = it }
    }
}