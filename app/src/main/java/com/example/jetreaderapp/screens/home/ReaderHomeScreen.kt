package com.example.jetreaderapp.screens.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.jetreaderapp.components.BookRating
import com.example.jetreaderapp.components.FABContent
import com.example.jetreaderapp.components.ReaderAppBar
import com.example.jetreaderapp.components.RoundedButton
import com.example.jetreaderapp.components.TitleSection
import com.example.jetreaderapp.model.MBook
import com.example.jetreaderapp.navigation.ReaderScreens
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun HomeScreen(navController: NavController = rememberNavController(),
               viewModel: HomeScreenViewModel = hiltViewModel<HomeScreenViewModel>()) {
    Scaffold(topBar = { ReaderAppBar(navController = navController, title = "Reader App")},
        floatingActionButton = {
            FABContent(){
                navController.navigate(ReaderScreens.SearchScreen.name)

            }
        }) {

        Surface(modifier = Modifier
            .padding(it)
            .fillMaxSize()) {

            HomeContent(navController,viewModel)


        }

    }

}

@Composable

fun HomeContent(navController: NavController, viewModel: HomeScreenViewModel) {



    var listOfBooks = emptyList<MBook>()
    val currentUser = FirebaseAuth.getInstance().currentUser

//    if(!viewModel.data.value.data.isNullOrEmpty()){
//        listOfBooks = viewModel.data.value.data!!.toList().filter {mbook->
//            mbook.userId == currentUser?.uid.toString()
//
//        }
        listOfBooks = viewModel.data.value.data!!
        Log.d("Books", "HomeContent: ${listOfBooks.size}")
//    }


    val email = FirebaseAuth.getInstance().currentUser?.email
    val currentUsrName = if(!email.isNullOrEmpty()){
        email?.split("@")?.get(0)
    }else{
        "N/A"
    }

    Column(verticalArrangement = Arrangement.Top) {

        Row(modifier= Modifier
            .fillMaxWidth()
            .padding(3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
            ) {
            TitleSection(label = "Your Reading \n " + "Activity right now")

            Column(modifier = Modifier.padding(end = 10.dp)) {
                IconButton(onClick = {
                                     navController.navigate(ReaderScreens.ReaderStatsScreen.name)
                },

                ) {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "Profile",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(45.dp)
                    )

                }
                
                Text(text = currentUsrName!!,
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                    )

//                Divider()
            }

        }

        ReadingRightNowArea(
            books = listOfBooks,
            navController = navController
        )
        
        TitleSection(label = "Reading List")
        BookListArea(listofBooks= listOfBooks,navController)



    }

}

@Composable
fun BookListArea(listofBooks: List<MBook>, navController: NavController) {

    val addedBookslist = listofBooks.filter { mBook ->
    mBook.startedReading == null && mBook.finishedReading == null

    }



    HorizontalScrollableComponent(addedBookslist){
//        Todo : on Card cliked go to details
        Log.d("OnCardPress", "BookListArea: $it")
        Log.d("Follow-p", "ReaderNavigation: first number")
        navController.navigate(ReaderScreens.UpdateScreen.name + "/$it")
//        Log.d("BookId", "BookListArea: $it")

    }

}




@Composable
fun ReadingRightNowArea(books : List<MBook>, navController: NavController){
//    ListCard()

    val readingNowBooks = books.filter {mBook ->
    mBook.startedReading != null && mBook.finishedReading != null

    }
    HorizontalScrollableComponent(listofBooks = readingNowBooks){

        navController.navigate(ReaderScreens.UpdateScreen.name + "/$it")


    }



}

@Composable
fun HorizontalScrollableComponent(listofBooks: List<MBook>,
                                  viewModel: HomeScreenViewModel = hiltViewModel(),
                                  onCardPress : (String) -> Unit = {}) {

    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(280.dp)
            .horizontalScroll(scrollState),
    ) {

        if(viewModel.data.value.loading == true){
            LinearProgressIndicator()
        }else{
            if(listofBooks.isNullOrEmpty()){
                Surface(modifier = Modifier.padding(23.dp)) {
                    Text(text = "No books Found")

                }
            }else{
                for(book in listofBooks){
                    ListCard(book){
                        Log.d("OnCardPress", "HorizontalScrollableComponent: ${book.googleBookId.toString()}")
                        onCardPress.invoke(book.googleBookId.toString() )
                    }
                }

            }
        }


    }


}

@Composable
@Preview
fun ListCard(book : MBook = MBook("1234","Title","Author","Notes"),
             onPressDetails : (String) -> Unit = {}) {

    val context = LocalContext.current
    val resources = context.resources
    val displayMetrics = resources.displayMetrics
    val screenWidth = displayMetrics.widthPixels / displayMetrics.density
    val spacing = 10.dp

    Card(shape = RoundedCornerShape(29.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation =CardDefaults.cardElevation(6.dp),
        modifier = Modifier
            .padding(16.dp)
            .height(242.dp)
            .width(202.dp)
            .clickable { onPressDetails.invoke(book.title.toString()) },

        ){

        Column(modifier = Modifier.width(screenWidth.dp - (2 * spacing)),
            horizontalAlignment = Alignment.Start) {

            Row(horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()) {

//                Image
                Image(
                    painter = rememberAsyncImagePainter(book.photoUrl.toString()),
                    contentDescription = "Book Image",
                    modifier = Modifier
                        .height(140.dp)
                        .width(100.dp)
                        .padding(4.dp)
                )

                Column(modifier = Modifier.padding(top = 25.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {


                    Icon(imageVector = Icons.Filled.FavoriteBorder,
                        contentDescription = "Fav Icon",
                        modifier = Modifier.padding(bottom = 1.dp))

                    BookRating(score = 3.5)




                }

            }

            Text(
                text = book.title.toString(),
                modifier = Modifier.padding(4.dp),
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis

            )
            Text(text = book.authors.toString() ,
                modifier = Modifier.padding(4.dp),
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis


                )







        }
        val isStartedReading = remember{
            mutableStateOf(false)
        }
        Row(horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxSize()
        ) {
            isStartedReading.value = book.startedReading !=null
            RoundedButton(label = if(isStartedReading.value) "Reading" else "Not Started" , radius = 70)

        }



    }







}








