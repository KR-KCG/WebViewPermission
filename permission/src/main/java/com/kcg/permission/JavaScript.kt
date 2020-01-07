package com.kcg.permission

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class JavaScript(private val activity: AppCompatActivity, private val webView: WebView) {

    @JavascriptInterface
    fun finish() = Handler().post { activity.finish() }

    @JavascriptInterface
    fun close() = Handler().post { WebViewFragment.with(activity).close() }

    @JavascriptInterface
    fun visible() = Handler(Looper.getMainLooper()).post { webView.visibility = View.VISIBLE }

    @JavascriptInterface
    fun invisible() = Handler(Looper.getMainLooper()).post { webView.visibility = View.INVISIBLE }

    @JavascriptInterface
    fun gone() = Handler(Looper.getMainLooper()).post { webView.visibility = View.GONE }

    @JavascriptInterface
    fun loadUrl(url: String) = Handler().post { webView.loadUrl(url) }

    @JavascriptInterface
    fun setLockBackPress(boolean: Boolean) = Handler().post { WebViewFragment.with(activity).setLockBackPress(boolean) }

    @JavascriptInterface
    fun optionalPermission(permission: Array<String>) = Handler().post { PermissionHelper.optionalPermission(permission) }

    @JavascriptInterface
    fun clearOptionalPermission() = Handler().post { PermissionHelper.clearOptionalPermission() }

    @JavascriptInterface
    fun requiredPermission(permission: Array<String>) = Handler().post { PermissionHelper.requiredPermission(permission) }

    @JavascriptInterface
    fun clearRequiredPermission() = Handler().post { PermissionHelper.clearRequiredPermission() }

    @JavascriptInterface
    fun checkPermission(permission: String): Boolean = PermissionHelper.checkPermission(activity, permission)

    @JavascriptInterface
    fun requestPermissions() = Handler().post { PermissionHelper.requestPermissions(activity) }

    @JavascriptInterface
    fun getAppVersionCode(): String {
        val info = activity.packageManager.getPackageInfo(activity.packageName, 0)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) info.longVersionCode.toString()
        else info.versionCode.toString()
    }

    @JavascriptInterface
    fun getAppVersionName(): String = activity.packageManager.getPackageInfo(activity.packageName, 0).versionName

    @JavascriptInterface
    fun getPackageName(): String = activity.packageName

    @JavascriptInterface
    fun webViewReSizing(widthRatio: Float, heightRatio: Float) = Handler(Looper.getMainLooper()).post { Utils.reSizingView(activity, webView, widthRatio, heightRatio) }

    @JavascriptInterface
    fun agreementResult(result: String) = Handler().post {
        Log.e("T", "in $result")
        var obj = try {
            JSONObject(result)
        } catch (e: JSONException) {
            null
        }

        if (obj == null) {
            val jsonArray = try {
                JSONArray(result)
            } catch (e: JSONException) {
                return@post
            }

            for (i in 0 until jsonArray.length()) obj = jsonArray.optJSONObject(i) ?: continue
        }

        obj?.let {
            WebViewFragment.with(activity)
                    .agreementResult?.result(
                    it.optString("terms_of_user") == "1",
                    it.optString("personal_information_collection") == "1",
                    it.optString("marketing") == "1"
            )
        }
    }
}