// MainActivity.kt
package com.nothing.mass

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import com.nothing.mass.service.MassGlyphService
import com.nothing.mass.util.PermissionManager

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!PermissionManager.hasUsageStatsPermission(this)) {
            PermissionManager.requestUsageStatsPermission(this)
        }

        startService(Intent(this, MassGlyphService::class.java))
        finish()
    }
}
