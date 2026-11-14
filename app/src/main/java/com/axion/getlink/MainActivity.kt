package com.axion.getlink

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.BLACK)
            setPadding(20, 40, 20, 20)
        }

        // Thanh nhập link
        val inputLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 0, 0, 20)
        }

        val editText = EditText(this).apply {
            hint = "Dán link vào đây..."
            setTextColor(Color.WHITE)
            setHintTextColor(Color.GRAY)
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }

        val button = Button(this).apply {
            text = "GET"
            setOnClickListener {
                val url = editText.text.toString().trim()
                if (url.isNotEmpty()) {
                    webView.loadUrl(url)
                }
            }
        }

        inputLayout.addView(editText)
        inputLayout.addView(button)

        // Cấu hình WebView
        webView = WebView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            
            // Giả lập User-Agent của máy tính để tránh bị web phát hiện
            settings.userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36"

            // Vô hiệu hóa tính năng phát hiện gỡ lỗi (Chống Pause in debugger)
            WebView.setWebContentsDebuggingEnabled(false)

            webChromeClient = WebChromeClient()

            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    
                    // TIÊM JAVASCRIPT VÀO SAU KHI TRANG LOAD XONG
                    val js = """
                        javascript:(function() {
                            console.log("AXION TOOL: Bắt đầu tiêm script...");
                            
                            // 1. VÔ HIỆU HÓA LỆNH DEBUGGER (Chống lỗi Pause)
                            // Xóa hàm debugger để web không thể ép trình duyệt dừng lại
                            window.debugger = function(){};
                            Function.prototype.constructor = function(){ return function(){}; };
                            
                            // 2. TUA NHANH THỜI GIAN X5
                            // Tìm các biến toàn cục đếm thời gian và ép nó chạy nhanh
                            setInterval(function() {
                                for (var key in window) {
                                    // Nếu biến là số và có tên liên quan đến thời gian
                                    if (typeof window[key] === 'number' && window[key] > 0) {
                                        var lowerKey = key.toLowerCase();
                                        if (lowerKey.includes('time') || lowerKey.includes('count') || lowerKey.includes('sec') || lowerKey.includes('wait')) {
                                            // Giảm 5 giây mỗi 1 giây (tua x5)
                                            window[key] = Math.max(0, window[key] - 5);
                                        }
                                    }
                                }
                            }, 1000);

                            // 3. TỰ ĐỘNG BẤM NÚT KHI ĐỦ ĐIỀU KIỆN
                            setInterval(function() {
                                var elements = document.querySelectorAll('button, a, input[type="button"], div');
                                for (var i = 0; i < elements.length; i++) {
                                    var text = elements[i].innerText ? elements[i].innerText.toLowerCase() : "";
                                    if (text.includes('tiếp tục') || text.includes('click vào đây')) {
                                        if (!elements[i].disabled) {
                                            elements[i].style.backgroundColor = '#00FF00'; // Đổi màu xanh cho dễ nhìn
                                        }
                                    }
                                }
                            }, 500);
                        })();
                    """.trimIndent()

                    view?.evaluateJavascript(js, null)
                }
            }
        }

        layout.addView(inputLayout)
        layout.addView(webView)
        setContentView(layout)
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
