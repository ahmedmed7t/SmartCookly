package com.nexable.smartcookly.feature.auth.data

data class GoogleAuthCredential(
    val idToken: String,
    val accessToken: String?
)

expect class GoogleSignInProvider(context: Any) {
    suspend fun signIn(): Result<GoogleAuthCredential>
}
