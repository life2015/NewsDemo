package com.example.newsdemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.thefinestartist.finestwebview.FinestWebView;
import com.thefinestartist.finestwebview.FinestWebViewActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 冀辰阳 on 2016/3/9.
 */
public class RecyclerViewAdapterDemo extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<NewsBean> newses = new ArrayList<>();
    private Context context;
    public final static int TYPE_FOOTER = 2;//底部--往往是loading_more
    public final static int TYPE_NORMAL = 1; // 正常的一条文章

    public RecyclerViewAdapterDemo(List<NewsBean> newses, Context context) {
        this.newses = newses;
        this.context = context;
    }

    //自定义ViewHolder类
    static class NewsViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        ImageView news_photo;
        TextView news_title;
        TextView news_desc;
        TextView visit_count;
        TextView comments_count;
        //Button readMore;

        public NewsViewHolder(final View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            news_photo = (ImageView) itemView.findViewById(R.id.news_photo);
            news_title = (TextView) itemView.findViewById(R.id.news_title);
            news_desc = (TextView) itemView.findViewById(R.id.news_desc);
            visit_count = (TextView) itemView.findViewById(R.id.visitcount);
            //readMore = (Button) itemView.findViewById(R.id.btn_more);
            //设置TextView背景为半透明
            comments_count = (TextView) itemView.findViewById(R.id.comments_count);
            news_title.setBackgroundColor(Color.argb(20, 0, 0, 0));

        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public FooterViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View view;
        switch (viewType) {
            case TYPE_NORMAL:
                view = LayoutInflater.from(context).inflate(R.layout.news_item, parent, false);
                viewHolder = new NewsViewHolder(view);
                return viewHolder;
            case TYPE_FOOTER:
                view = LayoutInflater.from(context).inflate(R.layout.footer, parent, false);
                viewHolder = new FooterViewHolder(view);
                return viewHolder;
        }
        return viewHolder;
    }


    @Override
    public int getItemViewType(int position) {

        if (position + 1 >= getItemCount()) {
            Log.d("gg", "print Footer");
            return TYPE_FOOTER;
        } else {
            return TYPE_NORMAL;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final int j = position;
        final List<Integer> integerList = new ArrayList<>();

        if (getItemViewType(position) == TYPE_NORMAL) {

            final NewsViewHolder personViewHolder = (NewsViewHolder) holder;
            personViewHolder.news_title.setText(newses.get(j).getSubject());
            personViewHolder.news_desc.setText(newses.get(j).getSummary());
            personViewHolder.visit_count.setText("阅读量:" + String.valueOf(newses.get(j).getVisitCount()));
            personViewHolder.comments_count.setText("评论数:" + String.valueOf(newses.get(j).getComments()));
            Glide.with(context).load(newses.get(j).getPic()).asBitmap().error(R.drawable.error_no_photo).into(new BitmapImageViewTarget(personViewHolder.news_photo) {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    super.onResourceReady(resource, glideAnimation);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                                                            @Override
                                                            public void onGenerated(Palette palette) {
                                                                int vibrant = palette.getLightMutedColor(0x000000);
                                                                integerList.add(vibrant);
                                                                personViewHolder.cardView.setBackgroundColor(vibrant);
                                                            }
                                                        }
                        );
                    }
                }
            });

            //为btn_share btn_readMore cardView设置点击事件
            personViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(context, NewsContentActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putString("index", String.valueOf(newses.get(j).getIndex()));
//                    bundle.putString("bitmap_url", newses.get(j).getPic());
//                    intent.putExtras(bundle);
//                    context.startActivity(intent);

                    if (integerList.size() == 1) {
                        int color = integerList.get(0);
                        new FinestWebView.Builder(context).titleDefault("加载中...")
                                .toolbarScrollFlags(0)
                                .statusBarColor(color)
                                .toolbarColor(color)
                                .show("https://news.twt.edu.cn/?c=default&a=pernews&id=" + String.valueOf(newses.get(j).getIndex()));

                    } else {
                        new FinestWebView.Builder(context).titleDefault("加载中...")
                                .toolbarScrollFlags(0)
                                .show("https://news.twt.edu.cn/?c=default&a=pernews&id=" + String.valueOf(newses.get(j).getIndex()));

                    }
                }
            });


        }

    }

    @Override
    public int getItemCount() {
        return newses.size();
    }
}
