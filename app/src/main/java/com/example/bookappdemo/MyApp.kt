package com.example.bookappdemo

import android.app.Application
import android.util.Log
import com.example.bookappdemo.di.component.AppComponent
import com.example.bookappdemo.di.component.DaggerAppComponent
import com.google.android.recaptcha.Recaptcha
import com.google.android.recaptcha.RecaptchaClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MyApp : Application() {

    lateinit var appComponent: AppComponent

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /** reCAPTCHA Enterprise client; null until [R.string.recaptcha_enterprise_site_key] is set and init succeeds. */
    @Volatile
    var recaptchaClient: RecaptchaClient? = null
        private set

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.create()
        val siteKey = getString(R.string.recaptcha_enterprise_site_key)
        if (siteKey.isNotBlank()) {
            applicationScope.launch {
                // recaptcha:18.5.1 exposes getClient; fetchClient requires a newer SDK.
                Recaptcha.getClient(this@MyApp, siteKey, INIT_TIMEOUT_MS)
                    .onSuccess { client ->
                        recaptchaClient = client
                        Log.i(TAG, "reCAPTCHA Enterprise client ready")
                    }
                    .onFailure { e ->
                        Log.e(TAG, "reCAPTCHA Enterprise init failed", e)
                    }
            }
        } else {
            Log.w(TAG, "recaptcha_enterprise_site_key is empty; skipping reCAPTCHA init")
        }
    }

    private companion object {
        private const val TAG = "MyApp"
        private const val INIT_TIMEOUT_MS = 15_000L
    }
}
