package com.kids.tangshi.util

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale

/**
 * TTS 帮助类
 */
class TtsHelper(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var pendingText: String? = null
    private var lastSpeakResult: Int = -99

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.let { engine ->
                // 尝试中文语言
                engine.setLanguage(Locale.SIMPLIFIED_CHINESE)
                engine.setSpeechRate(1.0f)
                engine.setPitch(1.0f)

                isInitialized = true
                Log.d(TAG, "TTS initialized")

                pendingText?.let {
                    speak(it)
                    pendingText = null
                }
            }
        } else {
            Log.e(TAG, "TTS init failed: $status")
        }
    }

    /**
     * 朗读，返回 speak 结果码
     * 0 = SUCCESS, -1 = ERROR, -2 = SERVER_ERROR
     */
    fun speak(text: String): Int {
        lastSpeakResult = -99
        if (!isInitialized) {
            pendingText = text
            return -99
        }
        tts?.stop()

        val utteranceId = "poem_${System.currentTimeMillis()}"
        lastSpeakResult = tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId) ?: -99
        Log.d(TAG, "speak() = $lastSpeakResult")
        return lastSpeakResult
    }

    fun stop() {
        tts?.stop()
    }

    fun setSpeechRate(rate: Float) {
        tts?.setSpeechRate(rate)
    }

    fun isSpeaking(): Boolean = tts?.isSpeaking ?: false

    fun isInitialized(): Boolean = isInitialized

    fun getLastSpeakResult(): Int = lastSpeakResult

    fun isChineseAvailable(): Boolean {
        if (!isInitialized) return false
        val r = tts?.isLanguageAvailable(Locale.SIMPLIFIED_CHINESE) ?: -1
        return r >= TextToSpeech.LANG_COUNTRY_AVAILABLE
    }

    fun getLanguageStatus(): String {
        if (!isInitialized) return "未初始化"
        val r = tts?.isLanguageAvailable(Locale.SIMPLIFIED_CHINESE) ?: -1
        return when (r) {
            TextToSpeech.LANG_AVAILABLE -> "可用"
            TextToSpeech.LANG_COUNTRY_AVAILABLE -> "国家可用"
            TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE -> "语言变体可用"
            TextToSpeech.LANG_MISSING_DATA -> "缺少语言数据"
            TextToSpeech.LANG_NOT_SUPPORTED -> "不支持"
            else -> "未知($r)"
        }
    }

    fun setOnUtteranceProgressListener(listener: UtteranceProgressListener) {
        tts?.setOnUtteranceProgressListener(listener)
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }

    companion object {
        private const val TAG = "TtsHelper"
    }
}
