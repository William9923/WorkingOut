package com.softhouse.androidutils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

fun Context.color(@ColorRes colorResId: Int) = ContextCompat.getColor(this, colorResId)
fun Context.drawable(@DrawableRes drawableId: Int) = ContextCompat.getDrawable(this, drawableId)

fun Context.copyToClipboard(text: String, userMessage: String? = null) {
    val toastMessage = userMessage ?: getString(R.string.copy_to_clipboard)
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("label", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
}

fun Context.share(text: String, userMessage: String? = null) {
    val intentMessage = userMessage ?: getString(R.string.share_with)
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, intentMessage)
    startActivity(shareIntent)
}
