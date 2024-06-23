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
import com.github.slznvk.domain.dto.Event
import com.github.slznvk.domain.dto.EventType
import com.github.slznvk.nework.R
import com.github.slznvk.nework.databinding.CardEventBinding
import com.github.slznvk.nework.observer.MediaLifecycleObserver
import com.github.slznvk.nework.utills.formatDateTime
import com.github.slznvk.nework.utills.load

class EventsAdapter(
    private val onInteractionListener: OnInteractionListener,
    private val observer: MediaLifecycleObserver
) : PagingDataAdapter<Event, EventsViewHolder>(EventDiffCallBack) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsViewHolder {
        val view = CardEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventsViewHolder(view, onInteractionListener, observer)
    }

    override fun onBindViewHolder(holder: EventsViewHolder, position: Int) {
        val point = getItem(position) ?: return
        holder.bind(point)
    }
}

class EventsViewHolder(
    private val binding: CardEventBinding,
    private val onInteractionListener: OnInteractionListener,
    private val observer: MediaLifecycleObserver
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(event: Event) {
        binding.apply {
            avatar.load(event.authorAvatar, true)
            author.text = event.author
            published.text = event.published.formatDateTime()
            content.text = event.content
            content.isVisible = event.content != ""

            imageAttachment.visibility = View.GONE
            videoAttachment.visibility = View.GONE
            audioAttachment.visibility = View.GONE

            if (event.attachment != null) {
                when (event.attachment?.type) {
                    AttachmentType.IMAGE -> imageAttachment.apply {
                        visibility = View.VISIBLE
                        load(event.attachment?.url)
                    }

                    AttachmentType.VIDEO -> videoAttachment.apply {
                        visibility = View.VISIBLE
                        setMediaController(MediaController(context))
                        setVideoURI(Uri.parse(event.attachment?.url))
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
                            observer.playSong(event.attachment!!.url, event.songPlaying)
                            event.songPlaying = !event.songPlaying
                            playPauseButton.setImageResource(
                                if (event.songPlaying) {
                                    R.drawable.pause_icon
                                } else {
                                    R.drawable.play_icon
                                }
                            )
                        }
                    }

                    else -> error("Unknown attachment type: ${event.attachment?.type}")
                }
            }

            type.text = event.type.value
            datetime.text = event.datetime.formatDateTime()

            link.isVisible = event.link != null
            if (event.link != null) {
                link.text = event.link
            }

            like.text = event.likeOwnerIds.size.toString()
            like.setOnClickListener {
                onInteractionListener.onLike(event)
            }

            playButton.isVisible = event.type == EventType.ONLINE

            if (!event.ownedByMe) {
                menu.isVisible = false
            } else {
                menu.setOnClickListener {
                    PopupMenu(it.context, it).apply {
                        inflate(R.menu.options_menu)
                        setOnMenuItemClickListener { menuItem ->
                            when (menuItem.itemId) {
                                R.id.edit -> {
                                    onInteractionListener.onEdit(event)
                                    true
                                }

                                R.id.delete -> {
                                    onInteractionListener.onRemove(event)
                                    true
                                }

                                else -> false
                            }
                        }
                    }
                }
            }

            root.setOnClickListener {
                onInteractionListener.onItem(event)
            }
        }
    }
}

object EventDiffCallBack : DiffUtil.ItemCallback<Event>() {
    override fun areItemsTheSame(oldItem: Event, newItem: Event) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Event, newItem: Event) = oldItem == newItem
}