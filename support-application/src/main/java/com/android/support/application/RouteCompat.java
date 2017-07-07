package com.android.support.application;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * route
 *
 * @author lizhangqu
 * @version V1.0
 * @since 2017-07-07 16:40
 */
public class RouteCompat {

    public static final String KExtraReferrer = "referrer";

    /**
     * Outer resolver provider for every route attempts
     */
    public static interface RouteResolver {

        /**
         * Provider outer resolver.
         *
         * @return {@link ResolveInfo} list.
         */
        List<ResolveInfo> queryIntentActivities(PackageManager pm, Intent intent, int flags);

        /**
         * Provider outer resolver.
         *
         * @return {@link ResolveInfo}.
         */
        ResolveInfo resolveActivity(PackageManager pm, Intent intent, int flags);
    }

    /**
     * Preprocessor for every route attempts. it only change the intent
     */
    public static interface RoutePreprocessor {

        /**
         * Check the intent and make changes if needed.
         *
         * @return true to pass, or false to abort the route request.
         */
        boolean beforeRouteTo(Intent intent);
    }

    /**
     * Hooker for every route attempts, if hook() return true, route will do nothing, hooker take over route
     */
    public static interface RouteHooker {


        /**
         * Define the hooker priority. One priority only can accordant to one hooker.
         */
        public static final int ROUTE_HOOKER_HIGH_PRIORITY = 3;
        public static final int ROUTE_HOOKER_NORMAL_PRIORITY = 2;
        public static final int ROUTE_HOOKER_LOW_PRIORITY = 1;

        static final int ROUTE_HOOKER_STICKMAX_PRIORITY = ROUTE_HOOKER_HIGH_PRIORITY + 1;

        /**
         * Check the intent and make changes if needed.
         *
         * @return true to pass, or false to abort the route request.
         */
        boolean hook(Context context, Intent intent);
    }

    /**
     * Exception handler to be triggered for route exception.
     */
    public static interface RouteExceptionHandler {

        /**
         * Called when failed to route to the specific destination.
         *
         * @param intent the activity intent to be started which caused the exception
         *               (may be modified by {@link RoutePreprocessor}). It is supposed to be changed if retry is needed.
         * @param e      the exception (most probably {@link android.content.ActivityNotFoundException})
         * @return whether to retry the route.
         * <b>When failed in retry, this called will not be called again.</b>
         */
        boolean onException(Intent intent, Exception e);
    }

    public static class RouteCanceledException extends Exception {

        private static final long serialVersionUID = 5015146091187397488L;
    }

    /**
     * @param context use current Activity if possible
     */
    public static RouteCompat from(final Context context) {
        return new RouteCompat(context);
    }

    /**
     * Category to be put into activity intent.
     */
    public RouteCompat withCategory(final String category) {
        mIntent.addCategory(category);
        return this;
    }

    /**
     * Extras to be put into activity intent.
     */
    public RouteCompat withExtras(final Bundle extras) {
        if (extras == null)
            return this;

        mIntent.putExtras(extras);
        return this;
    }

    /**
     * Flags to be added to activity intent
     */
    public RouteCompat withFlags(final int flags) {
        mIntent.addFlags(flags);
        return this;
    }

    /**
     * @param request_code should >= 0, or no result will be returned to source activity.
     */
    public RouteCompat forResult(final int request_code) {
        if (!(mContext instanceof Activity))
            throw new IllegalStateException("Only valid from Activity, but from " + mContext);
        mRequestCode = request_code;
        return this;
    }

    /**
     * Allow route to escape current application (for 3rd-party activity)
     */
    public RouteCompat allowEscape() {
        mAllowLeaving = true;
        return this;
    }

    /**
     * Disallow route to current activity itself (specified by {@link #from(Context)}).
     */
    public RouteCompat disallowLoopback() {
        mDisallowLoopback = true;
        return this;
    }

    /**
     * Disable global transition previously specified by {@link #setTransition(int, int)}, only valid if originated from Activity
     */
    public RouteCompat disableTransition() {
        mDisableTransition = true;
        return this;
    }

    public RouteCompat skipPreprocess() {
        mSkipPreprocess = true;
        return this;
    }

    public RouteCompat skipHooker() {
        mSkipHooker = true;
        return this;
    }

    public RouteCompat skipPriorHooker() {
        mSkipPriorHooker = true;
        return this;
    }

    public boolean toUri(final RouteUri uri) {
        return toUri(uri.build());
    }

    /**
     * Start UI component associated with the specific URI.
     *
     * @return true if successful, or false if no UI component matches the URI
     */
    public boolean toUri(final String uri) {
        if (TextUtils.isEmpty(uri)) return false;
        return toUri(Uri.parse(uri));
    }

