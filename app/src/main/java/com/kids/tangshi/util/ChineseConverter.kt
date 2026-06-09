package com.kids.tangshi.util

/**
 * 繁简转换工具
 * 简化版本，仅覆盖常用字
 */
object ChineseConverter {

    // 繁简对照表（常用字）
    private val traditionalToSimplified = mapOf(
        '縹' to '缥', '緲' to '缈', '蟲' to '虫', '殼' to '壳', '煙' to '烟',
        '霏' to '霏', '穠' to '秾', '豔' to '艳', '憂' to '忧', '雀' to '雀',
        '憐' to '怜', '遶' to '绕', '綺' to '绮', '羅' to '罗', '豈' to '岂',
        '關' to '关', '魂' to '魂', '夢' to '梦', '莊' to '庄', '說' to '说',
        '麗' to '丽', '愛' to '爱', '採' to '采', '攢' to '攒', '蘂' to '蕊',
        '房' to '房', '蟬' to '蝉', '分' to '分', '殊' to '殊', '迂' to '迂',
        '闊' to '阔', '空' to '空', '解' to '解', '秋' to '秋', '噪' to '噪',
        '夕' to '夕', '陽' to '阳', '綠' to '绿', '紅' to '红', '飛' to '飞',
        '風' to '风', '語' to '语', '聲' to '声', '聞' to '闻', '見' to '见',
        '書' to '书', '會' to '会', '時' to '时', '來' to '来', '國' to '国',
        '過' to '过', '這' to '这', '們' to '们', '個' to '个', '為' to '为',
        '對' to '对', '開' to '开', '長' to '长', '經' to '经', '問' to '问',
        '點' to '点', '學' to '学', '頭' to '头', '電' to '电', '機' to '机',
        '雲' to '云', '車' to '车', '門' to '门', '無' to '无', '東' to '东',
        '葉' to '叶', '處' to '处', '馬' to '马', '結' to '结', '動' to '动',
        '離' to '离', '實' to '实', '議' to '议', '兩' to '两', '還' to '还',
        '裏' to '里', '進' to '进', '樣' to '样', '體' to '体', '高' to '高',
        '想' to '想', '知' to '知', '成' to '成', '道' to '道', '地' to '地',
        '明' to '明', '看' to '看', '水' to '水', '落' to '落', '花' to '花',
        '月' to '月', '光' to '光', '頭' to '头', '床' to '床', '前' to '前',
        '疑' to '疑', '霜' to '霜', '舉' to '举', '望' to '望', '低' to '低',
        '思' to '思', '故' to '故', '鄉' to '乡', '白' to '白', '日' to '日',
        '依' to '依', '山' to '山', '盡' to '尽', '黃' to '黄', '河' to '河',
        '入' to '入', '海' to '海', '流' to '流', '欲' to '欲', '窮' to '穷',
        '千' to '千', '里' to '里', '目' to '目', '更' to '更', '上' to '上',
        '一' to '一', '層' to '层', '樓' to '楼'
    )

    /**
     * 繁体转简体
     */
    fun toSimplified(text: String): String {
        return text.map { char ->
            traditionalToSimplified[char] ?: char
        }.joinToString("")
    }
}
