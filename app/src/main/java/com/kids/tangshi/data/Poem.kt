package com.kids.tangshi.data

/**
 * 诗词数据模型
 */
data class Poem(
    val id: String = "",
    val title: String = "",
    val author: String = "",
    val dynasty: String = "",
    val content: String = "",
    val translation: String = "",
    val isFavorite: Boolean = false
)

/**
 * 诗词分类
 */
enum class PoemCategory(val displayName: String) {
    TANG("唐诗精选"),
    SONG("宋词精选"),
    THREE_HUNDRED("古诗三百首")
}

/**
 * 学习记录
 */
data class StudyRecord(
    val poemId: String,
    val date: String,
    val isCompleted: Boolean = false
)
