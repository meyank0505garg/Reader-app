package com.example.jetreaderapp.screens.search

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
        loadBooks("android")
    }

    fun loadBooks(string: String){
        searchBooks(string)
    }

     fun searchBooks(query: String) {
        viewModelScope.launch {
            isLoading = true
            if(query.isEmpty()){
                return@launch
            }

            try {

                when (val response = repository.getBooks(query)){
                    is Resource.Success -> {
                        listOfBooks = response.data!!
                        if(listOfBooks.isNotEmpty()) isLoading = false
                    }
                    is Resource.Error -> {
                        isLoading = false
                        Log.e("Network", "searchBooks: Failed getting books ${response.message} ", )

                    }
                    else ->{
                        isLoading = false

                    }
                }


            }catch (ex:Exception){
                isLoading = false
                Log.e("Network", "searchBooks: Failed getting books ${ex.message.toString()}} ", )


            }

        }

    }
}