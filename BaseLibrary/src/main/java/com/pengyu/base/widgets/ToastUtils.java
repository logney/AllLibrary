package com.pengyu.base.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.pengyu.base.R;
import com.pengyu.base.toast.DimensUtils;
import com.pengyu.base.toast.IToastStyle;
import com.pengyu.base.toast.XToast;
import com.pengyu.base.toast.style.ToastBlackStyle;

public final class ToastUtils {

    private static IToastStyle sDefaultStyle;

    private static Toast sToast;

    /**
     * 初始化ToastUtils，建议在Application中初始化
     *
     * @param context 应用的上下文
     */
    public static void init(Context context) {
        // 检查默认样式是否为空，如果是就创建一个默认样式
        if (sDefaultStyle == null) {
            sDefaultStyle = new ToastBlackStyle();
        }

        // 如果这个上下文不是全局的上下文，就自动换成全局的上下文
        if (context != context.getApplicationContext()) {
            context = context.getApplicationContext();
        }

        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(sDefaultStyle.getBackgroundColor()); // 设置背景色
        drawable.setCornerRadius(DimensUtils.dp2px(context, sDefaultStyle.getCornerRadius())); // 设置圆角

        TextView textView = new TextView(context);
        textView.setTextColor(sDefaultStyle.getTextColor());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DimensUtils.sp2px(context, sDefaultStyle.getTextSize()));
        textView.setPadding(DimensUtils.dp2px(context, sDefaultStyle.getPaddingLeft()), DimensUtils.dp2px(context, sDefaultStyle.getPaddingTop()),
                DimensUtils.dp2px(context, sDefaultStyle.getPaddingRight()), DimensUtils.dp2px(context, sDefaultStyle.getPaddingBottom()));
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        // setBackground API版本兼容
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            textView.setBackground(drawable);
        } else {
            textView.setBackgroundDrawable(drawable);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textView.setZ(sDefaultStyle.getZ()); // 设置 Z 轴阴影
        }

        if (sDefaultStyle.getMaxLines() > 0) {
            textView.setMaxLines(sDefaultStyle.getMaxLines()); // 设置最大显示行数
        }

        sToast = new XToast(context);
        sToast.setGravity(sDefaultStyle.getGravity(), sDefaultStyle.getXOffset(), sDefaultStyle.getYOffset());
//        sToast.setView(textView);
        setView(context, R.layout.toast_custom_view);
    }

    /**
     * 显示一个对象的吐司
     *
     * @param object 对象
     */
    public static void show(Object object) {
        show(object != null ? object.toString() : "null");
    }

    /**
     * 显示一个吐司
     *
     * @param id 如果传入的是正确的string id就显示对应字符串
     *           如果不是则显示一个整数的string
     */
    public static void show(int id) {

        checkToastState();

        try {
            // 如果这是一个资源id
            show(sToast.getView().getContext().getResources().getText(id));
        } catch (Resources.NotFoundException ignored) {
            // 如果这是一个int类型
            show(String.valueOf(id));
        }
    }

    /**
     * 显示一个吐司
     *
     * @param text 需要显示的文本
     */
    public static void show(CharSequence text) {

        checkToastState();

        if (text == null || text.equals("")) return;

        sToast.setDuration(Toast.LENGTH_LONG);
        sToast.setText(text);
        sToast.show();
    }

    /**
     * 取消吐司的显示
     */
    public void cancel() {
        checkToastState();
        sToast.cancel();
    }

    /**
     * 获取当前Toast对象
     */
    public static Toast getToast() {
        return sToast;
    }

    /**
     * 给当前Toast设置新的布局，具体实现可看{@link XToast#setView(View)}
     */
    public static void setView(Context context, int layoutId) {
        if (context != context.getApplicationContext()) {
            context = context.getApplicationContext();
        }
        setView(View.inflate(context, layoutId, null));
    }

    public static void setView(View view) {

        checkToastState();

        if (view == null) {
            throw new IllegalArgumentException("Views cannot be empty");
        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            view.findViewById(R.id.toast_main_text_view_id).setZ(DimensUtils.dp2px(view.getContext(), 8)); // 设置 Z 轴阴影
//        }
        sToast.setView(view);
    }

    /**
     * 统一全局的Toast样式，建议在{@link android.app.Application#onCreate()}中初始化
     *
     * @param style 样式实现类，框架已经实现三种不同的样式
     *              黑色样式：{@link ToastBlackStyle}
     *              白色样式：{@link com.pengyu.base.toast.style.ToastWhiteStyle}
     *              仿QQ样式：{@link com.pengyu.base.toast.style.ToastQQStyle}
     */
    public static void initStyle(IToastStyle style) {
        ToastUtils.sDefaultStyle = style;
        //如果吐司已经创建，就重新初始化吐司
        if (sToast != null) {
            //取消原有吐司的显示
            sToast.cancel();
            //重新初始化吐司类
            init(sToast.getView().getContext().getApplicationContext());
        }
    }

    /**
     * 检查吐司状态，如果未初始化请先调用{@link ToastUtils#init(Context)}
     */
    private static void checkToastState() {
        //吐司工具类还没有被初始化，必须要先调用init方法进行初始化
        if (sToast == null) {
            throw new IllegalStateException("ToastUtils has not been initialized");
        } else {
//            sToast.cancel();
        }
    }
}
