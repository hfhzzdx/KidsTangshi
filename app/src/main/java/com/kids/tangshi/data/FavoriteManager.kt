package com.kids.tangshi.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 收藏管理器（使用 org.json，无第三方依赖）
 */
class FavoriteManager(private val context: Context) {

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * 添加收藏
     */
    suspend fun addFavorite(poem: Poem) = withContext(Dispatchers.IO) {
        val favorites = getFavorites().toMutableList()
        if (!favorites.any { it.id == poem.id }) {
            favorites.add(poem.copy(isFavorite = true))
            saveFavorites(favorites)
        }
    }

    /**
     * 移除收藏
     */
    suspend fun removeFavorite(poemId: String) = withContext(Dispatchers.IO) {
        val favorites = getFavorites().toMutableList()
        favorites.removeAll { it.id == poemId }
        saveFavorites(favorites)
    }

    /**
     * 检查是否已收藏
     */
    suspend fun isFavorite(poemId: String): Boolean = withContext(Dispatchers.IO) {
        getFavorites().any { it.id == poemId }
    }

    /**
     * 获取所有收藏
     */
    suspend fun getFavorites(): List<Poem> = withContext(Dispatchers.IO) {
        val json = prefs.getString(KEY_FAVORITES, "[]") ?: "[]"
        parseFavoritesJson(json)
    }

    private fun parseFavoritesJson(json: String): List<Poem> {
        val poems = mutableListOf<Poem>()
        val array = JSONArray(json)
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            poems.add(
                Poem(
                    id = obj.optString("id", ""),
                    title = obj.optString("title", ""),
                    author = obj.optString("author", ""),
                    dynasty = obj.optString("dynasty", ""),
                    content = obj.optString("content", ""),
                    translation = obj.optString("translation", ""),
                    isFavorite = true
                )
            )
        }
        return poems
    }

    private fun saveFavorites(favorites: List<Poem>) {
        val array = JSONArray()
        favorites.forEach { poem ->
            val obj = JSONObject().apply {
                put("id", poem.id)
                put("title", poem.title)
                put("author", poem.author)
                put("dynasty", poem.dynasty)
                put("content", poem.content)
                put("translation", poem.translation)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_FAVORITES, array.toString()).apply()
    }

    /**
     * 记录学习打卡
     */
    suspend fun markAsStudied(poemId: String, date: String) = withContext(Dispatchers.IO) {
        val records = getStudyRecords().toMutableList()
        if (!records.any { it.poemId == poemId && it.date == date }) {
            records.add(StudyRecord(poemId, date, true))
            saveStudyRecords(records)
        }
    }

    /**
     * 获取学习记录
     */
    suspend fun getStudyRecords(): List<StudyRecord> = withContext(Dispatchers.IO) {
        val json = prefs.getString(KEY_STUDY_RECORDS, "[]") ?: "[]"
        val array = JSONArray(json)
        val records = mutableListOf<StudyRecord>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            records.add(
                StudyRecord(
                    poemId = obj.optString("poemId", ""),
                    date = obj.optString("date", ""),
                    isCompleted = obj.optBoolean("isCompleted", false)
                )
            )
        }
        records
    }

    /**
     * 检查今天是否已打卡
     */
    suspend fun hasStudiedToday(): Boolean = withContext(Dispatchers.IO) {
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            .format(java.util.Date())
        getStudyRecords().any { it.date == today && it.isCompleted }
    }

    private fun saveStudyRecords(records: List<StudyRecord>) {
        val array = JSONArray()
        records.forEach { record ->
            val obj = JSONObject().apply {
                put("poemId", record.poemId)
                put("date", record.date)
                put("isCompleted", record.isCompleted)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_STUDY_RECORDS, array.toString()).apply()
    }

    companion object {
        private const val PREFS_NAME = "kids_tangshi_prefs"
        private const val KEY_FAVORITES = "favorites"
        private const val KEY_STUDY_RECORDS = "study_records"
    }
}
