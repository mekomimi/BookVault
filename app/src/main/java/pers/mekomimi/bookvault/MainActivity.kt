package pers.mekomimi.bookvault

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pers.mekomimi.bookvault.db.AppDatabase
import pers.mekomimi.bookvault.db.scanBooks
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                    "package:$packageName".toUri()
                )
                startActivity(intent)
            }
        }

        //扫描书籍
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "bookvault-db"
        ).build()
        val dao = db.bookDao()

        lifecycleScope.launch(Dispatchers.IO) {
            dao.clear()
            scanBooks(File("/storage/emulated/0/Download"), dao)
        }

        //UI设置
        setContent {
            val books by dao.getAll().collectAsState(initial = emptyList())

            LazyColumn {
                items(books) { book ->
                    Text(book.path)
                }
                item {
                    Button(
                        onClick = {
                            lifecycleScope.launch(Dispatchers.IO) {
                                scanBooks(File("/storage/emulated/0/Download"), dao)
                            }
                        }
                    ) {
                        Text("刷新列表")
                    }
                }
            }
        }
    }
}