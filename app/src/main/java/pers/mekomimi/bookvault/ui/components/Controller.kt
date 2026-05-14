package pers.mekomimi.bookvault.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Controller(
    currentPage: Int,
    totalPages: Int,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        //process control
        Row {
            Button(
                onClick = onPrev,
                enabled = currentPage > 1
            ) {
                Text("上一页")
            }

            Text("第 $currentPage / $totalPages 页")

            Button(
                onClick = onNext,
                enabled = currentPage < totalPages
            ) {
                Text("下一页")
            }
        }

        Row{
            //refresh
            Button(
                onClick = onRefresh
            ) {
                Text("刷新列表")
            }

            //添加文件夹
            Button(
                onClick = onRefresh
            ) {
                Text("添加文件夹")
            }
        }

    }
}