package com.kcg.permissiondemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kcg.permission.PermissionHelper
import com.kcg.permission.WebViewFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        openWebView.setOnClickListener { WebViewFragment.with(this@MainActivity).setUrl("https://www.google.com").open() }
    }

    override fun onBackPressed() {
        if (WebViewFragment.onBackPress()) return

        super.onBackPressed()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionHelper.onRequestPermissionsResult(this@MainActivity, requestCode, permissions, grantResults)
    }

    override fun onStop() {
        super.onStop()
        WebViewFragment.onStop()
    }

    override fun onStart() {
        super.onStart()
        WebViewFragment.onStart(this)

    }
}