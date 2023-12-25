package com.example.jetreaderapp.screens.details

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.jetreaderapp.components.ReaderAppBar
import com.example.jetreaderapp.components.RoundedButton
import com.example.jetreaderapp.data.Resource
import com.example.jetreaderapp.model.Item
import com.example.jetreaderapp.model.MBook
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(navController: NavController,
                      bookId:String,
                      viewModel: DetailsViewModel = hiltViewModel<DetailsViewModel>()) {

    Scaffold(
        topBar = {
            ReaderAppBar(
                navController = navController,
                title = "Book Details",
                icon = Icons.Default.ArrowBack,
                showProfile = false,


                ){
                navController.popBackStack()

            }
        }
    ) {
        Surface(modifier = Modifier
            .padding(it)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
        ) {
            Column(modifier = Modifier.padding(top = 12.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally) {

                val book_info = produceState<Resource<Item>>(initialValue = Resource.Loading()){
                    value  = viewModel.getBookInfo(bookId)
                }.value

                if(book_info.data == null){
                    LinearProgressIndicator()
                }else{
                    ShowBookDetails(bookInfo = book_info, navController = navController)
                    

                }



            }

        }

    }

}

@Composable
fun ShowBookDetails(bookInfo: Resource<Item>, navController: NavController) {
    val bookData = bookInfo.data?.volumeInfo
    val googleBookId = bookInfo.data?.id
    
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()) {

        Card(modifier = Modifier.padding(34.dp),
            shape = CircleShape,
        ) {

            Image(painter = rememberAsyncImagePainter(model = bookData!!.imageLinks.thumbnail), contentDescription = "Book Image",
                modifier = Modifier
                    .size(90.dp)
                    .padding(1.dp),
                contentScale = ContentScale.Crop)



        }

        Text(text = bookData?.title.toString(),
            overflow = TextOverflow.Ellipsis)
        
        Text(text = "Authors : ${bookData?.authors}")
        Text(text = "Page Count : ${bookData?.pageCount.toString()}")
        Text(text = "Published : ${bookData?.publishedDate.toString()}")
        
        Spacer(modifier = Modifier.height(10.dp))

        val cleanDescription = HtmlCompat.fromHtml(bookData!!.description,HtmlCompat.FROM_HTML_MODE_LEGACY).toString()

        Surface(modifier = Modifier
            .height(300.dp)
            .padding(4.dp),
            shape = RectangleShape,
            border = BorderStroke(1.dp, Color.DarkGray)
        ) {

            LazyColumn(modifier=Modifier.padding(3.dp)){
                item{
                    Text(text = cleanDescription )
                }

            }
        }


        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly) {
            RoundedButton(label = "Save"){
                try {
                    val book = MBook(
                        title = bookData.title?.toString(),
                        authors = bookData.authors?.toString(),
                        description = bookData.description?.toString(),
                        categories = bookData.categories?.toString(),
                        notes = "",
                        photoUrl = bookData.imageLinks.thumbnail.toString(),
                        publishedDate = bookData.publishedDate.toString(),
                        pageCount = bookData.pageCount?.toString(),
                        rating = 0.0,
                        googleBookId = googleBookId,
                        userId = FirebaseAuth.getInstance().currentUser?.uid.toString()

                    )
                    SaveToFireBase(book,navController)

                }catch (ex:Exception){
                    Log.d("book", "ShowBookDetails: Exception occur ${ex.message} ")
                                    navController.popBackStack()
                    
                }





//                SaveToFireBase(book,navController)

            }

            RoundedButton(label = "Cancel"){
                navController.popBackStack()
            }

        }


    }

    
}



//@Composable
fun SaveToFireBase(book: MBook,navController: NavController ) {
    val db=FirebaseFirestore.getInstance()
    val dbCollection = db.collection("books")

    if(book.toString().isNotEmpty()){
        dbCollection.add(book)
            .addOnSuccessListener {documentRef->
            val docId = documentRef.id
                dbCollection.document(docId)
                    .update(hashMapOf("id" to docId) as Map<String, Any>)

            }
            .addOnCompleteListener {task->
                if(task.isSuccessful){
                    navController.popBackStack()
                }

            }
            .addOnFailureListener{
//                Log.d(TAG, "SaveToFireBase: ")
            }

    }else{

    }


}
