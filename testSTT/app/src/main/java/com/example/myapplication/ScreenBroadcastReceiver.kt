package com.example.myapplication

import android.bluetooth.BluetoothHeadset
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class ScreenBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        var cur_action : String = ""
        cur_action += intent.getAction()
        if(cur_action.equals(BluetoothHeadset.ACTION_VENDOR_SPECIFIC_HEADSET_EVENT)){
            Toast.makeText(context, "current action: $cur_action", Toast.LENGTH_SHORT).show()

        }
        else if(cur_action.equals(BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED)){
            Toast.makeText(context, "Audio connected, $cur_action", Toast.LENGTH_SHORT).show()

        }
    }
}