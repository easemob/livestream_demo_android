package com.easemob.livedemo.ui.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.livedemo.R;
import com.github.florent37.viewanimator.AnimationBuilder;
import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wei on 2016/6/13.
 */
public class BarrageLayout extends LinearLayout {
    @BindView(R.id.container1)
    RelativeLayout container1;
    @BindView(R.id.container2)
    RelativeLayout container2;

    int count = 0;

    int screenWidth;

    public BarrageLayout(Context context) {
        super(context);
        init(context, null);
    }

    public BarrageLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BarrageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            final View barrageView = (View) msg.obj;
            System.out.println("what = " + what);
            if(what == 0){
                container2.addView(barrageView);
            }else{
                container1.addView(barrageView);
            }
            barrageView.measure(0, 0);
            int barrageWidth = barrageView.getMeasuredWidth();
            AnimationBuilder builder = ViewAnimator.animate(barrageView).translationX(screenWidth, -barrageWidth).interpolator(new LinearInterpolator()).duration(5000);
            builder.onStop(new AnimationListener.Stop() {
                @Override
                public void onStop() {
                    removeView(barrageView);
                }
            });
            builder.start();
        }
    };

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.widget_barrage_layout, this);
        ButterKnife.bind(this);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();

    }

    public synchronized void addBarrage(String msgContent, String username) {
        int i = count % 2;
        Message message = handler.obtainMessage();
        message.what = i;
        message.obj = newBarrageView(msgContent, username);
        handler.sendMessage(message);
        count++;
    }

    private View newBarrageView(String msgContent, String username){
        View barrageView = LayoutInflater.from(getContext()).inflate(R.layout.layout_barrage_show, null);
        TextView nameView = (TextView) barrageView.findViewById(R.id.name);
        TextView contentView = (TextView) barrageView.findViewById(R.id.content);
        nameView.setText(username);
        contentView.setText(msgContent);
        return barrageView;
    }
}
