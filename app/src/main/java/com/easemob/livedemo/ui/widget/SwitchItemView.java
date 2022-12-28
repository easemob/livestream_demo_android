package com.easemob.livedemo.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.hyphenate.easeui.widget.EaseImageView;

import com.easemob.livedemo.R;


public class SwitchItemView extends ConstraintLayout {
    private TextView tvTitle;
    private TextView tvHint;
    private View viewDivider;
    private String title;
    private String content;
    private int titleColor;
    private int contentColor;
    private float titleSize;
    private float contentSize;
    private String hint;
    private View root;
    private SwitchCompat switchItem;
    private OnCheckedChangeListener listener;
    private EaseImageView avatar;

    public SwitchItemView(Context context) {
        this(context, null);
    }

    public SwitchItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        root = LayoutInflater.from(context).inflate(R.layout.layout_item_switch, this);
        avatar = findViewById(R.id.avatar);
        tvTitle = findViewById(R.id.tv_title);
        tvHint = findViewById(R.id.tv_hint);
        viewDivider = findViewById(R.id.view_divider);
        switchItem = findViewById(R.id.switch_item);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SwitchItemView);
        int titleResourceId = a.getResourceId(R.styleable.SwitchItemView_switchItemTitle, -1);
        title = a.getString(R.styleable.SwitchItemView_switchItemTitle);
        if (titleResourceId != -1) {
            title = getContext().getString(titleResourceId);
        }
        tvTitle.setText(title);

        int titleColorId = a.getResourceId(R.styleable.SwitchItemView_switchItemTitleColor, -1);
        titleColor = a.getColor(R.styleable.SwitchItemView_switchItemTitleColor, ContextCompat.getColor(getContext(), R.color.color_black_333333));
        if (titleColorId != -1) {
            titleColor = ContextCompat.getColor(getContext(), titleColorId);
        }
        tvTitle.setTextColor(titleColor);

        int titleSizeId = a.getResourceId(R.styleable.SwitchItemView_switchItemTitleSize, -1);
        titleSize = a.getDimension(R.styleable.SwitchItemView_switchItemTitleSize, sp2px(getContext(), 14));

        if (titleSizeId != -1) {
            titleSize = getResources().getDimension(titleSizeId);
        }
        tvTitle.getPaint().setTextSize(titleSize);

        boolean showDivider = a.getBoolean(R.styleable.SwitchItemView_switchItemShowDivider, true);
        viewDivider.setVisibility(showDivider ? VISIBLE : GONE);

        int hintResourceId = a.getResourceId(R.styleable.SwitchItemView_switchItemHint, -1);
        hint = a.getString(R.styleable.SwitchItemView_switchItemHint);
        if (hintResourceId != -1) {
            hint = getContext().getString(hintResourceId);
        }
        tvHint.setText(hint);

        boolean checkEnable = a.getBoolean(R.styleable.SwitchItemView_switchItemCheckEnable, true);
        switchItem.setEnabled(checkEnable);

        boolean clickable = a.getBoolean(R.styleable.SwitchItemView_switchItemClickable, true);
        switchItem.setClickable(clickable);

        boolean showAvatar = a.getBoolean(R.styleable.SwitchItemView_switchItemShowAvatar, false);
        avatar.setVisibility(showAvatar ? VISIBLE : GONE);

        int avatarSrcResourceId = a.getResourceId(R.styleable.SwitchItemView_switchItemAvatarSrc, -1);
        if (avatarSrcResourceId != -1) {
            avatar.setImageResource(avatarSrcResourceId);
        }

        int avatarHeightId = a.getResourceId(R.styleable.SwitchItemView_switchItemAvatarHeight, -1);
        float height = a.getDimension(R.styleable.SwitchItemView_switchItemAvatarHeight, 0);
        if (avatarHeightId != -1) {
            height = getResources().getDimension(avatarHeightId);
        }

        int avatarWidthId = a.getResourceId(R.styleable.SwitchItemView_switchItemAvatarWidth, -1);
        float width = a.getDimension(R.styleable.SwitchItemView_switchItemAvatarWidth, 0);
        if (avatarWidthId != -1) {
            width = getResources().getDimension(avatarWidthId);
        }

        a.recycle();

        ViewGroup.LayoutParams params = avatar.getLayoutParams();
        params.height = height == 0 ? ViewGroup.LayoutParams.WRAP_CONTENT : (int) height;
        params.width = width == 0 ? ViewGroup.LayoutParams.WRAP_CONTENT : (int) width;

        setListener();

        tvHint.setVisibility(TextUtils.isEmpty(hint) ? GONE : VISIBLE);
    }

    private void setListener() {
        switchItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (listener != null) {
                    listener.onCheckedChanged(SwitchItemView.this, isChecked);
                }
            }
        });
    }

    public EaseImageView getAvatar() {
        return avatar;
    }

    public TextView getTvTitle() {
        return tvTitle;
    }

    public SwitchCompat getSwitch() {
        return switchItem;
    }

    public TextView getTvHint() {
        return tvHint;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.listener = listener;
    }

    /**
     * sp to px
     *
     * @param context
     * @param value
     * @return
     */
    public static float sp2px(Context context, float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, context.getResources().getDisplayMetrics());
    }

    /**
     * Interface definition for a callback to be invoked when the checked state
     * of a compound button changed.
     */
    public interface OnCheckedChangeListener {
        /**
         * Called when the checked state of a compound button has changed.
         *
         * @param buttonView The compound button view whose state has changed.
         * @param isChecked  The new checked state of buttonView.
         */
        void onCheckedChanged(SwitchItemView buttonView, boolean isChecked);
    }
}
