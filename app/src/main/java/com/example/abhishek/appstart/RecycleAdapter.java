package com.example.abhishek.appstart;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

/**
 * Created by abhishek on 20/4/16.
 */
public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {

    ArrayList<HotOffers> cont = new ArrayList<>();
    Context context;
    int lastPosition = -1;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();


    public RecycleAdapter(Context context, ArrayList<HotOffers> cont) {
        this.cont = cont;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
        }
        public void clearAnimation()
        {
            mView.clearAnimation();
        }
    }

    @Override
    public RecycleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardlayout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        NetworkImageView im2 = (NetworkImageView) holder.mView.findViewById(R.id.logo);
        TextView tx1 = (TextView) holder.mView.findViewById(R.id.txt1);
        TextView tx2 = (TextView) holder.mView.findViewById(R.id.txt2);
        TextView tx3 = (TextView) holder.mView.findViewById(R.id.txt3);
        tx1.setText(cont.get(position).cashbackTitle);
        tx2.setText(cont.get(position).offerName);
        tx3.setText(cont.get(position).OfferCashback);
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        im2.setImageUrl(cont.get(position).storeImg, imageLoader);
        setAnimation(holder.mView, position);
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        AnimationSet animationSet = new AnimationSet(context, null);
        int y = viewToAnimate.getHeight()/2;
        int x = viewToAnimate.getWidth()/2;
        animationSet.addAnimation(new AlphaAnimation(0f, 1f));
        //animationSet.addAnimation(new ScaleAnimation(1,1,0.5f,1,x,y));
        animationSet.addAnimation(new TranslateAnimation(0,0,y,0));
        animationSet.setDuration(context.getResources().getInteger(android.R.integer.config_longAnimTime));
        if (position > lastPosition) {
            //Animation animation = AnimationUtils.loadAnimation(context, R.anim.scale);
            viewToAnimate.startAnimation(animationSet);
            lastPosition = position;
        }
    }


    @Override
    public int getItemCount() {
        return cont.size();
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        ((RecycleAdapter.ViewHolder)holder).clearAnimation();
    }

}
