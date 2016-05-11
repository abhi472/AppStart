package com.example.abhishek.appstart;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

/**
 * Created by abhishek on 18/3/16.
 */
public class BackService extends AccessibilityService implements ICallBack{

    private WindowManager windowManager;
    private RelativeLayout popView, removeView,rem_scrim;
    private ImageView removeImg;
    ArrayList<HotOffers> hotoffers = new ArrayList<>();
    private int x_init_cord, y_init_cord, x_init_margin, y_init_margin;
    private Point szWindow = new Point();
    ObjectMapper om = new ObjectMapper();
    public static String LogTag = "mytest";
    RecyclerView rv;
    int shift = 0;
    int i =0;
    ArrayList<HotOffers> hot_offer_temp = new ArrayList<>();
    static final String TAG = "RecorderService";




    @Override
    public void onCreate() {
        super.onCreate();
        String url = "https://www.lafalafa.com/api/hotOffersApi/IN";
        ApiManager.getInstance().sendReq(this, url);
        for (int i =0;i<10;i++)
        {
            HotOffers ho = new HotOffers();
            ho.cashbackTitle = "hey";
            hot_offer_temp.add(ho);

        }

        }




    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {


        if ((event.getPackageName().equals("com.flipkart.android")) && (event.getClassName().equals("com.flipkart.android.SplashActivity"))) {


            notification(event.getPackageName().toString());
            if(popView!=null) {
                if (!popView.isShown()) {
                    handleStart();
                }
            }
            else
            {
                handleStart();
            }


        }
        else
        if(event.getPackageName().equals("com.android.settings"))
        {
            if(popView!=null)
            {
                if (popView.isShown()) {
                    windowManager.removeView(popView);
                }
            }
        }

    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void handleStart() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        removeView = (RelativeLayout) inflater.inflate(R.layout.remove, null);
        rem_scrim = new RelativeLayout(getApplicationContext());
        WindowManager.LayoutParams paramRemove = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        paramRemove.gravity = Gravity.TOP | Gravity.START;
        WindowManager.LayoutParams paramScrim= new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        paramScrim.gravity = Gravity.BOTTOM;
        rem_scrim.setVisibility(View.GONE);
        paramScrim.height= 300;
        rem_scrim.setBackground(getDrawable(R.drawable.shape_scrim));
        windowManager.addView(rem_scrim, paramScrim);


        removeView.setVisibility(View.GONE);
        removeImg = (ImageView) removeView.findViewById(R.id.remove_img);
        windowManager.addView(removeView, paramRemove);


        popView = (RelativeLayout) inflater.inflate(R.layout.chathead, null);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            windowManager.getDefaultDisplay().getSize(szWindow);
        } else {
            int w = windowManager.getDefaultDisplay().getWidth();
            int h = windowManager.getDefaultDisplay().getHeight();
            szWindow.set(w, h);
        }

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;
        popView.setScaleX(0);
        popView.setScaleY(0);
        popView.animate().scaleX(1).scaleY(1).setDuration(1000);
        windowManager.addView(popView, params);

