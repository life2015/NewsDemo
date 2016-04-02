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
import android.util.Log;
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
    int NEWS_LIST_ID = 1;
    //i为吐司辅助
    int i=1;
    SwipeRefreshLayout mSwipeRefreshLayout;
    List<NewsBean> mNewsBeanList = new ArrayList<>();
    RecyclerViewAdapterDemo adapter;
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
                if(!loading&&totalcount<(lastvisableitem)+2)
                {
                    if(NEWS_LIST_ID<5)
                    {NEWS_LIST_ID++;
                    new MyAsyncTask().execute(NEWS_LIST_ID);
                        Log.d("gg","触发");
                        //mSwipeRefreshLayout.setRefreshing(true);

                    }else {
                        //i为吐司辅助变量
                        if(i==1)
                        {Toast.makeText(MainActivity.this,"没有新闻啦",Toast.LENGTH_SHORT).show();}
                        i=2;
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
                mNewsBeanList.clear();
                new MyAsyncTask().execute(NEWS_LIST_ID);

                //i为吐司辅助
                int i=1;
            }
        });

        adapter = new RecyclerViewAdapterDemo(mNewsBeanList, MainActivity.this);
        recyclerView.setAdapter(adapter);

        //new MyAsyncTask().execute(url);


    }

    class MyAsyncTask extends AsyncTask<Integer, Void, List<NewsBean>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(NEWS_LIST_ID!=1)
            {
                NewsBean newsBean=new NewsBean();
                newsBean.TYPE=2;
                mNewsBeanList.add(newsBean);
                adapter.notifyItemChanged(mNewsBeanList.size()-1);
//                adapter.notifyDataSetChanged();
                Log.d("gg","出现");
            }

        }

        @Override
        protected List<NewsBean> doInBackground(Integer... params) {
            loading=true;
            String url = "http://open.twtstudio.com/api/v1/news/1/page/"+String.valueOf(params[0]);
            List<NewsBean> newsBeanList = getJsonData(url);
            return newsBeanList;
        }

        @Override
        protected void onPostExecute(List<NewsBean> newsBeen) {
            super.onPostExecute(newsBeen);
            //System.out.println(newsBeen);
            //mNewsBeanList=newsBeen;
            if(NEWS_LIST_ID!=1)
            {
                mNewsBeanList.remove(mNewsBeanList.size()-1);
                Log.d("gg","移除项");
            }
            mNewsBeanList.addAll(mNewsBeanList.size(),newsBeen);
            if(NEWS_LIST_ID==1)
            {adapter = new RecyclerViewAdapterDemo(mNewsBeanList, MainActivity.this);
            recyclerView.setAdapter(adapter);}
            else
            {adapter.notifyDataSetChanged();
            Log.d("gg","改变适配器");}
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
                newsBean.TYPE=1;
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

}
