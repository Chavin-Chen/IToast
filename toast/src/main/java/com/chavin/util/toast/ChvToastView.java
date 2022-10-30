package com.chavin.util.toast;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * Created by ChavinChen on 2018/08/25 0:31
 * EMAIL: <a href="mailto:chavinchen@hotmail.com">chavinchen@hotmail.com</a>
 */
public class ChvToastView extends DialogFragment implements DialogInterface.OnShowListener {

    private static final String KEY_MESSAGE = "KEY_MESSAGE";
    private static final String KEY_DURATION = "KEY_DURATION";
    private static final String KEY_GRAVITY = "KEY_GRAVITY";
    private static final String KEY_ICON = "KEY_ICON";

    private static final CharSequence DEFAULT_MESSAGE = "Empty Message! {.Chavin}";

    public static final String TAG = "I_TOAST";


    public static ChvToastView newInst(@NonNull ChvToast.RequestArguments reqArgs) {
        if (TextUtils.isEmpty(reqArgs.message)) {
            reqArgs.message = DEFAULT_MESSAGE;
        }
        ChvToastView toastView = new ChvToastView();

        Bundle args = new Bundle();
        args.putCharSequence(KEY_MESSAGE, reqArgs.message);
        args.putInt(KEY_DURATION, reqArgs.duration);
        args.putInt(KEY_GRAVITY, reqArgs.gravity);
        args.putInt(KEY_ICON, reqArgs.icon);
        toastView.setArguments(args);

        toastView.setCustomView(reqArgs.view);
        toastView.setListener(reqArgs.listener);

        toastView.setArgument(reqArgs);
        return toastView;
    }


    static final long LENGTH_LONG = 3500L;
    static final long LENGTH_SHORT = 2000L;

    private static final int WHAT_HIDE = 0x001;
    private Handler mHandler;

    private ChvToast.RequestArguments mArgument;

    @Nullable
    private View mCustomView;

    private ToastDisappearListener mListener;

    private boolean mActive = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_INPUT, 0);
        setCancelable(false);
        setShowsDialog(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null != mCustomView) {
            return mCustomView;
        }

        CharSequence mes = DEFAULT_MESSAGE;

        @DrawableRes
        int icon = 0;

        @SuppressLint("ShowToast")
        Toast toast = Toast.makeText(getActivity(), mes, Toast.LENGTH_SHORT);

        Bundle args = getArguments();
        if (null != args) {
            mes = args.getCharSequence(KEY_MESSAGE, DEFAULT_MESSAGE);
            icon = args.getInt(KEY_ICON, 0);
        }

        toast.setText(mes);

        View view = toast.getView();
        TextView textView = view.findViewById(android.R.id.message);
        if (0 != icon) {
            Drawable dwLeft = ContextCompat.getDrawable(view.getContext(), icon);
            if (null != dwLeft) {
                dwLeft.setBounds(0, 0, (int) textView.getTextSize(), (int) textView.getTextSize());
                textView.setCompoundDrawables(dwLeft, null, null, null);
                textView.setCompoundDrawablePadding((int) textView.getTextSize() / 2);
            }
        }

        return view;

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();
        if (null != window) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.CENTER_HORIZONTAL;

            @SuppressLint("ShowToast")
            Toast toast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
            int mYOffset = toast.getYOffset();

            int gravity = ChvToast.GRAVITY.BOTTOM;
            Bundle args = getArguments();
            if (null != args) {
                gravity = args.getInt(KEY_GRAVITY, ChvToast.GRAVITY.BOTTOM);
            }
            switch (gravity) {
                case ChvToast.GRAVITY.BOTTOM:
                    params.gravity = params.gravity | Gravity.BOTTOM;
                    params.y = mYOffset;
                    break;
                case ChvToast.GRAVITY.CENTER:
                    params.gravity = params.gravity | Gravity.CENTER_VERTICAL;
                    break;
                case ChvToast.GRAVITY.TOP:
                    params.gravity = params.gravity | Gravity.TOP;
                    params.y = mYOffset;
                    break;
            }
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
            params.format = PixelFormat.TRANSLUCENT;
            params.windowAnimations = android.R.style.Animation_Toast;
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;// 1000
        }
        dialog.setOnShowListener(this);
        return dialog;
    }


    @Override
    public void onPause() {
        super.onPause();
        clearWindowAnim();
    }

    @Override
    public void onStop() {
        super.onStop();
        clearDuration();
    }

    @Override
    public void onShow(DialogInterface dialog) {
        if (!mActive) {
            dismissAllowingStateLoss();
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (null != mListener) {
            mListener.onDismiss(mArgument);
        }
    }

    @Override
    public void show(@NonNull FragmentManager manager, String tag) {
        super.show(manager, tag);
        applyDuration();
    }

    @Override
    public int show(@NonNull FragmentTransaction transaction, String tag) {
        int res = super.show(transaction, tag);
        applyDuration();
        return res;
    }

    public ChvToastView show(FragmentManager manager, boolean active) {
        mActive = active;
        show(manager, TAG);
        return this;
    }

    public void cancel() {
        mActive = false;
        dismissAllowingStateLoss();
    }

    private void applyDuration() {
        if (null == mHandler) {
            mHandler = new Handler(Looper.getMainLooper(), msg -> {
                if (msg.what == WHAT_HIDE) {
                    dismissAllowingStateLoss();
                    return true;
                }
                return false;
            });
        }

        int duration = ChvToast.DURATION.SHORT;
        Bundle args = getArguments();
        if (null != args) {
            duration = args.getInt(KEY_DURATION, duration);
        }
        switch (duration) {
            case ChvToast.DURATION.LONG:
                mHandler.sendEmptyMessageDelayed(WHAT_HIDE, LENGTH_LONG);
                break;
            case ChvToast.DURATION.SHORT:
                mHandler.sendEmptyMessageDelayed(WHAT_HIDE, LENGTH_SHORT);
                break;
        }
    }

    private void clearDuration() {
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        dismissAllowingStateLoss();
    }

    private void clearWindowAnim() {
        Dialog dialog = getDialog();
        if (null != dialog) {
            Window window = dialog.getWindow();
            if (null != window) {
                window.setWindowAnimations(0);
            }
        }
    }

    private void setArgument(ChvToast.RequestArguments argument) {
        mArgument = argument;
    }

    private void setCustomView(@Nullable View view) {
        mCustomView = view;
    }

    private void setListener(ToastDisappearListener listener) {
        mListener = listener;
    }

    interface ToastDisappearListener {
        void onDismiss(ChvToast.RequestArguments arg);
    }

}