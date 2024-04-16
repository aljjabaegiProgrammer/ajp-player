package com.aljjabaegi.player.util.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 * URL 커넥션 처리 용 Thread class
 * @author GEON LEE
 * @since 2024-01-08
 *
 * */
public class URLConnection implements Runnable {
    private final Logger LOGGER = LoggerFactory.getLogger(URLConnection.class);
    private int resultCode;
    private String url;
    private int timeout = 3000;
    private String result;
    private String auth;
    private HttpURLConnection uc;
    public URLConnection auth(String auth) {
        this.auth = auth;
        return this;
    }
    public int getResultCode() {
        return this.resultCode;
    }
    private void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }
    public String getResult() {
        return this.result;
    }
    private void setResult(String result) {
        this.result = result;
    }
    public URLConnection timeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public URLConnection url(String url) {
        this.url = url;
        return this;
    }

    @Override
    public void run() {
        BufferedReader br = null;
        try {
            URL url = new URL(this.url);
            this.uc = (HttpURLConnection) url.openConnection();
            this.uc.setConnectTimeout(this.timeout);
            if(this.auth != null) {
                this.uc.setRequestProperty("Authorization", this.auth);
            }
            this.uc.connect();
            setResultCode(uc.getResponseCode());
            if(getResultCode() == 200) {
                String line = "";
                StringBuilder response = new StringBuilder();
                br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                this.setResult(response.toString());
            }
        } catch (IOException e) {
            LOGGER.error("URL 연결에 실패 하였습니다. url : {}, {}", this.url, e.getMessage());
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
