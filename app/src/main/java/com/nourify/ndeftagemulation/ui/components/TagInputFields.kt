package com.nourify.ndeftagemulation.ui.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun TextTagField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardController: SoftwareKeyboardController? = null,
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = { Text("Tag content") },
        singleLine = true,
        keyboardOptions =
            KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text,
            ),
        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
    )
}

@Composable
fun UrlTagField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardController: SoftwareKeyboardController? = null,
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = { Text("Tag content") },
        singleLine = true,
        keyboardOptions =
            KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Uri,
            ),
        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
    )
}

@Composable
fun WifiTagField(
    ssidValue: String,
    passValue: String,
    onSsidValueChange: (String) -> Unit,
    onPassValueChange: (String) -> Unit,
    keyboardController: SoftwareKeyboardController? = null,
) {
    OutlinedTextField(
        value = ssidValue,
        onValueChange = onSsidValueChange,
        label = { Text("Wifi ssid") },
        keyboardOptions =
            KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text,
            ),
    )
    OutlinedTextField(
        value = passValue,
        onValueChange = onPassValueChange,
        label = { Text("Wifi password") },
        keyboardOptions =
            KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Password,
            ),
        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
    )
}

@Composable
fun VCardTagField(
    firstname: String,
    lastname: String,
    phoneNumber: String,
    email: String,
    onFirstnameValueChange: (String) -> Unit,
    onLastnameValueChange: (String) -> Unit,
    onPhoneNumberValueChange: (String) -> Unit,
    onEmailValueChange: (String) -> Unit,
    keyboardController: SoftwareKeyboardController? = null,
) {
    OutlinedTextField(
        value = firstname,
        onValueChange = onFirstnameValueChange,
        label = { Text("vcard firstname") },
        keyboardOptions =
            KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text,
            ),
    )
    OutlinedTextField(
        value = lastname,
        onValueChange = onLastnameValueChange,
        label = { Text("Tag lastname") },
        keyboardOptions =
            KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text,
            ),
    )
    OutlinedTextField(
        value = phoneNumber,
        onValueChange = onPhoneNumberValueChange,
        label = { Text("vcard phoneNumber") },
        keyboardOptions =
            KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Phone,
            ),
    )
    OutlinedTextField(
        value = email,
        onValueChange = onEmailValueChange,
        label = { Text("vcard email") },
        keyboardOptions =
            KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Email,
            ),
        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
    )
}
