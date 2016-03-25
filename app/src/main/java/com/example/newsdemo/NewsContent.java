package com.example.newsdemo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;

public class NewsContent extends AppCompatActivity {
    WebView webView;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_content_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        //获取得到的index数据，拼接成为URL
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        String index = bundle.getString("index");
        String contenturl = "http://open.twtstudio.com/api/v1/news/" + index;
        webView = (WebView) findViewById(R.id.webView);
        System.out.println(contenturl);
        //执行asynctask
        new DescAsyncTask().execute(contenturl);

    }

    class DescAsyncTask extends AsyncTask<String, Void, ContentBean> {
        @Override
        protected ContentBean doInBackground(String... params) {
            return getJsonContent(params[0]);
        }

        @Override
        protected void onPostExecute(ContentBean contentBean) {
            super.onPostExecute(contentBean);
            System.out.println(contentBean.subject);
            toolbar.setTitle(contentBean.subject);
            webView.loadData(contentBean.content, "text/html;charset=utf-8", null);

        }
    }

    //解析intent传来index对应URL的json
    private ContentBean getJsonContent(String url) {
        String jsonString = null;
        try {
            jsonString = readStream(new URL(url).openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        ContentBean contentBean = new ContentBean();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            jsonObject = jsonObject.getJSONObject("data");
            contentBean.subject = jsonObject.getString("subject");
            contentBean.content = jsonObject.getString("content");
            contentBean.newscome = jsonObject.getString("newscome");
            contentBean.gonggao = jsonObject.getString("gonggao");
            contentBean.shenggao = jsonObject.getString("shenggao");
            contentBean.sheying = jsonObject.getString("sheying");
            contentBean.visitcount = jsonObject.getInt("visitcount");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return contentBean;
    }

    public String readStream(InputStream is) {
        InputStreamReader isr;
        String result = " ";
        try {
            String line = " ";
            isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                result += line;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
