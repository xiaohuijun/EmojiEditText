package com.emoji.example

import java.util.regex.Pattern

object EmojiUtil {

    fun getCnEmoji(key: String): String? {
        if (key.isEmpty()) return null
        if (EmojiData.EmojiMap.containsKey(key)) {
            return EmojiData.EmojiMap.get(key)
        }
        return null
    }

    fun getEmojiCode(key: String): String? {
        if (key.isEmpty()) return null
        if (EmojiData.IEmojiMap.containsKey(key)) {
            return EmojiData.IEmojiMap.get(key)
        }
        return null
    }

    /**
     * 表情描述转化为表情
     */
    fun unicodeToString(str: String): String {
        var rstr = str
        if (rstr.isEmpty()) return ""
        try {
            //Pattern to match
            val pattern = Pattern.compile("\\[[a-zA-Z0-9\\u4e00-\\u9fa5]+]")
            val matcher = pattern.matcher(rstr)
            while (matcher.find()) {
                val found = matcher.group()
                val emojiCode = getEmojiCode(found)
                if (emojiCode == null) continue
                val emojiCodes = emojiCode.split(",")
                var emoji = ""
                for (i in emojiCodes.indices) {
                    emoji += getEmojiStringByUnicode(emojiCodes[i])
                }
                rstr = rstr.replace(found, emoji)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return rstr
    }

    /**
     * unicode转表情
     *
     * @param unicode
     * @return
     */
    fun getEmojiStringByUnicode(unicode: String): String {
        return String(Character.toChars(unicode.toInt(16)))
    }
}