package pers.mekomimi.bookvault

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pers.mekomimi.bookvault.db.AppDatabase
import pers.mekomimi.bookvault.db.folder.FolderManager
import pers.mekomimi.bookvault.db.folder.FolderScanner
import pers.mekomimi.bookvault.ui.screen.ShelfScreen
import android.view.KeyEvent


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
        ).fallbackToDestructiveMigration().build()
        val bookDao = db.bookDao()

        val folderManager = FolderManager(
            this,
            db
        )

        //选择扫描文件夹
        val picker = registerForActivityResult(
            ActivityResultContracts.OpenDocumentTree()
        ) { uri ->
            if (uri != null) {
                lifecycleScope.launch {
                    folderManager.addFolder(uri)
                    //folderManager.scanAllFolders()
                }
            }
        }
        picker.launch(null)

        //启动文件扫描
//        lifecycleScope.launch(Dispatchers.IO) {
//            val hasBooks = bookDao.count() > 0
//            if (!hasBooks) {
//                scanBooks(File("/storage/emulated/0/Download"), bookDao)
//            }
//        }

        val scanner = FolderScanner(this, db)
        lifecycleScope.launch(Dispatchers.IO) {
            val hasBooks = bookDao.count() > 0
            if (!hasBooks) {
                scanner.scanAllFolders()
            }
        }

        //UI设置
        setContent {
            val books by bookDao.getAll().collectAsState(initial = null)
            val context = LocalContext.current

            when {
                books == null -> LoadingScreen()

                books!!.isEmpty() -> EmptyScreen()

                else -> ShelfScreen(
                    books = books!!,
                    dao = bookDao,
                    context = context
                )
            }
        }
    }

    var onVolumeDown: (() -> Unit)? = null
    var onVolumeUp: (() -> Unit)? = null

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        when (keyCode) {

            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                onVolumeDown?.invoke()
                return true
            }

            KeyEvent.KEYCODE_VOLUME_UP -> {
                onVolumeUp?.invoke()
                return true
            }
        }

        return super.onKeyDown(keyCode, event)
    }
}

@Composable
fun EmptyScreen() {
    Text("没有书籍")
}

@Composable
fun LoadingScreen() {
    Text("加载中")
}


