package com.example.newsdemo;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jcy on 16/4/2.
 */
public class MyFragment5 extends android.support.v4.app.Fragment {

    //type:5视点观察
    String url = "http://open.twtstudio.com/api/v1/news/5/page/1";
    private RecyclerView recyclerView;
    int NEWS_LIST_ID = 1;
    //i为吐司辅助
    int i=1;
    SwipeRefreshLayout mSwipeRefreshLayout;
    List<NewsBean> mNewsBeanList = new ArrayList<>();
    RecyclerViewAdapterDemo adapter;
    private boolean loading=false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_main,container,false);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
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
                    NEWS_LIST_ID++;
                    new MyAsyncTask().execute(NEWS_LIST_ID);
                    Log.d("gg","触发");
                }
            }
        });
        mSwipeRefreshLayout= (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        assert mSwipeRefreshLayout != null;
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED,Color.BLUE,Color.YELLOW,Color.GREEN);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                NEWS_LIST_ID=1;
                adapter = new RecyclerViewAdapterDemo(mNewsBeanList, getActivity());
                recyclerView.setAdapter(adapter);
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

        //new MyAsyncTask().execute(url);
        return view;
    }

    class MyAsyncTask extends AsyncTask<Integer, Void, List<NewsBean>> {

        @Override
        protected List<NewsBean> doInBackground(Integer... params) {
            loading=true;
            String url = "http://open.twtstudio.com/api/v1/news/5/page/"+String.valueOf(params[0]);
            List<NewsBean> newsBeanList = getJsonData(url);
            return newsBeanList;
        }

        @Override
        protected void onPostExecute(List<NewsBean> newsBeen) {
            super.onPostExecute(newsBeen);
            //System.out.println(newsBeen);
            //mNewsBeanList=newsBeen;

            mNewsBeanList.addAll(mNewsBeanList.size(),newsBeen);

            adapter.notifyDataSetChanged();
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
            Toast.makeText(getActivity(),"网络出现问题，应用自动关闭",Toast.LENGTH_LONG).show();

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


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
    }
}
