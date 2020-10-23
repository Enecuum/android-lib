package com.enecuum.app.vvm.home

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.crashlytics.android.Crashlytics
import com.enecuum.app.BuildConfig
import com.enecuum.app.R
import com.enecuum.app.data.keys.KeyStore
import com.enecuum.app.extensions.hideProgress
import com.enecuum.app.extensions.showProgress
import com.enecuum.app.service.DateService
import com.enecuum.app.utils.AmountValue
import com.enecuum.app.utils.Constants
import com.enecuum.app.utils.SageSign
import com.enecuum.app.vvm.common.TextButton
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.getViewModel
import kotlin.coroutines.CoroutineContext

class HomeFragment : Fragment(), CoroutineScope {

    private val viewModel: HomeViewModel by lazy {
        requireActivity().getViewModel<HomeViewModel>()
    }

    private var dateService: DateService? = null
    private var isServiceBound = false
    private var isMining = false

    private lateinit var parentJob: Job
    private val coroutineScope: CoroutineScope
        get() = CoroutineScope(Dispatchers.Main + parentJob)

    private var data: SageSign.GeneratedData? = null

    override val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.IO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentJob = Job()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService()
        parentJob.cancel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        balance.configure(R.string.balance, backStyle = TextButton.BackStyle.GRAY_DISABLED, allCaps = true)
        getBIT.configure(R.string.get_bit, backStyle = TextButton.BackStyle.GRAY_DISABLED, allCaps = true)
        mining.configure(R.string.tab_mining, backStyle = TextButton.BackStyle.GRAY_DISABLED, allCaps = true)
        signIn.configure(R.string.signin, backStyle = TextButton.BackStyle.BLUE)
        signIn.setText("GENERATE KEY")
        signIn.setOnClickListener {
            signIn()
        }

        Prefs.putString(Constants.MINING_TOKEN, BuildConfig.TOKEN)

        viewModel.observeDetailedBalance(this, Observer {
            if (it == null) {
                balance.setText("ERROR")
                return@Observer
            }
            balance.setText(AmountValue.formatFromApiRaw(it.amount))
            enableCheckBalance()
        })

        context?.let {
            if (KeyStore.publicKey(it).isNotEmpty()) {

                signIn.setText(KeyStore.publicKey(it))
                signIn.setDisabled()

                enableCheckBalance()
                viewModel.getDetailedBalance()
                enableGetBIT()
                setupStartMiningButton()
            }
        }
    }

    private fun signIn() {
        rootLayout.showProgress()
        coroutineScope.launch(Dispatchers.Main) {

            data = SageSign.generateKeys()
            KeyStore.saveKeys(view?.context!!, data!!)

//            context?.let { KeyStore.saveKeys(it, "db74d0c9bba6bc7451b43e02868b66910c74b818aa745bb358d9b1701ade785d") }
//            context?.let { KeyStore.saveKeys(it, "65b0931ccb672308b20526e4d80dce77dc3c15e38260e48ff06c8c7129265a47") }

            context?.let { signIn.setText(KeyStore.publicKey(it)) }

            signIn.setDisabled()

            enableCheckBalance()
            enableGetBIT()
            setupStartMiningButton()

            rootLayout.hideProgress()
        }
    }

    private fun enableCheckBalance() {
        balance.setEnabled()
        balance.setOnClickListener {
            balance.setDisabled()
            viewModel.getDetailedBalance()
        }
    }

    private fun enableGetBIT() {
        getBIT.setEnabled()
        getBIT.setOnClickListener {
            getBIT.setDisabled()
            viewModel.get25BIT { enableGetBIT() }
        }
    }

    private fun setupStartMiningButton() {
        mining.setEnabled()
        mining.setText("MINING")
        mining.setOnClickListener {
            mining.setDisabled()
            startService()
        }
    }

    private fun setupStopMiningButton() {
        mining.setEnabled()
        mining.setText("STOP")
        mining.setOnClickListener {
            mining.setDisabled()
            stopService()
        }
    }

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            dateService = (binder as DateService.DatesBinder).getService()
            dateService?.let { service ->
                service.service.status.observe(viewLifecycleOwner, Observer { status ->
                    run {

                        Log.d("Mining", "Status: $status")
                        Log.d("Mining", "Bind: $isServiceBound")
                        Log.d("Mining", "Mining: $isMining")

                        if (status.name == "CONNECTING") {
                            mining.setText("CONNECTING")
                        }

                        if (status.name == "STARTED" && !isMining) {
                            isMining = true
                            setupStopMiningButton()
                        }

                        if (status.name == "STOPPED" && isMining) {
                            isMining = false
                            setupStartMiningButton()
                        }
                    }
                })
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            dateService = null
        }
    }

    private fun startService() {
        val intent = Intent(context, DateService::class.java).apply {}
        if (!isServiceBound) bindService()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity?.startForegroundService(intent)
        } else {
            activity?.startService(intent)
        }
    }

    private fun stopService() {
        val intent = Intent(context, DateService::class.java)
        activity?.stopService(intent)
        unbindService()
    }

    private fun bindService() {
        activity?.bindService(
            Intent(context, DateService::class.java),
            connection,
            Context.BIND_AUTO_CREATE
        )
        isServiceBound = true
    }

    private fun unbindService() {
        if (isServiceBound) {
            try {
                activity?.unbindService(connection)
            } catch (e: Exception) {
                Crashlytics.logException(e)
            }
            isServiceBound = false
        }
    }
}
