package com.example.jetreaderapp.screens.search

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.jetreaderapp.components.InputField
import com.example.jetreaderapp.components.ReaderAppBar
import com.example.jetreaderapp.model.Item
import com.example.jetreaderapp.navigation.ReaderScreens
import kotlin.math.log


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController ,
                 viewModel: BookSearchViewModel = hiltViewModel<BookSearchViewModel>()
) {


    Scaffold(topBar = {
        ReaderAppBar(
            navController = navController,
            title = "Search Books",
            showProfile = false,
            icon = Icons.Default.ArrowBack
        ){
//            navController.popBackStack()
            navController.navigate(ReaderScreens.ReaderHomeScreen.name)
        }
    }) {

        Surface(modifier = Modifier.padding(it)) {
            
            Column {
                SearchComponent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    viewModel = viewModel



                    ){query->

                    viewModel.searchBooks(query)
                }
                
                Spacer(modifier = Modifier.height(13.dp))

                BookList(navController,viewModel)

            }
            


        }

    }


}

@Composable
fun BookList(
    navController: NavController,
    viewModel: BookSearchViewModel
) {


    val listofBooks = viewModel.listOfBooks
    Log.d("PPLK", "BookList: ${listofBooks.size}")
//        var listofBooks : List<Item> = emptyList()
    if(viewModel.isLoading){
        LinearProgressIndicator()
    }else{
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
            ,
            contentPadding = PaddingValues(18.dp)

        ){

            items(items = listofBooks) { book->

                BookRow(book,navController)

            }

        }

    }






}

@Composable
fun BookRow(book: Item, navController: NavController) {
    Card(modifier = Modifier
        .clickable {
            navController.navigate(ReaderScreens.DetailScreen.name + "/${book.id}")
        }
        .fillMaxWidth()
        .height(120.dp)
        .padding(3.dp),
        shape = RectangleShape,
        elevation =CardDefaults.cardElevation(2.dp),


    ) {

        Row(
            modifier = Modifier
                .padding(1.dp)
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
//            Log.d("PPLO", "BookRow: ${book.volumeInfo?.imageLinks?.smallThumbnail?.toString()} and id : ${book.id}")

            val imgUrl =  if(book.volumeInfo?.imageLinks?.smallThumbnail.isNullOrEmpty()){
//                temporary image
                "http://books.google.com/books/content?id=Q7vLPAAACAAJ&printsec=frontcover&img=1&zoom=5&source=gbs_api"



            }else{
                book.volumeInfo.imageLinks.smallThumbnail.toString()

            }



//
            Image(painter = rememberAsyncImagePainter(model = imgUrl),
                contentDescription = "Book Image " ,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(5.dp)
            )

            Column {
//                Text(text = " id : ${book.id}")
                Text(
                    text = "${book.volumeInfo?.title?.toString()?: "Not Available"}" ,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,

                )

                Text(
                    text = "Authors : ${book.volumeInfo?.authors?.toString()?: "Not Available"}",
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    maxLines = 1
                )

                Text(
                    text = "Date : ${book.volumeInfo?.publishedDate?.toString() ?: "Not Available"}",
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    maxLines = 1
                )

                Text(
                    text = "Category : ${book.volumeInfo?.categories?.toString()?: "Not Available"}",
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    maxLines = 1,
                )


            }

        }


    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchComponent(
    modifier: Modifier = Modifier,
    viewModel: BookSearchViewModel,
    loading : Boolean = false,
    hint : String = "Search",
    onSearch : (String) -> Unit = {}
) {

    Column {
        val searchQuerry = rememberSaveable {
            mutableStateOf("")

        }

        val keyboardController = LocalSoftwareKeyboardController.current
        val valid = remember(searchQuerry.value) {
            searchQuerry.value.trim().isNotEmpty()
        }
        
        InputField(
            valueState = searchQuerry,
            labelId = "Search",
            enabled = true,
            onAction = KeyboardActions{
                if(!valid) return@KeyboardActions
                onSearch(searchQuerry.value.trim())
                searchQuerry.value = ""
                keyboardController?.hide()
            },
        )








    }

}