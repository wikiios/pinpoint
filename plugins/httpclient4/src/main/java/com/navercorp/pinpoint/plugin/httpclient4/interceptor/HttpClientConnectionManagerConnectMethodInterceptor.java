/*
 * Copyright 2014 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.plugin.httpclient4.interceptor;

import org.apache.http.conn.routing.HttpRoute;

import com.navercorp.pinpoint.bootstrap.context.MethodDescriptor;
import com.navercorp.pinpoint.bootstrap.context.SpanEventRecorder;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.interceptor.SpanEventSimpleAroundInterceptorForPlugin;
import com.navercorp.pinpoint.common.trace.AnnotationKey;
import com.navercorp.pinpoint.plugin.httpclient4.HttpClient4Constants;

/**
 * @author jaehong.kim
 */
public class HttpClientConnectionManagerConnectMethodInterceptor extends SpanEventSimpleAroundInterceptorForPlugin {

    public HttpClientConnectionManagerConnectMethodInterceptor(TraceContext traceContext, MethodDescriptor methodDescriptor) {
        super(traceContext, methodDescriptor);
    }

    @Override
    protected void doInBeforeTrace(SpanEventRecorder recorder, Object target, Object[] args) {
        if(args != null && args.length >= 2 && args[1] != null && args[1] instanceof HttpRoute) {
            final HttpRoute route = (HttpRoute) args[1];
            final StringBuilder sb = new StringBuilder();
            if(route.getProxyHost() != null) {
                sb.append(route.getProxyHost().getHostName());
                if(route.getProxyHost().getPort() > 0) {
                    sb.append(":").append(route.getProxyHost().getPort());
                }
            } else {
                if(route.getTargetHost() != null) {
                    sb.append(route.getTargetHost().getHostName());
                    if(route.getTargetHost().getPort() > 0) {
                        sb.append(":").append(route.getTargetHost().getPort());
                    }
                }
            }
            recorder.recordAttribute(AnnotationKey.HTTP_INTERNAL_DISPLAY, sb.toString());
        }
        recorder.recordApi(methodDescriptor);
        recorder.recordServiceType(HttpClient4Constants.HTTP_CLIENT_4_INTERNAL);
    }

    @Override
    protected void doInAfterTrace(SpanEventRecorder recorder, Object target, Object[] args, Object result, Throwable throwable) {
        recorder.recordException(throwable);
    }
}