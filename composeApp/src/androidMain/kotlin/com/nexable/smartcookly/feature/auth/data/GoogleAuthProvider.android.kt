package com.nexable.smartcookly.feature.auth.data

import android.app.Activity
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual class GoogleSignInProvider actual constructor(context: Any) {
    private val activity: Activity = (context as? Activity)
        ?: throw IllegalArgumentException("Context must be an Activity for Google Sign-In")
    
    // Web Client ID from Firebase configuration
    private val webClientId = "87301617477-irhlg876q34g627vqht3dc551vdaa2cj.apps.googleusercontent.com"
    
    actual suspend fun signIn(): Result<GoogleAuthCredential> {
        return try {
            val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(webClientId)
                .build()
            
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInWithGoogleOption)
                .build()
            
            val credentialManager = androidx.credentials.CredentialManager.create(activity)

            val result = withContext(Dispatchers.Main) {
                credentialManager.getCredential(
                    context = activity,
                    request = request
                )
            }

            val credential = result.credential
            
            when {
                credential is CustomCredential && 
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL -> {
                    val googleIdTokenCredential = GoogleIdTokenCredential
                        .createFrom(credential.data)

                    Result.success(
                        GoogleAuthCredential(
                            idToken = googleIdTokenCredential.idToken,
                            accessToken = null // Credential Manager doesn't provide access token
                        )
                    )
                }
                else -> {
                    Result.failure(Exception("Unexpected credential type"))
                }
            }
        } catch (e: GetCredentialException) {
            Result.failure(e)
        } catch (e: GoogleIdTokenParsingException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
