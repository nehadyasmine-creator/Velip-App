package fr.Yasmine.Nehad.velibapp.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(entities = [FavoriteStation::class], version = 1, exportSchema = false)
abstract class VelibDatabase : RoomDatabase() {

    abstract fun favoriteStationDao(): FavoriteStationDao

    companion object {
        @Volatile
        private var INSTANCE: VelibDatabase? = null

        fun getDatabase(context: Context): VelibDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VelibDatabase::class.java,
                    "velib_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}