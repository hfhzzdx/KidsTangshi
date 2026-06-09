package com.kids.tangshi.data

import android.content.Context
import android.util.Log
import com.kids.tangshi.util.ChineseConverter
import org.json.JSONArray
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
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
        val allPoems = loadAllPoems()
        if (allPoems.isEmpty()) null else {
            val dayOfYear = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
            allPoems[dayOfYear % allPoems.size]
        }
    }

    /**
     * 加载导入的自定义诗词数据
     */
    suspend fun loadCustomPoems(): List<Poem> = withContext(Dispatchers.IO) {
        val poemsDir = File(context.filesDir, "poems")
        if (!poemsDir.exists()) return@withContext emptyList()
        val allPoems = mutableListOf<Poem>()
        poemsDir.listFiles { file -> file.extension == "json" }?.forEach { file ->
            try {
                val json = file.readText(Charsets.UTF_8)
                allPoems.addAll(parsePoemJsonFlexible(json))
            } catch (e: Exception) {
                Log.e(TAG, "Error loading custom file ${file.name}", e)
            }
        }
        allPoems
    }

    /**
     * 加载所有诗词（内置 + 导入）
     */
    suspend fun loadAllPoems(): List<Poem> = withContext(Dispatchers.IO) {
        loadTangPoems() + loadSongPoems() + loadCustomPoems()
    }

    /**
     * 搜索诗词
     */
    suspend fun searchPoems(keyword: String): List<Poem> = withContext(Dispatchers.IO) {
        loadAllPoems().filter { poem ->
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
     * 解析 JSON 数组为 Poem 列表（标准格式：id, title, author, dynasty, content, translation）
     */
    private fun parsePoemJson(jsonString: String): List<Poem> {
        val poems = mutableListOf<Poem>()
        val jsonArray = JSONArray(jsonString)
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            poems.add(
                Poem(
                    id = obj.optString("id", "builtin_$i"),
                    title = obj.optString("title", ""),
                    author = obj.optString("author", ""),
                    dynasty = obj.optString("dynasty", "唐"),
                    content = obj.optString("content", ""),
                    translation = obj.optString("translation", "")
                )
            )
        }
        return poems
    }

    /**
     * 灵活解析 JSON，兼容多种格式
     * 支持字段：
     *   - id / _id / uuid
     *   - title / name
     *   - author / poet
     *   - dynasty / period / strains
     *   - content / paragraphs / text
     *   - translation / translate / annotations
     */
    private fun parsePoemJsonFlexible(jsonString: String): List<Poem> {
        val poems = mutableListOf<Poem>()
        val jsonArray = JSONArray(jsonString)

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)

            // ID：支持多种字段名
            val id = obj.optString("id", obj.optString("_id", obj.optString("uuid", "custom_$i")))

            // 标题
            val title = ChineseConverter.toSimplified(
                obj.optString("title", obj.optString("name", "无标题"))
            )

            // 作者
            val author = ChineseConverter.toSimplified(
                obj.optString("author", obj.optString("poet", "佚名"))
            )

            // 朝代
            val dynasty = ChineseConverter.toSimplified(
                obj.optString("dynasty", obj.optString("period", obj.optString("strains", "未知")))
            )

            // 内容：支持 content（字符串）或 paragraphs（数组）
            val content = if (obj.has("paragraphs")) {
                val paragraphsArray = obj.getJSONArray("paragraphs")
                val sb = StringBuilder()
                for (j in 0 until paragraphsArray.length()) {
                    if (j > 0) sb.append("\n")
                    sb.append(paragraphsArray.getString(j))
                }
                ChineseConverter.toSimplified(sb.toString())
            } else {
                ChineseConverter.toSimplified(obj.optString("content", obj.optString("text", "")))
            }

            // 译文：支持多种字段名
            val translation = if (obj.has("translation")) {
                val transVal = obj.get("translation")
                if (transVal is String) {
                    ChineseConverter.toSimplified(transVal)
                } else if (transVal is JSONArray) {
                    val sb = StringBuilder()
                    for (j in 0 until transVal.length()) {
                        if (j > 0) sb.append("\n")
                        sb.append(transVal.getString(j))
                    }
                    ChineseConverter.toSimplified(sb.toString())
                } else {
                    ""
                }
            } else {
                ChineseConverter.toSimplified(
                    obj.optString("translate", obj.optString("annotations", ""))
                )
            }

            poems.add(Poem(id, title, author, dynasty, content, translation))
        }

        Log.d(TAG, "Parsed ${poems.size} poems from flexible JSON")
        return poems
    }

    companion object {
        private const val TAG = "PoemRepository"
    }
}
