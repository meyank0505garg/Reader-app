package com.example.jetreaderapp.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.jetreaderapp.R
import com.example.jetreaderapp.components.EmailInput
import com.example.jetreaderapp.components.PasswordInput
import com.example.jetreaderapp.navigation.ReaderScreens

@Composable
fun ReaderLoginScreen(navController: NavController,viewModel: LoginScreenViewModel = hiltViewModel()) {

    val showLoginForm = rememberSaveable {
        mutableStateOf(true)

    }
    Surface(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())) {

        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.padding(20.dp)) {

            Text(text = "A. Reader",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Red.copy(alpha = 0.5f),
                fontSize = 55.sp)



            val isLoading: State<Boolean> = viewModel.loading.observeAsState(initial = false)
            val isError : State<Boolean> = viewModel.is_Error.observeAsState(initial = false)



            if(showLoginForm.value){

                UserForm(
                    loading = isLoading.value,
                     isError = isError.value ,

                    isCreateAccount = false,
                ) { email, pwd ->
                    viewModel.signInWithEmailAndPassword(email, pwd) {
                        navController.navigate(ReaderScreens.ReaderHomeScreen.name)
                    }

                }

            }else{
                UserForm(loading = isLoading.value,
                    isError = isError.value,
                    isCreateAccount = true){email,pwd ->
                    viewModel.createUserWithEmailAndPassword(email,pwd){
                        navController.navigate(ReaderScreens.ReaderHomeScreen.name)
                    }


                }
            }



            Spacer(modifier = Modifier.height(15.dp))
            Row(
                modifier = Modifier.padding(15.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val text = if(showLoginForm.value) "Sign Up" else "Login"
                Text(text = "New User")
                Text(text = text,
                    modifier = Modifier
                        .clickable {
                            showLoginForm.value = !showLoginForm.value
                        }
                        .padding(start = 5.dp),
                    fontWeight = FontWeight.Bold,
                    color = Color.Red.copy(0.8f))

            }







        }

        


    }

}

@OptIn(ExperimentalComposeUiApi::class)
//@Preview
@Composable
fun UserForm(
    loading: Boolean,
    isError: Boolean,
    isCreateAccount: Boolean = false,
    onDone: (String, String) -> Unit = { email, pwd -> },

) {
    val email = rememberSaveable {
        mutableStateOf("")
    }

    val password = rememberSaveable {
        mutableStateOf("")
    }
    val passwordVisibility = rememberSaveable {
        mutableStateOf(false)

    }

    val passwordFocusRequest = remember{FocusRequester()}
    val keyboardController = LocalSoftwareKeyboardController.current
    val valid = remember(email.value,password.value){
        email.value.trim().isNotEmpty() && password.value.trim().isNotEmpty()
    }

    Column(modifier = Modifier
        .height(350.dp)
        .background(MaterialTheme.colorScheme.background)
        .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally) {
        if(isCreateAccount) Text(text = stringResource(id = R.string.create_acc))
        EmailInput( emailState = email,
            enabled = !loading,
            onAction = KeyboardActions{
                passwordFocusRequest.requestFocus()
            },
            isError = isError
            )

        PasswordInput(
            modifier = Modifier.focusRequester(passwordFocusRequest),
                passwordState = password,
                labelId = "Password",
                enabled = !loading,
                passwordVisibility = passwordVisibility,
                onAction = KeyboardActions{
                    if(!valid) return@KeyboardActions
                    onDone(email.value.trim() , password.value.trim())
                    keyboardController?.hide()


                },
            isError = isError
            )

        SubmitButton(
            textId = if(isCreateAccount) "Create Account" else "Login",
            loading = loading,
            validInputs = valid){
            onDone(email.value.trim(),password.value.trim())
            keyboardController?.hide()

        }




    }



}

@Composable
fun SubmitButton(textId: String,
                 loading: Boolean,
                 validInputs: Boolean,
                 onClick : () -> Unit) {
    Button(onClick = onClick,
        modifier = Modifier
            .padding(3.dp)
            .fillMaxWidth(),
        enabled = !loading && validInputs,
        shape = CircleShape) {
        if(loading) CircularProgressIndicator(modifier = Modifier.size(25.dp))
        else Text(text = textId, modifier = Modifier.padding(5.dp))


    }

}
