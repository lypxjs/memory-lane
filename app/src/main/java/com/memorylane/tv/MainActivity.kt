package com.memorylane.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


import com.memorylane.tv.ai.StoryEngines
import com.memorylane.tv.data.FamilyStore
import com.memorylane.tv.data.NoteStore
import com.memorylane.tv.data.Photo
import com.memorylane.tv.data.SampleAlbum
import com.memorylane.tv.ui.FamilyScreen
import com.memorylane.tv.ui.PhotoDetailScreen
import com.memorylane.tv.ui.PhotoGridScreen
import com.memorylane.tv.ui.RecordMemoryScreen
import com.memorylane.tv.ui.theme.MemoryLaneTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NoteStore.init(this)
        FamilyStore.init(this)
        val storyEngine = StoryEngines.create(this)
        val apiKey = com.memorylane.tv.BuildConfig.GLM_API_KEY

        // Family-recorded notes override the bundled samples. PhotoGridScreen
        // reads them through the observable NoteStore, so a fresh recording
        // refreshes the wall on the way back.
        fun effectivePhotos(): List<Photo> = SampleAlbum.photos.map { photo ->
            val own = NoteStore.effectiveNote(photo)
            if (own != photo.memoryNote) photo.copy(memoryNote = own) else photo
        }

        setContent {
            val navController = rememberNavController()
            MemoryLaneTheme {
                NavHost(
                    navController = navController,
                    startDestination = Routes.GRID,
                ) {
                    composable(Routes.GRID) {
                        PhotoGridScreen(
                            photos = effectivePhotos(),
                            onPhotoClick = { photo ->
                                navController.navigate(Routes.photo(photo.id))
                            },
                            onFamilyClick = { navController.navigate(Routes.FAMILY) },
                        )
                    }
                    composable(Routes.PHOTO) { entry ->
                        val photoId = entry.arguments?.getString("photoId")
                        val photo = effectivePhotos().firstOrNull { it.id == photoId }
                        if (photo == null) {
                            navController.popBackStack()
                        } else {
                            PhotoDetailScreen(
                                photo = photo,
                                storyEngine = storyEngine,
                                onRecord = { navController.navigate(Routes.record(photo.id)) },
                                onBack = { navController.popBackStack() },
                            )
                        }
                    }
                    composable(Routes.RECORD) { entry ->
                        val photoId = entry.arguments?.getString("photoId")
                        val photo = SampleAlbum.photos.firstOrNull { it.id == photoId }
                        if (photo == null) {
                            navController.popBackStack()
                        } else {
                            RecordMemoryScreen(
                                photo = photo,
                                apiKey = apiKey,
                                onSaved = { navController.popBackStack() },
                                onBack = { navController.popBackStack() },
                            )
                        }
                    }
                    composable(Routes.FAMILY) {
                        FamilyScreen(
                            photos = SampleAlbum.photos,
                            onBack = { navController.popBackStack() },
                        )
                    }
                }
            }
        }
    }
}
