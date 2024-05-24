package com.github.slznvk.nework.utills

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.github.slznvk.nework.R

fun ImageView.load(url: String?, circleCrop: Boolean = false) {

    Glide.with(this)
        .load(url)
        .timeout(10_000)
        .apply { if (circleCrop) this.circleCrop() }
        .error(R.drawable.account_icon)
        .into(this)
}