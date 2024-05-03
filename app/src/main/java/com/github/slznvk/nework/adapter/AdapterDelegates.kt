package com.github.slznvk.nework.adapter

import android.view.View
import androidx.core.view.isVisible
import com.github.slznvk.domain.dto.Event
import com.github.slznvk.domain.dto.ListItem
import com.github.slznvk.domain.dto.User
import com.github.slznvk.nework.databinding.CardEventBinding
import com.github.slznvk.nework.databinding.CardUserBinding
import com.github.slznvk.nework.utills.load
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object AdapterDelegates {

//    fun postsDelegate(onInteractionListener: OnInteractionListener?) =
//        adapterDelegateViewBinding<Post, ListItem, CardPostBinding>(
//            { layoutInflater, root -> CardPostBinding.inflate(layoutInflater, root, false) }
//        ) {
//            binding.root.setOnClickListener {
//                onInteractionListener!!.onItem(item)
//            }
//
//            binding.menu.setOnClickListener {
//                PopupMenu(context, it).apply {
//                    inflate(R.menu.options_menu)
//                    setOnMenuItemClickListener { menuItem ->
//                        when (menuItem.itemId) {
//                            R.id.edit -> {
//                                onInteractionListener!!.onEdit(item)
//                                true
//                            }
//
//                            R.id.delete -> {
//                                onInteractionListener!!.onRemove(item)
//                                true
//                            }
//
//                            else -> false
//                        }
//                    }
//                }
//            }
//
//            bind {
//                binding.apply {
//                    avatar.load(item.authorAvatar, true)
//                    author.text = item.author
//                    published.text = formatDateTime(item.published)
//
//                    if (item.attachment != null) {
//                        attachment.load(item.attachment!!.url)
//                        attachment.visibility = View.VISIBLE
//                    } else {
//                        attachment.visibility = View.GONE
//                    }
//
//                    content.text = item.content
//                    likeButton.text = item.likeOwnerIds.size.toString()
//                }
//            }
//        }

    fun usersDelegate() = adapterDelegateViewBinding<User, ListItem, CardUserBinding>(
        { layoutInflater, root -> CardUserBinding.inflate(layoutInflater, root, false) }
    ) {
        bind {
            binding.apply {
                avatar.load(item.avatar, true)
                author.text = item.name
                nickName.text = item.login
            }
        }
    }

    fun eventsDelegate() = adapterDelegateViewBinding<Event, ListItem, CardEventBinding>(
        { layoutInflater, root -> CardEventBinding.inflate(layoutInflater, root, false) }
    ) {
        bind {
            binding.apply {
                avatar.load(item.authorAvatar, true)
                author.text = item.author
                published.text = formatDateTime(item.published)
                content.text = item.content

                if (item.attachment != null) {
                    attachment.load(item.attachment!!.url)
                    attachment.visibility = View.VISIBLE
                } else {
                    attachment.visibility = View.GONE
                }

                type.text = item.type
                datetime.text = formatDateTime(item.datetime)

                if (item.link != null) {
                    link.text = item.link
                }

                like.text = item.likeOwnerIds.size.toString()

                playButton.isVisible = item.type == "ONLINE"

            }
        }
    }
}

fun formatDateTime(dateTimeString: String): String {
    val inputFormatter = DateTimeFormatter.ISO_DATE_TIME
    val outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

    val localDateTime = LocalDateTime.parse(dateTimeString, inputFormatter)
    return localDateTime.format(outputFormatter)
}