package com.padgett.progressnotes.injection.module

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providesFirebaseFirestore(): FirebaseFirestore =
        Firebase.firestore.apply {
            firestoreSettings =
                firestoreSettings {
                    setLocalCacheSettings(
                        PersistentCacheSettings
                            .newBuilder()
                            .setSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                            .build()
                    )
                }
            persistentCacheIndexManager?.apply {
                enableIndexAutoCreation()
            }
        }
}