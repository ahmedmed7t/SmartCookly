package com.nexable.smartcookly

import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.nexable.smartcookly.theme.CooklyTheme
import com.nexable.smartcookly.navigation.RootNavigation

@Composable
@Preview
fun App() {
    CooklyTheme {
        RootNavigation()
    }
}