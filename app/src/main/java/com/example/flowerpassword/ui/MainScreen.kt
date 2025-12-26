package com.example.flowerpassword.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flowerpassword.data.HistoryItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    uiState: MainUiState,
    onKeywordChange: (String) -> Unit,
    onCodeChange: (String) -> Unit,
    onToggleShowPassword: () -> Unit,
    onHistoryItemClick: (HistoryItem) -> Unit,
    onHistoryItemDelete: (HistoryItem) -> Unit,
    onClearError: () -> Unit
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    
    // Check if screen height is less than 60% of normal (roughly 400dp for typical phones)
    val isCompactHeight = configuration.screenHeightDp < 400

    // Snackbar for errors
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            onClearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF667eea),
                            Color(0xFF764ba2)
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = if (isCompactHeight) 8.dp else 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // App Title - hide on compact height
                if (!isCompactHeight) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "🌸 花密",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                    Text(
                        text = "FlowerPassword",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Main Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(if (isCompactHeight) 16.dp else 24.dp),
                        verticalArrangement = Arrangement.spacedBy(if (isCompactHeight) 8.dp else 16.dp)
                    ) {
                        // Memory Password Field
                        OutlinedTextField(
                            value = uiState.keyword,
                            onValueChange = onKeywordChange,
                            label = { Text("记忆密码") },
                            placeholder = { Text("输入你的记忆密码") },
                            leadingIcon = {
                                Icon(Icons.Outlined.Lock, contentDescription = null)
                            },
                            trailingIcon = {
                                IconButton(onClick = onToggleShowPassword) {
                                    Icon(
                                        if (uiState.showPassword) Icons.Filled.VisibilityOff
                                        else Icons.Filled.Visibility,
                                        contentDescription = if (uiState.showPassword) "隐藏" else "显示"
                                    )
                                }
                            },
                            visualTransformation = if (uiState.showPassword) 
                                VisualTransformation.None 
                            else 
                                PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Next
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true
                        )

                        // Code/Key Field
                        OutlinedTextField(
                            value = uiState.code,
                            onValueChange = onCodeChange,
                            label = { Text("区分代号") },
                            placeholder = { Text("如: google, facebook") },
                            leadingIcon = {
                                Icon(Icons.Outlined.Key, contentDescription = null)
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true
                        )

                        // Generated Password Display - click to copy
                        AnimatedVisibility(
                            visible = uiState.generatedPassword.isNotEmpty(),
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("password", uiState.generatedPassword)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "密码已复制", Toast.LENGTH_SHORT).show()
                                    },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = uiState.generatedPassword,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp,
                                            fontSize = 16.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        Icons.Outlined.ContentCopy,
                                        contentDescription = "点击复制",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(if (isCompactHeight) 8.dp else 24.dp))

                // History Section
                if (uiState.historyItems.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 12.dp)
                            ) {
                                Icon(
                                    Icons.Filled.History,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "历史记录",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(
                                    items = uiState.historyItems,
                                    key = { it.id }
                                ) { item ->
                                    HistoryItemCard(
                                        item = item,
                                        onClick = { onHistoryItemClick(item) },
                                        onDelete = { onHistoryItemDelete(item) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryItemCard(
    item: HistoryItem,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    Icons.Outlined.Label,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = item.code,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Outlined.Delete,
                    contentDescription = "删除",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
