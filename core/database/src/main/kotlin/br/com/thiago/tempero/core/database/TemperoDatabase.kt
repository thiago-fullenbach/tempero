package br.com.thiago.tempero.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.thiago.tempero.core.database.converter.MealConverters
import br.com.thiago.tempero.core.database.dao.FavoriteMealDao
import br.com.thiago.tempero.core.database.entity.FavoriteMealEntity

@Database(
    entities = [FavoriteMealEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(MealConverters::class)
abstract class TemperoDatabase : RoomDatabase() {
    abstract fun favoriteMealDao(): FavoriteMealDao
}
