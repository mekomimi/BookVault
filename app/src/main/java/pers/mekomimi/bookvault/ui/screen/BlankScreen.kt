package pers.mekomimi.bookvault.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun EmptyScreen(
    onfresh: () -> Unit,
    onAddFolder: () -> Unit
) {
    Column {
        Text("没有书籍")

        Button(onClick = {onfresh()}) { Text("扫描书籍") }
        Button(onClick = {onAddFolder()}) { Text("添加文件夹") }
    }
}

@Composable
fun LoadingScreen() {
    Text("加载中")
}