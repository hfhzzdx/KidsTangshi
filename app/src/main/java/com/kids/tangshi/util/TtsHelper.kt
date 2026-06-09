package com.kids.tangshi.util

import android.content.Context
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

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.let { engine ->
                // 按优先级尝试中文语言设置
                val langResult = engine.setLanguage(Locale.SIMPLIFIED_CHINESE)
                Log.d(TAG, "setLanguage(SIMPLIFIED_CHINESE) = $langResult")
                if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    engine.setLanguage(Locale("zh", "CN"))
                }
                engine.setSpeechRate(1.0f)

                isInitialized = true
                Log.d(TAG, "TTS initialized OK")

                pendingText?.let {
                    Log.d(TAG, "Speaking pending text")
                    speak(it)
                    pendingText = null
                }
            }
        } else {
            Log.e(TAG, "TTS init FAILED status=$status")
        }
    }

    fun speak(text: String) {
        if (!isInitialized) {
            pendingText = text
            return
        }
        tts?.stop()
        val result = tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "poem_${System.currentTimeMillis()}")
        Log.d(TAG, "speak() result=$result")
    }

    fun stop() {
        tts?.stop()
    }

    fun setSpeechRate(rate: Float) {
        tts?.setSpeechRate(rate)
    }

    fun isSpeaking(): Boolean = tts?.isSpeaking ?: false

    fun isChineseAvailable(): Boolean {
        if (!isInitialized) return false
        val result = tts?.setLanguage(Locale.SIMPLIFIED_CHINESE) ?: TextToSpeech.LANG_NOT_SUPPORTED
        return result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
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
