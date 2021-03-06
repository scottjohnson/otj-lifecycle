/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opentable.lifecycle.guice;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;

import org.junit.Test;

import com.opentable.lifecycle.Lifecycle;
import com.opentable.lifecycle.LifecycleStage;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.ProvisionException;
import com.google.inject.util.Providers;

public class TestLifecycleAnnotations {

    public static class LifecycleTest {
        boolean isStarted = false;
        boolean isStopped = false;

        @OnStage(LifecycleStage.START)
        public void startLifecycle() {
            isStarted = true;
        }

        @OnStage(LifecycleStage.STOP)
        public void stopLifecycle() {
            isStopped = true;
        }
    }

    @Inject
    Lifecycle lifecycle;

    @Inject
    @Nullable
    LifecycleTest tester;

    @Test
    public void testLifecycleAnnotationsOnClass() {
        Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                binder().requireExplicitBindings();
                binder().disableCircularProxies();

                install (new LifecycleModule());
                bind (LifecycleTest.class);

                requestInjection(TestLifecycleAnnotations.this);
            }
        });

        assertFalse(tester.isStarted);
        assertFalse(tester.isStopped);

        lifecycle.executeTo(LifecycleStage.START_STAGE);

        assertTrue(tester.isStarted);
        assertFalse(tester.isStopped);

        lifecycle.executeTo(LifecycleStage.STOP_STAGE);

        assertTrue(tester.isStarted);
        assertTrue(tester.isStopped);
    }

    @Test
    public void testLifecycleAnnotationsOnInstance() {
        Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                binder().requireExplicitBindings();
                binder().disableCircularProxies();

                install (new LifecycleModule());
                bind (LifecycleTest.class).toInstance(new LifecycleTest());

                requestInjection(TestLifecycleAnnotations.this);
            }
        });

        assertFalse(tester.isStarted);
        assertFalse(tester.isStopped);

        lifecycle.executeTo(LifecycleStage.START_STAGE);

        assertTrue(tester.isStarted);
        assertFalse(tester.isStopped);

        lifecycle.executeTo(LifecycleStage.STOP_STAGE);

        assertTrue(tester.isStarted);
        assertTrue(tester.isStopped);
    }

    @Test
    public void testLifecycleAnnotationsOnSuperclass() {
        final AtomicBoolean isConfigured = new AtomicBoolean();
        Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                binder().requireExplicitBindings();
                binder().disableCircularProxies();

                install (new LifecycleModule());
                bind (LifecycleTest.class).toInstance(new LifecycleTest() {
                    @SuppressWarnings("unused")
                    @OnStage(LifecycleStage.CONFIGURE)
                    void configure() {
                        Preconditions.checkState(isStarted == false && isStopped == false);
                        isConfigured.set(true);
                    }
                });

                requestInjection(TestLifecycleAnnotations.this);
            }
        });

        assertFalse(isConfigured.get());
        assertFalse(tester.isStarted);
        assertFalse(tester.isStopped);

        lifecycle.executeTo(LifecycleStage.START_STAGE);

        assertTrue(isConfigured.get());
        assertTrue(tester.isStarted);
        assertFalse(tester.isStopped);

        lifecycle.executeTo(LifecycleStage.STOP_STAGE);

        assertTrue(tester.isStarted);
        assertTrue(tester.isStopped);
    }

    @Test
    public void testLifecycleAnnotationsOnProvider() {
        final AtomicBoolean isConfigured = new AtomicBoolean();
        Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                binder().requireExplicitBindings();
                binder().disableCircularProxies();

                install (new LifecycleModule());
                bind (LifecycleTest.class).toProvider(() -> new LifecycleTest() {
                    @SuppressWarnings("unused")
                    @OnStage(LifecycleStage.CONFIGURE)
                    void configure() {
                        Preconditions.checkState(isStarted == false && isStopped == false);
                        isConfigured.set(true);
                    }
                });

                requestInjection(TestLifecycleAnnotations.this);
            }
        });

        assertFalse(isConfigured.get());
        assertFalse(tester.isStarted);
        assertFalse(tester.isStopped);

        lifecycle.executeTo(LifecycleStage.START_STAGE);

        assertTrue(isConfigured.get());
        assertTrue(tester.isStarted);
        assertFalse(tester.isStopped);

        lifecycle.executeTo(LifecycleStage.STOP_STAGE);

        assertTrue(tester.isStarted);
        assertTrue(tester.isStopped);
    }

    @Test(expected=ProvisionException.class)
    public void testLifecycleAnnotationsAfterStartFails() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                binder().requireExplicitBindings();
                binder().disableCircularProxies();

                install (new LifecycleModule());
                bind (LifecycleTest.class);

                requestInjection(TestLifecycleAnnotations.this);
            }
        });
        lifecycle.executeTo(LifecycleStage.START_STAGE);

        injector.getInstance(LifecycleTest.class); // Boom!
    }

    @Test
    public void testNullProvider() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                binder().requireExplicitBindings();
                binder().disableCircularProxies();

                install (new LifecycleModule());
                bind (LifecycleTest.class).toProvider(Providers.of(null));

                requestInjection(TestLifecycleAnnotations.this);
            }
        });
        lifecycle.executeTo(LifecycleStage.START_STAGE);

        injector.getInstance(LifecycleTest.class);
    }
}
