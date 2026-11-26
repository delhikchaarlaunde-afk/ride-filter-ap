package com.ridefilter.driver

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.provider.Settings
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*

class MainActivity : Activity() {
    private val prefs by lazy { getSharedPreferences("filters", Context.MODE_PRIVATE) }
    private lateinit var area: EditText
    private lateinit var minFare: EditText
    private lateinit var rateKm: EditText
    private lateinit var maxPickup: EditText
    private lateinit var ola: CheckBox
    private lateinit var uber: CheckBox
    private lateinit var rapido: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createChannel()
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100)
        }
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(24, 24, 24, 24); setBackgroundColor(0xFFF7F8FA.toInt()) }
        contentRoot = root
        val title = TextView(this).apply { text = "Ride Filter"; textSize = 28f; setTextColor(0xFF16202A.toInt()); gravity = Gravity.CENTER; setPadding(0, 8, 0, 8) }
        root.addView(title, match())
        root.addView(TextView(this).apply { text = "Ola • Uber • Rapido booking filter"; textSize = 15f; gravity = Gravity.CENTER; setTextColor(0xFF59636E.toInt()) }, match())
        area = field("Pickup area / location (e.g. Loni, Ghaziabad)", "area")
        minFare = field("Minimum booking fare (₹)", "minFare")
        rateKm = field("Minimum rate per km (₹)", "rateKm")
        maxPickup = field("Maximum pickup distance (km)", "maxPickup")
        ola = check("Ola", "ola", true)
        uber = check("Uber", "uber", true)
        rapido = check("Rapido", "rapido", true)
        val save = Button(this).apply { text = "Save settings"; setOnClickListener { saveSettings(); Toast.makeText(this@MainActivity, "Settings saved", Toast.LENGTH_SHORT).show() } }
        root.addView(save, match())
        val access = Button(this).apply { text = "Enable notification access"; setOnClickListener { startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)) } }
        root.addView(access, match())
        val note = TextView(this).apply {
            text = "How it works:\n1. Save your filters.\n2. Enable notification access.\n3. When a supported driver app shows a ride notification, this app checks the text and alerts if it appears to match.\n\nImportant: It cannot see every offer, guarantee fare/distance extraction, or accept rides automatically. You must review and accept in the original app. Official platform integration is required for automatic acceptance."
            textSize = 14f; setTextColor(0xFF354052.toInt()); setPadding(0, 18, 0, 0)
        }
        root.addView(note, match())
        val scroll = ScrollView(this).apply { addView(root) }
        setContentView(scroll)
        loadSettings()
    }

    private fun field(hintText: String, key: String): EditText {
        val e = EditText(this).apply { hint = hintText; textSize = 16f; singleLine = true; setPadding(12, 8, 12, 8) }
        val saved = prefs.getString(key, "") ?: ""
        e.setText(saved)
        (window.decorView as? ViewGroup)
        // Fields are added to the root in creation order through this helper's caller.
        (contentRoot ?: return e).addView(e, match())
        return e
    }
    private var contentRoot: LinearLayout? = null
    private fun check(label: String, key: String, default: Boolean): CheckBox {
        return CheckBox(this).apply {
            text = label; isChecked = prefs.getBoolean(key, default)
            contentRoot?.addView(this, match())
        }
    }
    private fun loadSettings() {
        area.setText(prefs.getString("area", "Loni, Ghaziabad"))
        minFare.setText(prefs.getString("minFare", "200"))
        rateKm.setText(prefs.getString("rateKm", "12"))
        maxPickup.setText(prefs.getString("maxPickup", "10"))
        ola.isChecked = prefs.getBoolean("ola", true); uber.isChecked = prefs.getBoolean("uber", true); rapido.isChecked = prefs.getBoolean("rapido", true)
    }
    private fun saveSettings() {
        prefs.edit().putString("area", area.text.toString()).putString("minFare", minFare.text.toString()).putString("rateKm", rateKm.text.toString()).putString("maxPickup", maxPickup.text.toString()).putBoolean("ola", ola.isChecked).putBoolean("uber", uber.isChecked).putBoolean("rapido", rapido.isChecked).apply()
    }
    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(NotificationChannel("matches", "Matching rides", NotificationManager.IMPORTANCE_HIGH))
        }
    }
    private fun match() = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply { bottomMargin = 10 }
}
