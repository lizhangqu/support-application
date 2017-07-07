//Copyright 2017 区长. All rights reserved.
//
//Redistribution and use in source and binary forms, with or without
//modification, are permitted provided that the following conditions are
//met:
//
//* Redistributions of source code must retain the above copyright
//notice, this list of conditions and the following disclaimer.
//* Redistributions in binary form must reproduce the above
//copyright notice, this list of conditions and the following disclaimer
//in the documentation and/or other materials provided with the
//distribution.
//* Neither the name of Google Inc. nor the names of its
//contributors may be used to endorse or promote products derived from
//this software without specific prior written permission.
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
//"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
//LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
//A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
//OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
//SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
//LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
//DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
//THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
//(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
//OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
