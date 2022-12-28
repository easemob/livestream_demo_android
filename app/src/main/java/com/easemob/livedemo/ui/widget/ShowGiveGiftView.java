package com.easemob.livedemo.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.widget.EaseImageView;

import java.util.Timer;
import java.util.TimerTask;

import com.easemob.livedemo.R;
import com.easemob.livedemo.common.utils.ThreadManager;
import com.easemob.livedemo.data.model.GiftBean;
import com.easemob.livedemo.data.model.User;
import com.easemob.livedemo.utils.Utils;

public class ShowGiveGiftView extends LinearLayout {
    private Context context;
    private NumberAnim giftNumberAnim;
    private Timer timer;
    private int duration = 200;
    private static final int maxExitTime = 3000;
    private static final int maxShowView = 2;

    public ShowGiveGiftView(Context context) {
        this(context, null);
    }

    public ShowGiveGiftView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShowGiveGiftView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initAttrs(context, attrs);
        init(context);
    }

    private void initAttrs(Context context, AttributeSet attrs) {

    }

    private void init(Context context) {
//        initAnimation(context);
    }

//    private void initAnimation(Context context) {
//    }

    public void showGift(GiftBean bean) {
        clearTiming();
        if (this.getChildCount() > maxShowView) {
            View giftView01 = getChildAt(0);
            ImageView iv_gift01 = giftView01.findViewById(R.id.iv_gift);
            long lastTime1 = (long) iv_gift01.getTag();
            int showTime1 = (int) giftView01.getTag();

            View giftView02 = getChildAt(1);
            ImageView iv_gift02 = giftView02.findViewById(R.id.iv_gift);
            long lastTime2 = (long) iv_gift02.getTag();
            int showTime2 = (int) giftView02.getTag();

            if (lastTime1 + showTime1 > lastTime2 + showTime2) {
                removeGiftView(giftView02);
            } else {
                removeGiftView(giftView01);
            }
        }


        View newGiftView = getNewGiftView(bean);
        newGiftView.setTag(calculateDuration(bean));
        addView(newGiftView);


        TranslateAnimation inAnim = (TranslateAnimation) AnimationUtils.loadAnimation(context, R.anim.gift_in);
        newGiftView.startAnimation(inAnim);
        final MagicTextView mtv_giftNum = newGiftView.findViewById(R.id.tv_gift_num);
        inAnim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                showGiftAnimation(mtv_giftNum, bean);
            }
        });

    }

    private void showGiftAnimation(MagicTextView tvGiftNum, GiftBean bean) {
        int num = bean.getNum();
        if (num > 0) {
            startGiftTimer(tvGiftNum, bean);
        }
    }

    private void startGiftTimer(MagicTextView tvGiftNum, GiftBean bean) {
        giftNumberAnim = new NumberAnim();
        int increment = calculateIncrease(bean);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ThreadManager.getInstance().runOnMainThread(() -> {
                    int tag = (int) tvGiftNum.getTag();
                    if (tag >= bean.getNum()) {
                        timer.cancel();
                        return;
                    }
                    if (tag + increment > bean.getNum()) {
                        tvGiftNum.setTag(bean.getNum());
                        tvGiftNum.setText(context.getString(R.string.show_gift_num, String.valueOf(bean.getNum())));
                    } else {
                        tvGiftNum.setTag(tag + increment);
                        tvGiftNum.setText(context.getString(R.string.show_gift_num, String.valueOf(tag + increment)));
                    }
                    giftNumberAnim.showAnimator(tvGiftNum);

                });
            }

        }, 0, duration);
    }


    private int calculateIncrease(GiftBean bean) {
        int increment = 1;
        if (bean.getNum() * duration > maxExitTime) {
            increment = (int) Math.ceil(bean.getNum() * 1f * duration / maxExitTime);
        }
        return increment;
    }

    private int calculateDuration(GiftBean bean) {
        if (bean.getNum() * duration > maxExitTime) {
            return maxExitTime;
        }
        return bean.getNum() * duration;
    }

    private void clearTiming() {
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    ThreadManager.getInstance().runOnMainThread(() -> {
                        int childCount = getChildCount();
                        long nowTime = System.currentTimeMillis();
                        for (int i = 0; i < childCount; i++) {

                            View childView = getChildAt(i);
                            ImageView iv_gift = (ImageView) childView.findViewById(R.id.iv_gift);
                            long lastUpdateTime = (long) iv_gift.getTag();

                            if (nowTime - lastUpdateTime >= maxExitTime) {
                                removeGiftView(childView);
                            }
                        }
                    });

                }
            }, 0, duration * 3);
        }
    }

    public void destroy() {
        if (timer != null) {
            timer.cancel();
        }
        if (giftNumberAnim != null && giftNumberAnim.lastAnimator != null && giftNumberAnim.lastAnimator.isRunning()) {
            giftNumberAnim.lastAnimator.cancel();
        }
    }

    private void removeGiftView(final View removeGiftView) {
        Animation animation = removeGiftView.getAnimation();
        if (animation == null) {
            TranslateAnimation outAnim = (TranslateAnimation) AnimationUtils.loadAnimation(context, R.anim.gift_out);
            outAnim.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    removeView(removeGiftView);
                }
            });

            removeGiftView.startAnimation(outAnim);
        }
    }


    private View getNewGiftView(GiftBean gift) {


        View giftView = LayoutInflater.from(context).inflate(R.layout.layout_item_gift_show, null);
        ImageView iv_gift = giftView.findViewById(R.id.iv_gift);
        iv_gift.setTag(System.currentTimeMillis());

        iv_gift.setImageResource(gift.getResource());


        TextView tvName = giftView.findViewById(R.id.tv_username);
        EaseImageView ivIcon = giftView.findViewById(R.id.iv_icon);
        User user = gift.getUser();
        if (user != null) {
            EaseUserUtils.setUserNick(user.getId(), tvName);
            EaseUserUtils.setUserAvatar(context, user.getId(), ivIcon);
        }

        TextView giftContent = giftView.findViewById(R.id.send_gift_content);
        giftContent.setText(context.getString(R.string.gift_show_send, gift.getName()));
        MagicTextView mtv_giftNum = giftView.findViewById(R.id.tv_gift_num);
        mtv_giftNum.setTypeface(Utils.getRobotoBlackItalicTypeface(context));
        mtv_giftNum.setTag(1);
        mtv_giftNum.setText(context.getString(R.string.show_gift_num, "1"));

        return giftView;
    }


    private class NumberAnim {
        private Animator lastAnimator;

        public void showAnimator(View v) {
            ObjectAnimator animScaleX = ObjectAnimator.ofFloat(v, "scaleX", 1.3f, 1.0f);
            ObjectAnimator animScaleY = ObjectAnimator.ofFloat(v, "scaleY", 1.3f, 1.0f);
            AnimatorSet animSet = new AnimatorSet();
            animSet.playTogether(animScaleX, animScaleY);
            animSet.setDuration(duration);
            lastAnimator = animSet;
            animSet.start();
        }
    }
}
