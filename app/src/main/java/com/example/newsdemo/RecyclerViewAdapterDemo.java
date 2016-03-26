package com.example.newsdemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.List;

/**
 * Created by 冀辰阳 on 2016/3/9.
 */
public class RecyclerViewAdapterDemo extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<NewsBean> newses;
    private Context context;
    public final static int TYPE_FOOTER = 2;//底部--往往是loading_more
    public final static int TYPE_NORMAL = 1; // 正常的一条文章
    private LayoutInflater mLayoutInflater;

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
        Button share;
        Button readMore;

        public NewsViewHolder(final View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            news_photo = (ImageView) itemView.findViewById(R.id.news_photo);
            news_title = (TextView) itemView.findViewById(R.id.news_title);
            news_desc = (TextView) itemView.findViewById(R.id.news_desc);
            share = (Button) itemView.findViewById(R.id.btn_share);
            readMore = (Button) itemView.findViewById(R.id.btn_more);
            //设置TextView背景为半透明
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

    //    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
//        View v = LayoutInflater.from(context).inflate(R.layout.news_item, viewGroup, false);
//        NewsViewHolder nvh = new NewsViewHolder(v);
//        return nvh;
//    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder=null;
        View view;
        switch (viewType)
        {
            case TYPE_NORMAL:
                view=mLayoutInflater.from(context).inflate(R.layout.news_item,parent,false);
                viewHolder=new NewsViewHolder(view);
                return viewHolder;
            case TYPE_FOOTER:
                view=mLayoutInflater.from(context).inflate(R.layout.footer,parent,false );
                viewHolder=new FooterViewHolder(view);
                return viewHolder;

        }
        return null;
    }


    @Override
    public int getItemViewType(int position) {
        NewsBean bean = newses.get(position);
        if (bean == null) {
            return TYPE_FOOTER;
        } else {
            return TYPE_NORMAL;
        }
        //return TYPE_NORMAL;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int i) {
        final int j = i;
        //这时候 article是 null，先把 footer 处理了
        if (holder instanceof FooterViewHolder) {
            Log.d("gg","真的出现了");
            //((FooterViewHolder) personViewHolder).rcvLoadMore.spin();
            return;
        }
        if (holder instanceof NewsViewHolder) {

            final NewsViewHolder personViewHolder=(NewsViewHolder)holder;
            personViewHolder.news_title.setText(newses.get(j).getSubject());
            personViewHolder.news_desc.setText(newses.get(j).getSummary());
            Glide.with(context).load(newses.get(j).getPicUrl()).asBitmap().error(R.drawable.error_no_photo).into(new BitmapImageViewTarget(personViewHolder.news_photo) {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    super.onResourceReady(resource, glideAnimation);
                    Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                                                        @Override
                                                        public void onGenerated(Palette palette) {
                                                            int vibrant = palette.getLightMutedColor(0x000000);
                                                            personViewHolder.cardView.setBackgroundColor(vibrant);
                                                        }
                                                    }
                    );
                }
            });

            personViewHolder.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
                    intent.putExtra(Intent.EXTRA_TEXT, newses.get(j).getSummary());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(Intent.createChooser(intent, newses.get(j).getSubject()));
                }
            });

            //为btn_share btn_readMore cardView设置点击事件
            personViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, NewsContent.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("index", String.valueOf(newses.get(j).getIndex()));
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });

            personViewHolder.readMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, NewsContent.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("index", String.valueOf(newses.get(j).getIndex()));
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return newses.size();
    }
}
