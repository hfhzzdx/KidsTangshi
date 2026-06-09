package com.kids.tangshi.data

import android.content.Context
import android.util.Log
import org.json.JSONArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * 诗词数据仓库
 */
class PoemRepository(private val context: Context) {

    /**
     * 加载唐诗数据
     */
    suspend fun loadTangPoems(): List<Poem> = withContext(Dispatchers.IO) {
        loadPoemsFromAsset("poems/tang精选.json")
    }

    /**
     * 加载宋词数据
     */
    suspend fun loadSongPoems(): List<Poem> = withContext(Dispatchers.IO) {
        loadPoemsFromAsset("poems/song精选.json")
    }

    /**
     * 获取每日推荐诗词
     */
    suspend fun getDailyPoem(): Poem? = withContext(Dispatchers.IO) {
        val allPoems = loadTangPoems() + loadSongPoems()
        if (allPoems.isEmpty()) null else {
            val dayOfYear = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
            allPoems[dayOfYear % allPoems.size]
        }
    }

    /**
     * 搜索诗词
     */
    suspend fun searchPoems(keyword: String): List<Poem> = withContext(Dispatchers.IO) {
        val allPoems = loadTangPoems() + loadSongPoems()
        allPoems.filter { poem ->
            poem.title.contains(keyword, ignoreCase = true) ||
            poem.author.contains(keyword, ignoreCase = true) ||
            poem.content.contains(keyword, ignoreCase = true)
        }
    }

    /**
     * 从 assets 加载诗词数据
     */
    private fun loadPoemsFromAsset(fileName: String): List<Poem> {
        return try {
            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            parsePoemJson(jsonString)
        } catch (e: IOException) {
            Log.e(TAG, "Error loading poems from $fileName", e)
            emptyList()
        }
    }

    /**
     * 解析 JSON 数组为 Poem 列表
     */
    private fun parsePoemJson(jsonString: String): List<Poem> {
        val poems = mutableListOf<Poem>()
        val jsonArray = JSONArray(jsonString)
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            poems.add(
                Poem(
                    id = obj.optString("id", ""),
                    title = obj.optString("title", ""),
                    author = obj.optString("author", ""),
                    dynasty = obj.optString("dynasty", ""),
                    content = obj.optString("content", ""),
                    translation = obj.optString("translation", "")
                )
            )
        }
        return poems
    }

    companion object {
        private const val TAG = "PoemRepository"
    }
}
