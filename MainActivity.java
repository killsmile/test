package com.bluetooth.bluetoothdn;

import java.util.Arrays;
import java.util.Iterator;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bluetooth.entity.ResultModel;
import com.bluetooth.impl.IDImpl;
import com.bluetooth.inter.ID;
import com.example.sdtverify.sdtVerify;
import com.fri.bluetoothdn.BluetoothApi;

public class MainActivity extends Activity {

	protected static final String TAG = "BLUETOOTH";
	private final boolean DEBUG = true;
	private BluetoothApi bt;
	// private BluetoothConnect btc;
	private TextView view;
	private TextView tv_content;
	boolean flag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		String packageName = this.getPackageName();
		sdtVerify.setEnvPath(packageName);

		bt = new BluetoothApi();
		// btc = new BluetoothConnect();

		Button mbtnConnect = (Button) this.findViewById(R.id.btnConnect);
		Button mbtnRead = (Button) this.findViewById(R.id.btnRead);
		Button mbtnDisconnect = (Button) this.findViewById(R.id.btnDisconnect);

		view = (TextView) this.findViewById(R.id.textView1);

		tv_content = (TextView) findViewById(R.id.tv_content);
		tv_content.setMovementMethod(ScrollingMovementMethod.getInstance());

		// �����豸
		mbtnConnect.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (bt.btIsConnected()) {
					view.setText("\n已连接蓝牙设备");
					return;
				}

				// ȡ���Ѱ�ģ����ΪSPP-CA���豸
				BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
						.getDefaultAdapter();
				Iterator<BluetoothDevice> list = mBluetoothAdapter
						.getBondedDevices().iterator();
				String address = "";// "00:BA:55:56:E5:C7";
				while (list.hasNext()) {
					BluetoothDevice bd = list.next();
					// if("SPP-CA".equalsIgnoreCase(bd.getName())){
					address = bd.getAddress();// ������Ե������豸
					Log.d(TAG, address);
					break;
					// }
				}

				view.setText("\n连接 -> " + address);

				try {
					flag = bt.btConnect(address);
					if (flag) {
						view.setText("\n连接成功");
					} else {
						view.setText("\n连接失败");
					}
				} catch (Exception e) {
					e.printStackTrace();
					view.setText("\n" + e.getLocalizedMessage());
				}
			}
		});
		// ����
		mbtnRead.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (flag) {
					byte[] SJ = { 65, 52, 48, 65, 49, 52, 55, 65, 68, 56, 55,
							67, 65, 51, 68, 65, 0, 43, 48, 69, 2, 33, 0, -5,
							-82, -2, 37, -53, 125, 44, -2, -60, -30, -6, 59,
							-96, -20, -123, -37, 102, 126, -81, 20, -80, 102,
							-19, 57, 78, 27, 55, -78, 110, 47, -24, -117, 2,
							32, 98, -3, -116, 101, 22, 22, -7, -61, -115, 80,
							-1, 109, 22, 10, 68, -43, 62, 68, 6, -69, -36, 73,
							-63, -27, -15, -22, 16, -123, 110, 1, -84, -89 };
					short SJChangDu = (short) SJ.length;
					ID id = new IDImpl();

					ResultModel resultModel = id.ID_YanZheng(bt, SJChangDu, SJ,
							1);
					tv_content.setText("DN数据:" + resultModel.getWZName() + "\n"
							+ "返回结果:" + resultModel.getStatus() + "\n"
							+ "副本路经:" + resultModel.getWZPath() + "\n"
							+ "ID验证数据："
							+ Arrays.toString(resultModel.getIDData()) + "\n");
					switch (resultModel.getStatus()) {
					case 0:
						Toast.makeText(MainActivity.this, "读取数据", 0).show();
						break;
					case 1:
						Toast.makeText(MainActivity.this, "请放卡或重新放卡", 0).show();
						break;
					case 2:
						Toast.makeText(MainActivity.this, "未找到网上副本", 0).show();
						break;
					case 3:
						Toast.makeText(MainActivity.this, "验签失败", 0).show();
						break;
					}

				}
			}
		});

		// �Ͽ�
		mbtnDisconnect.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (bt != null && bt.btIsConnected()) {
					bt.btDisconnect();
				}
				view.setText("\n断开连接");
			}
		});
	}

	private String byteToString(byte[] ba) {
		String s;
		s = "";
		for (int i = 0; i < ba.length; i++) {
			s += String.format("%02x ", ba[i]);
		}
		return s;
	}

}
