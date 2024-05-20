package com.github.slznvk.nework.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.slznvk.domain.dto.AttachmentType
import com.github.slznvk.domain.dto.ListItem
import com.github.slznvk.domain.dto.Post
import com.github.slznvk.nework.R
import com.github.slznvk.nework.databinding.CardPostBinding
import com.github.slznvk.nework.utills.formatDateTime
import com.github.slznvk.nework.utills.load

interface OnInteractionListener {
    fun onLike(item: ListItem)
    fun onRemove(item: ListItem)
    fun onEdit(item: ListItem)
    fun onItem(item: ListItem)
}

class PostsAdapter(
    private val onInteractionListener: OnInteractionListener,
) : PagingDataAdapter<Post, PostsViewHolder>(PostDiffCallBack) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
        val view = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostsViewHolder(view, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        val point = getItem(position) ?: return
        holder.bind(point)
    }
}

class PostsViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        with(binding) {
            avatar.load(post.authorAvatar, true)
            author.text = post.author
            published.text = post.published.formatDateTime()
            content.text = post.content

            likeButton.text = post.likeOwnerIds.first().toString()
            likeButton.isChecked = post.likedByMe

            likeButton.setOnClickListener {
                onInteractionListener.onLike(post)
            }

            root.setOnClickListener {
                onInteractionListener.onItem(post)
            }

            if (!post.ownedByMe) {
                menu.isVisible = false
            } else {
                menu.setOnClickListener {
                    PopupMenu(it.context, it).apply {
                        inflate(R.menu.options_menu)
                        setOnMenuItemClickListener { menuItem ->
                            when (menuItem.itemId) {
                                R.id.edit -> {
                                    onInteractionListener.onEdit(post)
                                    true
                                }

                                R.id.delete -> {
                                    onInteractionListener.onRemove(post)
                                    true
                                }

                                else -> false
                            }
                        }
                    }
                }
            }
            if (post.attachment != null) {
                when (post.attachment?.type) {
                    AttachmentType.IMAGE -> {
                        imageAttachment.visibility = View.VISIBLE
                        videoAttachment.visibility = View.GONE
                        stubView.visibility = View.GONE
                        imageAttachment.load(post.attachment?.url)
                    }

                    AttachmentType.VIDEO -> {
                        videoAttachment.visibility = View.VISIBLE
                        imageAttachment.visibility = View.GONE
                        stubView.visibility = View.GONE
                        videoAttachment.apply {
                            setMediaController(MediaController(context))
                            setVideoURI(Uri.parse(post.attachment?.url))
                            setOnPreparedListener {
                                start()
                            }
                            setOnCompletionListener {
                                stopPlayback()
                            }
                        }
                    }

                    AttachmentType.AUDIO -> {
                        videoAttachment.visibility = View.GONE
                        imageAttachment.visibility = View.GONE
                        stubView.inflate()

                    }

                    else -> error("Unknown attachment type: ${post.attachment?.type}")
                }
            } else {
                imageAttachment.visibility = View.GONE
                videoAttachment.visibility = View.GONE
                stubView.visibility = View.GONE
            }
        }
    }
}

object PostDiffCallBack : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Post, newItem: Post) = oldItem == newItem
}