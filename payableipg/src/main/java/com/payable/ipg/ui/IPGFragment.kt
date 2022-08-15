package com.payable.ipg.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.payable.ipg.PAYableIPGClient
import com.payable.ipg.R
import com.payable.ipg.databinding.FragmentIpgBinding
import org.json.JSONException
import org.json.JSONObject

internal class IPGFragment(val ipgClient: PAYableIPGClient) : DialogFragment() {

    private lateinit var binding: FragmentIpgBinding
    private var urlBeforeError = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = Dialog(requireContext(), R.style.DialogSlideAnim)
        dialog.setContentView(FragmentIpgBinding.inflate(layoutInflater).root)
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        dialog?.apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
        }

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ipg, container, false)


        binding.buttonClose.setOnClickListener { finishView() }
        binding.buttonReload.setOnClickListener { reloadView() }

        /*progressDialog = ProgressDialog(activity).apply {
            setMessage("Please Wait....")
            setCancelable(false)
        }*/

        binding.webView.settings.javaScriptEnabled = true

        binding.webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                return false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                view?.onPageFinished()
            }
        }

        binding.webView.webChromeClient = object : WebChromeClient() {

            override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                console("onConsoleMessage: " + consoleMessage.message())
                return true
            }

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress == 100) {
                    binding.buttonLayout.visibility = View.VISIBLE
                    binding.progressLayout.visibility = View.GONE
                } else if (!binding.progressLayout.isVisible) {
                    binding.buttonLayout.visibility = View.GONE
                    binding.progressLayout.visibility = View.VISIBLE
                }
            }
        }

        class WebAppInterface {

            @JavascriptInterface
            fun onPaymentPageLoaded() {
                console("onPaymentPageLoaded")
                Handler(Looper.getMainLooper()).post {
                    ipgClient.ipgListener?.onPaymentPageLoaded()
                }
            }

            @JavascriptInterface
            fun onPaymentStarted() {
                console("onPaymentStarted")
                Handler(Looper.getMainLooper()).post {
                    ipgClient.ipgListener?.onPaymentStarted()
                }
            }

            @JavascriptInterface
            fun onPaymentCancelled() {
                console("onPaymentCancelled")
                Handler(Looper.getMainLooper()).post {
                    ipgClient.ipgListener?.onPaymentCancelled()
                }
            }

            @JavascriptInterface
            fun onPaymentError(data: String) {
                console("onPaymentError: $data")
                Handler(Looper.getMainLooper()).post {
                    ipgClient.ipgListener?.onPaymentError(data)
                }
            }

            val closeHandler = Handler(Looper.getMainLooper())
            val closeRun = Runnable { binding.buttonClose.performClick() }

            @JavascriptInterface
            fun onPaymentCompleted(data: String) {
                console("onPaymentCompleted: $data")
                Handler(Looper.getMainLooper()).post {

                    // binding.buttonReload.visibility = View.GONE
                    binding.buttonClose.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_check_24))

                    binding.buttonClose.setOnClickListener {
                        closeHandler.removeCallbacks(closeRun)
                        ipgClient.ipgListener?.onPaymentCompleted(data)
                        finishView(true)
                    }

                    ipgClient.ipgPayment?.let {
                        closeHandler.removeCallbacks(closeRun)
                        closeHandler.postDelayed(closeRun, it.uiConfig.statusViewDuration.toLong() * 1000)
                    }
                }
            }
        }

        binding.webView.addJavascriptInterface(WebAppInterface(), "Android")

        val paymentURL = ipgClient.generatePaymentURL()

        if (paymentURL != null) {
            binding.webView.safeLoadUrl(paymentURL)
        }

        return binding.root
    }

    fun reloadView() {
        if (binding.webView.isErrorPage()) {
            binding.webView.safeLoadUrl(urlBeforeError)
        } else {
            binding.webView.reload()
        }
    }

    fun finishView(isCompleted: Boolean = false) {
        // binding.webView.clearCache(true)
        if (!isCompleted) {
            ipgClient.ipgListener?.onPaymentCancelled()
        }
        dialog?.dismiss()
    }

    private fun WebView.safeLoadUrl(viewUrl: String) {
        if (context.isInternetAvailable()) {
            loadUrl(viewUrl)
        } else {
            urlBeforeError = viewUrl
            onPageFinished()
        }
    }

    private fun WebView.getContent(action: (content: String?) -> Unit) {
        evaluateJavascript("(function(){return window.document.body.outerHTML})();") {
            val html = it.replace("\\u003C", "<")
            action(if (html == "null") null else html)
        }
    }

    private fun getJSON(content: String?): JSONObject? {
        return try {
            if (content == null) return null
            val data = content.replace(Regex("<[^>]*>"), "").replace("\\", "")
            JSONObject(data.substring(data.indexOf("{"), data.lastIndexOf("}") + 1))
        } catch (ex: JSONException) {
            null
        }
    }

    private fun WebView.onPageFinished() {

        getContent {

            console("onPageFinished: $url $it")

            if (it == null
                || url == null
                || it == "\"<body></body>\""
                || "Web page not available" in it
                || "Something went wrong!" in it
                || "err-message" in it
            ) {

                if (!isErrorPage()) {

                    var error = it ?: "NULL"
                    urlBeforeError = url ?: urlBeforeError

                    if (it != null && "err-message" in it) {
                        val jsonError = getJSON(it)
                        if (jsonError != null) {
                            error = jsonError.toString()
                        }
                    }

                    console("onPaymentError: $error")
                    ipgClient.ipgListener?.onPaymentError(error)
                    loadUrl("file:///android_asset/error.html")
                }
            }
        }
    }

    private fun WebView.isErrorPage() = url == "file:///android_asset/error.html"

    @Suppress("DEPRECATION")
    private fun Context.isInternetAvailable(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                val cap = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
                return cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                val networks = cm.allNetworks
                for (n in networks) {
                    val nInfo = cm.getNetworkInfo(n)
                    if (nInfo != null && nInfo.isConnected) return true
                }
            }
            else -> {
                val networks = cm.allNetworkInfo
                for (nInfo in networks) {
                    if (nInfo != null && nInfo.isConnected) return true
                }
            }
        }
        return false
    }

    private fun console(message: String) {
        if (ipgClient.debug) {
            Log.e("PAYABLE-IPG", message)
        }
    }
}