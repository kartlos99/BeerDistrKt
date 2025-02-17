package com.example.beerdistrkt.utils

import android.content.Context
import android.content.Intent
import com.example.beerdistrkt.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LogoutUtil(
    private val context: Context,
    private val session: Session,
) {
    private val job = Job()
    private val ioScope = CoroutineScope(Dispatchers.IO + job)

    fun logout() {
        ioScope.launch {
            session.clearSession()
            session.clearUserPreference()
            session.loggedIn = false

            restart()
        }
    }

    val token: String?
        get() = session.accessToken

    private fun restart() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}