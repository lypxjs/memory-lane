package com.memorylane.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


import com.memorylane.tv.ai.StoryEngines
import com.memorylane.tv.data.SampleAlbum
import com.memorylane.tv.ui.PhotoDetailScreen
import com.memorylane.tv.ui.PhotoGridScreen
import com.memorylane.tv.ui.theme.MemoryLaneTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val storyEngine = StoryEngines.create(this)
        setContent {
            val navController = rememberNavController()
            MemoryLaneTheme {
                NavHost(
                    navController = navController,
                    startDestination = Routes.GRID,
                ) {
                    composable(Routes.GRID) {
                        PhotoGridScreen(
                            photos = SampleAlbum.photos,
                            onPhotoClick = { photo ->
                                navController.navigate(Routes.photo(photo.id))
                            },
                        )
                    }
                    composable(Routes.PHOTO) { entry ->
                        val photoId = entry.arguments?.getString("photoId")
                        val photo = SampleAlbum.photos.firstOrNull { it.id == photoId }
                        if (photo == null) {
                            navController.popBackStack()
                        } else {
                            PhotoDetailScreen(
                                photo = photo,
                                storyEngine = storyEngine,
                                onBack = { navController.popBackStack() },
                            )
                        }
                    }
                }
            }
        }
    }
}
