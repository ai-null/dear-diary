package com.example.notes.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.databinding.ListItemBinding
import com.example.notes.entities.NoteEntity

class NoteListAdapter(private val clickListener: NoteClickListener) :
    RecyclerView.Adapter<NoteListAdapter.NoteViewHolder>() {
    var data = listOf<NoteEntity>()
        set(value) {
            field = value

            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return NoteViewHolder(ListItemBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            clickListener.onClick(item)
        }
    }

    override fun getItemCount() = data.size

    class NoteViewHolder(private val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: NoteEntity) {
            binding.itemTitle.text = data.title
            binding.itemSubtitle.text = data.subtitle
        }
    }

    class NoteClickListener(val clickListener: (NoteEntity) -> Unit) {
        fun onClick(noteEntity: NoteEntity) = clickListener(noteEntity)
    }
}