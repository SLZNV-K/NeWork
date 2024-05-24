package com.github.slznvk.nework.utills

import android.content.Context
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import com.github.slznvk.nework.R

object ViewExtension {

    fun createImageView(
        context: Context,
        imageUrl: String?,
        isMargin: Boolean = true
    ): ImageView {
        return ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                40.dpToPixelsInt(context),
                40.dpToPixelsInt(context)
            ).apply {
                if (isMargin) marginEnd = (-16).dpToPixelsInt(context)
            }
            setPadding(
                2.dpToPixelsInt(context),
                2.dpToPixelsInt(context),
                2.dpToPixelsInt(context),
                2.dpToPixelsInt(context)
            )
            contentDescription = context.getString(R.string.description_user_s_avatar)
            setBackgroundResource(R.drawable.circle_button_background)
            load(imageUrl, true)
        }
    }

    fun createSeeMoreUsersButton(context: Context): ImageButton {
        return ImageButton(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                40.dpToPixelsInt(context),
                40.dpToPixelsInt(context)
            ).apply {
                marginStart = (-16).dpToPixelsInt(context)
            }
            setPadding(
                16.dpToPixelsInt(context),
                16.dpToPixelsInt(context),
                16.dpToPixelsInt(context),
                16.dpToPixelsInt(context)
            )
            setBackgroundResource(R.drawable.circle_button_background)
            setImageResource(R.drawable.add_icon)
        }
    }

    private fun Int.dpToPixelsInt(context: Context): Int =
        (this * context.resources.displayMetrics.density).toInt()
}