package com.example.bookappdemo.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

enum class BottomNavItem(val title: String) {
    Home("Home"),
    Add("Add")
}


@Composable
fun MyBottomNavigationBar(
    selectedTab: BottomNavItem,
    onTabSelected: (BottomNavItem) -> Unit
) {
    NavigationBar {
        BottomNavItem.entries.forEach { item ->
            NavigationBarItem(
                selected = selectedTab == item,
                onClick = { onTabSelected(item) },
                label = { Text(item.title) },
                icon = {
                    Icon(
                        imageVector = when (item) {
                            BottomNavItem.Home -> Icons.Default.Home
                            BottomNavItem.Add -> Icons.Default.Add
                        },
                        contentDescription = null
                    )
                }
            )
        }
    }
}