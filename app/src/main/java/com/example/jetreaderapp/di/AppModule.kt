package com.example.jetreaderapp.di

import com.example.jetreaderapp.network.BooksApi
import com.example.jetreaderapp.repository.BookRepository
import com.example.jetreaderapp.repository.FireRepository
import com.example.jetreaderapp.utils.Constants.Base_URL
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideBookApi() : BooksApi{
        return Retrofit.Builder()
            .baseUrl(Base_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BooksApi::class.java)
    }

    @Singleton
    @Provides
    fun providesBookRepo(api : BooksApi) = BookRepository(api)

    @Singleton
    @Provides
    fun provideFireBookRepository() : FireRepository = FireRepository(queryBook = FirebaseFirestore.getInstance().collection("books"))


}