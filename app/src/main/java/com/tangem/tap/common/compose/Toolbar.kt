package com.tangem.tap.common.compose

import androidx.annotation.DrawableRes
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.tangem.wallet.R

@Composable
fun Toolbar(modifier: Modifier = Modifier, onNavigationClick: () -> Unit = {}, title: String, @DrawableRes navigationIcon: Int) {
    TopAppBar(
        title = { Text(text = title) },
        elevation = 0.dp,
        modifier = modifier,
        backgroundColor = colorResource(R.color.backgroundLightGray),
        navigationIcon = {
            IconButton(onClick = onNavigationClick) {
                Icon(
                    painterResource(id = navigationIcon),
                    contentDescription = null
                )
            }
        }
    )
}