package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private lateinit var textField: TextView
    private lateinit var searchButton: Button
    private lateinit var bluetooth: Bluetooth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bluetooth = Bluetooth(this)
        textField = findViewById(R.id.debug)
        searchButton = findViewById(R.id.searchButton)

        searchButton.setOnClickListener {
            Thread {
                for (device in bluetooth.findDevices()) {
                    show(device.name)
                }
            }.start()
        }
    }

    override fun onDestroy() {
        bluetooth.unregister()
        super.onDestroy()
    }

    fun show(text: String) {
        textField.post {
            textField.append("$text\n")
        }
    }

    fun waitUntil(condition: () -> Boolean) {
        println("Starting wait")
        while (!condition()) {
            Thread.sleep(10)
        }
        println("Ending wait")
    }
}