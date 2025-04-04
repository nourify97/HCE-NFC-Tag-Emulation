package com.nourify.ndeftagemulation.ui.screens.taglist

import android.content.Context
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nourify.ndeftagemulation.R
import com.nourify.ndeftagemulation.ui.components.ActionIcon
import com.nourify.ndeftagemulation.ui.components.SwipeableItemWithActions
import com.nourify.ndeftagemulation.ui.screens.cardemulation.TagType
import org.koin.androidx.compose.koinViewModel

@Composable
fun TagList(
    context: Context,
    modifier: Modifier = Modifier,
    vm: TagListVM = koinViewModel(),
) {
    val savedTags = vm.savedTags

    Content(
        tagList = savedTags,
        deleteTag = vm::deleteTag,
        initTagEmulation = { vm.initTagEmulation(context, it) },
        updateActionBtnVisibility = vm::updateActionVisibility,
        modifier = modifier.fillMaxSize(),
    )
}

@Composable
fun Content(
    tagList: List<UiTag>,
    deleteTag: (id: Long) -> Unit,
    initTagEmulation: (NdefMessage) -> Unit,
    updateActionBtnVisibility: (Long, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

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
            verticalArrangement = Arrangement.spacedBy(25.dp, Alignment.Top),
        ) {
            items(tagList, key = { it.id }) { tag ->
                SwipeableItemWithActions(
                    isRevealed = tag.isOptionsRevealed,
                    onExpanded = { updateActionBtnVisibility(tag.id, true) },
                    onCollapsed = { updateActionBtnVisibility(tag.id, false) },
                    actions = {
                        ActionIcon(
                            onClick = {
                                Toast
                                    .makeText(
                                        context,
                                        "Tag ${tag.id} was deleted.",
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                deleteTag(tag.id)
                            },
                            backgroundColor = Color.Red,
                            icon = Icons.Default.Delete,
                            modifier = Modifier.fillMaxHeight(),
                        )
                    },
                ) {
                    TagItem(
                        tagDetail = tag,
                        initTagEmulation = initTagEmulation,
                        modifier = Modifier,
                    )
                }
            }
        }
    }
}

@Composable
fun TagItem(
    tagDetail: UiTag,
    initTagEmulation: (NdefMessage) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Icon(
            painter =
                painterResource(
                    when (tagDetail.tagType) {
                        TagType.TEXT_TAG -> R.drawable.baseline_text_fields_24
                        TagType.URL_TAG -> R.drawable.baseline_web_24
                        TagType.WIFI_TAG -> R.drawable.baseline_network_wifi_24
                        TagType.VCARD_TAG -> R.drawable.baseline_contacts_24
                    },
                ),
            contentDescription = null, // maybe should think about accessibility
            tint = Color.Black,
        )
        Text(text = tagDetail.name)
        IconButton(
            onClick = { initTagEmulation(tagDetail.ndefMessage) },
            colors =
                IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Gray,
                ),
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_nfc_24),
                contentDescription = "Start tag emulation",
                tint = Color.White,
            )
        }
    }
}

@Preview
@Composable
private fun TagListPrev() {
    val ndefMessage = NdefMessage(NdefRecord.createTextRecord("en", "fake"))

    val tagDetailsList =
        listOf(
            UiTag(
                id = 0,
                tagType = TagType.TEXT_TAG,
                name = "Hello, world!",
                ndefMessage = ndefMessage,
                isOptionsRevealed = false,
            ),
            UiTag(
                id = 1,
                tagType = TagType.URL_TAG,
                name = "Example URL",
                ndefMessage = ndefMessage,
                isOptionsRevealed = false,
            ),
            UiTag(
                id = 2,
                tagType = TagType.WIFI_TAG,
                name = "WiFi Credentials",
                ndefMessage = ndefMessage,
                isOptionsRevealed = false,
            ),
            UiTag(
                id = 3,
                tagType = TagType.VCARD_TAG,
                name = "John Doe",
                ndefMessage = ndefMessage,
                isOptionsRevealed = false,
            ),
        )

    Content(
        tagList = tagDetailsList,
        deleteTag = {},
        initTagEmulation = {},
        updateActionBtnVisibility = { _, _ -> },
        modifier = Modifier.fillMaxSize(),
    )
}
