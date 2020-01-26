package com.example.kotlinwifiscanner

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val REQUEST_LOCATION_CODE = 1
    var resultList = ArrayList<ScanResult>()
    lateinit var wifiManager: WifiManager

    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            resultList = wifiManager.scanResults as ArrayList<ScanResult>
            stopScanning()
            Toast.makeText(baseContext, resultList.count().toString() , Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setPermission()

        wifiManager = this.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        btn_search.setOnClickListener{
            startScanning()
        }
    }

    fun startScanning() {
        registerReceiver(broadcastReceiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
        wifiManager.startScan()
    }

    fun stopScanning() {
        unregisterReceiver(broadcastReceiver)
    }

    private fun setPermission(){
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
        if(permission!=PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show()
        }

        if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION))
        {
            val builder=AlertDialog.Builder(this)
            builder.setMessage("Permission to access the wifi is required for this app")
            builder.setTitle("Permission Required")
            builder.setPositiveButton("OK")
            {
                dialogInterface, i ->  Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show()
                makeRequest()
            }
            val dialog=builder.create()
            dialog.show()
        }
        else
        {
            makeRequest()
        }
    }

    private fun makeRequest(){
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
        REQUEST_LOCATION_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode)
        {
            REQUEST_LOCATION_CODE -> {
                if(grantResults.isEmpty()||grantResults[0]!=PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
