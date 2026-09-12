package com.example.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

/**
 * Firebase Configuration for Ruhul Media
 * Project: ruhul-mesia
 */
object FirebaseConfig {
    const val API_KEY = "AIzaSyDcx1EzZ6S2V-ooN-rw1t25wLwmur_1ijI"
    const val AUTH_DOMAIN = "ruhul-mesia.firebaseapp.com"
    const val PROJECT_ID = "ruhul-mesia"
    const val STORAGE_BUCKET = "ruhul-mesia.firebasestorage.app"
    const val MESSAGING_SENDER_ID = "105920612724"
    const val APP_ID = "1:105920612724:web:540a00cef695fc70f9c421"
    const val MEASUREMENT_ID = "G-WGRS2H3ZYM"

    private const val TAG = "FirebaseConfig"

    /**
     * Initializes FirebaseApp with the provided configuration options.
     */
    fun initialize(context: Context) {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                val options = FirebaseOptions.Builder()
                    .setApiKey(API_KEY)
                    .setApplicationId(APP_ID)
                    .setProjectId(PROJECT_ID)
                    .setGcmSenderId(MESSAGING_SENDER_ID)
                    .setStorageBucket(STORAGE_BUCKET)
                    .build()

                FirebaseApp.initializeApp(context, options)
                Log.d(TAG, "FirebaseApp initialized programmatically with project: $PROJECT_ID")
            } else {
                Log.d(TAG, "FirebaseApp was already initialized by Google Services")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Firebase: ${e.message}", e)
        }
    }
}
