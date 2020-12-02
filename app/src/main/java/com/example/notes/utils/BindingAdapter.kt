package com.example.notes.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.notes.R
import java.io.File

@BindingAdapter("setImageSource")
fun ImageView.setImageSource(pathname: String?) {
    if (pathname != null) {
        if (this.id != R.id.button_delete_cover) {
            Glide.with(this.context).load(File(pathname)).override(480).into(this)
        }
        this.visibility = ImageView.VISIBLE
    } else {
        this.visibility = ImageView.GONE
    }
}