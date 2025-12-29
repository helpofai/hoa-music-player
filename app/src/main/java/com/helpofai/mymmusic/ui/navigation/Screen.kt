package com.helpofai.mymmusic.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")
    object Home : Screen("home_screen")
    object NowPlaying : Screen("now_playing_screen")
    object Eq : Screen("eq_screen")
    object Search : Screen("search_screen")
    object Folders : Screen("folders_screen")
    object FolderDetails : Screen("folder_details_screen/{folderPath}") {
        fun createRoute(path: String) = "folder_details_screen/${java.net.URLEncoder.encode(path, "UTF-8")}"
    }
    object Library : Screen("library_screen")
    object Settings : Screen("settings_screen")
    object About : Screen("about_screen")
}
