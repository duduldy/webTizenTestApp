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
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Initialize(); // 초기화
		setWebView(); // 웹뷰
		getMobileSensorData(); // 모바일 데이터 얻기
		getWatchSensorData(); // 와치 데이터 얻기

	}

	// 웹뷰
	private WebView webView;
	// 모바일 센서
	private TextView mobile_gyro_x;
	private TextView mobile_gyro_y;
	private TextView mobile_gyro_z;
	private TextView mobile_acc_x;
	private TextView mobile_acc_y;
	private TextView mobile_acc_z;
	// 와치 센서
	private TextView watch_gyro_x;
	private TextView watch_gyro_y;
	private TextView watch_gyro_z;
	private TextView watch_acc_x;
	private TextView watch_acc_y;
	private TextView watch_acc_z;
	/**
	 *  초기화
	 */
	public void Initialize(){
		//웹뷰
		webView = (WebView) findViewById(R.id.mainWeb);

		// 모바일 센서
		mobile_gyro_x = (TextView) findViewById(R.id.mobile_gyro_x);
		mobile_gyro_y = (TextView) findViewById(R.id.mobile_gyro_y);
		mobile_gyro_z = (TextView) findViewById(R.id.mobile_gyro_z);
		mobile_acc_x  = (TextView) findViewById(R.id.mobile_acc_x);
		mobile_acc_y  = (TextView) findViewById(R.id.mobile_acc_y);
		mobile_acc_z  = (TextView) findViewById(R.id.mobile_acc_z);
		mobile_gyro_x.setText("X:0");
		mobile_gyro_y.setText("Y:0");
		mobile_gyro_z.setText("Z:0");
		mobile_acc_x.setText("X:0.0000000000000000001");
		mobile_acc_y.setText("Y:0.0000000000000000001");
		mobile_acc_z.setText("Z:0.0000000000000000001");
		// 와치 센서
		watch_gyro_x = (TextView) findViewById(R.id.watch_gyro_x);
		watch_gyro_y = (TextView) findViewById(R.id.watch_gyro_y);
		watch_gyro_z = (TextView) findViewById(R.id.watch_gyro_z);
		watch_acc_x  = (TextView) findViewById(R.id.watch_acc_x);
		watch_acc_y  = (TextView) findViewById(R.id.watch_acc_y);
		watch_acc_z  = (TextView) findViewById(R.id.watch_acc_z);
		watch_gyro_x.setText("X:0");
		watch_gyro_y.setText("Y:0");
		watch_gyro_z.setText("Z:0");
		watch_acc_x.setText("X:0.0000000000000000001");
		watch_acc_y.setText("Y:0.0000000000000000001");
		watch_acc_z.setText("Z:0.0000000000000000001");

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
		webSettings.setJavaScriptEnabled(true);// xss 취약성 발생 가능성
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
		//webView.loadUrl("http://1.1.1.1:8080/"); //로컬호스트일 경우
	}

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

	/**
	 *  모바일 센서 데이터 얻기
	 */
	private void getMobileSensorData() {

	}
	/**
	 *  와치 센서 데이터 얻기
	 *  메소드 명을 get으로 했지만 추후 catch로 바뀔 수 있음
	 *  (와치에서 낙상의 경우에만 데이터를 보낼 듯)
	 */
	private void getWatchSensorData() {

	}


	public void onBackPressed() {
		if(webView.canGoBack()) webView.goBack();
		else finish();
	}
}