    /**
     * Start activity associated with the specific URI.
     * it may return false, when target activity didn't finded, the caller should handler this case
     */
    @SuppressLint("LogConditional")
    public boolean toUri(final Uri uri) {

        Log.d(TAG, uri.toString());

        RouteExceptionHandler exception_handler = mExceptionHandler;
        final Intent intent = to(uri);
        if (intent == null) {
            if (exception_handler != null)
                exception_handler.onException(mIntent, new RouteCanceledException());
            return false;
        } else if (intent instanceof RouteHookIntent) {
            //this mean's route has been hooked. do none!
            return true;
        }

        for (; ; )
            try {

                ComponentName target = null;
                if (mAllowLeaving) {
                    final ResolveInfo info = mRouteResolver.resolveActivity(mContext.getPackageManager(), intent, PackageManager.MATCH_DEFAULT_ONLY);
                    if (info == null)
                        throw new ActivityNotFoundException("No Activity found to handle " + intent);
                    target = new ComponentName(info.activityInfo.packageName, info.activityInfo.name);

                } else {
                    intent.setPackage(mContext.getPackageName());

                    final ResolveInfo info = mRouteResolver.resolveActivity(mContext.getPackageManager(), intent, PackageManager.MATCH_DEFAULT_ONLY);
                    if (info == null) {
                        //for some special permission-check phone, resolveActivity will return null.
                        //so we use queryIntentActivities to check again.
                        final List<ResolveInfo> list = mRouteResolver.queryIntentActivities(mContext.getPackageManager(), intent, PackageManager.MATCH_DEFAULT_ONLY);
                        final ResolveInfo rinfo = optimum(list);
                        if (rinfo == null)
                            throw new ActivityNotFoundException("No Activity found to handle " + intent);

                        intent.setClassName(rinfo.activityInfo.packageName, rinfo.activityInfo.name);
                    } else {
                        intent.setClassName(info.activityInfo.packageName, info.activityInfo.name);
                    }

                    target = intent.getComponent();
                }

                if (mDisallowLoopback && mContext instanceof Activity && target != null) {
                    if (target.equals(((Activity) mContext).getComponentName())) {
                        Log.w(TAG, "Loopback disallowed: " + uri);
                        return false;
                    }
                }

                if (mTaskStack != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    mTaskStack.add(mIntent);    // As the final intent
                    startActivities(mTaskStack.toArray(new Intent[mTaskStack.size()]));
                } else if (mRequestCode >= 0) {
                    ((Activity) mContext).startActivityForResult(intent, mRequestCode);
                } else {
                    if (!(mContext instanceof Activity))
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }

                if (!mDisableTransition && mTransition != null && mContext instanceof Activity) {
                    ((Activity) mContext).overridePendingTransition(mTransition[0], mTransition[1]);
                }
                return true;

            } catch (final ActivityNotFoundException e) {

                if (exception_handler != null && exception_handler.onException(intent, e)) {
                    exception_handler = null;        // To avoid dead-loop.
                    continue;
                }
                return false;
            }
    }

    /**
     * 返回intent但不跳转
     */
    public Intent getIntent(final RouteUri uri) {
        return getIntent(uri.build());
    }

    /**
     * 返回intent但不跳转
     */
    public Intent getIntent(final String uri) {
        if (TextUtils.isEmpty(uri)) return null;
        return getIntent(Uri.parse(uri));
    }

