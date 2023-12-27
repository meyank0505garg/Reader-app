package com.example.jetreaderapp.repository

import android.util.Log
import com.example.jetreaderapp.data.DataOrException
import com.example.jetreaderapp.model.MBook
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject



class FireRepository@Inject constructor(
    private val queryBook : Query
) {

suspend fun getAllbooksFromDatabase() : DataOrException<List<MBook>,Boolean,Exception>{
    val dataOrException = DataOrException<List<MBook>,Boolean,Exception>()

    try {
        dataOrException.loading = true
        dataOrException.data = queryBook.get().await().documents.map {documentSnapshot ->
        documentSnapshot.toObject(MBook::class.java)!!


        }
        val currentUser = FirebaseAuth.getInstance().currentUser
        if(!dataOrException.data.isNullOrEmpty()){
            dataOrException.data = dataOrException.data!!.filter {mBook ->
            mBook.userId == currentUser?.uid.toString()

            }
        }

//        if(!dataOrException.data.isNullOrEmpty()){
//            dataOrException.loading = false
//        }
//        Log.d("Ert", "getAllbooksFromDatabase: exception not occur ${dataOrException.data.toString()} ")

    }catch (ex:FirebaseFirestoreException){
//        Log.d("Ert", "getAllbooksFromDatabase: ${ex.message.toString()} ")
        dataOrException.e = ex
        dataOrException.data = emptyList()

    }
//    Log.d("return", "getAllbooksFromDatabase: exception not occur ${dataOrException.data.toString()} ")
    dataOrException.loading = false
    return dataOrException

}


}