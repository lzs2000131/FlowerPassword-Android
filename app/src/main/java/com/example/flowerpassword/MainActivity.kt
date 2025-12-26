package com.example.flowerpassword

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flowerpassword.ui.MainScreen
import com.example.flowerpassword.ui.MainViewModel
import com.example.flowerpassword.ui.theme.FlowerPasswordTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as FlowerPasswordApp
        
        setContent {
            FlowerPasswordTheme {
                val viewModel: MainViewModel = viewModel(
                    factory = MainViewModel.Factory(app.database, app.preferencesManager)
                )
                val uiState by viewModel.uiState.collectAsState()

                MainScreen(
                    uiState = uiState,
                    onKeywordChange = viewModel::updateKeyword,
                    onCodeChange = viewModel::updateCode,
                    onToggleShowPassword = viewModel::toggleShowPassword,
                    onHistoryItemClick = viewModel::selectHistoryItem,
                    onHistoryItemDelete = viewModel::deleteHistoryItem,
                    onClearError = viewModel::clearError
                )
            }
        }
    }
}
