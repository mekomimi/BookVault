package pers.mekomimi.bookvault.input

import androidx.compose.ui.input.key.Key


enum class ReaderAction {
    NEXT_PAGE,
    PREV_PAGE
}

object KeyMapper {

    fun map(key: Key): ReaderAction? {

        return when (key) {

            Key.VolumeDown -> ReaderAction.NEXT_PAGE

            Key.VolumeUp -> ReaderAction.PREV_PAGE

            else -> null
        }
    }
}

object ReaderController {


    fun dispatch(
        key: Key,
        currentPage: Int,
        onPageChange: (Int) -> Unit
    ) {
        val action = KeyMapper.map(key)

        when (action) {

            ReaderAction.NEXT_PAGE -> {
                onPageChange(currentPage + 1)
            }

            ReaderAction.PREV_PAGE -> {
                if (currentPage > 1) {
                    onPageChange(currentPage - 1)
                }
            }

            else -> {}
        }
    }
}