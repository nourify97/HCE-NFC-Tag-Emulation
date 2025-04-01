package com.nourify.ndeftagemulation.ui.screens.cardemulation

import kotlinx.serialization.Serializable

data class TagDetail(
    val tagMsgContent: String,
    val tagUrlContent: String,
    val wifiInfo: WifiInfo,
    val vCardInfo: VcardInfo,
    val tagType: TagType,
) {
    companion object {
        fun toInitialState() =
            TagDetail(
                tagType = TagType.TEXT_TAG,
                tagMsgContent = "",
                tagUrlContent = "",
                wifiInfo = WifiInfo("", ""),
                vCardInfo = VcardInfo("", "", "", ""),
            )
    }
}

@Serializable
data class WifiInfo(
    val ssid: String,
    val password: String,
)

@Serializable
data class VcardInfo(
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val email: String,
)

enum class TagType {
    TEXT_TAG, URL_TAG, WIFI_TAG, VCARD_TAG
}
