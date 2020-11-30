package com.example.notes.utils

import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.databinding.BindingAdapter

@BindingAdapter("setBitmapSource")
fun ImageView.setBitmapSource(pathname: String?) {
    if (pathname !== null) {
        this.setImageBitmap(BitmapFactory.decodeFile(pathname))
        this.visibility = ImageView.VISIBLE
    } else {
        this.visibility = ImageView.GONE
    }
}