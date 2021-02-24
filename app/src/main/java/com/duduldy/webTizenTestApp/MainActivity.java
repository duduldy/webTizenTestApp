package com.duduldy.webTizenTestApp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

	private WebView webView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		InitializeView(); // 초기화
		setWebView(); // 웹뷰

	}

	/**
	 *  초기화
	 */
	public void InitializeView(){
		//웹뷰
		webView = (WebView) findViewById(R.id.mainWeb);
	}

	/**
	 *  웹뷰
	 */
	public void setWebView(){
		/* 웹 세팅 */
		WebSettings webSettings = webView.getSettings();
		//webSettings.setJavaScriptEnabled(true);// xxs 취약성 발생 가능성
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setAllowFileAccessFromFileURLs(true); // API 30 지원 중지
		webSettings.setSaveFormData(false); // API 26 지원 중지
		webSettings.setSavePassword(false); // API 18 지원 중지
		//webSettings.setUseWideViewPort(true);
		webSettings.setSupportMultipleWindows(true);
		webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);  // SINGLE_COLUMN : API 12 지원 중지

		// Javascript 사용하기
		webSettings.setJavaScriptEnabled(true);// xxs 취약성 발생 가능성
		// WebView 내장 줌 사용여부
		webSettings.setBuiltInZoomControls(false);
		// 줌 컨트롤 사용 여부
		webSettings.setDisplayZoomControls(false);
		// 사용자 제스처를 통한 줌 기능 활성화 여부
		webSettings.setSupportZoom(false);
		// 화면에 맞게 WebView 사이즈를 정의
		webSettings.setLoadWithOverviewMode(true);
		// TextEncoding 이름 정의
		webSettings.setDefaultTextEncodingName("UTF-8");
		// ViewPort meta tag를 활성화 여부
		webSettings.setUseWideViewPort(true);
		// Setting Local Storage
		webSettings.setDatabaseEnabled(true);
		webSettings.setDomStorageEnabled(true);

		// 캐쉬 사용 방법을 정의
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

		/* 리다이렉트 브라우저 열기 방지*/
		webView.setWebViewClient(new WebViewClient());
		webView.setWebChromeClient(new WebChromeClient());

		/* 자바스크립트로 웹에서 호출할 메소드를 구현 */
		callJSWebToAnd();

		/* 자바스크립트로 안드로이드에서 호출할 메소드를 구현 */
		callJSAndToWeb();

		/* 웹 뷰 띄우기 */
		webView.loadUrl("https://naver.com/"); //접속할 URL - res/xml/network_security_config.xml에 정의 필요
		//webView.loadUrl("http://1.1.1.1:8080/"); 로컬호스트일 경우
	}


	/**
	 *  웹페이지 코드
	 */
	/*
	<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>테스트</title>
		<meta name="viewport" content="width=device-width, user-scalable=no">
		<script language="JavaScript">
		// 웹뷰에서 네이티브 메소드를 호출하여 값을 받아옴
		function webViewToApp() {
			alert(WebViewCallbackInterface.webViewToApp(1, 2));
		}

		// 웹뷰에서 접근 허용되지 않은 네이티브 메소드를 호출
		function appToWebViewNative() {
			alert(WebViewCallbackInterface.appToWebViewNative());
		}

		// 네이티브에서 자바 스크립트를 호출
		function executeFunction(value) {
			alert(value);
			return '네이티브로 반환';
		}
		</script>
	</head>
	<body>
		<div id="text"></div>
		<hr/>
		<h2>JavaScript - Android WebView Callback Interface Sample</h2>
		<hr/>
		<input type="button" style="HEIGHT: 60pt" value="1 + 2 앱 호출하여 계산하기" onclick="webViewToApp()"/>
		<br>
		<br>
		<input type="button" style="HEIGHT: 60pt" value="접근불가 메소드 호출" onclick="appToWebViewNative()"/>
	</body>
	</html>
	*/

	/**
	 *  웹에서 안드로이드 호출
	 */
	public void callJSWebToAnd() {
		// 웹뷰 자바 스크립트 인터페이스 추가(인터페이스 명 : WebViewCallbackInterface)
		webView.addJavascriptInterface(new JSCallback() {
			/**
			 * 웹에서 네이티브 메소드 호출
			 * @param valueA 인자
			 * @param valueB 인자
			 * @return 반한값
			 */
			@JavascriptInterface
			@Override
			public String webViewToApp(int valueA, int valueB) {
				return "계산 결과 : " + (valueA + valueB);
			}

			/**
			 * 웹뷰에서 호출할 수 없는 메소드 ( @JavascriptInterface 없음 )
			 * @return 반환값
			 */
			@Override
			public String appToWebViewNative() {
				return "접근불가";
			}
		}, "WebViewCallbackInterface");
	}

	/**
	 *  안드로이드에서 웹 호출
	 */
	public void callJSAndToWeb() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // SDK 19 이상일떄
			findViewById(R.id.webCallBtn).setOnClickListener(
					view -> webView.evaluateJavascript("javascript:executeFunction(\"앱에서 웹뷰 스크립트 호출\");"
							, value -> Toast.makeText(MainActivity.this, value.replace("\"", ""), Toast.LENGTH_SHORT).show()));
		} else {
			findViewById(R.id.webCallBtn).setVisibility(View.GONE);
		}
	}


	public void onBackPressed() {
		if(webView.canGoBack()) webView.goBack();
		else finish();
	}
}