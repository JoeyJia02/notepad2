package com.example.notepad2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notepad2.databinding.ActivityNoteListBinding
import kotlinx.coroutines.flow.collectLatest
// 协程核心导入
import kotlinx.coroutines.launch
class NoteListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoteListBinding
    private lateinit var noteDao: NoteDao
    private lateinit var adapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化数据库
        val db = NoteDatabase.getInstance(this)
        noteDao = db.noteDao()

        // 初始化RecyclerView
        adapter = NoteAdapter { note ->
            // 点击笔记跳转到编辑页，并传递笔记ID
            Intent(this, NoteEditActivity::class.java).apply {
                putExtra("NOTE_ID", note.id)
                startActivity(this)
            }
        }
        binding.rvNotes.adapter = adapter
        binding.rvNotes.layoutManager = LinearLayoutManager(this)

        // 监听笔记数据变化（Flow自动刷新列表）
        lifecycleScope.launch {
            noteDao.getAllNotesFlow().collectLatest { notes ->
                adapter.submitList(notes)
            }
        }

        // 添加笔记按钮点击事件
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, NoteEditActivity::class.java))
        }
    }
}