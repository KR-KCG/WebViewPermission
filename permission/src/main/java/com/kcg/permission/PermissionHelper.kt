package com.kcg.permission

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionHelper {

    private const val REQUEST_CODE = 2001

    private const val SUCCESS = "SUCCESS"
    private const val NOT_MATCH_REQUEST_CODE = "NOT_MATCH_REQUEST_CODE"
    private const val RESULTS_IS_EMPTY = "RESULTS_IS_EMPTY"
    private const val PERMISSION_DENIED = "PERMISSION_DENIED"


    private val requiredPermissionList = ArrayList<String>()
    private val optionalPermissionList = ArrayList<String>()

    fun optionalPermission(permission: Array<String>) = optionalPermissionList.addAll(permission)
    fun clearOptionalPermission() = optionalPermissionList.clear()
    fun checkOptionalPermission(context: Context): Boolean {
        var success = true
        optionalPermissionList.forEach { if (!checkPermission(context, it)) success = false }
        return success
    }

    fun requiredPermission(permission: Array<String>) = requiredPermissionList.addAll(permission)
    fun clearRequiredPermission() = requiredPermissionList.clear()
    fun checkRequiredPermission(context: Context): Boolean {
        var success = true
        requiredPermissionList.forEach { if (!checkPermission(context, it)) success = false }
        return success
    }

    fun checkPermission(context: Context, permission: String): Boolean = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    fun requestPermissions(activity: Activity) {

        val permissions = ArrayList<String>()

        requiredPermissionList.forEach {
            if (checkPermission(activity, it)) return@forEach
            permissions.add(it)
        }

        optionalPermissionList.forEach {
            if (checkPermission(activity, it)) return@forEach
            permissions.add(it)
        }

        ActivityCompat.requestPermissions(activity, permissions.toTypedArray(), REQUEST_CODE)
    }

    fun onRequestPermissionsResult(activity: AppCompatActivity, requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        val requireDenied = ArrayList<String>()
        val optionalDenied = ArrayList<String>()
        var result = SUCCESS

        if (requestCode != REQUEST_CODE) result = NOT_MATCH_REQUEST_CODE

        if (grantResults.isEmpty()) result = RESULTS_IS_EMPTY
        else {
            for (i in grantResults.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) continue

                when {
                    requiredPermissionList.contains(permissions[i]) -> requireDenied.add(permissions[i])
                    optionalPermissionList.contains(permissions[i]) -> optionalDenied.add(permissions[i])
                }
            }
        }

        if (requireDenied.isNotEmpty()) result = PERMISSION_DENIED

        WebViewFragment.with(activity).requestPermissionsResult(result, requireDenied, optionalDenied)
    }
}