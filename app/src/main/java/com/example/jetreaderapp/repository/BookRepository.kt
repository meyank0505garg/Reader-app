package com.example.jetreaderapp.repository

import android.util.Log
import com.example.jetreaderapp.data.DataOrException
import com.example.jetreaderapp.data.Resource
import com.example.jetreaderapp.model.Item
import com.example.jetreaderapp.network.BooksApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject



class BookRepository @Inject constructor(private val api : BooksApi) {
//    it's waste to change Boolean value of DataOrException in this case as we can't track it's Boolean value. to track this, use LiveData or StateFlow
    private val listOfBooksOrException =DataOrException<List<Item>,Boolean,Exception>()
    private val bookInfoDataOrException =DataOrException<Item,Boolean,Exception>()
   suspend fun getBooks(searchQuery:String):Resource<List<Item>>{

    return try{


           Resource.Loading(data =true)
           val itemList = api.getAllBooks(searchQuery).items
        if(itemList.isNotEmpty()){
            Resource.Loading(data =false)


        }
           Resource.Success(data = itemList)
       }catch (ex: Exception){
           Resource.Error(message = ex.message.toString())

       }

   }

    suspend fun getBooksDummy(searchQuery:String) : DataOrException<List<Item>,Boolean,Exception>{

        try {
//            listOfBooksOrException.loading = true
            listOfBooksOrException.data = api.getAllBooks(searchQuery).items


        }catch (ex:Exception){
            listOfBooksOrException.e = ex
            listOfBooksOrException.data = null

        }
        Log.d("PPOOXX", "getBooksDummy: $searchQuery  and ${listOfBooksOrException.data?.size}")

//        listOfBooksOrException.loading = false
        return  listOfBooksOrException


    }


    suspend fun getBookInfo(bookId:String) : Resource<Item> {
        val response = try{
            Resource.Loading(data = true)
            api.getBookInfo(bookId)

        }catch (ex:Exception){

          return  Resource.Error(message = ex.message.toString())
        }
        Resource.Loading(data = false)
        return Resource.Success(data=response)
    }
}