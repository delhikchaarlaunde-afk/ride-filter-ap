package com.ridefilter.driver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import java.util.Locale

class RideNotificationListener : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val platform = platformFor(sbn.packageName) ?: return
        val p = getSharedPreferences("filters", Context.MODE_PRIVATE)
        if (!p.getBoolean(platform, true)) return
        val extras = sbn.notification.extras
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty()
        val body = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString().orEmpty()
        val big = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString().orEmpty()
        val text = listOf(title, body, big).joinToString(" ").trim()
        if (text.isBlank()) return
        val fare = Regex("(?:₹|Rs\\.?\\s?)(\\d{2,5})", RegexOption.IGNORE_CASE).find(text)?.groupValues?.getOrNull(1)?.toIntOrNull()
        val km = Regex("(\\d+(?:\\.\\d+)?)\\s*(?:km|kms|kilomet\\w*)", RegexOption.IGNORE_CASE).find(text)?.groupValues?.getOrNull(1)?.toDoubleOrNull()
        val area = p.getString("area", "").orEmpty().trim()
        val areaMatch = area.isBlank() || text.contains(area, ignoreCase = true)
        val minFare = p.getString("minFare", "0")?.toIntOrNull() ?: 0
        val rate = p.getString("rateKm", "0")?.toDoubleOrNull() ?: 0.0
        val maxPickup = p.getString("maxPickup", "999")?.toDoubleOrNull() ?: 999.0
        // Unknown fare/distance is never treated as a confirmed match.
        if (fare == null || km == null) return
        val perKm = if (km > 0) fare / km else 0.0
        if (areaMatch && fare >= minFare && perKm >= rate && km <= maxPickup) {
            sendMatch(platform, text, fare, km, perKm)
        }
    }

    private fun platformFor(pkg: String): String? = when {
        pkg.contains("ola", true) -> "ola"
        pkg.contains("uber", true) -> "uber"
        pkg.contains("rapido", true) -> "rapido"
        else -> null
    }

    private fun sendMatch(platform: String, original: String, fare: Int, km: Double, perKm: Double) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= 26) manager.createNotificationChannel(NotificationChannel("matches", "Matching rides", NotificationManager.IMPORTANCE_HIGH))
        val n = NotificationCompat.Builder(this, "matches")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Matching ride: ${platform.uppercase(Locale.getDefault())}")
            .setContentText("₹$fare • ${km} km • ₹${"%.1f".format(perKm)}/km")
            .setStyle(NotificationCompat.BigTextStyle().bigText("$original\nEstimated rate: ₹${"%.1f".format(perKm)}/km\nOpen the original driver app and review the offer."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        manager.notify((System.currentTimeMillis() % Int.MAX_VALUE).toInt(), n)
    }
}
