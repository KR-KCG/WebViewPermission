package com.kcg.permission

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import org.json.JSONArray
import org.json.JSONObject

class WebViewFragment(private val activity: AppCompatActivity) : Fragment() {

    companion object {
        private var INSTANCE: WebViewFragment? = null

        fun with(activity: AppCompatActivity): WebViewFragment {
            INSTANCE?.let {
                if (it.activity == activity) return@let

                INSTANCE = null
                INSTANCE = WebViewFragment(activity)
            } ?: let { INSTANCE = WebViewFragment(activity) }

            return INSTANCE!!
        }

        fun onStart(activity: AppCompatActivity) {
            if (INSTANCE == null || !INSTANCE!!.isStop) return

            val url = INSTANCE!!.webView.url
            val widthRatio = INSTANCE!!.widthRatio
            val heightRatio = INSTANCE!!.heightRatio
            val isLockBackPress = INSTANCE!!.isLockBackPress
            val agreementResult = INSTANCE!!.agreementResult

            if (INSTANCE!!.isOpen) INSTANCE!!.close()
            INSTANCE = null

            with(activity)
                    .setUrl(url)
                    .setWidthRatio(widthRatio)
                    .setHeightRatio(heightRatio)
                    .setLockBackPress(isLockBackPress)
                    .setAgreementResult(agreementResult)
                    .open()
        }

        fun onStop() {
            if (INSTANCE == null || !INSTANCE!!.isOpen) return
            INSTANCE!!.close()
            INSTANCE!!.isStop = true
        }

        fun onBackPress(): Boolean {
            INSTANCE ?: return false

            INSTANCE!!.webView.loadUrl("javascript:onBackPressForAndroid();")

            return INSTANCE!!.isOpen && INSTANCE!!.isLockBackPress
        }
    }

    private lateinit var webView: WebView
    private var isStop = false
    private var url: String = ""
    private var isOpen = false
    private var isLockBackPress = false
    private var widthRatio = 0.5f
    private var heightRatio = 0.5f
    internal var agreementResult: AgreementResult? = null

    fun setAgreementResult(agreementResult: AgreementResult?): WebViewFragment {
        this.agreementResult = agreementResult
        return this
    }

    fun setWidthRatio(widthRatio: Float): WebViewFragment {
        this.widthRatio = widthRatio
        return this
    }

    fun setHeightRatio(heightRatio: Float): WebViewFragment {
        this.heightRatio = heightRatio
        return this
    }

    fun setUrl(url: String): WebViewFragment {
        this.url = url
        return this
    }

    fun open() {
        if (activity.window.decorView.id == -1) activity.window.decorView.id = View.generateViewId()

        val fragmentTransaction = activity.supportFragmentManager.beginTransaction()
        fragmentTransaction.add(activity.window.decorView.id, this)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commitAllowingStateLoss()
        isOpen = true
    }

    fun close() {
        val fragmentTransaction = activity.supportFragmentManager.beginTransaction()
        fragmentTransaction.remove(this)
        fragmentTransaction.commitAllowingStateLoss()
        isOpen = false
    }

    internal fun setLockBackPress(boolean: Boolean): WebViewFragment {
        isLockBackPress = boolean
        return this
    }

    internal fun requestPermissionsResult(message: String, requireDenied: ArrayList<String>, optionalDenied: ArrayList<String>) {

        val obj = JSONObject()

        obj.put("result", if (message == "SUCCESS") 1 else 0)
        obj.put("message", message)

        if (requireDenied.isNotEmpty()) obj.put("requireDenied", JSONArray(requireDenied))
        if (optionalDenied.isNotEmpty()) obj.put("optionalDenied", JSONArray(optionalDenied))

        webView.loadUrl("javascript:requestPermissionsResultForAndroid('$obj');")
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.webview, container, false)
        view.setOnTouchListener { _, _ -> true }

        webView = view!!.findViewById(R.id.webView)

        webView.webViewClient = object : WebViewClient() {}
        webView.webChromeClient = object : WebChromeClient() {}

        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(JavaScript(activity, webView), "android")

        Utils.reSizingView(activity, webView, widthRatio, heightRatio)

        webView.loadUrl(url)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        retainInstance = true
    }

    override fun onPause() {
        super.onPause()
        webView.onPause()
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        webView.destroy()
    }
}