package com.enecuum.app.vvm.host

//import com.enecuum.app.utils.Constants.WS_URL
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import com.enecuum.app.R
import com.enecuum.app.data.keys.KeyStore
import com.enecuum.app.utils.Constants.LANGUAGE_KEY
import com.enecuum.app.utils.LocaleHelper
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.coroutines.launch
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (KeyStore.publicKey(applicationContext).isNotEmpty() && KeyStore.userReferral(applicationContext).isEmpty()) {
            KeyStore.savePublicKey(applicationContext, KeyStore.publicKey(applicationContext))
        }
    }


}