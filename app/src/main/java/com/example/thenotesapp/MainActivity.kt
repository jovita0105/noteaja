package com.example.thenotesapp

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.thenotesapp.repository.NoteRepository
import com.example.thenotesapp.screens.AddNoteScreen
import com.example.thenotesapp.screens.EditNoteScreen
import com.example.thenotesapp.screens.HomeScreen
import com.example.thenotesapp.screens.LoginScreen
import com.example.thenotesapp.screens.RegisterScreen
import com.example.thenotesapp.viewmodel.NoteViewModel
import com.example.thenotesapp.viewmodel.NoteViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val noteRepository = NoteRepository()
        val viewModelProviderFactory = NoteViewModelFactory(application, noteRepository)
        val noteViewModel = ViewModelProvider(this, viewModelProviderFactory)[NoteViewModel::class.java]

        setContent {
            val view = LocalView.current
            if (!view.isInEditMode) {
                SideEffect {
                    val window = (view.context as Activity).window
                    window.statusBarColor = Color(0xFFF48FB1).toArgb()
                    WindowInsetsControllerCompat(window, view).isAppearanceLightStatusBars = false
                }
            }

            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "login_screen") {

                composable("login_screen") {
                    LoginScreen(navController, noteViewModel)
                }

                composable("register_screen") {
                    RegisterScreen(navController, noteViewModel)
                }

                composable(
                    route = "home_screen/{userId}",
                    arguments = listOf(navArgument("userId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val userId = backStackEntry.arguments?.getString("userId") ?: ""
                    HomeScreen(navController, noteViewModel, userId)
                }

                composable(
                    route = "add_note_screen/{userId}",
                    arguments = listOf(navArgument("userId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val userId = backStackEntry.arguments?.getString("userId") ?: ""
                    AddNoteScreen(navController, noteViewModel, userId)
                }

                composable(
                    route = "edit_note_screen/{noteId}/{userId}",
                    arguments = listOf(
                        navArgument("noteId") { type = NavType.StringType },
                        navArgument("userId") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val noteId = backStackEntry.arguments?.getString("noteId") ?: ""
                    val userId = backStackEntry.arguments?.getString("userId") ?: ""
                    EditNoteScreen(navController, noteViewModel, noteId, userId)
                }
            }
        }
    }
}