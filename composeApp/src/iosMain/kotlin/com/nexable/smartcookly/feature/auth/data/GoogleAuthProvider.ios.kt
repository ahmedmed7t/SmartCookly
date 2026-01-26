package com.nexable.smartcookly.feature.auth.data

import cocoapods.GoogleSignIn.GoogleSignInHelper
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

actual class GoogleSignInProvider actual constructor(context: Any) {
    // iOS Client ID from Firebase configuration
    private val iosClientId = "87301617477-hbppkuo0gg5pn2rvtvhlpgju4a1qseaa.apps.googleusercontent.com"
    
    actual suspend fun signIn(): Result<GoogleAuthCredential> = suspendCancellableCoroutine { cont ->
        GoogleSignInHelper.shared.signInWithClientId(
            clientId = iosClientId
        ) { idToken, accessToken, error ->
            if (error != null) {
                cont.resume(Result.failure(Exception(error.localizedDescription() ?: "Google Sign-In failed")))
            } else if (idToken != null) {
                cont.resume(Result.success(GoogleAuthCredential(idToken, accessToken)))
            } else {
                cont.resume(Result.failure(Exception("No ID token received")))
            }
        }
    }
}
