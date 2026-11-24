package com.example.tvlauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import androidx.tv.material3.Typography
import androidx.tv.material3.darkColorScheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val DarkBackground = Color(0xFF121212)
val CardBackground = Color(0xFF1E1E1E)
val Accent = Color(0xFFBB86FC)
val TextPrimary = Color(0xFFE1E1E1)

private val TvColorScheme = darkColorScheme(
    primary = Accent,
    onPrimary = Color.Black,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = CardBackground,
    onSurface = TextPrimary,
    surfaceVariant = CardBackground,
    onSurfaceVariant = TextPrimary
)

@Composable
fun TvLauncherTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TvColorScheme,
        typography = Typography(),
        content = content
    )
}

@Immutable
data class AppInfo(
    val id: String,
    val name: String,
    val packageName: String,
    val emoji: String = "📺"
)

class HomeViewModel : ViewModel() {
    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()

    init {
        viewModelScope.launch {
            _apps.value = listOf(
                AppInfo("1", "YouTube", "com.google.android.youtube.tv", "▶️"),
                AppInfo("2", "Netflix", "com.netflix.ninja", "🎬"),
                AppInfo("3", "Кинопоиск", "ru.kinopoisk.tv", "🍿"),
                AppInfo("4", "Настройки", "com.android.tv.settings", "⚙️"),
                AppInfo("5", "Галерея", "com.android.gallery3d", "🖼️"),
                AppInfo("6", "Музыка", "com.google.android.music", "🎵"),
                AppInfo("7", "Браузер", "com.android.browser", "🌐"),
                AppInfo("8", "Файлы", "com.android.documentsui", "📁")
            )
        }
    }
}

@Composable
fun AppCard(app: AppInfo, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        modifier = modifier.width(180.dp).height(120.dp),
        colors = CardDefaults.colors(
            containerColor = CardBackground,
            focusedContainerColor = CardBackground,
            contentColor = TextPrimary,
            focusedContentColor = TextPrimary
        ),
        scale = CardDefaults.scale(focusedScale = 1.06f),
        border = CardDefaults.border(focusedBorder = BorderStroke(3.dp, Accent))
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = app.emoji, fontSize = 32.sp)
                Spacer(Modifier.height(8.dp))
                Text(text = app.name, color = TextPrimary, fontSize = 14.sp, maxLines = 1)
            }
        }
    }
}

@Composable
fun HomeScreen(apps: List<AppInfo>, onAppClick: (AppInfo) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(DarkBackground)) {
        TopBar()
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items = apps, key = { it.id }) { app ->
                AppCard(app = app, onClick = { onAppClick(app) })
            }
        }
    }
}

@Composable
private fun TopBar() {
    var currentTime by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        while (true) {
            currentTime = formatter.format(Date())
            delay(60_000L)
        }
    }
    Row(
        modifier = Modifier.fillMaxWidth().background(DarkBackground).padding(horizontal = 32.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = currentTime, color = TextPrimary, fontSize = 28.sp)
        Text(text = "⚙️ Настройки", color = TextPrimary, fontSize = 16.sp)
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TvLauncherTheme {
                val vm: HomeViewModel = viewModel()
                val apps by vm.apps.collectAsState()
                HomeScreen(apps = apps, onAppClick = { })
            }
        }
    }
}
