package xelagurd.socialdating.client

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dagger.hilt.android.AndroidEntryPoint
import xelagurd.socialdating.client.ui.SocialDatingApp
import xelagurd.socialdating.client.ui.theme.AppTheme
import xelagurd.socialdating.client.ui.theme.isDarkTheme
import xelagurd.socialdating.client.ui.viewmodel.MainViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    private val localNetworkPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            requestLocalNetworkPermission()
        }
        setContent {
            val themeMode by mainViewModel.themeMode.collectAsState()
            val isDarkTheme = themeMode.isDarkTheme()

            LaunchedEffect(isDarkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        Color.TRANSPARENT,
                        Color.TRANSPARENT
                    ) { isDarkTheme },
                    navigationBarStyle = SystemBarStyle.auto(
                        Color.TRANSPARENT,
                        Color.TRANSPARENT
                    ) { isDarkTheme }
                )
            }

            AppTheme(themeMode = themeMode) {
                SocialDatingApp()
            }
        }
    }

    // the debug build reaches the server by the local network address 10.0.2.2, which apps targeting API 37 reach
    // only with this permission, it is granted on install, but is reset together with the cleared app data
    private fun requestLocalNetworkPermission() {
        if (BuildConfig.DEBUG &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.CINNAMON_BUN &&
            checkSelfPermission(Manifest.permission.ACCESS_LOCAL_NETWORK) != PackageManager.PERMISSION_GRANTED
        ) {
            localNetworkPermissionRequest.launch(Manifest.permission.ACCESS_LOCAL_NETWORK)
        }
    }
}
