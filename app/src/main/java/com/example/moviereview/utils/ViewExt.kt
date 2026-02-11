package com.example.moviereview.utils

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.moviereview.R
import com.google.android.material.snackbar.Snackbar
import androidx.core.view.isVisible

// This function adds a .showSnackbar() method to any View in your app!
fun View.showSnackbar(message: String) {
    val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_SHORT)

    snackbar.view.setBackgroundColor(ContextCompat.getColor(context, R.color.surface_dark))

    val fab = this.rootView.findViewById<View>(R.id.fab_add)

    // If FAB exists and is visible, anchor the Snackbar to it
    if (fab != null) {
        snackbar.anchorView = fab
    }

    val textView = snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
    textView.setTextColor(ContextCompat.getColor(context, R.color.imdb_gold))
    textView.layoutDirection = View.LAYOUT_DIRECTION_LTR
    snackbar.show()
}