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

    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false
    private var pendingText: String? = null
    private var utteranceListener: UtteranceProgressListener? = null

    fun setSpeechRate(rate: Float) {
        textToSpeech?.setSpeechRate(rate)
    }

    init {
        textToSpeech = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.let { tts ->
                val result = tts.setLanguage(Locale.SIMPLIFIED_CHINESE)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.w(TAG, "Simplified Chinese not available, trying zh_CN")
                    tts.setLanguage(Locale("zh", "CN"))
                }
                tts.setSpeechRate(1.0f)
                // 确保音频输出
                tts.setAudioAttributes(
                    android.media.AudioAttributes.Builder()
                        .setUsage(android.media.AudioAttributes.USAGE_MEDIA)
                        .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build()
                )
                isInitialized = true
                Log.d(TAG, "TTS initialized successfully")
                utteranceListener?.let { tts.setOnUtteranceProgressListener(it) }
                pendingText?.let {
                    speak(it)
                    pendingText = null
                }
            }
        } else {
            Log.e(TAG, "TTS initialization failed: $status")
        }
    }

    fun speak(text: String) {
        Log.d(TAG, "speak() called, initialized=$isInitialized, text length=${text.length}")
        if (isInitialized) {
            textToSpeech?.stop()
            val result = textToSpeech?.speak(
                text,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "poem_${System.currentTimeMillis()}"
            )
            Log.d(TAG, "speak() result=$result")
        } else {
            Log.d(TAG, "TTS not ready, queuing text")
            pendingText = text
        }
    }

    fun stop() {
        textToSpeech?.stop()
    }

    fun isSpeaking(): Boolean = textToSpeech?.isSpeaking ?: false

    fun setOnUtteranceProgressListener(listener: UtteranceProgressListener) {
        utteranceListener = listener
        textToSpeech?.setOnUtteranceProgressListener(listener)
    }

    fun shutdown() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
        isInitialized = false
    }

    companion object {
        private const val TAG = "TtsHelper"
    }
}
