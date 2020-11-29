package com.example.notes.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.adapter.listener.NoteClickListener
import com.example.notes.databinding.ListItemBinding
import com.example.notes.entities.NoteEntity

class NoteListDiffUtil : DiffUtil.ItemCallback<NoteEntity>() {
    override fun areItemsTheSame(oldItem: NoteEntity, newItem: NoteEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: NoteEntity, newItem: NoteEntity): Boolean {
        return oldItem == newItem
    }
}

class NoteListAdapter(private val clickListener: NoteClickListener) :
    ListAdapter<NoteEntity, NoteListAdapter.NoteViewHolder>(NoteListDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemBinding.inflate(inflater, parent, false)

        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener {
            clickListener.onClick(item)
        }
    }

    class NoteViewHolder(private val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: NoteEntity) {
            // set data to the xml
            binding.noteEntity = data

            // Set image cover to the card conditions
            // since ListAdapter will be used for RecyclerView, the else condition is important.
            // TODO: make adapter binding to reduce the code here
            if (data.imagePath != null) {
                binding.itemCover.setImageBitmap(BitmapFactory.decodeFile(data.imagePath))
                binding.itemCover.visibility = View.VISIBLE
            } else {
                binding.itemCover.visibility = View.GONE
            }
            binding.executePendingBindings()
        }
    }
}