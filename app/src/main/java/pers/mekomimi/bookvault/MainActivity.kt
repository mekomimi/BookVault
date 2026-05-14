package pers.mekomimi.bookvault

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.content.FileProvider
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
            val context = LocalContext.current
            val books by dao.getAll().collectAsState(initial = emptyList())

            //page state
            val pageSize = 9
            var currentPage by remember { mutableIntStateOf(1) }
            val totalPages = (books.size + pageSize - 1) / pageSize
            val pagedBooks = books
                .drop((currentPage - 1) * pageSize)
                .take(pageSize)

            Scaffold { innerPadding ->
                Column(
                    modifier = Modifier.padding(innerPadding)
                ) {
                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {
                        items(pagedBooks) { book ->
                            Text(
                                text = book.title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Button(onClick = {
                                val file = File(book.path)
                                val uri = FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.provider",
                                    file
                                )

                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    setDataAndType(uri, "*/*")
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }

                                context.startActivity(intent)
                            }) {
                                Text("打开")
                            }
                        }
                    }

                    //process control
                    Row {
                        Button(
                            onClick = { if (currentPage > 1) currentPage-- },
                            enabled = currentPage > 1
                        ) {
                            Text("上一页")
                        }

                        Text("第 $currentPage / $totalPages 页")

                        Button(
                            onClick = { if (currentPage < totalPages) currentPage++ },
                            enabled = currentPage < totalPages
                        ) {
                            Text("下一页")
                        }
                    }

                    //refresh
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