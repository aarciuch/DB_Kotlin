package art.tm.db_kotlin

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.InternalCoroutinesApi

@Database(entities = [Person::class], version = 1, exportSchema = false)
abstract class PersonDB : RoomDatabase() {
    abstract fun personDao() : PersonDao


    @InternalCoroutinesApi
    companion object {
        private var INSTANCE : PersonDB? = null


        fun getPersonDB(contex: Context) : PersonDB? {
            if (INSTANCE == null) {
                kotlinx.coroutines.internal.synchronized(PersonDB::class) {
                    INSTANCE = Room.databaseBuilder(
                        contex.applicationContext,
                        PersonDB::class.java,
                        "personDB")
                        .fallbackToDestructiveMigration()
                      //  .allowMainThreadQueries()
                        .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }

    }

}