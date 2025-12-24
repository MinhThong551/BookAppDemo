package com.example.bookappdemo.ui.listBook

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
     ) {
    var title by rememberSaveable { mutableStateOf("") }
    var author by rememberSaveable { mutableStateOf("") }

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
                onValueChange = { input ->
                    title = input.replace("\n", "").replace("\t", "")
                },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = author,
                onValueChange = { input ->
                    author= input.replace("\n", "").replace("\t", "")
                },
                label = { Text("Author") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    val cleanTitle = title.trim().replace(Regex("\\s+"), " ")
                    val cleanAuthor = author.trim().replace(Regex("\\s+"), " ")
                    onSaveClick(cleanTitle, cleanAuthor)
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = title.isNotBlank() && author.isNotBlank()
            ) {
                Text("SAVE")
            }
        }
    }
}