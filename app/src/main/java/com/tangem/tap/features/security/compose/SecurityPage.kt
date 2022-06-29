package com.tangem.tap.features.security.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontFamily.Companion
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tangem.tap.common.compose.CaptionText
import com.tangem.tap.common.compose.Toolbar
import com.tangem.wallet.R
import com.tangem.wallet.R.color

@Composable
fun SecurityPage(
    onChangeAccessMethodClick: () -> Unit = {},
    onBackClickListener: () -> Unit ={}
) {
    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(R.string.security_and_privacy_title),
                modifier = Modifier.padding(top = 32.dp),
                navigationIcon = R.drawable.ic_baseline_arrow_back_24,
                onNavigationClick = onBackClickListener
            )
        }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(color.backgroundLightGray))
                .padding(it)
        ) {
            CaptionText(
                text = stringResource(R.string.security_group_subtitle),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp)
            )

            SecurityItem(
                onItemClick = onChangeAccessMethodClick,
                title = stringResource(R.string.security_change_access_method)
            )
        }
    }
}

@Composable
private fun SecurityItem(onItemClick: () -> Unit, title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
    ) {
        Text(text = title, modifier = Modifier.padding(16.dp))
    }
}

@Preview
@Composable
fun SecurityPagePreview() {
    SecurityPage()
}