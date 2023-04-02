package ru.plumsoftware.calendarnotes.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Color
import android.provider.BaseColumns

object DealDatabaseEntry {
    object DealDatabaseConstants : BaseColumns {
        const val TABLE_NAME = "Deals"
        const val COLUMN_NAME_DEAL = "deal_name"
        const val COLUMN_DESCRIPTION_DEAL = "deal_description"
        const val COLUMN_MODE_DEAL = "mode_deal"
        const val COLUMN_DEAL_ADD_TIME = "deal_add_time"
        const val COLUMN_DEAL_START_TIME = "deal_start_time"
        const val COLUMN_DEAL_FINISH_TIME = "deal_finish_time"
        const val COLUMN_ROOT_DEAL_MONTH = "root_deal_month"
        const val COLUMN_ROOT_DEAL_YEAR = "root_deal_year"
        const val COLUMN_ROOT_DEAL_DAY = "root_deal_day"

//        Colors
        const val TABLE_NAME_2 = "Colors"
        const val COLUMN_NAME_COLOR = "color_value"
    }

    private const val SQL_CREATE_ENTRIES =
        "CREATE TABLE IF NOT EXISTS ${DealDatabaseConstants.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${DealDatabaseConstants.COLUMN_NAME_DEAL} TEXT," +
                "${DealDatabaseConstants.COLUMN_DESCRIPTION_DEAL} TEXT," +
                "${DealDatabaseConstants.COLUMN_MODE_DEAL} INTEGER," +
                "${DealDatabaseConstants.COLUMN_DEAL_ADD_TIME} LONG," +
                "${DealDatabaseConstants.COLUMN_DEAL_START_TIME} LONG," +
                "${DealDatabaseConstants.COLUMN_DEAL_FINISH_TIME} LONG," +
                "${DealDatabaseConstants.COLUMN_ROOT_DEAL_MONTH} INTEGER," +
                "${DealDatabaseConstants.COLUMN_ROOT_DEAL_YEAR} INTEGER," +
                "${DealDatabaseConstants.COLUMN_ROOT_DEAL_DAY} INTEGER)"

    private const val SQL_DELETE_ENTRIES =
        "DROP TABLE IF EXISTS ${DealDatabaseConstants.TABLE_NAME}"

    private const val SQL_CREATE_COLOR_ENTRIES =
        "CREATE TABLE IF NOT EXISTS ${DealDatabaseEntry.DealDatabaseConstants.TABLE_NAME_2} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${DealDatabaseEntry.DealDatabaseConstants.COLUMN_ROOT_DEAL_MONTH} INTEGER," +
                "${DealDatabaseEntry.DealDatabaseConstants.COLUMN_ROOT_DEAL_YEAR} INTEGER," +
                "${DealDatabaseEntry.DealDatabaseConstants.COLUMN_ROOT_DEAL_DAY} INTEGER," +
                "${DealDatabaseEntry.DealDatabaseConstants.COLUMN_NAME_COLOR} INTEGER)"

    private const val SQL_DELETE_COLOR_ENTRIES =
        "DROP TABLE IF EXISTS ${DealDatabaseEntry.DealDatabaseConstants.TABLE_NAME_2}"

    class ColorDatabaseHelper(context: Context) :
        SQLiteOpenHelper(context, DATABASE_NAME_2, null, DATABASE_VERSION_2) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(DealDatabaseEntry.SQL_CREATE_COLOR_ENTRIES)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL(DealDatabaseEntry.SQL_DELETE_COLOR_ENTRIES)
            onCreate(db)
        }

        override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            onUpgrade(db, oldVersion, newVersion)
        }

        companion object {
            const val DATABASE_VERSION_2 = 1
            const val DATABASE_NAME_2 = "ru_plumsoftware_calendarnotes_2.db"
        }
    }

    class DealDatabaseHelper(context: Context) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(SQL_CREATE_ENTRIES)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL(SQL_DELETE_ENTRIES)
            onCreate(db)
        }

        override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            onUpgrade(db, oldVersion, newVersion)
        }

        companion object {
            const val DATABASE_VERSION = 1
            const val DATABASE_NAME = "ru_plumsoftware_calendarnotes.db"
        }
    }
}