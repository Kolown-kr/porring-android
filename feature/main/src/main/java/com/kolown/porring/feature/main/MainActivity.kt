package com.kolown.porring.feature.main

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.kolown.porring.core.designsystem.ui.theme.PorringTheme
import com.kolown.porring.core.navigation.Route
import com.kolown.porring.core.ui.component.LocalSnackBarBridge
import com.kolown.porring.core.ui.component.SnackBarBridge
import com.kolown.porring.feature.main.component.NotAvailableVersionScreen
import com.kolown.porring.feature.main.navigation.MainNavigator
import com.kolown.porring.feature.main.navigation.rememberMainNavigator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navigator: MainNavigator = rememberMainNavigator()
            val currentRoute = navigator.currentDestination?.route?.substringAfterLast(".") ?: ""
            val versionNameState by mainViewModel.versionNameFlow.collectAsState("")
            val vName = this.packageManager.getPackageInfo(this.packageName, 0).versionName

            PorringTheme(currentRoute = currentRoute) {
                if (versionNameState.isNotBlank() && vName != versionNameState) {
                    NotAvailableVersionScreen(onExitAppButtonClicked = {
                        finishAndRemoveTask()
                    })
                } else {
                    MainRoute(
                        navigator = navigator,
                        mainViewModel = mainViewModel
                    )
                }
            }
        }

        if (!BuildConfig.DEBUG) {
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
}