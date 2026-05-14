package pers.mekomimi.bookvault

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pers.mekomimi.bookvault.db.books.AppDatabase
import pers.mekomimi.bookvault.db.books.scanBooks
import pers.mekomimi.bookvault.ui.screen.ShelfScreen
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

        //连接数据库
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "bookvault-db"
        ).build()
        val dao = db.bookDao()

        lifecycleScope.launch(Dispatchers.IO) {
            val hasBooks = dao.count() > 0
            if (!hasBooks) {
                scanBooks(File("/storage/emulated/0/Download"), dao)
            }
        }

        //UI设置
        setContent {
            val books by dao.getAll().collectAsState(initial = null)
            val context = LocalContext.current

            when {
                books == null -> LoadingScreen()

                books!!.isEmpty() -> EmptyScreen()

                else -> ShelfScreen(
                    books = books!!,
                    dao = dao,
                    context = context
                )
            }
        }
    }
}

annotation class EmptyScreen

annotation class LoadingScreen
