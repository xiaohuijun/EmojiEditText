package com.emoji.example

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.support.v7.widget.AppCompatEditText
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.util.AttributeSet
import java.nio.charset.Charset
import java.util.concurrent.CopyOnWriteArrayList

/**
 * 自定义表情输入框
 */
class EmojiEditText : AppCompatEditText {
    val contentList: CopyOnWriteArrayList<TextData> = CopyOnWriteArrayList()

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

    }

    private fun init() {
        contentList.clear()
        filters = arrayOf(object : InputFilter {
            override fun filter(source: CharSequence, start: Int, end: Int,
                                dest: Spanned, dstart: Int, dend: Int): CharSequence {
                try {
                    if (end > 0) {
                        //添加
                        val bytes = source.toString().toByteArray()
                        val addStr = if (bytes.size > 0) source.toString() else " "
                        val isEmoji = (bytes.size > 0 && isEmojiCharacter(source.toString().first().toInt()))
                        val chars = addStr.toCharArray()
                        val emojiRandom = (Math.random() * 1000000).toInt()
                        for (i in chars.indices) {
                            val tdstart = if (isEmoji) dstart + emojiRandom else dstart + (Math.random() * 100000).toInt()
                            val textData = TextData(tdstart, chars[i])
                            contentList.add(dstart + i, textData)
                        }
                    } else {
                        //删除
                        for (i in dstart..(dend - 1)) {
                            contentList.removeAt(dstart)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return source
            }
        })

        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                if (editable.toString().isEmpty()) {
                    contentList.clear()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
    }

    private fun isEmojiCharacter(codePoint: Int): Boolean {
        return !((codePoint == 0x0) ||
                (codePoint == 0x9) ||
                (codePoint == 0xA) ||
                (codePoint == 0xD) ||
                ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
                ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)))
    }

    fun clearInputString() {
        contentList.clear()
    }

    fun getInputString(): String {
        try {
            val sb = StringBuilder()
            val groupData = groupTextData(contentList)
            for ((key, value) in groupData) {
                val textDataList = value
                val tempSb = StringBuilder()
                for (textData in textDataList) {
                    tempSb.append(textData.character)
                }
                var addstr = tempSb.toString()
                val bytes = addstr.toByteArray()
                if (isEmojiCharacter(addstr.first().toInt())) {
                    addstr = "[?]"
                    val hexStr = convert(bytes)
                    if (hexStr!!.length > 0) {
                        val cnEmoji = EmojiUtil.getCnEmoji(hexStr.substring(0, hexStr.length - 1))
                        if (cnEmoji != null) addstr = cnEmoji
                    }
                }
                sb.append(addstr)
            }
            return sb.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            return getText().toString()
        }
    }

    /**
     * 按照dstart进行分组
     *
     * @param textDataList
     * @return
     * @throws Exception
     */
    private fun groupTextData(textDataList: MutableList<TextData>): LinkedHashMap<Int, MutableList<TextData>> {
        val resultMap = LinkedHashMap<Int, MutableList<TextData>>()
        try {
            for (textData in textDataList) {
                if (resultMap.containsKey(textData.dstart)) {
                    //map中dstart已存在，将该数据存放到同一个key（key存放的是异常批次）的map中
                    resultMap.get(textData.dstart)?.add(textData)
                } else {
                    //map中不存在，新建key，用来存放数据
                    val tmpList = mutableListOf(textData)
                    resultMap.put(textData.dstart, tmpList)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return resultMap
    }

    /**
     * 表情转unicode
     *
     * @param bytes
     * @return
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private fun convert(bytes: ByteArray): String? {
        try {
            val str = String(bytes, Charset.forName("UTF-8"))
            val result = toCodePointArray(str)
            var hex_result = ""
            for (i in result.indices) {
                hex_result += "${Integer.toHexString(result[i])},"
            }
            return hex_result
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun toCodePointArray(str: String): IntArray {
        val ach = str.toCharArray()
        val len = ach.size
        val acp = IntArray(Character.codePointCount(ach, 0, len))
        var j = 0
        var i = 0
        var cp: Int
        while (i < len) {
            cp = Character.codePointAt(ach, i)
            acp[j++] = cp
            i += Character.charCount(cp)
        }
        return acp
    }

    data class TextData(var dstart: Int, var character: Char)
}