package com.example.firebase

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

data class UserAccount(
    val uid: String,
    val email: String,
    val displayName: String,
    val photoUrl: String = "",
    val idToken: String = ""
)

class FirebaseAuthService(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("ruhul_media_auth_prefs", Context.MODE_PRIVATE)

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    companion object {
        private const val TAG = "FirebaseAuthService"
        private const val BASE_URL = "https://identitytoolkit.googleapis.com/v1/accounts"
        private const val PREF_KEY_UID = "auth_uid"
        private const val PREF_KEY_EMAIL = "auth_email"
        private const val PREF_KEY_NAME = "auth_name"
        private const val PREF_KEY_PHOTO = "auth_photo"
        private const val PREF_KEY_TOKEN = "auth_token"
    }

    /**
     * Retrieves currently signed in user from local storage.
     */
    fun getCurrentUser(): UserAccount? {
        val uid = prefs.getString(PREF_KEY_UID, null) ?: return null
        val email = prefs.getString(PREF_KEY_EMAIL, "") ?: ""
        val name = prefs.getString(PREF_KEY_NAME, "") ?: ""
        val photo = prefs.getString(PREF_KEY_PHOTO, "") ?: ""
        val token = prefs.getString(PREF_KEY_TOKEN, "") ?: ""
        return UserAccount(
            uid = uid,
            email = email,
            displayName = if (name.isNotBlank()) name else email.substringBefore("@"),
            photoUrl = photo,
            idToken = token
        )
    }

    /**
     * Signs up a new user with Firebase Authentication.
     */
    suspend fun signUp(email: String, password: String, displayName: String): Result<UserAccount> =
        withContext(Dispatchers.IO) {
            try {
                val url = "$BASE_URL:signUp?key=${FirebaseConfig.API_KEY}"
                val jsonBody = JSONObject().apply {
                    put("email", email.trim())
                    put("password", password.trim())
                    put("returnSecureToken", true)
                }

                val request = Request.Builder()
                    .url(url)
                    .post(jsonBody.toString().toRequestBody(jsonMediaType))
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""

                if (!response.isSuccessful) {
                    val errorMsg = parseFirebaseError(responseBody)
                    return@withContext Result.failure(Exception(errorMsg))
                }

                val jsonResponse = JSONObject(responseBody)
                val uid = jsonResponse.optString("localId", "")
                val idToken = jsonResponse.optString("idToken", "")
                val userEmail = jsonResponse.optString("email", email)

                val effectiveName = if (displayName.isNotBlank()) displayName.trim() else userEmail.substringBefore("@")

                // Update display name in Firebase if provided
                if (displayName.isNotBlank() && idToken.isNotBlank()) {
                    updateDisplayName(idToken, effectiveName)
                }

                val user = UserAccount(
                    uid = uid,
                    email = userEmail,
                    displayName = effectiveName,
                    photoUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150&q=80",
                    idToken = idToken
                )

                saveUserToPrefs(user)
                Result.success(user)
            } catch (e: IOException) {
                Log.e(TAG, "Network error during sign up: ${e.message}", e)
                Result.failure(Exception("ইন্টারনেট সংযোগে সমস্যা হয়েছে। দয়া করে আপনার ইন্টারনেট চেক করুন।"))
            } catch (e: Exception) {
                Log.e(TAG, "Sign up error: ${e.message}", e)
                Result.failure(Exception(e.message ?: "সাইন-আপ ব্যর্থ হয়েছে"))
            }
        }

    /**
     * Signs in an existing user with Firebase Authentication.
     */
    suspend fun signIn(email: String, password: String): Result<UserAccount> =
        withContext(Dispatchers.IO) {
            try {
                val url = "$BASE_URL:signInWithPassword?key=${FirebaseConfig.API_KEY}"
                val jsonBody = JSONObject().apply {
                    put("email", email.trim())
                    put("password", password.trim())
                    put("returnSecureToken", true)
                }

                val request = Request.Builder()
                    .url(url)
                    .post(jsonBody.toString().toRequestBody(jsonMediaType))
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""

                if (!response.isSuccessful) {
                    val errorMsg = parseFirebaseError(responseBody)
                    return@withContext Result.failure(Exception(errorMsg))
                }

                val jsonResponse = JSONObject(responseBody)
                val uid = jsonResponse.optString("localId", "")
                val userEmail = jsonResponse.optString("email", email)
                val idToken = jsonResponse.optString("idToken", "")
                val serverName = jsonResponse.optString("displayName", "")
                val displayName = if (serverName.isNotBlank()) serverName else userEmail.substringBefore("@")

                val user = UserAccount(
                    uid = uid,
                    email = userEmail,
                    displayName = displayName,
                    photoUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150&q=80",
                    idToken = idToken
                )

                saveUserToPrefs(user)
                Result.success(user)
            } catch (e: IOException) {
                Log.e(TAG, "Network error during sign in: ${e.message}", e)
                Result.failure(Exception("ইন্টারনেট সংযোগে সমস্যা হয়েছে। দয়া করে আপনার ইন্টারনেট চেক করুন।"))
            } catch (e: Exception) {
                Log.e(TAG, "Sign in error: ${e.message}", e)
                Result.failure(Exception(e.message ?: "লগইন ব্যর্থ হয়েছে"))
            }
        }

    /**
     * Sends password reset email.
     */
    suspend fun sendPasswordReset(email: String): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                val url = "$BASE_URL:sendOobCode?key=${FirebaseConfig.API_KEY}"
                val jsonBody = JSONObject().apply {
                    put("requestType", "PASSWORD_RESET")
                    put("email", email.trim())
                }

                val request = Request.Builder()
                    .url(url)
                    .post(jsonBody.toString().toRequestBody(jsonMediaType))
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""

                if (!response.isSuccessful) {
                    val errorMsg = parseFirebaseError(responseBody)
                    return@withContext Result.failure(Exception(errorMsg))
                }

                Result.success("পাসওয়ার্ড রিসেট লিংক আপনার ইমেইলে পাঠানো হয়েছে। অনুগ্রহ করে ইনবক্স চেক করুন।")
            } catch (e: Exception) {
                Result.failure(Exception(e.message ?: "পাসওয়ার্ড রিসেট ব্যর্থ হয়েছে"))
            }
        }

    /**
     * Updates profile display name on Firebase.
     */
    private fun updateDisplayName(idToken: String, displayName: String) {
        try {
            val url = "$BASE_URL:update?key=${FirebaseConfig.API_KEY}"
            val jsonBody = JSONObject().apply {
                put("idToken", idToken)
                put("displayName", displayName)
                put("returnSecureToken", true)
            }
            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody(jsonMediaType))
                .build()
            client.newCall(request).execute()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to update display name: ${e.message}")
        }
    }

    /**
     * Saves user to SharedPreferences for session persistence.
     */
    fun saveUserToPrefs(user: UserAccount) {
        prefs.edit()
            .putString(PREF_KEY_UID, user.uid)
            .putString(PREF_KEY_EMAIL, user.email)
            .putString(PREF_KEY_NAME, user.displayName)
            .putString(PREF_KEY_PHOTO, user.photoUrl)
            .putString(PREF_KEY_TOKEN, user.idToken)
            .apply()
    }

    /**
     * Signs out the user and clears local session.
     */
    fun signOut() {
        prefs.edit().clear().apply()
    }

    /**
     * Parses Firebase REST errors into clear messages.
     */
    private fun parseFirebaseError(responseBody: String): String {
        return try {
            val root = JSONObject(responseBody)
            val errorObj = root.optJSONObject("error")
            val codeMessage = errorObj?.optString("message", "") ?: ""

            when {
                codeMessage.startsWith("EMAIL_EXISTS") ->
                    "এই ইমেইল দিয়ে ইতিমধ্যে অ্যাকাউন্ট তৈরি আছে। অনুগ্রহ করে লগইন করুন।"
                codeMessage.startsWith("INVALID_LOGIN_CREDENTIALS") ||
                codeMessage.startsWith("INVALID_PASSWORD") ||
                codeMessage.startsWith("EMAIL_NOT_FOUND") ->
                    "ইমেইল অথবা পাসওয়ার্ড সঠিক নয়। আবার চেষ্টা করুন।"
                codeMessage.startsWith("WEAK_PASSWORD") ->
                    "পাসওয়ার্ড অত্যন্ত দুর্বল। কমপক্ষে ৬টি অক্ষর ব্যবহার করুন।"
                codeMessage.startsWith("INVALID_EMAIL") ->
                    "একটি সঠিক ইমেইল এড্রেস প্রদান করুন।"
                codeMessage.startsWith("OPERATION_NOT_ALLOWED") ->
                    "Firebase Console-এ Email/Password Authentication সক্রিয় (Enabled) নেই। Firebase Console > Authentication এ গিয়ে Email/Password এনাবল করুন।"
                codeMessage.startsWith("TOO_MANY_ATTEMPTS_TRY_LATER") ->
                    "অতিরিক্ত ভুল চেষ্টার কারণে সাময়িকভাবে ব্লক করা হয়েছে। কিছুক্ষণ পর আবার চেষ্টা করুন।"
                codeMessage.startsWith("USER_DISABLED") ->
                    "এই অ্যাকাউন্টটি নিষ্ক্রিয় করা হয়েছে।"
                else ->
                    if (codeMessage.isNotBlank()) "Firebase ত্রুটি: $codeMessage"
                    else "অনুরোধটি সম্পন্ন করা যায়নি। পুনরায় চেষ্টা করুন।"
            }
        } catch (e: Exception) {
            "অনুরোধটি সম্পন্ন করা যায়নি।"
        }
    }
}
