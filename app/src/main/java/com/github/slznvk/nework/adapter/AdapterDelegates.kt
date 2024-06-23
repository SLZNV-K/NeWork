package com.github.slznvk.nework.adapter

import android.view.View
import androidx.core.view.isVisible
import com.github.slznvk.domain.dto.Job
import com.github.slznvk.domain.dto.ListItem
import com.github.slznvk.domain.dto.User
import com.github.slznvk.nework.R
import com.github.slznvk.nework.databinding.CardJobBinding
import com.github.slznvk.nework.databinding.CardUserBinding
import com.github.slznvk.nework.utills.formatDateTime
import com.github.slznvk.nework.utills.load
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding

object AdapterDelegates {

    fun usersDelegate(
        itemClickedListener: (User) -> Unit,
        isChoose: Boolean,
        checkBoxClickListener: (User) -> Unit
    ) =
        adapterDelegateViewBinding<User, ListItem, CardUserBinding>(
            { layoutInflater, root -> CardUserBinding.inflate(layoutInflater, root, false) }
        ) {
            binding.root.setOnClickListener {
                itemClickedListener(item)
            }
            binding.checkButton.setOnClickListener {
                checkBoxClickListener(item)
            }
            bind {
                binding.apply {
                    avatar.load(item.avatar, true)
                    author.text = item.name
                    nickName.text = item.login
                    checkButton.isChecked = item.isChooser
                    checkButton.visibility = if (isChoose) {
                        View.VISIBLE
                    } else View.GONE

                }
            }
        }

    fun jobsDelegate(itemClickedListener: (Job) -> Unit, isAuthorized: Boolean) =
        adapterDelegateViewBinding<Job, ListItem, CardJobBinding>(
            { layoutInflater, root -> CardJobBinding.inflate(layoutInflater, root, false) }
        ) {
            binding.removeButton.setOnClickListener {
                itemClickedListener(item)
            }
            bind {
                binding.apply {
                    val startText = item.start.formatDateTime()
                    val finishText =
                        item.finish?.formatDateTime() ?: context.getString(R.string.now)
                    companyName.text = item.name
                    experience.text =
                        context.getString(R.string.experience_period, startText, finishText)
                    position.text = item.position
                    link.text = item.link
                    link.isVisible = item.link != null
                    removeButton.isVisible = isAuthorized
                }
            }
        }
}
