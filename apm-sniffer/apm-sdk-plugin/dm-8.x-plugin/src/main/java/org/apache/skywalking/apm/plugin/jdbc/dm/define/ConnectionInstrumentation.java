/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.skywalking.apm.plugin.jdbc.dm.define;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.ClassInstanceMethodsEnhancePluginDefine;
import org.apache.skywalking.apm.agent.core.plugin.match.ClassMatch;
import org.apache.skywalking.apm.agent.core.plugin.match.NameMatch;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;
import static org.apache.skywalking.apm.plugin.jdbc.define.Constants.SERVICE_METHOD_INTERCEPT_CLASS;

public class ConnectionInstrumentation extends ClassInstanceMethodsEnhancePluginDefine {
    public static final String ENHANCE_CLASS = "dm.jdbc.driver.DmdbConnection";

    public static final String PREPARED_STATEMENT_INTERCEPT_CLASS = "org.apache.skywalking.apm.plugin.jdbc.dm.CreatePreparedStatementInterceptor";

    public static final String CALLABLE_INTERCEPT_CLASS = "org.apache.skywalking.apm.plugin.jdbc.dm.CreateCallableInterceptor";

    public static final String CREATE_STATEMENT_INTERCEPT_CLASS = "org.apache.skywalking.apm.plugin.jdbc.dm.CreateStatementInterceptor";

    public ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return new ConstructorInterceptPoint[0];
    }

    public InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[]{new InstanceMethodsInterceptPoint() {
            public ElementMatcher<MethodDescription> getMethodsMatcher() {
                return named("prepareStatement");
            }

            public String getMethodsInterceptor() {
                return PREPARED_STATEMENT_INTERCEPT_CLASS;
            }

            public boolean isOverrideArgs() {
                return false;
            }
        }, new InstanceMethodsInterceptPoint() {
            public ElementMatcher<MethodDescription> getMethodsMatcher() {
                return named("prepareCall").and(takesArguments(3));
            }

            public String getMethodsInterceptor() {
                return CALLABLE_INTERCEPT_CLASS;
            }

            public boolean isOverrideArgs() {
                return false;
            }
        }, new InstanceMethodsInterceptPoint() {
            public ElementMatcher<MethodDescription> getMethodsMatcher() {
                return named("createStatement").and(takesArguments(2));
            }

            public String getMethodsInterceptor() {
                return CREATE_STATEMENT_INTERCEPT_CLASS;
            }

            public boolean isOverrideArgs() {
                return false;
            }
        }, new InstanceMethodsInterceptPoint() {
            public ElementMatcher<MethodDescription> getMethodsMatcher() {
                return named("commit").or(named("rollback")).or(named("close")).or(named("releaseSavepoint"));
            }

            public String getMethodsInterceptor() {
                return SERVICE_METHOD_INTERCEPT_CLASS;
            }

            public boolean isOverrideArgs() {
                return false;
            }
        } };
    }

    protected ClassMatch enhanceClass() {
        return (ClassMatch) NameMatch.byName(ENHANCE_CLASS);
    }
}
