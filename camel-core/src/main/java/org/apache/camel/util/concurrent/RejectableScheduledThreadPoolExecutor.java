/**
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
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

/**
 * Scheduled thread pool executor that creates {@link RejectableFutureTask} instead of
 * {@link java.util.concurrent.FutureTask} when registering new tasks for execution.
 * <p/>
 * Instances of {@link RejectableFutureTask} are required to handle {@link org.apache.camel.ThreadPoolRejectedPolicy#Discard}
 * and {@link org.apache.camel.ThreadPoolRejectedPolicy#DiscardOldest} policies correctly, e.g. notify
 * {@link Callable} and {@link Runnable} tasks when they are rejected.
 * To be notified of rejection tasks have to implement {@link org.apache.camel.Rejectable} interface: <br/>
 * <code><pre>
 * public class RejectableTask implements Runnable, Rejectable {
 *     &#064;Override
 *     public void run() {
 *         // execute task
 *     }
 *     &#064;Override
 *     public void reject() {
 *         // do something useful on rejection
 *     }
 * }
 * </pre></code>
 * <p/>
 * If the task does not implement {@link org.apache.camel.Rejectable} interface the behavior is exactly the same as with
 * ordinary {@link ScheduledThreadPoolExecutor}.
 *
 * @see RejectableFutureTask
 * @see org.apache.camel.Rejectable
 * @see RejectableThreadPoolExecutor
 */
public class RejectableScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor {

    public RejectableScheduledThreadPoolExecutor(int corePoolSize) {
        super(corePoolSize);
    }

    public RejectableScheduledThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory) {
        super(corePoolSize, threadFactory);
    }

    public RejectableScheduledThreadPoolExecutor(int corePoolSize, RejectedExecutionHandler handler) {
        super(corePoolSize, handler);
    }

    public RejectableScheduledThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, threadFactory, handler);
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        return new RejectableFutureTask<T>(runnable, value);
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return new RejectableFutureTask<T>(callable);
    }

}
