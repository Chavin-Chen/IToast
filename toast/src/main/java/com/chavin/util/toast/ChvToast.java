package com.chavin.util.toast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.SoftReference;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by ChavinChen on 2018/08/24 23:39
 * EMAIL: <a href="mailto:chavinchen@hotmail.com">chavinchen@hotmail.com</a>
 */
public class ChvToast {

    private Request mRequest;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private ChvToast() {
    }

    private void init(Context context, RequestArguments argument) {
        mRequest = new Request(context, argument);
    }

    // ====================================== public static ========================================

    @IntDef({DURATION.SHORT, DURATION.LONG})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DURATION {
        int SHORT = 0;
        int LONG = 1;
    }

    @IntDef({GRAVITY.TOP, GRAVITY.CENTER, GRAVITY.BOTTOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface GRAVITY {
        int TOP = 1;
        int CENTER = 2;
        int BOTTOM = 3;
    }

    @IntDef({STRATEGY.ANDROID_FIRST, STRATEGY.CUSTOM_FIRST})
    @Retention(RetentionPolicy.SOURCE)
    public @interface STRATEGY {
        int ANDROID_FIRST = 0;
        int CUSTOM_FIRST = 1;
    }

    /**
     * change strategy
     *
     * @param strategy {@link STRATEGY} first use {@link STRATEGY#ANDROID_FIRST}Android or
     *                 {@link STRATEGY#ANDROID_FIRST} Custom
     * @param provider {@link ActivityProvider} null of a provider that can getTopActivity
     */
    public static void setup(@STRATEGY int strategy, @Nullable ActivityProvider provider) {
        ConfigHolder.mStrategy = strategy;
        ConfigHolder.sActivityProvider = provider;
    }

    public static ChvToast makeText(@NonNull Context context, @StringRes int strId,
                                    @DURATION int duration) {
        CharSequence mes = context.getApplicationContext().getText(strId);
        return makeText(context, mes, duration);
    }

    public static ChvToast makeText(@NonNull Context context, CharSequence message,
                                    @DURATION int duration) {
        return makeText(context, message, duration, GRAVITY.BOTTOM, 0);
    }

    public static ChvToast makeText(@NonNull Context context, @StringRes int strId,
                                    @DURATION int duration,
                                    @GRAVITY int gravity) {
        CharSequence mes = context.getApplicationContext().getText(strId);
        return makeText(context, mes, duration, gravity);
    }

    public static ChvToast makeText(@NonNull Context context, CharSequence message,
                                    @DURATION int duration,
                                    @GRAVITY int gravity) {
        return makeText(context, message, duration, gravity, 0);
    }


    public static ChvToast makeText(@NonNull Context context, CharSequence message,
                                    @DURATION int duration,
                                    @GRAVITY int gravity,
                                    @DrawableRes int icon) {
        final ChvToast toast = new ChvToast();
        RequestArguments argument = new RequestArguments(message, duration, gravity, icon, null, arg -> toast.response());
        toast.init(context, argument);
        return toast;
    }

    public static ChvToast makeView(@NonNull Context context,
                                    @DURATION int duration,
                                    @NonNull View view) {
        return makeView(context, duration, GRAVITY.BOTTOM, view);
    }

    public static ChvToast makeView(@NonNull Context context,
                                    @DURATION int duration,
                                    @GRAVITY int gravity,
                                    @NonNull View view) {
        final ChvToast toast = new ChvToast();
        RequestArguments argument = new RequestArguments("", duration, gravity, 0, view, arg -> toast.response());
        toast.init(context, argument);
        return toast;
    }

    public interface ActivityProvider {
        FragmentActivity getTopActivity();
    }

    // ====================================== public ===============================================

    public ChvToast show() {
        if (null == mRequest || ConfigHolder.mRequests.contains(mRequest)) {
            return this;
        }
        request(mRequest);
        return this;
    }

    public void cancel() {
        Request request = mRequest;
        if (null == request) {
            return;
        }
        request.active = false;
        if (null != request.mChvToastView) {
            request.mChvToastView.cancel();
        }
        if (null != request.aToastView) {
            request.aToastView.cancel();
            if (null != request.aHandler) {
                request.aHandler.removeCallbacksAndMessages(null);
                response();
            }
        }

    }

    // ===================================== private ===============================================


    private void request(Request request) {
        ConfigHolder.mRequests.offer(request);
        if (ConfigHolder.mRequests.size() > 0 && !ConfigHolder.mIsApplying) {
            apply();
        }
    }

    private void apply() {
        ConfigHolder.mIsApplying = true;
        final Request request = ConfigHolder.mRequests.peek();
        final Context context;
        if (null == request
                || null == request.context
                || null == (context = request.context.get())) {
            response();
            return;
        }
        if (STRATEGY.ANDROID_FIRST == ConfigHolder.mStrategy) {
            if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                applyAToast(context, request);
            } else if (context instanceof FragmentActivity) {
                applyCToast((FragmentActivity) context, request);
            } else {
                if (null != ConfigHolder.sActivityProvider && null != ConfigHolder.sActivityProvider.getTopActivity()) {
                    applyCToast(ConfigHolder.sActivityProvider.getTopActivity(), request);
                }
            }
        } else {
            if (context instanceof FragmentActivity) {
                applyCToast((FragmentActivity) context, request);
            } else if (null != ConfigHolder.sActivityProvider
                    && null != ConfigHolder.sActivityProvider.getTopActivity()) {
                applyCToast(ConfigHolder.sActivityProvider.getTopActivity(), request);

            } else if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                applyAToast(context, request);
            }
        }
    }

