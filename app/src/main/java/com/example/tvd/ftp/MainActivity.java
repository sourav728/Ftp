package com.example.tvd.ftp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;

public class MainActivity extends AppCompatActivity {
    static final String FTP_HOST = "45.114.246.216";
    static final String FTP_USER = "TVDDEMO1";
    static final String FTP_PASS = "123123";
    static final int FTP_PORT = 21;
    OutputStream outputStream = null;
    String fileName;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File dir = new File(Environment.getExternalStorageDirectory() + "/oc");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        ReadValues readValues = new ReadValues();
        readValues.execute();
    }

    public class ReadValues extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("Downloading");
            progressDialog.setIcon(R.drawable.ic_file_download_black_24dp);
            progressDialog.setMessage("File is downloading please wait..");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            FTPClient ftpClient = new FTPClient();
            try {

                ftpClient.connect(FTP_HOST, FTP_PORT);
                Log.d("connected", "connected successfully");
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                ftpClient.login(FTP_USER, FTP_PASS);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                ftpClient.changeWorkingDirectory("/sourav");
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                FTPFile[] ftpFiles = ftpClient.listFiles("/sourav");
                int length = ftpFiles.length;
                for (int i = 0; i < length; i++) {
                    fileName = ftpFiles[i].getName();
                    outputStream = new BufferedOutputStream(new FileOutputStream(getPathh() + "/" + fileName));
                    ftpClient.retrieveFile(fileName, outputStream);
                    Log.d("debug", "Filename" + fileName);
                    Log.d("debug", fileName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            /*try {
                 outputStream = new BufferedOutputStream(new FileOutputStream(getPathh() + "/" + fileName));
                    ftpClient.retrieveFile(fileName, outputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }*/ finally {
                if (ftpClient != null) {
                    try {
                        ftpClient.logout();
                        ftpClient.disconnect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (progressDialog.isShowing())
            {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "File Downloaded Successfully..", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }
    }

    public String getPathh() {
        File dir = new File(Environment.getExternalStorageDirectory() + "/oc");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir.toString();
    }
}
