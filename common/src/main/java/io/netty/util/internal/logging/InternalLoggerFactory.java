/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package io.netty.util.internal.logging;

/**
 * Creates an {@link InternalLogger} or changes the default factory
 * implementation.  This factory allows you to choose what logging framework
 * Netty should use.  The default factory is {@link Slf4JLoggerFactory}.  If SLF4J
 * is not available, {@link Log4JLoggerFactory} is used.  If Log4J is not available,
 * {@link JdkLoggerFactory} is used.  You can change it to your preferred
 * logging framework before other Netty classes are loaded:
 * 创建{@link InternalLogger}或更改默认工厂*实现。这个工厂允许您选择Netty应该使用的日志框架。默认工厂是{@link Slf4JLoggerFactory}。
 * 如果SLF4J *不可用，则使用{@link Log4JLoggerFactory}。如果Log4J不可用，则使用* {@link JdkLoggerFactory}。在加载其他Netty类之前，您可以将其更改为首选*日志框架：
 * <pre>
 * {@link InternalLoggerFactory}.setDefaultFactory({@link Log4JLoggerFactory}.INSTANCE);
 * {@link InternalLoggerFactory} .setDefaultFactory（{@ link Log4JLoggerFactory} .INSTANCE);
 * </pre>
 * Please note that the new default factory is effective only for the classes
 * which were loaded after the default factory is changed.  Therefore,
 * {@link #setDefaultFactory(InternalLoggerFactory)} should be called as early
 * as possible and shouldn't be called more than once.
 * 请注意，新的默认工厂仅对更改默认工厂后加载的类*有效。因此，* {@link #setDefaultFactory（InternalLoggerFactory）}应尽可能早地调用，不应多次调用。
 */
public abstract class InternalLoggerFactory {

    private static volatile InternalLoggerFactory defaultFactory;

    @SuppressWarnings("UnusedCatchParameter")
    private static InternalLoggerFactory newDefaultFactory(String name) {
        InternalLoggerFactory f;
        try {
            f = new Slf4JLoggerFactory(true);
            f.newInstance(name).debug("Using SLF4J as the default logging framework");
        } catch (Throwable ignore1) {
            try {
                f = Log4JLoggerFactory.INSTANCE;
                f.newInstance(name).debug("Using Log4J as the default logging framework");
            } catch (Throwable ignore2) {
                try {
                    f = Log4J2LoggerFactory.INSTANCE;
                    f.newInstance(name).debug("Using Log4J2 as the default logging framework");
                } catch (Throwable ignore3) {
                    f = JdkLoggerFactory.INSTANCE;
                    f.newInstance(name).debug("Using java.util.logging as the default logging framework");
                }
            }
        }
        return f;
    }

    /**
     * Returns the default factory.  The initial default factory is {@link JdkLoggerFactory}.
     * 返回默认工厂。最初的默认工厂是{@link JdkLoggerFactory}.
     */
    public static InternalLoggerFactory getDefaultFactory() {
        if (defaultFactory == null) {
            defaultFactory = newDefaultFactory(InternalLoggerFactory.class.getName());
        }
        return defaultFactory;
    }

    /**
     * Changes the default factory.更改默认工厂。
     */
    public static void setDefaultFactory(InternalLoggerFactory defaultFactory) {
        if (defaultFactory == null) {
            throw new NullPointerException("defaultFactory");
        }
        InternalLoggerFactory.defaultFactory = defaultFactory;
    }

    /**
     * 使用指定类的名称创建新的记录器实例。
     */
    public static InternalLogger getInstance(Class<?> clazz) {
        return getInstance(clazz.getName());
    }

    /**
     * 创建具有指定名称的新记录器实例。
     */
    public static InternalLogger getInstance(String name) {
        return getDefaultFactory().newInstance(name);
    }

    /**
     * 创建具有指定名称的新记录器实例。
     */
    protected abstract InternalLogger newInstance(String name);

}
