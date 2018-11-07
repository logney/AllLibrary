package com.pengyu.base.toast;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.pengyu.base.R;

public final class XToast extends Toast implements Runnable {

    private Handler mHandler = new Handler(Looper.getMainLooper()); // 吐司处理消息线程

    private TextView mTextView; // 吐司消息View
    private CharSequence mContent; // 吐司显示的文本

    public XToast(Context context) {
        super(context);
    }

    @Override
    public void setView(View view) {
        super.setView(view);
        mTextView = (view.findViewById(R.id.toast_main_text_view_id));
        if (mTextView != null) {
            return;
        } else if (view instanceof TextView) {
            mTextView = (TextView) view;
            return;
        } else if (view instanceof ViewGroup) {
            this.mTextView = getTextView((ViewGroup) view);
            if (this.mTextView != null) return;
        }
        // 如果设置的布局没有包含一个TextView则抛出异常，必须要包含一个TextView作为Message对象
        throw new IllegalArgumentException("The layout must contain a TextView");
    }

    @Override
    public void setText(CharSequence s) {
        // 记录本次吐司欲显示的文本
        mContent = s;
    }

    @Override
    public void show() {
        // 移除之前显示吐司的任务
        mHandler.removeCallbacks(this);
        super.cancel();
        // 添加一个显示吐司的任务
        mHandler.postDelayed(this, 300);
    }

    /**
     * {@link Runnable}
     */
    @Override
    public void run() {
        // 设置吐司文本
        mTextView.setText(mContent);
        // 显示吐司
        super.show();
    }

    @Override
    public void cancel() {
        // 移除之前显示吐司的任务
        mHandler.removeCallbacks(this);
        // 取消显示
        super.cancel();
    }

    /**
     * 递归获取ViewGroup中的TextView对象
     */
    private static TextView getTextView(ViewGroup group) {
        for (int i = 0; i < group.getChildCount(); i++) {
            View view = group.getChildAt(i);
            if ((view instanceof TextView)) {
                return (TextView) view;
            } else if (view instanceof ViewGroup) {
                TextView textView = getTextView((ViewGroup) view);
                if (textView != null) return textView;
            }
        }
        return null;
    }
}
