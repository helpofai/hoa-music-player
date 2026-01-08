package com.helpofai.mymmusic

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.helpofai.mymmusic.ui.MusicViewModel
import com.helpofai.mymmusic.ui.ThemeViewModel
import com.helpofai.mymmusic.ui.navigation.Screen
import com.helpofai.mymmusic.ui.screens.*
import com.helpofai.mymmusic.ui.theme.ColorExtractor
import com.helpofai.mymmusic.ui.theme.MyMMusicTheme
import com.helpofai.mymmusic.ui.components.MusicNavigationDrawer
import com.helpofai.mymmusic.ui.components.SleepTimerSheet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MusicViewModel by viewModels()
    private val themeViewModel: ThemeViewModel by viewModels()

    @OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val currentTheme by themeViewModel.currentTheme.collectAsState()
            val dynamicColors by themeViewModel.dynamicColors.collectAsState()
            val currentMediaItem by viewModel.currentMediaItem.collectAsState()
            val context = LocalContext.current
            
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            val navController = rememberNavController()
            
            var showSleepTimer by remember { mutableStateOf(false) }
            val sleepTimerSheetState = rememberModalBottomSheetState()
            val sleepTimerMillis by viewModel.sleepTimerMillis.collectAsState()

            // Extract colors when song changes
            LaunchedEffect(currentMediaItem) {
                currentMediaItem?.mediaMetadata?.artworkUri?.let { uri ->
                    val colors = ColorExtractor.extractFromUri(context, uri)
                    themeViewModel.updateDynamicColors(colors)
                } ?: run {
                    themeViewModel.updateDynamicColors(null)
                }
            }

            // Permissions
            val mediaPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_AUDIO
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
            val recordAudioPermission = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
            val storagePermission = rememberPermissionState(mediaPermission)

            LaunchedEffect(Unit) {
                storagePermission.launchPermissionRequest()
                recordAudioPermission.launchPermissionRequest()
            }

            MyMMusicTheme(
                themeType = currentTheme,
                dynamicExtractedColors = dynamicColors
            ) {
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        MusicNavigationDrawer(
                            onItemClick = { label ->
                                scope.launch { drawerState.close() }
                                when(label) {
                                    "Home" -> navController.navigate(Screen.Home.route) {
                                        popUpTo(Screen.Home.route) { inclusive = true }
                                    }
                                    "Library" -> navController.navigate(Screen.Library.route)
                                    "Folders" -> navController.navigate(Screen.Folders.route)
                                    "Equalizer" -> navController.navigate(Screen.Eq.route)
                                    "Sleep Timer" -> showSleepTimer = true
                                    "Settings" -> navController.navigate(Screen.Settings.route)
                                    "About" -> navController.navigate(Screen.About.route)
                                }
                            },
                            currentRoute = null
                        )
                    }
                ) {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            NavHost(
                                navController = navController,
                                startDestination = Screen.Splash.route
                            ) {
                                composable(Screen.Splash.route) {
                                    SplashScreen(navController = navController)
                                }
                                composable(Screen.Home.route) {
                                                                        HomeScreen(
                                                                            viewModel = viewModel,
                                                                            onThemeToggle = { themeViewModel.toggleTheme() },
                                                                            onNavigateToPlayer = { navController.navigate(Screen.NowPlaying.route) },
                                                                            onSearchClick = { navController.navigate(Screen.Search.route) },
                                                                            onFoldersClick = { navController.navigate(Screen.Folders.route) },
                                                                            onLibraryClick = { navController.navigate(Screen.Library.route) },
                                                                            onMenuClick = { scope.launch { drawerState.open() } },
                                                                            onEqClick = { navController.navigate(Screen.Eq.route) }
                                                                        )                                }
                                composable(Screen.NowPlaying.route) {
                                    NowPlayingScreen(
                                        viewModel = viewModel,
                                        onBackClick = { navController.popBackStack() },
                                        onEqClick = { navController.navigate(Screen.Eq.route) }
                                    )
                                }
                                composable(Screen.Eq.route) {
                                    EqScreen(
                                        viewModel = viewModel,
                                        onBackClick = { navController.popBackStack() }
                                    )
                                }
                                composable(Screen.Search.route) {
                                    SearchScreen(
                                        viewModel = viewModel,
                                        onBackClick = { navController.popBackStack() }
                                    )
                                }
                                composable(Screen.Folders.route) {
                                    FoldersScreen(
                                        viewModel = viewModel,
                                        onBackClick = { navController.popBackStack() },
                                        onFolderClick = { folder ->
                                            navController.navigate(Screen.FolderDetails.createRoute(folder.path))
                                        },
                                        onEqClick = { navController.navigate(Screen.Eq.route) }
                                    )
                                }
                                composable(Screen.FolderDetails.route) { backStackEntry ->
                                    val folderPath = backStackEntry.arguments?.getString("folderPath") ?: ""
                                    FolderDetailsScreen(
                                        viewModel = viewModel,
                                        folderPath = folderPath,
                                        onBackClick = { navController.popBackStack() }
                                    )
                                }
                                composable(Screen.Library.route) {
                                    LibraryScreen(
                                        viewModel = viewModel,
                                        onBackClick = { navController.popBackStack() },
                                        onEqClick = { navController.navigate(Screen.Eq.route) }
                                    )
                                }
                                composable(Screen.Settings.route) {
                                    SettingsScreen(
                                        themeViewModel = themeViewModel,
                                        onBackClick = { navController.popBackStack() }
                                    )
                                }
                                composable(Screen.About.route) {
                                    AboutScreen(
                                        onBackClick = { navController.popBackStack() }
                                    )
                                }
                            }
                        }
                        
                        if (showSleepTimer) {
                            ModalBottomSheet(
                                onDismissRequest = { showSleepTimer = false },
                                sheetState = sleepTimerSheetState,
                                containerColor = MaterialTheme.colorScheme.surface
                            ) {
                                SleepTimerSheet(
                                    remainingMillis = sleepTimerMillis,
                                    onSetTimer = { 
                                        viewModel.setSleepTimer(it)
                                        showSleepTimer = false 
                                    },
                                    onCancelTimer = {
                                        viewModel.cancelSleepTimer()
                                        showSleepTimer = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
