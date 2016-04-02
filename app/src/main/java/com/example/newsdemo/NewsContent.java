package com.example.newsdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;

public class NewsContent extends AppCompatActivity {
    WebView webView;
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbar;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_details);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT)
        {//透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);}
        toolbar = (Toolbar) findViewById(R.id.toolbar_news);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setSupportActionBar(toolbar);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        imageView= (ImageView) findViewById(R.id.image_news);
        //获取得到的index数据，拼接成为URL
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        String index = bundle.getString("index");
        String BitmapUrl=bundle.getString("bitmap_url");
        String contenturl = "http://open.twtstudio.com/api/v1/news/" + index;
        webView = (WebView) findViewById(R.id.webView_news);
        System.out.println(BitmapUrl);
        //执行asynctask
        new DescAsyncTask().execute(contenturl,BitmapUrl);

    }

    class DescAsyncTask extends AsyncTask<String, Void, ContentBean> {
        @Override
        protected ContentBean doInBackground(String... params) {
            ContentBean contentBean= getJsonContent(params[0]);
            contentBean.bitmap=getImageFromUrl(params[1]);
            return contentBean;
        }

        @Override
        protected void onPostExecute(ContentBean contentBean) {
            super.onPostExecute(contentBean);
            System.out.println(contentBean.subject);
            toolbar.setTitle(contentBean.subject);
            collapsingToolbar.setTitle(contentBean.subject);
            imageView.setImageBitmap(contentBean.bitmap);
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
    private Bitmap getImageFromUrl(String url) {

        Bitmap bitmap = null;
        try {
            URL mUrl = new URL(url);
            URLConnection connection = mUrl.openConnection();
            InputStream inputStream = connection.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            bitmap = BitmapFactory.decodeStream(bis);
            inputStream.close();
            bis.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("jcy","加载bitmap");
        return bitmap;
    }
}
