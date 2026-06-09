package com.kids.tangshi.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale

/**
 * TTS 帮助类
 * 包含诊断功能，排查无声音问题
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
                // 列出可用引擎
                val engines = engine.availableEngines
                Log.d(TAG, "Available TTS engines: $engines")

                // 尝试设置中文语言，按优先级尝试
                val langResult = engine.setLanguage(Locale.SIMPLIFIED_CHINESE)
                Log.d(TAG, "setLanguage(SIMPLIFIED_CHINESE) = $langResult")
                if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    val r2 = engine.setLanguage(Locale("zh", "CN"))
                    Log.d(TAG, "setLanguage(zh_CN) = $r2")
                    if (r2 == TextToSpeech.LANG_MISSING_DATA || r2 == TextToSpeech.LANG_NOT_SUPPORTED) {
                        val r3 = engine.setLanguage(Locale.CHINESE)
                        Log.d(TAG, "setLanguage(CHINESE) = $r3")
                    }
                }

                // 设置语速
                engine.setSpeechRate(1.0f)

                isInitialized = true
                Log.d(TAG, "TTS initialized OK, isInitialized=true")

                // 如果有待朗读文本
                pendingText?.let {
                    Log.d(TAG, "Speaking pending text: ${it.take(20)}...")
                    speak(it)
                    pendingText = null
                }
            }
        } else {
            Log.e(TAG, "TTS init FAILED status=$status")
        }
    }

    /**
     * 朗读文本
     */
    fun speak(text: String) {
        if (!isInitialized) {
            Log.d(TAG, "TTS not ready, queuing text")
            pendingText = text
            return
        }

        tts?.let { engine ->
            // 停止当前朗读
            engine.stop()

            val params = android.os.Bundle().apply {
                putString(TextToSpeech.Engine.KEY_PARAM_VOLUME, "1.0")
            }

            val result = engine.speak(
                text,
                TextToSpeech.QUEUE_FLUSH,
                params,
                "poem_${System.currentTimeMillis()}"
            )

            Log.d(TAG, "speak() result=$result (0=SUCCESS, -1=ERROR)")
        }
    }

    /**
     * 停止朗读
     */
    fun stop() {
        tts?.stop()
    }

    /**
     * 设置语速
     */
    fun setSpeechRate(rate: Float) {
        tts?.setSpeechRate(rate)
    }

    /**
     * 是否正在朗读
     */
    fun isSpeaking(): Boolean = tts?.isSpeaking ?: false

    /**
     * 设置朗读完成监听
     */
    fun setOnUtteranceProgressListener(listener: UtteranceProgressListener) {
        tts?.setOnUtteranceProgressListener(listener)
    }

    /**
     * 检查 TTS 是否支持中文
     */
    fun isChineseAvailable(): Boolean {
        if (!isInitialized) return false
        val result = tts?.setLanguage(Locale.SIMPLIFIED_CHINESE) ?: TextToSpeech.LANG_NOT_SUPPORTED
        return result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
    }

    /**
     * 获取可用引擎列表（供调试用）
     */
    fun getAvailableEngines(): List<String> {
        return tts?.availableEngines ?: emptyList()
    }

    /**
     * 释放资源
     */
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