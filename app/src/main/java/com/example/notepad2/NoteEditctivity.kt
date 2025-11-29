package com.example.notepad2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.notepad2.databinding.ActivityNoteEditBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.Toast

class NoteEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoteEditBinding
    private lateinit var noteDao: NoteDao
    private var currentNoteId: Int = -1 // -1表示新增笔记

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化数据库
        val db = NoteDatabase.getInstance(this)
        noteDao = db.noteDao()

        // 获取传递的笔记ID（编辑模式）
        currentNoteId = intent.getIntExtra("NOTE_ID", -1)
        if (currentNoteId != -1) {
            // 加载已有笔记内容
            lifecycleScope.launch {
                val note = noteDao.getNoteById(currentNoteId)
                note?.let {
                    binding.etTitle.setText(it.title)
                    binding.etContent.setText(it.content)
                }
            }
        }

        // 保存按钮点击事件
        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val content = binding.etContent.text.toString().trim()

            if (title.isEmpty()) {
                Toast.makeText(this, R.string.toast_title_empty, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 在后台线程处理数据库操作
            lifecycleScope.launch(Dispatchers.IO) {
                if (currentNoteId == -1) {
                    // 新增笔记
                    val note = Note(title = title, content = content)
                    noteDao.insertNote(note)
                } else {
                    // 更新笔记
                    val note = Note(id = currentNoteId, title = title, content = content)
                    noteDao.updateNote(note)
                }
                // 回到主线程显示提示并关闭页面
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@NoteEditActivity, R.string.toast_note_saved, Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        // 删除按钮点击事件
        binding.btnDelete.setOnClickListener {
            if (currentNoteId != -1) {
                lifecycleScope.launch(Dispatchers.IO) {
                    // 查询并删除笔记
                    val note = noteDao.getNoteById(currentNoteId)
                    note?.let { noteDao.deleteNote(it) }
                    // 主线程提示并关闭
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@NoteEditActivity, R.string.toast_note_deleted, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            } else {
                // 新增模式下点击删除直接关闭
                finish()
            }
        }
    }
}