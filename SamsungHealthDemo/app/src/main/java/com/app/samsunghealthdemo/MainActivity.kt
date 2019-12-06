package com.app.samsunghealthdemo

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.app.samsunghealthdemo.StepCountReporter.StepCountObserver
import com.samsung.android.sdk.healthdata.HealthConnectionErrorResult
import com.samsung.android.sdk.healthdata.HealthConstants
import com.samsung.android.sdk.healthdata.HealthDataStore
import com.samsung.android.sdk.healthdata.HealthDataStore.ConnectionListener
import com.samsung.android.sdk.healthdata.HealthPermissionManager
import com.samsung.android.sdk.healthdata.HealthPermissionManager.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    companion object{
        const val APP_TAG = "SimpleHealth"
    }

    private lateinit var mStore: HealthDataStore
    private lateinit var mReporter: StepCountReporter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create a HealthDataStore instance and set its listener
        // Create a HealthDataStore instance and set its listener
        mStore = HealthDataStore(this, mConnectionListener)
        // Request the connection to the health data store
        // Request the connection to the health data store
        mStore.connectService()
    }

    private val mConnectionListener: ConnectionListener = object : ConnectionListener {
        override fun onConnected() {
            Log.d(APP_TAG, "Health data service is connected.")
            mReporter = StepCountReporter(mStore)
            if (isPermissionAcquired()) {
                mReporter.start(mStepCountObserver)
            } else {
                requestPermission()
            }
        }

        override fun onConnectionFailed(error: HealthConnectionErrorResult) {
            Log.d(APP_TAG, "Health data service is not available.")
            showConnectionFailureDialog(error)
        }

        override fun onDisconnected() {
            Log.d(APP_TAG, "Health data service is disconnected.")
            if (!isFinishing) {
                mStore.connectService()
            }
        }
    }

    override fun onDestroy() {
        mStore.disconnectService()
        super.onDestroy()
    }

    private fun showPermissionAlarmDialog() {
        if (isFinishing) {
            return
        }
        val alert: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
        alert.setTitle(R.string.notice)
            .setMessage(R.string.msg_perm_acquired)
            .setPositiveButton(R.string.ok, null)
            .show()
    }

    private fun showConnectionFailureDialog(error: HealthConnectionErrorResult)
    {
        if (isFinishing) {
            return
        }

        val alert =
            AlertDialog.Builder(this)

        if (error.hasResolution()) {
            when (error.errorCode) {
                HealthConnectionErrorResult.PLATFORM_NOT_INSTALLED -> alert.setMessage(R.string.msg_req_install)
                HealthConnectionErrorResult.OLD_VERSION_PLATFORM -> alert.setMessage(R.string.msg_req_upgrade)
                HealthConnectionErrorResult.PLATFORM_DISABLED -> alert.setMessage(R.string.msg_req_enable)
                HealthConnectionErrorResult.USER_AGREEMENT_NEEDED -> alert.setMessage(R.string.msg_req_agree)
                else -> alert.setMessage(R.string.msg_req_available)
            }
        } else {
            alert.setMessage(R.string.msg_conn_not_available)
        }

        alert.setPositiveButton(
            R.string.ok
        ) { dialog: DialogInterface?, id: Int ->
            if (error.hasResolution()) {
                error.resolve(this@MainActivity)
            }
        }

        if (error.hasResolution()) {
            alert.setNegativeButton(R.string.cancel, null)
        }

        alert.show()
    }



    private fun isPermissionAcquired(): Boolean {
        val permKey =
            PermissionKey(HealthConstants.StepCount.HEALTH_DATA_TYPE, PermissionType.READ)
        val pmsManager = HealthPermissionManager(mStore)
        try { // Check whether the permissions that this application needs are acquired
            val resultMap =
                pmsManager.isPermissionAcquired(Collections.singleton(permKey))
            return resultMap[permKey]!!
        } catch (e: java.lang.Exception) {
            Log.e(APP_TAG, "Permission request fails.", e)
        }
        return false
    }


    private fun requestPermission() {
        val permKey =
            PermissionKey(HealthConstants.StepCount.HEALTH_DATA_TYPE, PermissionType.READ)
        val pmsManager = HealthPermissionManager(mStore)
        try { // Show user permission UI for allowing user to change options
            pmsManager.requestPermissions(Collections.singleton(permKey), this@MainActivity)
                .setResultListener { result: PermissionResult ->
                    Log.d(APP_TAG, "Permission callback is received.")
                    val resultMap =
                        result.resultMap
                    if (resultMap.containsValue(java.lang.Boolean.FALSE)) {
                        updateStepCountView("")
                        showPermissionAlarmDialog()
                    } else { // Get the current step count and display it
                        mReporter.start(mStepCountObserver)
                    }
                }
        } catch (e: Exception) {
            Log.e(APP_TAG, "Permission setting fails.", e)
        }
    }


    private val mStepCountObserver = StepCountObserver { count: Int ->
        Log.d(APP_TAG, "Step reported : $count")
        updateStepCountView(count.toString())
    }

    private fun updateStepCountView(count: String) {
        runOnUiThread { editHealthDateValue1.text = count }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.connect) {
            requestPermission()
        }
        return true
    }
}
