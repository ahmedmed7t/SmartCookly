package com.nexable.smartcookly.feature.auth.data.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth

class AuthRepository {
    private val auth: FirebaseAuth = Firebase.auth
    
    suspend fun signUp(email: String, password: String): Result<FirebaseUser?> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password)
            Result.success(result.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun signIn(email: String, password: String): Result<FirebaseUser?> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password)
            Result.success(result.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateProfile(displayName: String): Result<Unit> {
        return try {
            val user = auth.currentUser
            if (user != null) {
                user.updateProfile (displayName)
                Result.success(Unit)
            } else {
                Result.failure(Exception("No user is currently signed in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
    
    suspend fun signOut() {
        auth.signOut()
    }
}
