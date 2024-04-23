object HidConfig {
    const val NAME = "Evin Keyboard"
    const val DESCRIPTION = "Evin for you"
    const val PROVIDER = "Evin"
    val KEYBOARD_COMBO = byteArrayOf(
        0x05.toByte(), 0x01.toByte(), //USAGE_PAGE (Generic Desktop)
        0x09.toByte(), 0x06.toByte(), //USAGE (Keyboard)
        0xA1.toByte(), 0x01.toByte(), //COLLECTION (Application)
        0x85.toByte(), 0x08.toByte(), //REPORT_ID (8)
        0x05.toByte(), 0x07.toByte(), //USAGE_PAGE (Keyboard)
        0x19.toByte(), 0xE0.toByte(), //USAGE_MINIMUM (Keyboard LeftControl)
        0x29.toByte(), 0xE7.toByte(), //USAGE_MAXIMUM (Keyboard Right GUI)
        0x15.toByte(), 0x00.toByte(), //LOGICAL_MINIMUM (0)
        0x25.toByte(), 0x01.toByte(), //LOGICAL_MAXIMUM (1)
        //第一个字节
        0x75.toByte(), 0x01.toByte(), //REPORT_SIZE (1)
        0x95.toByte(), 0x08.toByte(), //REPORT_COUNT (8)
        0x81.toByte(), 0x02.toByte(), //INPUT (Data,Var,Abs)
        //第二个字节
        0x95.toByte(), 0x01.toByte(), //REPORT_COUNT (1)
        0x75.toByte(), 0x08.toByte(), //REPORT_SIZE (8)
        0x81.toByte(), 0x03.toByte(), //INPUT (Cnst,Var,Abs)
        //后六个字节
        0x95.toByte(), 0x06.toByte(), //REPORT_COUNT (6)
        0x75.toByte(), 0x08.toByte(), //REPORT_SIZE (8)
        0x15.toByte(), 0x00.toByte(), //LOGICAL_MINIMUM (0)
        0x25.toByte(), 0x65.toByte(), //LOGICAL_MAXIMUM (101)
        0x05.toByte(), 0x07.toByte(), //USAGE_PAGE (Keyboard)
        0x19.toByte(), 0x00.toByte(), //USAGE_MINIMUM (Reserved (no event indicated))
        0x29.toByte(), 0x65.toByte(), //USAGE_MAXIMUM (Keyboard Application)
        0x81.toByte(), 0x00.toByte(), //INPUT (Data,Ary,Abs)
        0xC0.toByte()  //END_COLLECTION
    )
}
