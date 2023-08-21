package com.example.practicalappdev.roomdb.database

import android.content.Context
import androidx.room.Room
import com.example.practicalappdev.R
import com.example.practicalappdev.roomdb.dao.LogDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    fun provideLogDao(database: AppDatabase): LogDao {
        return database.logDao()
    }

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            context.getString(R.string.app_name)
        ).allowMainThreadQueries()
            //.addTypeConverter(Converters::class)
            //.addTypeConverter(AreaValidationConverter::class)
            .build()
    }

}