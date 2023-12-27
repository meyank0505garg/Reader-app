package com.example.jetreaderapp.screens.search

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetreaderapp.data.Resource
import com.example.jetreaderapp.model.Item
import com.example.jetreaderapp.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class BookSearchViewModel @Inject constructor(private val repository: BookRepository) : ViewModel() {
     var listOfBooks : List<Item> by mutableStateOf(
        listOf()
    )
    var isLoading :Boolean by mutableStateOf(true)



    init {
        loadBooks("ppo")
    }

    fun loadBooks(string: String){
        searchBooks(string)
    }

     fun searchBooks(query: String) {




        viewModelScope.launch {
            Log.d("PPOOXX", "searchBooks: before \n")
            printId(listOfBooks)







            isLoading = true
            if(query.isEmpty()){
                return@launch
            }

            try {


                val response = repository.getBooksDummy(query)

                    if(response.data != null){
                        listOfBooks = response.data!!

                    }else{
//                        TODO : show Toast that network error. try again
                        throw response.e!!
                    }



                isLoading = false





            }catch (ex:Exception){
                isLoading = false
                //                        TODO : show Toast that network error. try again
//                Log.e("Network", "searchBooks: Failed getting books ${ex.message.toString()}} ", )


            }

//            Log.d("PPOOXX", "searchBooks: ${listOfBooks.size}")
            Log.d("PPOOXX", "searchBooks: after \n")
            printId(listOfBooks)

        }





    }

    fun printId(listOfBooks : List<Item>){
        for(item in listOfBooks){
            Log.d("PPOOXX", "printId: ${item.id} \n")
        }
    }




}