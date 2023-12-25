package com.example.jetreaderapp.screens.login

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetreaderapp.components.showToast
import com.example.jetreaderapp.model.MUser
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor() : ViewModel() {

//    val loadingState = MutableStateFlow(LoadingState.IDLE)
    private val auth : FirebaseAuth = Firebase.auth
//    _loading value is used as mutex for perticular app.
    private val _loading = MutableLiveData(false)
    val loading : LiveData<Boolean> = _loading
    private val _is_Error = MutableLiveData(false)
    val is_Error : LiveData<Boolean> = _is_Error

//    private val _loading : MutableState<Boolean> = mutableStateOf(false)
//    val loading = _loading.value


    fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        home: () -> Unit
    ) {

        viewModelScope.launch {
            if(_loading.value == false){
                _loading.value = true
                auth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener {task->
                        if(task.isSuccessful){
                            val displayName = task.result.user?.email?.split('@')?.get(0)
                            createUser(displayName)
                            home()
                        }else{
//                            Log.d("FB", "createUserWithEmailAndPassword: Failure ${task.result.toString()}")

                        }
                        _loading.value = false


                    }
            }

        }




    }

    private fun createUser(displayName: String?) {
        val userId = auth.currentUser?.uid
        val user = MUser(userId = userId.toString(), displayName = displayName.toString(), avatarUrl = "", quote = "Life is great", profession = "Android Dev",id = null).toMap()


        FirebaseFirestore.getInstance().collection("users")
            .add(user)
    }

    fun signInWithEmailAndPassword(email : String , password : String, home :() -> Unit) =

//        if error the try to remove {} of function and add = before viewmodelscope direcltly


        viewModelScope.launch {
//            Log.d("FBT", "signInWithEmailAndPassword: before : ${is_Error.value}")

            if(_loading.value == false){
                _loading.value = true
                try {

                    auth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener {task->
                            if(task.isSuccessful){
                                home()

                            }
                            _loading.value = false
                            _is_Error.value = false


                        }
                        .addOnFailureListener {

                            _loading.value = false
                            _is_Error.value = true

//                            Log.d("FBT", "signInWithEmailAndPassword: in fun in try!  ${it.message}")
//                            Log.d("FBT", "signInWithEmailAndPassword: in fun in try!  ${is_Error.value}")
                        }

                }catch (ex:Exception){
                    _loading.value = false
                    _is_Error.value = true

                }



            }


        }








}