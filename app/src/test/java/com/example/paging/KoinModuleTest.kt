package com.example.paging

import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import org.junit.Test
import org.koin.android.test.verify.androidVerify

import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.koinApplication
import org.koin.ksp.generated.com_example_paging_AppModule
import org.koin.ksp.generated.module
import org.koin.test.verify.verify

class KoinModuleTest {
    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun koinModuleAndroidVerify(){
        koinApplication {
            // Koin test, provided by package `koin-android-test`
            AppModule().module.androidVerify(
                extraTypes = listOf(
                    HttpClientEngine::class,
                    HttpClientConfig::class
                )
            )
        }
    }
}