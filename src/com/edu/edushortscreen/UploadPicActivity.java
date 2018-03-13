package com.edu.edushortscreen;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class UploadPicActivity extends Activity {
	private TextView mtv1;
	private TextView mtv2;
	private Button bupload;
	private TextView mtv3;
	private Button bupload1;
	private String uploadFile = Environment.getExternalStorageDirectory().getPath() + File.separator + "shortScreen.png";
	private String actionUrl = "http://192.168.1.172/adzhbp/index.php/test/index/e0b04a2162685039c4ecb52201e87d35";
	private String actionUrl2 = "http://117.78.33.90/teach_portal/index.php/shortscreen/index/e0b04a2162685039c4ecb52201e87d35";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mtv1 = (TextView) findViewById(R.id.mtv1);
		mtv1.setText("文件路径：\n" + uploadFile);
		mtv2 = (TextView) findViewById(R.id.mtv2);
		mtv2.setText("上传地址：\n" + actionUrl);
		bupload = (Button) findViewById(R.id.bupload);
		bupload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FileUploadTask fileuploadtask = new FileUploadTask();

				fileuploadtask.execute();
			}
		});

		mtv3 = (TextView) findViewById(R.id.mtv3);
		mtv3.setText("上传地址：\n" + actionUrl2);
		bupload1 = (Button) findViewById(R.id.bupload1);
		bupload1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FileUploadTask1 fileuploadtask = new FileUploadTask1();

				fileuploadtask.execute();
			}
		});
	}

	class FileUploadTask extends AsyncTask<Object, Integer, Void> {

		private ProgressDialog dialog = null;

		HttpURLConnection connection = null;

		DataOutputStream outputStream = null;

		DataInputStream inputStream = null;

		String pathToOurFile = Environment.getExternalStorageDirectory().getPath() + File.separator + "shortScreen.png";;

		String urlServer = actionUrl;

		String lineEnd = "\r\n";

		String twoHyphens = "--";

		String boundary = "*****";

		File uploadFile = new File(pathToOurFile);

		long totalSize = uploadFile.length();

		@Override

		protected void onPreExecute() {

			dialog = new ProgressDialog(UploadPicActivity.this);

			dialog.setMessage("正在上传...");

			dialog.setIndeterminate(false);

			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

			dialog.setProgress(0);

			dialog.show();

		}

		@Override
		protected Void doInBackground(Object... arg0) {

			long length = 0;

			int progress;

			int bytesRead, bytesAvailable, bufferSize;

			byte[] buffer;

			int maxBufferSize = 256 * 1024;// 256KB

			try {

				FileInputStream fileInputStream = new FileInputStream(new File(

				pathToOurFile));

				URL url = new URL(urlServer);

				connection = (HttpURLConnection) url.openConnection();

				connection.setChunkedStreamingMode(256 * 1024);// 256KB

				connection.setDoInput(true);

				connection.setDoOutput(true);

				connection.setUseCaches(false);

				connection.setRequestMethod("POST");

				connection.setRequestProperty("Connection", "Keep-Alive");

				connection.setRequestProperty("Charset", "UTF-8");

				connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

				outputStream = new DataOutputStream(

				connection.getOutputStream());

				outputStream.writeBytes(twoHyphens + boundary + lineEnd);

				outputStream.writeBytes("Content-Disposition: form-data;name=\"uploadedfile\";filename=\"" + pathToOurFile + "\"" + lineEnd);

				outputStream.writeBytes(lineEnd);

				bytesAvailable = fileInputStream.available();

				bufferSize = Math.min(bytesAvailable, maxBufferSize);

				buffer = new byte[bufferSize];

				bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				while (bytesRead > 0) {

					outputStream.write(buffer, 0, bufferSize);

					length += bufferSize;
					progress = (int) ((length * 100) / totalSize);

					publishProgress(progress);

					bytesAvailable = fileInputStream.available();

					bufferSize = Math.min(bytesAvailable, maxBufferSize);

					bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				}

				outputStream.writeBytes(lineEnd);

				outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

				publishProgress(100);

				fileInputStream.close();

				outputStream.flush();

				outputStream.close();

			} catch (Exception ex) {

			}

			return null;

		}

		@Override

		protected void onProgressUpdate(Integer... progress) {

			dialog.setProgress(progress[0]);

		}

		@Override

		protected void onPostExecute(Void result) {

			try {

				dialog.dismiss();

			} catch (Exception e) {

			}

		}

	}

	class FileUploadTask1 extends AsyncTask<Object, Integer, Void> {

		private ProgressDialog dialog = null;

		HttpURLConnection connection = null;

		DataOutputStream outputStream = null;

		DataInputStream inputStream = null;

		String pathToOurFile = Environment.getExternalStorageDirectory().getPath() + File.separator + "shortScreen.png";;

		String urlServer = actionUrl2;

		String lineEnd = "\r\n";

		String twoHyphens = "--";

		String boundary = "*****";

		File uploadFile = new File(pathToOurFile);

		long totalSize = uploadFile.length();

		@Override

		protected void onPreExecute() {

			dialog = new ProgressDialog(UploadPicActivity.this);

			dialog.setMessage("正在上传...");

			dialog.setIndeterminate(true);

			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

			dialog.setProgress(0);

			dialog.show();

		}

		@Override
		protected Void doInBackground(Object... arg0) {

			long length = 0;

			int progress;

			int bytesRead, bytesAvailable, bufferSize;

			byte[] buffer;

			int maxBufferSize = 256 * 1024;// 256KB

			try {

				FileInputStream fileInputStream = new FileInputStream(new File(

				pathToOurFile));

				URL url = new URL(urlServer);

				connection = (HttpURLConnection) url.openConnection();

				connection.setChunkedStreamingMode(256 * 1024);// 256KB

				connection.setDoInput(true);

				connection.setDoOutput(true);

				connection.setUseCaches(false);

				connection.setRequestMethod("POST");

				connection.setRequestProperty("Connection", "Keep-Alive");

				connection.setRequestProperty("Charset", "UTF-8");

				connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

				outputStream = new DataOutputStream(

				connection.getOutputStream());

				outputStream.writeBytes(twoHyphens + boundary + lineEnd);

				outputStream.writeBytes("Content-Disposition: form-data;name=\"uploadedfile\";filename=\"" + pathToOurFile + "\"" + lineEnd);

				outputStream.writeBytes(lineEnd);

				bytesAvailable = fileInputStream.available();

				bufferSize = Math.min(bytesAvailable, maxBufferSize);

				buffer = new byte[bufferSize];

				bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				while (bytesRead > 0) {

					outputStream.write(buffer, 0, bufferSize);

					length += bufferSize;
					progress = (int) ((length * 100) / totalSize);

					publishProgress(progress);

					bytesAvailable = fileInputStream.available();

					bufferSize = Math.min(bytesAvailable, maxBufferSize);

					bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				}

				outputStream.writeBytes(lineEnd);

				outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

				publishProgress(100);

				fileInputStream.close();

				outputStream.flush();

				outputStream.close();

			} catch (Exception ex) {

			}

			return null;

		}

		@Override

		protected void onProgressUpdate(Integer... progress) {

			dialog.setProgress(progress[0]);

		}

		@Override

		protected void onPostExecute(Void result) {

			try {

				dialog.dismiss();

			} catch (Exception e) {

			}

		}

	}
}