    private void response() {
        ConfigHolder.mIsApplying = false;
        if (null == mRequest || !mRequest.active || mRequest.equals(ConfigHolder.mRequests.peek())) {
            ConfigHolder.mRequests.poll();
        }
        destroy();
        if (ConfigHolder.mRequests.size() <= 0) {
            return;
        }
        apply();
    }

    private void applyAToast(final Context context, final Request request) {
        Runnable r = () -> showAToast(context.getApplicationContext(), request);
        if (isMainThread()) {
            r.run();
        } else {
            mHandler.post(r);
        }
    }

    private void applyCToast(final FragmentActivity activity, final Request request) {
        if (activity.isRestricted()
                || activity.getSupportFragmentManager().isDestroyed()
                || activity.getSupportFragmentManager().isStateSaved()) {
            response();
            return;
        }
        Runnable r = () -> request.mChvToastView = ChvToastView.newInst(request.argument)
                .show(activity.getSupportFragmentManager(), request.active);
        if (isMainThread()) {
            r.run();
        } else {
            mHandler.post(r);
        }
    }

    @SuppressLint({"ShowToast", "SwitchIntDef"})
    private void showAToast(Context context, Request request) {
        RequestArguments argument = request.argument;
        if (null != argument.view) {
            request.aToastView = new Toast(context.getApplicationContext());
            request.aToastView.setView(argument.view);
        } else {
            request.aToastView = Toast.makeText(context.getApplicationContext(),
                    argument.message, Toast.LENGTH_SHORT);
            if (0 != argument.icon) {
                View view = request.aToastView.getView();
                TextView textView = view.findViewById(android.R.id.message);
                Drawable dwLeft = ContextCompat.getDrawable(view.getContext(), argument.icon);
                if (null != dwLeft) {
                    dwLeft.setBounds(0, 0, (int) textView.getTextSize(), (int) textView.getTextSize());
                    textView.setCompoundDrawables(dwLeft, null, null, null);
                    textView.setCompoundDrawablePadding((int) textView.getTextSize() / 2);
                }
            }
        }
        long delay;
        if (argument.duration == DURATION.LONG) {
            request.aToastView.setDuration(Toast.LENGTH_LONG);
            delay = ChvToastView.LENGTH_LONG;
        } else {
            request.aToastView.setDuration(Toast.LENGTH_SHORT);
            delay = ChvToastView.LENGTH_SHORT;
        }
        switch (argument.gravity) {
            case GRAVITY.TOP:
                request.aToastView.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL,
                        request.aToastView.getXOffset(), request.aToastView.getYOffset());
                break;
            case GRAVITY.CENTER:
                request.aToastView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL,
                        request.aToastView.getXOffset(), request.aToastView.getYOffset());
                break;
            default:
                request.aToastView.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL,
                        request.aToastView.getXOffset(), request.aToastView.getYOffset());

        }
        request.aToastView.show();
        if (!request.active) {
            request.aToastView.cancel();
            response();
            return;
        }
        request.aHandler = new Handler(Looper.getMainLooper());
        request.aHandler.postDelayed(this::response, delay);

    }

    private void destroy() {
        mRequest = null;
    }

    private boolean isMainThread() {
        Looper myLooper = Looper.myLooper();
        Looper mainLooper = Looper.getMainLooper();
        return mainLooper.equals(myLooper);
    }

    // ===================================== classes ===============================================

    private static final class ConfigHolder {
        private static final int MAX_REQUEST = 50;

        private static volatile boolean mIsApplying = false;

        private static volatile ActivityProvider sActivityProvider;

        @STRATEGY
        private static volatile int mStrategy = STRATEGY.ANDROID_FIRST;

        private static final Queue<Request> mRequests = new ArrayBlockingQueue<>(MAX_REQUEST);
    }

    static class RequestArguments {

        int contextHashCode;

        @Nullable
        CharSequence message;
        @DURATION
        int duration;
        @GRAVITY
        int gravity;
        @DrawableRes
        int icon;
        @Nullable
        View view;
        @Nullable
        ChvToastView.ToastDisappearListener listener;

        RequestArguments(@Nullable CharSequence message,
                         int duration, int gravity, int icon,
                         @Nullable View view, @Nullable ChvToastView.ToastDisappearListener listener) {
            this.message = message;
            this.duration = duration;
            this.gravity = gravity;
            this.icon = icon;
            this.view = view;
            this.listener = listener;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RequestArguments)) return false;

            RequestArguments that = (RequestArguments) o;

            if (contextHashCode != that.contextHashCode) return false;
            if (duration != that.duration) return false;
            if (gravity != that.gravity) return false;
            if (icon != that.icon) return false;
            if (!Objects.equals(message, that.message))
                return false;
            if (!Objects.equals(view, that.view)) return false;
            return Objects.equals(listener, that.listener);
        }

        @Override
        public int hashCode() {
            int result = contextHashCode;
            result = 31 * result + (message != null ? message.hashCode() : 0);
            result = 31 * result + duration;
            result = 31 * result + gravity;
            result = 31 * result + icon;
            result = 31 * result + (view != null ? view.hashCode() : 0);
            result = 31 * result + (listener != null ? listener.hashCode() : 0);
            return result;
        }
    }

    static class Request {

        SoftReference<Context> context;
        volatile boolean active = true;
        volatile ChvToastView mChvToastView;

        volatile Handler aHandler;
        volatile Toast aToastView;

        RequestArguments argument;

        Request(Context ctx, RequestArguments arg) {
            context = new SoftReference<>(ctx);
            argument = arg;
            argument.contextHashCode = ctx.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Request)) return false;

            Request request = (Request) o;

            if (active != request.active) return false;
            if (!Objects.equals(context, request.context))
                return false;
            return Objects.equals(argument, request.argument);
        }

        @Override
        public int hashCode() {
            int result = context != null ? context.hashCode() : 0;
            result = 31 * result + (active ? 1 : 0);
            result = 31 * result + (argument != null ? argument.hashCode() : 0);
            return result;
        }
    }

}