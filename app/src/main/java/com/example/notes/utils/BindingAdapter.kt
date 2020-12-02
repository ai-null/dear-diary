package com.example.notes.utils

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import java.io.File
import java.lang.Exception

@BindingAdapter("setBitmapSource")
fun ImageView.setBitmapSource(pathname: String?) {
    if (pathname != null) {
        Glide.with(this.context).load(File(pathname)).override(480).into(this)
        this.visibility = ImageView.VISIBLE
    } else {
        this.visibility = ImageView.GONE
    }
}