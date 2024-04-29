package com.sy.bhid.data

data class HidReport(val deviceType: DeviceType?, val reportId: Int, val data: ByteArray) {

    var SendState = State.None

    enum class DeviceType {
        None,
        Mouse,
        Keyboard
    }


    enum class State {
        None,
        Sending,
        Sended,
        Failded
    }
}
