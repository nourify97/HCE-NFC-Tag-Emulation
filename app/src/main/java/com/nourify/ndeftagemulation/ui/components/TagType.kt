package com.nourify.ndeftagemulation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nourify.ndeftagemulation.ui.screens.cardemulation.TagType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagTypeItem(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    selectedTagType: Int,
    onExpanded: (Boolean) -> Unit,
    onSelectedTagType: (Int) -> Unit,
) {
    Box(
        modifier = modifier
            .padding(start = 32.dp, end = 32.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = onExpanded
        ) {
            TextField(
                value = TagType.entries[selectedTagType].name,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpanded(false) }
            ) {
                TagType.entries.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item.name) },
                        onClick = {
                            onSelectedTagType(item.ordinal)
                            onExpanded(false)
                        }
                    )
                }
            }
        }
    }
}