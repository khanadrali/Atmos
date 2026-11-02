package com.abbaskhan.atmos

import androidx.compose.ui.window.ComposeUIViewController
import com.abbaskhan.atmos.di.initKoin


fun MainViewController() = run {
    initKoin()
    ComposeUIViewController {
        App()
    }
}