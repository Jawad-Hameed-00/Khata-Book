package com.jawadhameed.khatabook.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jawadhameed.khatabook.models.Detail

@Database(entities = [Detail::class], version = 1, exportSchema = false)
abstract class DetailsDatabase : RoomDatabase() {
    abstract fun detailsDao(): DetailsDao

    companion object {
        @Volatile
        private var INSTANCE: DetailsDatabase? = null

        fun getDatabase(context: Context, dbName: String): DetailsDatabase {
            return synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        DetailsDatabase::class.java,
                        dbName
                    ).build()
                } else {
                    if (INSTANCE!!.openHelper.databaseName != dbName) {

                        INSTANCE!!.close()
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            DetailsDatabase::class.java,
                            dbName
                        ).build()
                    }
                }
                INSTANCE!!
            }
        }
    }
}
