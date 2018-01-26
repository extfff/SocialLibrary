package com.vendor.social.support.wxapi;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * 网络请求
 * Created by ljfan on 16/9/2.
 */
class HttpManager {

    private static final int SC_OK = 200;

    private static final String CHARSET = "UTF-8";
    private static final String ERROR_OTHER = "出现错误了";

    public final void request(int requestCode, String rawPath, OnHttpListener l){
        new HttpTask().execute(requestCode, rawPath, l);
    }

    /**
     * 网络异步请求task<br>
     * 适应基本网络数据请求 基本流文件下载请求
     */
    private class HttpTask extends AsyncTask<Object, Object, Response> {

        @Override
        protected Response doInBackground(Object... params) {
            Response response = new Response();
            response.requestCode = (int)params[0];
            response.httpListener = (OnHttpListener) params[2];

            try {
                HttpURLConnection conn = null;
                InputStream in = null;

                try {
                    //创建连接
                    URL url = new URL(params[1].toString());
                    if (url.getProtocol().toLowerCase().equals("https")) {
                        SSLContext sc = SSLContext.getInstance("TLS");
                        sc.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());
                        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                        HttpsURLConnection.setDefaultHostnameVerifier(new MyHostnameVerifier());
                        conn = (HttpsURLConnection)url.openConnection();
                    } else {
                        conn = (HttpURLConnection)(url.openConnection());
                    }

                    conn.setDoOutput(true);  //如果通过post提交数据，必须设置允许对外输出数据
                    conn.setDoInput(true);
                    conn.setRequestMethod("GET");
                    conn.setUseCaches(false);
                    conn.setInstanceFollowRedirects(true);

                    conn.setRequestProperty("connection", "Keep-Alive");
                    conn.connect();

                    response.responseCode = conn.getResponseCode();

                    String encoding = conn.getContentEncoding();
                    //gzip请求头必须支持 "Accept-Encoding", "gzip";

                    if (response.responseCode == SC_OK) {
                        in = new BufferedInputStream(conn.getInputStream());

                        if("gzip".equals(encoding)) {    //如果支持gzip压缩
                            java.util.zip.GZIPInputStream gzin = new java.util.zip.GZIPInputStream(in);
                            response.data = readInStream(gzin, CHARSET);
                        } else {
                            response.data = readInStream(in, CHARSET);
                        }
                    } else {
                        in = new BufferedInputStream(conn.getErrorStream());

                        if("gzip".equals(encoding)) {    //如果支持gzip压缩
                            java.util.zip.GZIPInputStream gzin = new java.util.zip.GZIPInputStream(in);
                            response.errorMsg = conn.getResponseMessage() + ": " + readInStream(gzin, CHARSET);
                        } else {
                            response.errorMsg = conn.getResponseMessage() + ": " + readInStream(in, CHARSET);
                        }
                    }

                    in.close();
                } catch (IOException e){
                    if(in != null){
                        in.close();
                    }
                    throw e;
                }finally {
                    if(conn != null){
                        conn.disconnect();  //断开连接
                    }
                }
            } catch (Exception e) {
                response.errorMsg = ERROR_OTHER;
            }finally {
                //如果发生了异常情况 那么就进行此操作
                if(response.responseCode != SC_OK){
                    response.errorMsg = ERROR_OTHER;
                }
            }

            return response;
        }

        @Override
        protected void onPostExecute(Response response) {
            response.httpListener.onResponse(response);
        }
    }

    private static class MyTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

    private static class MyHostnameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    /**
     * 将输入流转换成字符串
     * @param is 流
     * @param charset 字符编码
     * @return data
     * @throws IOException 异常
     */
    private static String readInStream(InputStream is, String charset) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len;
        while ((len = is.read(buf)) != -1) {
            baos.write(buf, 0, len);
        }
        return new String(baos.toByteArray(), charset);
    }
}
