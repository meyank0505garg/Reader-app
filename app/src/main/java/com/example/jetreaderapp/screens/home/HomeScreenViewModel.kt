package com.example.jetreaderapp.screens.home

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetreaderapp.data.DataOrException
import com.example.jetreaderapp.model.MBook
import com.example.jetreaderapp.repository.FireRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val repository: FireRepository) : ViewModel() {
        val data : MutableState<DataOrException<List<MBook>,Boolean,Exception>> = mutableStateOf(
            DataOrException(listOf(),true,Exception(""))
        )

    init {
        getAllBooksFromDataBase()
    }

    private fun getAllBooksFromDataBase() {

         viewModelScope.launch {
//             Log.d("CheckingValue", "getAllBooksFromDataBase: before \n ${data.value.data} \n \n ${data.value.e?.message}")

             data.value.loading = true

             data.value =  repository.getAllbooksFromDatabase()
             data.value.loading = false
//

//             Log.d("CheckingValue", "getAllBooksFromDataBase: after \n ${data.value.data} \n \n ${data.value.e?.message}")
//             Log.d("CheckingValue", "getAllBooksFromDataBase: after ${data.value.data}")
//
         }

//
    }


}