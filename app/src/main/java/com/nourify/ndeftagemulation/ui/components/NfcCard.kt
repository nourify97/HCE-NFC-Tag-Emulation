package com.nourify.ndeftagemulation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nourify.ndeftagemulation.R

@Composable
fun NfcCard(text: String) {
    Box(
        modifier =
            Modifier
                .size(300.dp, 200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Blue),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.baseline_nfc_24),
            tint = Color.White,
            contentDescription = "NFC Logo",
            modifier =
                Modifier
                    .padding(10.dp)
                    .size(30.dp)
                    .align(Alignment.TopStart),
        )
        Text(
            text = text,
            color = Color.White,
            fontSize = 20.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}
