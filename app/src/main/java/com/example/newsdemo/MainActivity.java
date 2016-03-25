package com.example.newsdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;


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
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    //String url = "http://open.twtstudio.com/api/v1/news/tmp/1?page=1";
    String url = "http://open.twtstudio.com/api/v1/news/1/page/1";
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    int NEWS_LIST_ID = 1;
    SwipeRefreshLayout mSwipeRefreshLayout;
    List<NewsBean> mNewsBeanList;
    RecyclerViewAdapter adapter;
    private boolean loading=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        InputStream inputStream=this.getResources().openRawResource(R.raw.twtnews);
//        String jsonStr=readStream(inputStream);
//        List<NewsBean> newslist=getJsonData(jsonStr);
//        System.out.println(newslist);
        mNewsBeanList=new ArrayList<>();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        assert recyclerView != null;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalcount=layoutManager.getItemCount();
                int lastvisableitem=layoutManager.findLastVisibleItemPosition();
                if(!loading&&totalcount<(lastvisableitem+2))
                {
                    if(NEWS_LIST_ID<5)
                    {NEWS_LIST_ID++;
                    new MyAsyncTask().execute(NEWS_LIST_ID);
                        mSwipeRefreshLayout.setRefreshing(true);
                    }else {

                        //Toast.makeText(MainActivity.this,"没有新闻了",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        mSwipeRefreshLayout= (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        assert mSwipeRefreshLayout != null;
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED,Color.BLUE,Color.YELLOW,Color.GREEN);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                new MyAsyncTask().execute(NEWS_LIST_ID);
            }
        });


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                NEWS_LIST_ID=1;
                new MyAsyncTask().execute(NEWS_LIST_ID);
            }
        });

        //new MyAsyncTask().execute(url);


    }

    class MyAsyncTask extends AsyncTask<Integer, Void, List<NewsBean>> {


        @Override
        protected List<NewsBean> doInBackground(Integer... params) {
            loading=true;
            String url = "http://open.twtstudio.com/api/v1/news/"+String.valueOf(params[0])+"/page/1";
            if(params[0]!=1)
            {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            List<NewsBean> newsBeanList = getJsonData(url);

            return newsBeanList;
        }

        @Override
        protected void onPostExecute(List<NewsBean> newsBeen) {
            super.onPostExecute(newsBeen);
            //System.out.println(newsBeen);
            //mNewsBeanList=newsBeen;
            mNewsBeanList.addAll(mNewsBeanList.size(),newsBeen);
            if(NEWS_LIST_ID==1)
            {adapter = new RecyclerViewAdapter(mNewsBeanList, MainActivity.this);
            recyclerView.setAdapter(adapter);}
            else
            {adapter.notifyDataSetChanged();}
            loading=false;
            mSwipeRefreshLayout.setRefreshing(false);
        }
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

    private List<NewsBean> getJsonData(String url) {
        String jsonString = null;
        try {
            jsonString = readStream(new URL(url).openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<NewsBean> newsBeanList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            NewsBean newsBean;
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                newsBean = new NewsBean();
                newsBean.index = jsonObject.getInt("index");
                newsBean.subject = jsonObject.getString("subject");
                newsBean.picUrl = jsonObject.getString("pic");
                newsBean.visitCount = jsonObject.getInt("visitcount");
                newsBean.comments = jsonObject.getInt("comments");
                newsBean.summary = jsonObject.getString("summary");
                newsBeanList.add(newsBean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newsBeanList;
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
        return bitmap;
    }

}
