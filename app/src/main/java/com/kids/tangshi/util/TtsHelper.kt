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

    init {
        textToSpeech = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.let { tts ->
                // 尝试多种中文语言设置
                var langResult = tts.setLanguage(Locale.SIMPLIFIED_CHINESE)
                if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    langResult = tts.setLanguage(Locale("zh", "CN"))
                }
                if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    langResult = tts.setLanguage(Locale.CHINESE)
                }
                Log.d(TAG, "Language set result: $langResult")

                // 设置语速和音调
                tts.setSpeechRate(0.9f)
                tts.setPitch(1.0f)

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
        Log.d(TAG, "speak() called, initialized=$isInitialized, text=${text.take(20)}...")
        if (isInitialized) {
            textToSpeech?.stop()
            val utteranceId = "poem_${System.currentTimeMillis()}"
            val result = textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
            Log.d(TAG, "speak() result=$result, utteranceId=$utteranceId")
        } else {
            Log.d(TAG, "TTS not ready, queuing text")
            pendingText = text
        }
    }

    fun stop() {
        textToSpeech?.stop()
    }

    fun setSpeechRate(rate: Float) {
        textToSpeech?.setSpeechRate(rate)
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
