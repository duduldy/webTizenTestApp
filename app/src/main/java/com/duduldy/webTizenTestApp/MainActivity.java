package com.duduldy.webTizenTestApp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Initialize(); // 초기화
		//setWebView(); // 웹뷰
		//setMobileSensorServiceReceiver(); // 모바일 센서 서비스 Receiver 설정
		//srtMobileSensorService(); // 모바일 센서 서비스 시작
		//getWatchSensorData(); // 와치 데이터 얻기

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		endMobileSensorServiceReceiver(); // 모바일 센서 서비스 Receiver 종료
	}

	private Boolean DEBUGLOG = true; // 디버그 로브 출력여부
	// 앱 뷰
	private WebView webView; // 웹뷰
	private LinearLayout mobile_fall; // 모바일 넘어짐 메세지
	private LinearLayout mobile_not_fall; // 모바일 안넘어짐 (센서데이터)
	private Button mobile_fall_btn; // 모바일 낙상기능 작동 버튼
	private Boolean mobile_fall_btn_state = true; // 모바일 낙상기능 작동 버튼 상태

	// 모바일 센서
	private TextView mobile_gyro_time;
	private TextView mobile_gyro_x;
	private TextView mobile_gyro_y;
	private TextView mobile_gyro_z;
	private TextView mobile_acc_time;
	private TextView mobile_acc_x;
	private TextView mobile_acc_y;
	private TextView mobile_acc_z;
	// 와치 센서
	private TextView watch_gyro_time;
	private TextView watch_gyro_x;
	private TextView watch_gyro_y;
	private TextView watch_gyro_z;
	private TextView watch_acc_time;
	private TextView watch_acc_x;
	private TextView watch_acc_y;
	private TextView watch_acc_z;
	/**
	 *  초기화
	 */
	public void Initialize(){
		if(DEBUGLOG) Log.d("MainActivity", "Initialize()");
		//웹뷰
		webView = (WebView) findViewById(R.id.mainWeb);

		// 모바일 넘어짐 메세지
		mobile_fall = (LinearLayout) findViewById(R.id.mobile_fall);
		mobile_fall.setVisibility(View.GONE);
		// 모바일 안넘어짐 (센서데이터)
		mobile_not_fall = (LinearLayout) findViewById(R.id.mobile_not_fall);
		mobile_not_fall.setVisibility(View.VISIBLE);

		// 모바일 안넘어짐 (센서데이터)
		mobile_fall_btn = (Button) findViewById(R.id.mobile_fall_btn);
		mobile_fall_btn.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View view) {
				setFallBtn();
			}
		});

		// 모바일 센서
		mobile_gyro_time = (TextView) findViewById(R.id.mobile_gyro_time);
		mobile_gyro_x = (TextView) findViewById(R.id.mobile_gyro_x);
		mobile_gyro_y = (TextView) findViewById(R.id.mobile_gyro_y);
		mobile_gyro_z = (TextView) findViewById(R.id.mobile_gyro_z);
		mobile_acc_time = (TextView) findViewById(R.id.mobile_acc_time);
		mobile_acc_x  = (TextView) findViewById(R.id.mobile_acc_x);
		mobile_acc_y  = (TextView) findViewById(R.id.mobile_acc_y);
		mobile_acc_z  = (TextView) findViewById(R.id.mobile_acc_z);
		mobile_gyro_time.setText("Time:");
		mobile_gyro_x.setText("X:");
		mobile_gyro_y.setText("Y:");
		mobile_gyro_z.setText("Z:");
		mobile_acc_time.setText("Time:");
		mobile_acc_x.setText("X:");
		mobile_acc_y.setText("Y:");
		mobile_acc_z.setText("Z:");
		// 와치 센서
		watch_gyro_time = (TextView) findViewById(R.id.watch_gyro_time);
		watch_gyro_x = (TextView) findViewById(R.id.watch_gyro_x);
		watch_gyro_y = (TextView) findViewById(R.id.watch_gyro_y);
		watch_gyro_z = (TextView) findViewById(R.id.watch_gyro_z);
		watch_acc_time = (TextView) findViewById(R.id.watch_acc_time);
		watch_acc_x  = (TextView) findViewById(R.id.watch_acc_x);
		watch_acc_y  = (TextView) findViewById(R.id.watch_acc_y);
		watch_acc_z  = (TextView) findViewById(R.id.watch_acc_z);
		watch_gyro_time.setText("Time:");
		watch_gyro_x.setText("X:");
		watch_gyro_y.setText("Y:");
		watch_gyro_z.setText("Z:");
		watch_acc_time.setText("Time:");
		watch_acc_x.setText("X:");
		watch_acc_y.setText("Y:");
		watch_acc_z.setText("Z:");

	}

	/**
	 *  웹뷰
	 */
	public void setWebView(){
		if(DEBUGLOG) Log.d("MainActivity", "setWebView()");
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
		if(DEBUGLOG) Log.d("MainActivity", "callJSWebToAnd()");
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
		if(DEBUGLOG) Log.d("MainActivity", "callJSAndToWeb()");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // SDK 19 이상일떄
			findViewById(R.id.webCallBtn).setOnClickListener(
					view -> webView.evaluateJavascript("javascript:executeFunction(\"앱에서 웹뷰 스크립트 호출\");"
							, value -> Toast.makeText(MainActivity.this, value.replace("\"", ""), Toast.LENGTH_SHORT).show()));
		} else {
			findViewById(R.id.webCallBtn).setVisibility(View.GONE);
		}
	}

	/**
	 *  모바일 센서 서비스 Receiver 설정
	 */
	private void setMobileSensorServiceReceiver() {
		if(DEBUGLOG) Log.d("MainActivity", "setMobileSensorServiceReceiver()");
		LocalBroadcastManager.getInstance(this).registerReceiver(sensorDataReceiver,
				new IntentFilter("mobile_data"));
		LocalBroadcastManager.getInstance(this).registerReceiver(sensorFallReceiver,
				new IntentFilter("mobile_fall"));
	}

	/**
	 *  모바일 센서 서비스 시작
	 */
	private void srtMobileSensorService() {
		if(DEBUGLOG) Log.d("MainActivity", "srtMobileSensorService()");
		Toast.makeText(getApplicationContext(),getString(R.string.start_service_safe),Toast.LENGTH_SHORT).show();
		/*Intent intent= new Intent(getApplicationContext(), // 현재제어권자
		 							SensorService.class); // 이동할 컴포넌트*/
		Intent intent= new Intent(MainActivity.this, SensorService.class);
		startService(intent); // 서비스 시작
	}

	/**
	 *  모바일 센서 서비스 종료
	 */
	private void endMobileSensorService() {
		if(DEBUGLOG) Log.d("MainActivity", "endMobileSensorService()");
		Toast.makeText(getApplicationContext(),getString(R.string.start_service_safe_end),Toast.LENGTH_SHORT).show();
		/*Intent intent= new Intent(getApplicationContext(), // 현재제어권자
		 							SensorService.class); // 이동할 컴포넌트*/
		Intent intent= new Intent(MainActivity.this, SensorService.class);
		stopService(intent); // 서비스 종료
	}

	/**
	 *  모바일 센서 데이터 받기
	 */
	private BroadcastReceiver sensorDataReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String mobile_data_type = intent.getStringExtra("mobile_data_type");
			String mobile_time_stamp = intent.getStringExtra("mobile_time_stamp");
			String mobile_time_date = intent.getStringExtra("mobile_time_date");
			String mobile_x = intent.getStringExtra("mobile_x");
			String mobile_y = intent.getStringExtra("mobile_y");
			String mobile_z = intent.getStringExtra("mobile_z");
			if(DEBUGLOG) Log.d("MainActivity", "mBroadcastReceiver() get data - "+mobile_data_type+" - "+mobile_time_stamp+" - "+mobile_time_date+" - "+mobile_x+"/"+mobile_y+"/"+mobile_z);

			if("gyro".equals(mobile_data_type)){
				mobile_gyro_time.setText("Time:"+mobile_time_date);
				mobile_gyro_x.setText("X:"+mobile_x);
				mobile_gyro_y.setText("Y:"+mobile_y);
				mobile_gyro_z.setText("Z:"+mobile_z);
			} else if("acc".equals(mobile_data_type)){
				mobile_acc_time.setText("Time:"+mobile_time_date);
				mobile_acc_x.setText("X:"+mobile_x);
				mobile_acc_y.setText("Y:"+mobile_y);
				mobile_acc_z.setText("Z:"+mobile_z);
			}
			// 로그
			// 센서 : g - 자이로, a - 가속도
			// 레코드 설명 : 센서,시간,x,y,z
			/*String logStr = mobile_data_type.charAt(0)+","+mobile_time_date+","+mobile_x+","+mobile_y+","+mobile_z;
			setSensorAndLog(logStr);*/
		}
	};

	/**
	 *  모바일 넘어짐 감지 받기
	 *  SensorService boolean state -> MainActive String state
	 *  true:모바일 넘어짐 메세지 <-> false:안넘어짐 (데이터 표시)
	 */
	private BroadcastReceiver sensorFallReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String fallCk = intent.getStringExtra("fall_ck");
			if(DEBUGLOG) Log.d("MainActivity", "mBroadcastReceiver() fallCk - "+fallCk);

			if("true".equals(fallCk)){ // true:넘어짐
				mobile_fall.setVisibility(View.VISIBLE);
				mobile_not_fall.setVisibility(View.GONE);
			} else { // false:안넘어짐
				mobile_fall.setVisibility(View.GONE);
				mobile_not_fall.setVisibility(View.VISIBLE);
			}
		}
	};

	/**
	 *  모바일 낙상기능 작동 버튼
	 *  true:모바일 넘어짐 메세지 <-> false:안넘어짐 (데이터 표시)
	 */
	private void setFallBtn() {
		if(DEBUGLOG) Log.d("MainActivity", "setFallBtn() - "+mobile_fall_btn_state);
		// 초기값 false 일때 누르면 낙상기능
		if(mobile_fall_btn_state){ // true:넘어짐
			mobile_fall_btn_state = false;
			mobile_fall.setVisibility(View.VISIBLE);
			mobile_not_fall.setVisibility(View.GONE);
			//endMobileSensorService(); // 모바일 센서 서비스 종료
		} else { // false:안넘어짐
			mobile_fall_btn_state = true;
			mobile_fall.setVisibility(View.GONE);
			mobile_not_fall.setVisibility(View.VISIBLE);
			//setMobileSensorServiceReceiver(); // 모바일 센서 서비스 Receiver 설정
			//srtMobileSensorService(); // 모바일 센서 서비스 시작
		}
		// 데이터 저장 테스트
		setSensorAndLog("테스트 텍스트");
	}

	/**
	 *  모바일 센서 서비스 Receiver 종료
	 */
	private void endMobileSensorServiceReceiver() {
		if(DEBUGLOG) Log.d("MainActivity", "endMobileSensorServiceReceiver()");
		LocalBroadcastManager.getInstance(this).unregisterReceiver(sensorDataReceiver);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(sensorFallReceiver);
	}

	/**
	 *  와치 센서 데이터 얻기
	 *  메소드 명을 get으로 했지만 추후 catch로 바뀔 수 있음
	 *  (와치에서 낙상의 경우에만 데이터를 보낼 듯)
	 */
	private void getWatchSensorData() {
		if(DEBUGLOG) Log.d("MainActivity", "getWatchSensorData()");

	}

	/**
	 *  안드로이드 센서 데이터 로그 저장
	 */
	private void setSensorAndLog(String logStr) {
		if(DEBUGLOG) Log.d("MainActivity", "setSensorAndLog()");

		/////////////////////// 내부저장소 설정 ///////////////////////
		// /data/user/0/[앱 패키지 명(com.sample.test)]/files/
		////// 디렉토리 설정
		String dirPath = getFilesDir() +"/";

		////// 파일 이름 설정
		// WTTApp_sensor_and_ + 현재날짜
		String TimeStamp = new SimpleDateFormat("yyMMdd").format(new Date());
		String FileName = "WTTApp_sensor_and_"+TimeStamp;
		String filePath = dirPath + FileName;

		////// 파일 삭제 설정
		// 7일 지난 파일
		int delFlagDay = 7;
		// 남은 용량 체크 파일 삭제 KB, MB, GB
		String delFlagFmt = "GB"; // 파일삭제 기준 용량 단위
		int delFlagOver = 1; // 파일삭제 기준 용량

		/////////////////////// 내부저장소 폴더 검색 ///////////////////////
		ArrayList fileList = searchItnDir(dirPath);

		/////////////////////// 내부저장소 파일 삭제 ///////////////////////
		// 기준 : 7일 지난 파일 or 특정 용량 차면 마지막 파일 삭제

		// 7일
		deleteItnFile(fileList, delFlagDay);

		// 용량
		// 사용가능 용량
		Long memory = getAvlIFSize();
		if (DEBUGLOG) Log.d("MainActivity", "memory : " + memory);

		if (memory >= 1024) { // KB
			if("KB".equals(delFlagFmt) && memory <= delFlagOver) deleteItnFile(fileList, fileList.size() - 1);
			if (memory >= 1024 / 1024) { // MB
				if("MB".equals(delFlagFmt) && memory <= delFlagOver) deleteItnFile(fileList, fileList.size() - 1);
				if (memory >= 1024 / 1024 / 1024) { // GB
					if("GB".equals(delFlagFmt) && memory <= delFlagOver) deleteItnFile(fileList, fileList.size() - 1);
				}
			}
		}

		//if (DEBUGLOG) Log.d("MainActivity", "getSize : " + getSizeStr);

		/////////////////////// 내부저장소 파일 쓰기 ///////////////////////
		writeItnFile(filePath, logStr);

		/////////////////////// 내부저장소 파일 읽기 ///////////////////////
		// 파일 쓸때는 끝에 추가 가능하지만 읽을때는 한줄씩
		readItnFile(filePath);

	}

	/**
	 * 내부 저장소(internal File) 파일 목록 검색
	 * @param dirPath
	 * @return fileList
	 */
	private ArrayList searchItnDir(String dirPath) {
		if(DEBUGLOG) Log.d("MainActivity", "writeItnFile()");
		File directory = new File(dirPath);

		File[] files = directory.listFiles();
		ArrayList fileList = new ArrayList();
		for (File file : files) {
			if (file.isFile()) {
				String str = (file+"").replaceAll(dirPath, "");
				fileList.add(str);
			} else if (file.isDirectory()) {
				//addFileToList(file);
			}
		}

		Collections.sort(fileList);

		if(DEBUGLOG) Log.d("MainActivity", "dirPath : "+dirPath);
		if(DEBUGLOG) Log.d("MainActivity", "listFile : "+fileList.toString());
		if(DEBUGLOG) Log.d("MainActivity", "listFile.size : "+fileList.size());

		return fileList;
	}

	/**
	 * 내부 저장소(internal File) 파일 목록 검색
	 * @param fileName
	 */
	private void deleteItnFile(ArrayList  fileList, int delFlagDay) {
		if(DEBUGLOG) Log.d("MainActivity", "deleteItnFile()");


		int listSize = fileList.size() - 1;
		for (int i = 0; i <= listSize - delFlagDay; i++) {
			//if (DEBUGLOG) Log.d("MainActivity", "fileList.get(" + i + ") : " + fileList.get(i));
			if(deleteFile(fileList.get(i).toString())) {
				if(DEBUGLOG) Log.d("MainActivity", "파일("+fileList.get(i).toString()+") 삭제 - 성공");
			} else {
				if(DEBUGLOG) Log.d("MainActivity", "파일("+fileList.get(i).toString()+") 삭제 - 실패");
			}
		}


	}

	/**
	 * 내부 저장소(internal File) 총 용량
	 * @return fmtMemSize()
	 */
	public Long getIFSize() {
		if(DEBUGLOG) Log.d("MainActivity", "getIFSize()");
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = 0;
		long totalBlocks = 0;
		if(Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
			blockSize = stat.getBlockSizeLong();
			totalBlocks = stat.getBlockCountLong();
		} else {
			blockSize = stat.getBlockSize();
			totalBlocks = stat.getBlockCount();
		}
		return  totalBlocks * blockSize;
	}

	/**
	 * 내부 저장소(internal File) 사용가능 용량
	 * @return availableBlocks * blockSize
	 */
	public Long getAvlIFSize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = 0;
		long availableBlocks = 0;
		if(Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
			blockSize = stat.getBlockSizeLong();
			availableBlocks = stat.getAvailableBlocksLong();
		} else {
			blockSize = stat.getBlockSize();
			availableBlocks = stat.getAvailableBlocks();
		}

		return availableBlocks * blockSize;
	}

	/**
	 * 내부 저장소(internal File) 파일 쓰기
	 * @param filePath
	 * @param writeTxt
	 */
	private void writeItnFile(String filePath, String writeTxt) {
		if(DEBUGLOG) Log.d("MainActivity", "writeItnFile()");
		try{
			// append : true - 이어쓰기, false - 덮어쓰기
			BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true));
			bw.write(writeTxt+"\n");
			bw.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 내부 저장소(internal File) 파일 읽기
	 * @param filePath
	 */
	private void readItnFile(String filePath) {
		if(DEBUGLOG) Log.d("MainActivity", "readItnFile()");
		try{
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			String readStr = "";
			String str = null;
			while(((str = br.readLine()) != null)){
				readStr += str +"\n";
			}
			br.close();
			if(DEBUGLOG) Log.d("MainActivity", "read file:"+readStr.substring(0, readStr.length()-1));
		}catch (FileNotFoundException e){
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
}
