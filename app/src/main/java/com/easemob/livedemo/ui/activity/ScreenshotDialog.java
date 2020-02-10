package com.easemob.livedemo.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.easemob.livedemo.R;

/**
 * Created by wei on 2016/7/27.
 */
public class ScreenshotDialog extends Dialog{

  @BindView(R.id.imageview) ImageView imageView;

  private Bitmap bitmap;

  public ScreenshotDialog(Context context, Bitmap bitmap) {
    super(context);
    this.bitmap = bitmap;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.dialog_screenshot);
    ButterKnife.bind(this);
    setCanceledOnTouchOutside(false);

    if(bitmap != null){
      imageView.setImageBitmap(bitmap);
    }


  }

  @OnClick(R.id.btn_share) void onShare(){

  }

  @OnClick(R.id.btn_cancel) void onCancel(){
    if(bitmap != null) {
      bitmap.recycle();
      bitmap = null;
    }
    dismiss();
  }
}