        popView.setOnTouchListener(new View.OnTouchListener() {
            long time_start = 0, time_end = 0;
            boolean isLongclick = false, inBounded = false;
            int remove_img_width = 0, remove_img_height = 0;

            Handler handler_longClick = new Handler();
            Runnable runnable_longClick = new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Log.d(LogTag, "Into runnable_longClick");

                    isLongclick = true;
                    removeView.setVisibility(View.VISIBLE);
                    chathead_longclick();
                }
            };

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) popView.getLayoutParams();

                int x_cord = (int) event.getRawX();
                int y_cord = (int) event.getRawY();
                int x_cord_Destination, y_cord_Destination;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        time_start = System.currentTimeMillis();
                        handler_longClick.postDelayed(runnable_longClick, 600);

                        remove_img_width = removeImg.getLayoutParams().width;
                        remove_img_height = removeImg.getLayoutParams().height;

                        x_init_cord = x_cord;
                        y_init_cord = y_cord;

                        x_init_margin = layoutParams.x;
                        y_init_margin = layoutParams.y;


                        break;
                    case MotionEvent.ACTION_MOVE:
                        shift = 0;
                        int x_diff_move = x_cord - x_init_cord;
                        int y_diff_move = y_cord - y_init_cord;

                        x_cord_Destination = x_init_margin + x_diff_move;
                        y_cord_Destination = y_init_margin + y_diff_move;

                        if (isLongclick) {
                            shift = 0;
                            rem_scrim.setVisibility(View.VISIBLE);
                            int x_bound_left = szWindow.x / 2 - (int) (remove_img_width * 1.5);
                            int x_bound_right = szWindow.x / 2 + (int) (remove_img_width * 1.5);
                            int y_bound_top = szWindow.y - (int) (remove_img_height * 1.5);

                            if ((x_cord >= x_bound_left && x_cord <= x_bound_right) && y_cord >= y_bound_top) {
                                shift = 0;
                                inBounded = true;

                                int x_cord_remove = (int) ((szWindow.x - (remove_img_height * 1.5)) / 2);
                                int y_cord_remove = (int) (szWindow.y - ((remove_img_width * 1.5) + getStatusBarHeight()));

                                if (removeImg.getLayoutParams().height == remove_img_height) {
                                    shift = 0;
                                    removeImg.getLayoutParams().height = (int) (remove_img_height * 1.5);
                                    removeImg.getLayoutParams().width = (int) (remove_img_width * 1.5);

                                    WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();
                                    param_remove.x = x_cord_remove;
                                    param_remove.y = y_cord_remove;

                                    windowManager.updateViewLayout(removeView, param_remove);
                                }

                                layoutParams.x = x_cord_remove + (Math.abs(removeView.getWidth() - popView.getWidth())) / 2;
                                layoutParams.y = y_cord_remove + (Math.abs(removeView.getHeight() - popView.getHeight())) / 2;

                                windowManager.updateViewLayout(popView, layoutParams);
                                break;
                            } else {
                                shift = 0;
                                inBounded = false;
                                removeImg.getLayoutParams().height = remove_img_height;
                                removeImg.getLayoutParams().width = remove_img_width;

                                WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();
                                int x_cord_remove = (szWindow.x - removeView.getWidth()) / 2;
                                int y_cord_remove = szWindow.y - (removeView.getHeight() + getStatusBarHeight());

                                param_remove.x = x_cord_remove;
                                param_remove.y = y_cord_remove;

                                windowManager.updateViewLayout(removeView, param_remove);
                            }

                        }


                        layoutParams.x = x_cord_Destination;
                        layoutParams.y = y_cord_Destination;

                        windowManager.updateViewLayout(popView, layoutParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        shift = 0;
                        isLongclick = false;
                        removeView.setVisibility(View.GONE);
                        rem_scrim.setVisibility(View.GONE);
                        removeImg.getLayoutParams().height = remove_img_height;
                        removeImg.getLayoutParams().width = remove_img_width;
                        handler_longClick.removeCallbacks(runnable_longClick);

                        if (inBounded) {
                            shift = 0;
                            windowManager.removeView(popView);
                            stopService(new Intent(BackService.this, BackService.class));
                            inBounded = false;
                            break;
                        }

                        shift = 0;
                        int x_diff = x_cord - x_init_cord;
                        int y_diff = y_cord - y_init_cord;

                        if (Math.abs(x_diff) < 5 && Math.abs(y_diff) < 5) {
                            time_end = System.currentTimeMillis();
                            if ((time_end - time_start) < 300) {
                                chathead_click();
                                shift = 1;
                            }
                        }

                        y_cord_Destination = y_init_margin + y_diff;

                        int BarHeight = getStatusBarHeight();
                        if (y_cord_Destination < 0) {
                            y_cord_Destination = 0;
                        } else if (y_cord_Destination + (popView.getHeight() + BarHeight) > szWindow.y) {
                            y_cord_Destination = szWindow.y - (popView.getHeight() + BarHeight);
                        }
                        layoutParams.y = y_cord_Destination;

                        inBounded = false;
                        if (shift == 0) {
                            resetPosition(x_cord);
                        }

                        break;
                    default:
                        Log.d(LogTag, "popView.setOnTouchListener  -> event.getAction() : default");
                        break;
                }
                return true;
            }
        });


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            windowManager.getDefaultDisplay().getSize(szWindow);
        } else {
            int w = windowManager.getDefaultDisplay().getWidth();
            int h = windowManager.getDefaultDisplay().getHeight();
            szWindow.set(w, h);
        }

        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) popView.getLayoutParams();

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d(LogTag, "ChatHeadService.onConfigurationChanged -> landscap");


            if (layoutParams.y + (popView.getHeight() + getStatusBarHeight()) > szWindow.y) {
                layoutParams.y = szWindow.y - (popView.getHeight() + getStatusBarHeight());
                windowManager.updateViewLayout(popView, layoutParams);
            }

            if (layoutParams.x != 0 && layoutParams.x < szWindow.x) {
                resetPosition(szWindow.x);
            }

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.d(LogTag, "ChatHeadService.onConfigurationChanged -> portrait");


            if (layoutParams.x > szWindow.x) {
                resetPosition(szWindow.x);
            }

        }

    }

    private void resetPosition(int x_cord_now) {
        if (x_cord_now <= szWindow.x / 2) {
            moveToLeft(x_cord_now);
        } else {
            moveToRight(x_cord_now);

        }

    }

    private void moveToLeft(final int x_cord_now) {
        final int x = szWindow.x - x_cord_now;

        new CountDownTimer(500, 5) {
            WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) popView.getLayoutParams();

            public void onTick(long t) {
                long step = (500 - t) / 5;
                mParams.x = 0 - (int) (double) bounceValue(step, x);
                windowManager.updateViewLayout(popView, mParams);
            }

            public void onFinish() {
                mParams.x = 0;
                windowManager.updateViewLayout(popView, mParams);
            }
        }.start();
    }

    private void moveToRight(final int x_cord_now) {
        new CountDownTimer(500, 5) {
            WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) popView.getLayoutParams();

            public void onTick(long t) {
                long step = (500 - t) / 5;
                mParams.x = szWindow.x + (int) (double) bounceValue(step, x_cord_now) - popView.getWidth();
                windowManager.updateViewLayout(popView, mParams);
            }

            public void onFinish() {
                mParams.x = szWindow.x - popView.getWidth();
                windowManager.updateViewLayout(popView, mParams);
            }
        }.start();
    }

    private double bounceValue(long step, long scale) {
        return scale * Math.exp(-0.055 * step) * Math.cos(0.08 * step);
    }

    private int getStatusBarHeight() {
        return (int) Math.ceil(25 * getApplicationContext().getResources().getDisplayMetrics().density);
    }

    private void chathead_click() {
        View cancel;
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.BOTTOM;
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final LinearLayout listview;
        listview = (LinearLayout) inflater.inflate(R.layout.list, null);
        rv = (RecyclerView) listview.findViewById(R.id.list);
        cancel = listview.findViewById(R.id.close_layout);
        setupRecyclerView(rv, hot_offer_temp);

        popView.animate().translationY(-100).alpha(0).setDuration(300);
        windowManager.addView(listview, params);



rv.addOnItemTouchListener(new RecycleAdapter(getApplicationContext(), new RecycleAdapter.OnItemClickListener() {
    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.example.abhishek.hotoffers","com.example.abhishek.hotoffers.MainActivity"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                windowManager.removeView(listview);
                stopService(new Intent(BackService.this, BackService.class));
                startActivity(intent);
        Log.e(TAG, "onItemClick: "+position );
    }
}));

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                windowManager.removeView(listview);
                popView.animate().translationY(0).alpha(1).setDuration(300);

            }
        });




    }

    private void chathead_longclick() {
        Log.d(LogTag, "Into ChatHeadService.chathead_longclick() ");

        WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();
        int x_cord_remove = (szWindow.x - removeView.getWidth()) / 2;
        int y_cord_remove = szWindow.y - (removeView.getHeight() + getStatusBarHeight());

        param_remove.x = x_cord_remove;
        param_remove.y = y_cord_remove;

        windowManager.updateViewLayout(removeView, param_remove);
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        if (popView != null) {
            windowManager.removeView(popView);
        }


        if (removeView != null) {
            windowManager.removeView(removeView);
        }

    }


    @Override
    public void onInterrupt() {
        Log.v(TAG, "onInterrupt");
    }

    void setupRecyclerView(RecyclerView rv, ArrayList<HotOffers> cont) {
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
        rv.setAdapter(new RecycleAdapter(getApplicationContext(), cont));
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.v(TAG, "onServiceConnected");
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.flags = AccessibilityServiceInfo.DEFAULT;
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        setServiceInfo(info);
    }

    public void notification(String packageName) {
        ComponentName cn = new ComponentName("com.example.abhishek.appstart", "com.example.abhishek.appstart.FirstPage");
        Intent intent = new Intent();
        intent.setComponent(cn);
        PendingIntent pendingIntent = PendingIntent.getActivity(BackService.this, 0, intent, 0);
        Notification not = new NotificationCompat.Builder(BackService.this)
                .setTicker("ticker title")
                .setContentText(packageName)
                .setContentTitle("LafaLafa")
                .setSmallIcon(R.drawable.ic_stat_action_account_box)
                .setContentIntent(pendingIntent).build();
        not.flags = Notification.FLAG_AUTO_CANCEL;

        NotificationManager notm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notm.notify(0, not);


    }

    @Override
    public void onResult(String result) {
        Content content = null;


        try {
            JsonFactory jsonFactory = new JsonFactory();
            JsonParser jsonParser = jsonFactory.createParser(result);
            content  = om.readValue(jsonParser, Content.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
        hotoffers = content.hotOffer;
    }


    @Override
    public void onError(String error) {

    }
}
