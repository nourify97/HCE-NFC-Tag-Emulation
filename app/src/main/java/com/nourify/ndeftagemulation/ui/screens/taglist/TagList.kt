package com.nourify.ndeftagemulation.ui.screens.taglist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nourify.ndeftagemulation.R
import com.nourify.ndeftagemulation.data.storage.NdefTag
import com.nourify.ndeftagemulation.ui.screens.cardemulation.TagDetail
import com.nourify.ndeftagemulation.ui.screens.cardemulation.TagType
import com.nourify.ndeftagemulation.ui.screens.cardemulation.VcardInfo
import com.nourify.ndeftagemulation.ui.screens.cardemulation.WifiInfo
import org.koin.androidx.compose.koinViewModel

@Composable
fun TagList(
    modifier: Modifier = Modifier,
    vm: TagListVM = koinViewModel(),
) {
    Content(
        tagList = tagList,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun Content(
    tagList: List<NdefTag>,
    modifier: Modifier = Modifier,
) {
    if (tagList.isEmpty()) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("No tags Available")
        }
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(25.dp, Alignment.Top)
        ) {
            items(tagList) { tag ->
                TagItem(
                    tagDetail = tag,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun TagItem(
    tagDetail: TagDetail,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Icon(
            painter = painterResource(
                when(tagDetail.tagType) {
                    TagType.TEXT_TAG -> R.drawable.baseline_text_fields_24
                    TagType.URL_TAG -> R.drawable.baseline_web_24
                    TagType.WIFI_TAG -> R.drawable.baseline_network_wifi_24
                    TagType.VCARD_TAG -> R.drawable.baseline_contacts_24
                }
            ),
            contentDescription = null, // maybe should think about accessibility
            tint = Color.LightGray
        )
        Text(
            when(tagDetail.tagType) {
                TagType.TEXT_TAG -> tagDetail.tagMsgContent
                TagType.URL_TAG -> tagDetail.tagUrlContent
                TagType.WIFI_TAG -> tagDetail.wifiInfo.ssid
                TagType.VCARD_TAG -> tagDetail.vCardInfo.firstName
            }
        )
        IconButton(onClick = {}) {
            Icon(
                painter = painterResource(R.drawable.baseline_nfc_24),
                contentDescription = "Start tag emulation",
                tint = Color.Gray
            )
        }
    }
}

@Preview
@Composable
private fun TagListPrev() {

    val tagDetailsList = listOf(
        TagDetail(
            tagType = TagType.TEXT_TAG,
            tagMsgContent = "Hello, world!",
            tagUrlContent = "",
            wifiInfo = WifiInfo("", ""),
            vCardInfo = VcardInfo("", "", "", "")
        ),
        TagDetail(
            tagType = TagType.URL_TAG,
            tagMsgContent = "",
            tagUrlContent = "https://example.com",
            wifiInfo = WifiInfo("", ""),
            vCardInfo = VcardInfo("", "", "", "")
        ),
        TagDetail(
            tagType = TagType.WIFI_TAG,
            tagMsgContent = "",
            tagUrlContent = "",
            wifiInfo = WifiInfo("MyNetwork", "SecurePass123"),
            vCardInfo = VcardInfo("", "", "", "")
        ),
        TagDetail(
            tagType = TagType.VCARD_TAG,
            tagMsgContent = "",
            tagUrlContent = "",
            wifiInfo = WifiInfo("", ""),
            vCardInfo = VcardInfo("John", "Doe", "+1234567890", "john.doe@example.com")
        )
    )

    Content(
        tagList = tagDetailsList,
        modifier = Modifier.fillMaxSize()
    )
}