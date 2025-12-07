package com.example.paging

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.paging.presentation.composable.navigation3.NavigationApp
import com.example.paging.ui.theme.PagingTheme
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.annotation.KoinApplication
import org.koin.core.logger.Level
import org.koin.ksp.generated.startKoin
import org.koin.mp.KoinPlatform


@KoinApplication(modules = [AppModule::class])
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Be careful of the startKoin import
        // It should be `org.koin.ksp.generated.startKoin` which loads modules defined in `AppModule`
        // Not `import org.koin.core.context.startKoin`, which makes no module available
        startKoin {
            androidLogger()
            printLogger(Level.DEBUG)
            androidContext(this@MainActivity)
            androidFileProperties("paging_setting.properties")
        }

        enableEdgeToEdge()
        setContent {
            PagingTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavigationApp(Modifier.padding(innerPadding))
                }
            }
        }
    }

    // avoid `org.koin.core.error.KoinApplicationAlreadyStartedException: A Koin Application has already been started` after a configuration change
    // https://slack-chats.kotlinlang.org/t/22906932/hello-i-m-using-koin-in-my-android-jetpack-comopose-app-and-
    override fun onDestroy() {
        super.onDestroy()
        KoinPlatform.stopKoin()
    }
}
