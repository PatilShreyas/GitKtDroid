package dev.shreyaspatil.ktdroid.utils

import android.app.Activity
import com.google.android.material.snackbar.Snackbar

fun Activity.showSnackbar(message: String) {
    Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
}