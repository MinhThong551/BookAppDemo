package com.example.bookappdemo.ui.AddBook

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.bookappdemo.ui.components.BottomNavItem
import com.example.bookappdemo.ui.components.MyBottomNavigationBar
import kotlinx.coroutines.flow.Flow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookScreen(
    onNavigateToHome: () -> Unit,
    onSaveClick: (String, String) -> Unit,
    toastMessageFlow: Flow<String>? = null
) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        toastMessageFlow?.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            if(message.contains("SUCCESS")) onNavigateToHome()
        }
    }
    Scaffold(
        topBar = { TopAppBar(title = { Text("➕ ADD NEW BOOK") }) },
        bottomBar = {
            MyBottomNavigationBar(
                selectedTab = BottomNavItem.Add,
                onTabSelected = { if (it == BottomNavItem.Home) onNavigateToHome() }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = author,
                onValueChange = { author = it },
                label = { Text("Author") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    onSaveClick(title, author)
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = title.isNotBlank() && author.isNotBlank()
            ) {
                Text("SAVE")
            }
        }
    }
}