    /**
     * 返回intent但不跳转
     */
    @SuppressLint("LogConditional")
    public Intent getIntent(final Uri uri) {

        Log.d(TAG, "getIntent:" + uri.toString());

        RouteExceptionHandler exception_handler = mExceptionHandler;
        final Intent intent = to(uri);
        if (intent == null) {
            if (exception_handler != null)
                exception_handler.onException(mIntent, new RouteCanceledException());
            return null;
        } else if (intent instanceof RouteHookIntent) {
            //this mean's route has been hooked. do none!
            return null;
        }

        for (; ; )
            try {

                ComponentName target = null;
                if (mAllowLeaving) {
                    final ResolveInfo info = mRouteResolver.resolveActivity(mContext.getPackageManager(), intent, PackageManager.MATCH_DEFAULT_ONLY);
                    if (info == null)
                        throw new ActivityNotFoundException("No Activity found to handle " + intent);
                    target = new ComponentName(info.activityInfo.packageName, info.activityInfo.name);

                } else {
                    intent.setPackage(mContext.getPackageName());

                    final ResolveInfo info = mRouteResolver.resolveActivity(mContext.getPackageManager(), intent, PackageManager.MATCH_DEFAULT_ONLY);
                    if (info == null) {
                        //for some special permission-check phone, resolveActivity will return null.
                        //so we use queryIntentActivities to check again.
                        final List<ResolveInfo> list = mRouteResolver.queryIntentActivities(mContext.getPackageManager(), intent, PackageManager.MATCH_DEFAULT_ONLY);
                        final ResolveInfo rinfo = optimum(list);
                        if (rinfo == null)
                            throw new ActivityNotFoundException("No Activity found to handle " + intent);

                        intent.setClassName(rinfo.activityInfo.packageName, rinfo.activityInfo.name);
                    } else {
                        intent.setClassName(info.activityInfo.packageName, info.activityInfo.name);
                    }

                    target = intent.getComponent();
                }

                if (mDisallowLoopback && mContext instanceof Activity && target != null) {
                    if (target.equals(((Activity) mContext).getComponentName())) {
                        Log.w(TAG, "Loopback disallowed: " + uri);
                        return null;
                    }
                }
                return intent;

            } catch (final ActivityNotFoundException e) {
                if (exception_handler != null && exception_handler.onException(intent, e)) {
                    exception_handler = null;        // To avoid dead-loop.
                    continue;
                }
                return null;
            }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void startActivities(final Intent[] intents) {
        mContext.startActivities(intents);
    }

    private Intent to(final Uri uri) {
        return to(uri, !mSkipPreprocess);
    }

    private Intent to(final Uri uri, final boolean preprocess) {

        mIntent.setData(uri);
        RouteHooker hooker = mPriorHookers.get(RouteHooker.ROUTE_HOOKER_STICKMAX_PRIORITY);
        if (!mSkipHooker && hooker != null) {
            if (!hooker.hook(mContext, mIntent)) {
                return new RouteHookIntent();
            }
        }

        //check prior hooker
        if (!mSkipPriorHooker) {
            for (int i = 0; i < mPriorHookers.size(); i++) {
                int priority = mPriorHookers.keyAt(i);

                if (priority == RouteHooker.ROUTE_HOOKER_STICKMAX_PRIORITY) {
                    continue;
                }

                hooker = mPriorHookers.get(priority);
                if (!hooker.hook(mContext, mIntent)) {
                    return new RouteHookIntent();
                }
            }
        }

        // Add referrer extra if not present
        if (!mIntent.hasExtra(KExtraReferrer)) {
            if (mContext instanceof Activity) {
                final Intent from_intent = ((Activity) mContext).getIntent();
                if (from_intent != null) {
                    final Uri referrer_uri = from_intent.getData();
                    ComponentName comp;
                    if (referrer_uri != null) {
                        mIntent.putExtra(KExtraReferrer, referrer_uri.toString());
                    } else if ((comp = from_intent.getComponent()) != null) {    // Compact (component only)
                        mIntent.putExtra(KExtraReferrer, new Intent().setComponent(comp).toUri(0));
                    } else {
                        mIntent.putExtra(KExtraReferrer, from_intent.toUri(0));    // Legacy
                    }
                }
            } else {
                //if other contex, we can't get the real referer url, but we know it was from application
                mIntent.putExtra(KExtraReferrer, mContext.getPackageName());
            }
        }

        // Run preprocessors
        if (!mStickPreprocessor.isEmpty()) {
            for (final RoutePreprocessor preprocessor : mStickPreprocessor) {
                if (!preprocessor.beforeRouteTo(mIntent)) {
                    return null;
                }
            }
        }

        if (preprocess && !mPreprocessor.isEmpty()) {
            for (final RoutePreprocessor preprocessor : mPreprocessor) {
                if (!preprocessor.beforeRouteTo(mIntent)) return null;
            }
        }
        return mIntent;
    }

    /**
     * Called to get the best specific destination.
     * <p>
     * <b>only use for condition not mAllowLeaving.</b>
     * <b>only same or similar package name will return.</b>
     * ResolveInfo's priority was highest,
     * if priority equal, same package return.
     * <b>we asume the packagename is xxx.xxx.xxx style.</b>
     */
    private ResolveInfo optimum(final List<ResolveInfo> list) {

        if (list == null)
            return null;
        else if (list.size() == 1) {
            return list.get(0);
        }

        final ArrayList<SortedResolveInfo> resolveInfo = new ArrayList<SortedResolveInfo>();

        for (final ResolveInfo info : list) {

            if (!TextUtils.isEmpty(info.activityInfo.packageName)) {
                if (info.activityInfo.packageName.endsWith(mContext.getPackageName())) {
                    resolveInfo.add(new SortedResolveInfo(info, info.priority, 1));
                } else {
                    final String p1 = info.activityInfo.packageName;
                    final String p2 = mContext.getPackageName();
                    final String[] l1 = p1.split("\\.");
                    final String[] l2 = p2.split("\\.");
                    if (l1.length >= 2 && l2.length >= 2) {
                        if (l1[0].equals(l2[0]) && l1[1].equals(l2[1]))
                            resolveInfo.add(new SortedResolveInfo(info, info.priority, 0));
                    }
                }
            }
        }

        if (resolveInfo.size() > 0) {
            if (resolveInfo.size() > 1) {
                Collections.sort(resolveInfo);
            }
            final ResolveInfo ret = resolveInfo.get(0).info;
            resolveInfo.clear();
            return ret;
        } else {
            return null;
        }
    }

    private final class SortedResolveInfo implements Comparable<SortedResolveInfo> {

        public SortedResolveInfo(final ResolveInfo info, final int weight, final int same) {
            this.info = info;
            this.weight = weight;
            this.same = same;
        }

        private final ResolveInfo info;
        private int weight = 0;
        private int same = 0;

        @Override
        public int compareTo(final SortedResolveInfo other) {
            if (this == other)
                return 0;

            // order descending by priority
            if (other.weight != this.weight)
                return other.weight - this.weight;
                // order descending by same package
            else if (other.same != this.same)
                return other.same - this.same;
                // then randomly
            else if (System.identityHashCode(this) < System.identityHashCode(other))
                return -1;
            else
                return 1;
        }
    }


    /**
     * Register the stick pre-processor.
     * <p>
     * <b>The stick pre-processor will always be called, whatever {@link #skipPreprocess()} called.</b>
     */
    public static void registerStickPreprocessor(final RoutePreprocessor preprocessor) {
        mStickPreprocessor.add(preprocessor);
    }

    public static void unregisterStickPreprocessor(final RoutePreprocessor preprocessor) {
        mStickPreprocessor.remove(preprocessor);
    }

    public static void registerPreprocessor(final RoutePreprocessor preprocessor) {
        mPreprocessor.add(preprocessor);
    }

    public static void unregisterPreprocessor(final RoutePreprocessor preprocessor) {
        mPreprocessor.remove(preprocessor);
    }

    /**
     * Register the hooker processor.
     * <p>
     * <b>This hooker will always be the max priority, so it will be prior processed.</b>
     */
    public static void registerHooker(final RouteHooker hooker) {
        mPriorHookers.put(RouteHooker.ROUTE_HOOKER_STICKMAX_PRIORITY, hooker);
    }

    public static void registerPriorHooker(final RouteHooker hooker, int priority) {

        if (priority > RouteHooker.ROUTE_HOOKER_HIGH_PRIORITY || priority < RouteHooker.ROUTE_HOOKER_LOW_PRIORITY) {
            throw new RuntimeException("RouteHooker's priority less than ROUTE_HOOKER_HIGH_PRIORITY, larger than ROUTE_HOOKER_LOW_PRIORITY");
        }

        mPriorHookers.put(priority, hooker);
    }

    public static void setExceptionHandler(final RouteExceptionHandler handler) {
        mExceptionHandler = handler;
    }

    public static void setRouteResolver(final RouteResolver resolver) {
        mRouteResolver = resolver;
    }

    public static void setTransition(final int enterAnim, final int exitAnim) {
        mTransition = new int[2];
        mTransition[0] = enterAnim;
        mTransition[1] = exitAnim;
    }

    private RouteCompat(final Context context) {
        mContext = context;
        mIntent = new Intent(Intent.ACTION_VIEW);
    }

    private final static class DefaultResolver implements RouteResolver {

        @Override
        public List<ResolveInfo> queryIntentActivities(final PackageManager pm, final Intent intent, final int flags) {
            return pm.queryIntentActivities(intent, flags);
        }

        @Override
        public ResolveInfo resolveActivity(final PackageManager pm, final Intent intent, final int flags) {
            return pm.resolveActivity(intent, flags);
        }
    }

    private static class RouteHookIntent extends Intent {
    }

    private final Context mContext;
    private final Intent mIntent;
    private int mRequestCode = -1;
    private boolean mAllowLeaving;
    private boolean mDisallowLoopback;
    private boolean mSkipPreprocess;
    private boolean mSkipHooker;
    private boolean mSkipPriorHooker;
    private boolean mDisableTransition;
    private static int mTransition[];
    private List<Intent> mTaskStack;

    private static final List<RoutePreprocessor> mPreprocessor = new CopyOnWriteArrayList<RoutePreprocessor>();
    private static final List<RoutePreprocessor> mStickPreprocessor = new ArrayList<RoutePreprocessor>();
    private static final SparseArray<RouteHooker> mPriorHookers = new SparseArray<RouteHooker>();

    private static RouteExceptionHandler mExceptionHandler;

    private static final RouteResolver DEFAULT_RESOLVER = new DefaultResolver();
    private static volatile RouteResolver mRouteResolver = DEFAULT_RESOLVER;
    private static final String TAG = "RouteCompat";

}
