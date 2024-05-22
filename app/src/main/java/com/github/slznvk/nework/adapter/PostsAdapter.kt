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
import com.github.slznvk.nework.observer.MediaLifecycleObserver
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
    private val observer: MediaLifecycleObserver
) : PagingDataAdapter<Post, PostsViewHolder>(PostDiffCallBack) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
        val view = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostsViewHolder(view, onInteractionListener, observer)
    }

    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        val point = getItem(position) ?: return
        holder.bind(point)
    }
}

class PostsViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
    private val observer: MediaLifecycleObserver
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        with(binding) {
            avatar.load(post.authorAvatar, true)
            author.text = post.author
            published.text = post.published.formatDateTime()
            content.text = post.content
            content.isVisible = post.content != ""

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
            imageAttachment.visibility = View.GONE
            videoAttachment.visibility = View.GONE
            audioAttachment.visibility = View.GONE

            if (post.attachment != null) {
                when (post.attachment?.type) {
                    AttachmentType.IMAGE -> imageAttachment.apply {
                        visibility = View.VISIBLE
                        load(post.attachment?.url)
                    }

                    AttachmentType.VIDEO -> videoAttachment.apply {
                        visibility = View.VISIBLE
                        setMediaController(MediaController(context))
                        setVideoURI(Uri.parse(post.attachment?.url))
                        setOnPreparedListener {
                            start()
                        }
                        setOnCompletionListener {
                            stopPlayback()
                        }
                    }

                    AttachmentType.AUDIO -> {
                        audioAttachment.visibility = View.VISIBLE

                        playPauseButton.setOnClickListener {
                            observer.playSong(post.attachment!!.url, post.songPlaying)
                            post.songPlaying = !post.songPlaying
                            playPauseButton.setImageResource(
                                if (post.songPlaying) {
                                    R.drawable.pause_icon
                                } else {
                                    R.drawable.play_icon
                                }
                            )
                        }
                    }

                    else -> error("Unknown attachment type: ${post.attachment?.type}")
                }
            }
        }
    }
}

object PostDiffCallBack : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Post, newItem: Post) = oldItem == newItem
}