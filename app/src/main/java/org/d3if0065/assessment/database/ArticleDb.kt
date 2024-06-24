package org.d3if0065.assessment.database

import android.content.Context
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.d3if0065.assessment.model.Article

@Database(entities = [Article::class], version = 1, exportSchema = false)
abstract class ArticleDb: RoomDatabase() {

    abstract val dao: ArticleDao

    companion object {
        @Volatile
        private var INSTANCE: ArticleDb? = null

        fun getInstance(context: Context): ArticleDb {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ArticleDb::class.java,
                        "artikels.db"
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

}