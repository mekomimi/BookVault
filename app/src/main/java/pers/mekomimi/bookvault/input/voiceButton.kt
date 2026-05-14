package pers.mekomimi.bookvault.input

import androidx.compose.ui.input.key.Key


enum class ReaderAction {
    NEXT_PAGE,
    PREV_PAGE
}

data class ReaderState(
    val currentPage: Int = 0
)

object KeyMapper {

    fun map(key: Key): ReaderAction? {

        return when (key) {

            Key.VolumeDown -> ReaderAction.NEXT_PAGE

            Key.VolumeUp -> ReaderAction.PREV_PAGE

            else -> null
        }
    }
}

class ReaderController {

    fun dispatch(action: ReaderAction) {

        when (action) {

            ReaderAction.NEXT_PAGE -> {
                nextPage()
            }

            ReaderAction.PREV_PAGE -> {
                prevPage()
            }
        }
    }

    private fun prevPage() {
        TODO("Not yet implemented")
    }

    private fun nextPage() {
        TODO("Not yet implemented")
    }
}