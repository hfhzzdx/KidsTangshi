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
    private var speechRate: Float = 1.0f
    
    init {
        textToSpeech = TextToSpeech(context, this)
    }
    
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.let { tts ->
                // 设置中文语言
                val result = tts.setLanguage(Locale.CHINESE)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e(TAG, "Chinese language is not supported")
                    // 尝试使用简体中文
                    tts.setLanguage(Locale.SIMPLIFIED_CHINESE)
                }
                
                // 设置语速
                tts.setSpeechRate(speechRate)
                
                isInitialized = true
                Log.d(TAG, "TTS initialized successfully")
                
                // 如果有待朗读的文本，立即朗读
                pendingText?.let {
                    speak(it)
                    pendingText = null
                }
            }
        } else {
            Log.e(TAG, "TTS initialization failed")
        }
    }
    
    /**
     * 朗读文本
     */
    fun speak(text: String) {
        if (isInitialized) {
            // 停止当前朗读
            textToSpeech?.stop()
            
            // 开始新的朗读
            textToSpeech?.speak(
                text,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "TTS_${System.currentTimeMillis()}"
            )
        } else {
            // TTS 未初始化，保存文本等待初始化后朗读
            pendingText = text
        }
    }
    
    /**
     * 停止朗读
     */
    fun stop() {
        textToSpeech?.stop()
    }
    
    /**
     * 设置语速
     */
    fun setSpeechRate(rate: Float) {
        speechRate = rate
        textToSpeech?.setSpeechRate(rate)
    }
    
    /**
     * 获取当前语速
     */
    fun getSpeechRate(): Float = speechRate
    
    /**
     * 检查是否正在朗读
     */
    fun isSpeaking(): Boolean = textToSpeech?.isSpeaking ?: false
    
    /**
     * 释放资源
     */
    fun shutdown() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
        isInitialized = false
    }
    
    /**
     * 设置朗读进度监听器
     */
    fun setOnUtteranceProgressListener(listener: UtteranceProgressListener) {
        textToSpeech?.setOnUtteranceProgressListener(listener)
    }
    
    companion object {
        private const val TAG = "TtsHelper"
    }
}
