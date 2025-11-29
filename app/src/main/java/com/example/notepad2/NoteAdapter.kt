package com.example.notepad2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notepad2.databinding.ItemNoteBinding
import java.text.SimpleDateFormat
import java.util.Locale

class NoteAdapter(private val onNoteClick: (Note) -> Unit) :
    ListAdapter<Note, NoteAdapter.NoteViewHolder>(NoteDiffCallback()) {

    // 时间格式化（显示为"年-月-日 时:分"）
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    inner class NoteViewHolder(private val binding: ItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(note: Note) {
            binding.tvTitle.text = note.title
            binding.tvTime.text = dateFormat.format(note.createTime)
            // 点击笔记项跳转到编辑页
            binding.root.setOnClickListener { onNoteClick(note) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // DiffUtil：高效更新列表（只刷新变化的项）
    class NoteDiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id // 按ID判断是否为同一项
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem // 内容是否完全相同
        }
    }
}