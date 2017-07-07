package com.android.support.application;

import android.net.Uri;

/**
 * 功能介绍
 *
 * @author lizhangqu
 * @version V1.0
 * @since 2017-07-07 16:39
 */
public class RouteUri {

    /**
     * Create a URI builder with given scheme.
     */
    public static Schemed scheme(final String scheme) {
        final RouteUri nav_uri = new RouteUri();
        nav_uri.mBuilder.scheme(scheme);
        return new Schemed() {
            @Override
            public RouteUri host(final String host) {
                nav_uri.mBuilder.authority(host);
                return nav_uri;
            }
        };
    }

    /**
     * Create a URI builder with default scheme "HTTP" and given host.
     */
    public static RouteUri host(final String host) {
        final RouteUri nav_uri = new RouteUri();
        nav_uri.mBuilder.scheme("http").authority(host);
        return nav_uri;
    }

    /**
     * Create a URI builder with default scheme "HTTP" and given host.
     */
    public static RouteUri host(final String host, boolean isHttps) {
        final RouteUri nav_uri = new RouteUri();
        if (isHttps) {
            nav_uri.mBuilder.scheme("https").authority(host);
        } else {
            nav_uri.mBuilder.scheme("http").authority(host);
        }
        return nav_uri;
    }

    /**
     * Sets the path. Leaves '/' characters intact but encodes others as necessary.
     * <p>
     * <p>If the path is not null and doesn't start with a '/', and if you specify
     * a scheme and/or authority, the builder will prepend the given path with a '/'.
     */
    public RouteUri path(final String path) {
        mBuilder.path(path);
        return this;
    }

    /**
     * Encodes and appends the given segment to the path
     */
    public RouteUri segment(final String new_segment) {
        mBuilder.appendEncodedPath(new_segment);
        return this;
    }

    /**
     * Encodes and appends the given segment to the path
     */
    public RouteUri segment(final long new_segment) {
        mBuilder.appendEncodedPath(String.valueOf(new_segment));
        return this;
    }

    /**
     * Encodes and appends the given segment to the path
     */
    public RouteUri segment(final int new_segment) {
        mBuilder.appendEncodedPath(String.valueOf(new_segment));
        return this;
    }

    /**
     * Encodes the key and value and then appends the parameter to the query string.
     */
    public RouteUri param(final String key, final String value) {
        mBuilder.appendQueryParameter(key, value);
        return this;
    }

    /**
     * Encodes the key and value and then appends the parameter to the query string.
     */
    public RouteUri param(final String key, final long value) {
        mBuilder.appendQueryParameter(key, String.valueOf(value));
        return this;
    }

    /**
     * Encodes the key and value and then appends the parameter to the query string.
     */
    public RouteUri param(final String key, final int value) {
        mBuilder.appendQueryParameter(key, String.valueOf(value));
        return this;
    }

    /**
     * Encodes and sets the fragment.
     */
    public RouteUri fragment(final String fragment) {
        mBuilder.fragment(fragment);
        return this;
    }

    Uri build() {
        return mBuilder.build();
    }

    private RouteUri() {
    }

    protected Uri.Builder mBuilder = new Uri.Builder();

    public interface Schemed {
        RouteUri host(String host);
    }
}
