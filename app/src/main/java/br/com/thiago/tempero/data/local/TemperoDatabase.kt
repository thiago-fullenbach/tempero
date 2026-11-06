package br.com.thiago.tempero.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.thiago.tempero.data.local.converter.MealConverters
import br.com.thiago.tempero.data.local.dao.FavoriteMealDao
import br.com.thiago.tempero.data.local.entity.FavoriteMealEntity

@Database(
    entities = [FavoriteMealEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(MealConverters::class)
abstract class TemperoDatabase : RoomDatabase() {
    abstract fun favoriteMealDao(): FavoriteMealDao
}
