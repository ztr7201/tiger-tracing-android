package com.example.myapplication

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class Bluetooth(private val activity: MainActivity) {
    private val adapter: BluetoothAdapter =
        BluetoothAdapter.getDefaultAdapter() ?: error("No bluetooth on device")
    private var discoveryState = DiscoveryState.STOPPED
    private val receiver: BroadcastReceiver
    private val devices = mutableListOf<BluetoothDevice>()

    init {
        val filter = IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            addAction(BluetoothDevice.ACTION_FOUND)
        }

        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                onReceive(intent)
            }
        }
        activity.registerReceiver(receiver, filter)
    }

    private fun onReceive(intent: Intent) {
        when (intent.action) {
            BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                if (discoveryState == DiscoveryState.WAITING_FOR_START) {
                    discoveryState = DiscoveryState.RUNNING
                }
            }
            BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                if (discoveryState == DiscoveryState.RUNNING) {
                    discoveryState = DiscoveryState.STOPPED
                }
            }
            BluetoothDevice.ACTION_FOUND -> {
                if (discoveryState == DiscoveryState.RUNNING) {
                    val device =
                        intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    if (device != null) {
                        devices.add(device)
                    }
                }
            }
        }
    }

    fun findDevices(): List<BluetoothDevice> {
        activity.show("searching for devices")
        devices.clear()
        adapter.startDiscovery()
        discoveryState = DiscoveryState.WAITING_FOR_START
        while (discoveryState != DiscoveryState.STOPPED) {
            println(discoveryState)
        }
        return devices
    }

    fun unregister() {
        activity.unregisterReceiver(receiver)
    }
}

private enum class DiscoveryState {
    STOPPED, WAITING_FOR_START, RUNNING
}