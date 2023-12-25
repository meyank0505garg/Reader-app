package com.example.jetreaderapp.screens.details

import androidx.lifecycle.ViewModel
import com.example.jetreaderapp.data.Resource
import com.example.jetreaderapp.model.Item
import com.example.jetreaderapp.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class DetailsViewModel @Inject constructor(private val repository:BookRepository) : ViewModel() {
    suspend fun getBookInfo(bookId : String) : Resource<Item>{

        return repository.getBookInfo(bookId = bookId)

    }

}