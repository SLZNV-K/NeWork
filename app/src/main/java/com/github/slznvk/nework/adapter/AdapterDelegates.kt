package com.github.slznvk.nework.adapter

import androidx.core.view.isVisible
import com.github.slznvk.domain.dto.Job
import com.github.slznvk.domain.dto.ListItem
import com.github.slznvk.domain.dto.User
import com.github.slznvk.nework.databinding.CardJobBinding
import com.github.slznvk.nework.databinding.CardUserBinding
import com.github.slznvk.nework.utills.formatDateTime
import com.github.slznvk.nework.utills.load
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding

object AdapterDelegates {

    fun usersDelegate(itemClickedListener: (User) -> Unit) =
        adapterDelegateViewBinding<User, ListItem, CardUserBinding>(
            { layoutInflater, root -> CardUserBinding.inflate(layoutInflater, root, false) }
        ) {
            binding.root.setOnClickListener {
                itemClickedListener(item)
            }
            bind {
                binding.apply {
                    avatar.load(item.avatar, true)
                    author.text = item.name
                    nickName.text = item.login
                }
            }
        }

    //    fun jobsDelegate(isAuthorized: Boolean) =
    fun jobsDelegate(itemClickedListener: (Job) -> Unit, isAuthorized: Boolean) =
        adapterDelegateViewBinding<Job, ListItem, CardJobBinding>(
            { layoutInflater, root -> CardJobBinding.inflate(layoutInflater, root, false) }
        ) {
            binding.removeButton.setOnClickListener {
                itemClickedListener(item)
            }
            bind {
                binding.apply {
                    companyName.text = item.name
                    experience.text =
                        "${item.start.formatDateTime()} - ${item.finish?.formatDateTime() ?: "Now"}"
                    position.text = item.position
                    link.text = item.link
                    link.isVisible = item.link != null
                    removeButton.isVisible = isAuthorized
                }
            }
        }
}
