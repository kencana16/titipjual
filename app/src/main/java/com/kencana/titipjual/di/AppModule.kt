package com.kencana.titipjual.di

import com.kencana.titipjual.data.network.RemoteDataSource
import com.kencana.titipjual.data.network.api.BaseApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRemoteDataSource(): RemoteDataSource {
        return RemoteDataSource()
    }

    @Singleton
    @Provides
    fun provideBaseApi(
        remoteDataSource: RemoteDataSource,
    ): BaseApi {
        return remoteDataSource.buildApi(BaseApi::class.java)
    }

}
