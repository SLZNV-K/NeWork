package com.github.slznvk.nework.utills

import android.annotation.SuppressLint
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.github.slznvk.nework.R

@SuppressLint("CheckResult")
fun ImageView.load(url: String?, circleCrop: Boolean = false) {

    Glide.with(this)
        .load(url)
        .timeout(10_000)
        .apply { if (circleCrop) circleCrop() }
        .error(R.drawable.account_icon)
        .into(this)
}