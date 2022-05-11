package com.example.myapplication

import android.bluetooth.BluetoothAssignedNumbers
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHeadset
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.os.IResultReceiver
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.*


class BroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        var cur_action : String = ""

        cur_action += intent.getAction()
        Log.e("myAction", "current action: " + cur_action)
        when(cur_action){
            Intent.ACTION_SCREEN_ON ->
                Toast.makeText(context, "Screen On", Toast.LENGTH_SHORT).show()
            BluetoothDevice.ACTION_ACL_CONNECTED ->
                Toast.makeText(context, "CONNECT, $cur_action", Toast.LENGTH_SHORT).show()
            BluetoothDevice.ACTION_ACL_DISCONNECTED ->
                Toast.makeText(context, "DISCONNECT, $cur_action", Toast.LENGTH_SHORT).show()
            BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED->
                Toast.makeText(context, "STATECHANGE, $cur_action", Toast.LENGTH_SHORT).show()
            BluetoothHeadset.ACTION_VENDOR_SPECIFIC_HEADSET_EVENT->
                Toast.makeText(context, "HEADSET_EVENT, $cur_action", Toast.LENGTH_SHORT).show()

            else ->
                Toast.makeText(context, "another, $cur_action", Toast.LENGTH_SHORT).show()

        }

    }
    fun registerReceiver(context: Context?)
    {


    }

    fun unregisterReceiver(context: Context)
    {
        context.unregisterReceiver(this)
    }
}