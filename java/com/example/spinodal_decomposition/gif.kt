package com.example.spinodal_decomposition

import android.view.ViewGroup
import android.widget.ImageView
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Glide

@Composable
fun GifPlayer(gifFilePath: String?) {
    if (gifFilePath != null) {
        AndroidView(factory = { context ->
            ImageView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                Glide.with(context)
                    .asGif()
                    .load(gifFilePath)
                    .into(this)
            }
        })
    } else {
        Text("GIF路径未设置")
    }
}

