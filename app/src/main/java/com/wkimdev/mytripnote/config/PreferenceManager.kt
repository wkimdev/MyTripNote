package com.wkimdev.mytripnote.config

import android.content.Context
import android.content.SharedPreferences
import android.widget.TextView

/**
 * SharedPreferences 데이터 저장 및 로드 클래스
 */
object PreferenceManager {
    const val PREFERENCES_NAME = "pref"
    private const val DEFAULT_VALUE_STRING = " "
    private const val DEFAULT_VALUE_BOOLEAN = false
    private const val DEFAULT_VALUE_INT = -1
    private const val DEFAULT_VALUE_LONG = -1L
    private const val DEFAULT_VALUE_FLOAT = -1f
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    /* String 값 저장
       param context
       param key
       param value
     */
    fun setString(context: Context, key: String?, value: String?) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putString(key, value)
        editor.apply()
    }

    /* boolean 값 저장
       param context
       param key
       param value
     */
    fun setBoolean(context: Context, key: String?, value: Boolean) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    /* int 값 저장
       param context
       param key
       param value
     */
    fun setInt(context: Context, key: String?, value: Int) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    /* long 값 저장
       param context
       param key
       param value
     */
    fun setLong(context: Context, key: String?, value: Long) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    /* float 값 저장
       param context
       param key
       param value
     */
    fun setFloat(context: Context, key: String?, value: Float) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putFloat(key, value)
        editor.apply()
    }

    /* String 값 로드
       param context
       param key
       return
     */
    fun getString(context: Context, key: String?): String? {
        val prefs =
            getPreferences(context)
        return prefs.getString(key, DEFAULT_VALUE_STRING)
    }

    /* boolean 값 로드
       param context
       param key
       return
     */
    fun getBoolean(context: Context, key: String?): Boolean {
        val prefs =
            getPreferences(context)
        return prefs.getBoolean(key, DEFAULT_VALUE_BOOLEAN)
    }

    /* int 값 로드
       param context
       param key
       return
     */
    fun getInt(context: Context, key: String?): Int {
        val prefs =
            getPreferences(context)
        return prefs.getInt(key, DEFAULT_VALUE_INT)
    }

    /* long 값 로드
      param context
      param key
      return
    */
    fun getLong(context: Context, key: String?): Long {
        val prefs =
            getPreferences(context)
        return prefs.getLong(key, DEFAULT_VALUE_LONG)
    }

    /* long 값 로드
      param context
      param key
      return
    */
    fun getFloat(context: Context, key: String?): Float {
        val prefs =
            getPreferences(context)
        return prefs.getFloat(key, DEFAULT_VALUE_FLOAT)
    }

    /* 키 값 삭제
      param context
      param key
    */
    fun removeKey(context: Context, key: String?) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.remove(key)
        editor.commit()
    }

    /* 모든 저장 데이터 삭제
      param context
    */
    fun clear(context: Context) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.clear()
        editor.commit()
    }

    fun listData(context: Context, datalist: String?, tvlist: TextView) {
        var datalist = datalist
        val prefs = getPreferences(context)
        val totalValue = prefs.all
        for ((key, value) in totalValue) {
            datalist += """
                $key: ${value.toString()}
                
                """.trimIndent()
        }
        tvlist.text = datalist
    }
}