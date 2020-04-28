package com.tangem.devkit.extensions

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment

/**
 * Created by Anton Zhilenkov on 09/04/2020.
 */
fun Fragment.shareText(text: String) {
    requireActivity().shareText(text)
}

fun ComponentActivity.shareText(text: String) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    startActivity(shareIntent)
}