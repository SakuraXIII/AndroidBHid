package com.sy.bhid.data

import android.R


object KeyMap {
    // https://zhuanlan.zhihu.com/p/409558697
    private val maps: Map<String, String> = mapOf(
        "ESC" to "41",
        "1" to "30",
        "2" to "31",
        "3" to "32",
        "4" to "33",
        "5" to "34",
        "6" to "35",
        "7" to "36",
        "8" to "37",
        "9" to "38",
        "0" to "39",

        "a" to "4",
        "b" to "5",
        "c" to "6",
        "d" to "7",
        "e" to "8",
        "f" to "9",
        "g" to "10",
        "h" to "11",
        "i" to "12",
        "j" to "13",
        "k" to "14",
        "l" to "15",
        "m" to "16",
        "n" to "17",
        "o" to "18",
        "p" to "19",
        "q" to "20",
        "r" to "21",
        "s" to "22",
        "t" to "23",
        "u" to "24",
        "v" to "25",
        "w" to "26",
        "x" to "27",
        "y" to "28",
        "z" to "29",

        "f1" to "58",
        "f2" to "59",
        "f3" to "60",
        "f4" to "61",
        "f5" to "62",
        "f6" to "63",
        "f7" to "64",
        "f8" to "65",
        "f9" to "66",
        "f10" to "67",
        "f11" to "68",
        "f12" to "69",
        "del" to "76",

        "enter" to "40",
        "esc" to "41",
        "backspace" to "42",
        "tab" to "43",
        "space" to "44",
        "-" to "45",
        "=" to "46",
        "[" to "47",
        "]" to "48",
        "\\" to "49",
        ":" to "51",
        "\'" to "52",
        "caps" to "57",
        "," to "54",
        "." to "55",
        "/" to "56",
        "右" to "79",
        "左" to "80",
        "下" to "81",
        "上" to "82",
    )

    /**
     * @param key 按键字符
     * @return 相应键码，若无对应则返回 0
     */
    fun getKeys(key: String): String {
        return maps[key] ?: "0"
    }

//    fun getKeys2(): List<KeyBean> {
//        val data: MutableList<KeyBean> = ArrayList<KeyBean>()
//        data.add(KeyBean(R.id.tv_shift, "M2"))
//        data.add(KeyBean(R.id.tv_shift2, "M32"))
//        data.add(KeyBean(R.id.tv_ctrl, "M1"))
//        //        data.add(new KeyBean(R.id.tv_fn,"M32"));
//        data.add(KeyBean(R.id.tv_win, "M8"))
//        data.add(KeyBean(R.id.tv_alt, "M4"))
//        data.add(KeyBean(R.id.tv_alt2, "M4"))
//        data.add(KeyBean(R.id.tv_ctrl2, "M16"))
//        return data
//    }
//
//
//    fun getKeys3(): List<KeyBean> {
//        val data: MutableList<KeyBean> = ArrayList<KeyBean>()
//        data.add(KeyBean(R.id.iv_power, "102"))
//        data.add(KeyBean(R.id.iv_input, "88"))
//        data.add(KeyBean(R.id.iv_menu, "95"))
//        return data
//    }
//
//    fun getKeys4(): List<KeyBean> {
//        val data: MutableList<KeyBean> = ArrayList<KeyBean>()
//        data.add(KeyBean(R.id.iv_home, "40"))
//        data.add(KeyBean(R.id.iv_mute, "127"))
//        return data
//    }
//
//    fun getKeys5(): List<KeyBean> {
//        val data: MutableList<KeyBean> = ArrayList<KeyBean>()
//        data.add(KeyBean(R.id.iv_volume_up, "128"))
//        data.add(KeyBean(R.id.iv_volume_down, "129"))
//        data.add(KeyBean(R.id.iv_shang, "82"))
//        data.add(KeyBean(R.id.iv_xia, "81"))
//        data.add(KeyBean(R.id.iv_zuo, "80"))
//        data.add(KeyBean(R.id.iv_you, "79"))
//        data.add(KeyBean(R.id.iv_ok, "40"))
//        return data
//    }
//
//
//    fun getOtherKey(): List<KeyBean> {
//        val data: MutableList<KeyBean> = ArrayList<KeyBean>()
//        data.add(KeyBean("帮助", "117"))
//        data.add(KeyBean("选择", "118"))
//        data.add(KeyBean("停止", "119"))
//        data.add(KeyBean("取消", "155"))
//        data.add(KeyBean("清除", "156"))
//        data.add(KeyBean("退格", "42"))
//        data.add(KeyBean("ESCAPE", "41"))
//        data.add(KeyBean("Space", "44"))
//        data.add(KeyBean("TAB", "43"))
//        data.add(KeyBean("APP", "101"))
//        data.add(KeyBean("Execute", "116"))
//        data.add(KeyBean("PageUP", "75"))
//        data.add(KeyBean("PageDown", "78"))
//        data.add(KeyBean("Home1", "74"))
//        data.add(KeyBean("Right Arrow", "79"))
//        data.add(KeyBean("Left Arrow", "80"))
//        data.add(KeyBean("Down Arrow", "81"))
//        data.add(KeyBean("Up Arrow", "82"))
//        data.add(KeyBean("Mvioce", "62"))
//        data.add(KeyBean("ExSel", "164"))
//        return data
//    }
//
//    fun getOtherKey2(): List<KeyBean> {
//        val data: MutableList<KeyBean> = ArrayList<KeyBean>()
//        data.add(KeyBean(R.id.tv_bangzhu, "117"))
//        data.add(KeyBean(R.id.tv_xuanzhe, "118"))
//        data.add(KeyBean(R.id.tv_tingzhi, "119"))
//        data.add(KeyBean(R.id.tv_quxiao, "155"))
//        data.add(KeyBean(R.id.tv_qingchu, "156"))
//        data.add(KeyBean(R.id.tv_tuige, "42"))
//        data.add(KeyBean(R.id.tv_escape, "41"))
//        data.add(KeyBean(R.id.tv_space, "44"))
//        data.add(KeyBean(R.id.tv_tab, "43"))
//        data.add(KeyBean(R.id.tv_app, "101"))
//        data.add(KeyBean(R.id.tv_execute, "116"))
//        data.add(KeyBean(R.id.tv_pageup, "75"))
//        data.add(KeyBean(R.id.tv_pagedown, "78"))
//        data.add(KeyBean(R.id.tv_home1, "74"))
//        data.add(KeyBean(R.id.tv_right_arrow, "79"))
//        data.add(KeyBean(R.id.tv_left_arrow, "80"))
//        data.add(KeyBean(R.id.tv_down_arrow, "81"))
//        data.add(KeyBean(R.id.tv_up_arrow, "82"))
//        data.add(KeyBean(R.id.tv_mvioce, "62"))
//        data.add(KeyBean(R.id.tv_exsel, "164"))
//        return data
//    }
}
