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


import org.junit.Assert;
import org.junit.Test;

import com.opentable.lifecycle.DefaultLifecycle;
import com.opentable.lifecycle.Lifecycle;
import com.opentable.lifecycle.ServiceDiscoveryLifecycle;
import com.opentable.lifecycle.guice.LifecycleModule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;

public class TestLifecycleModule
{
    @Test
    public void testSimple()
    {
        final Injector injector = Guice.createInjector(Stage.PRODUCTION,
                                                       new LifecycleModule());

        final Lifecycle lifecycle = injector.getInstance(Lifecycle.class);

        Assert.assertEquals(DefaultLifecycle.class, lifecycle.getClass());
    }

    @Test
    public void testDefined()
    {
        final Injector injector = Guice.createInjector(Stage.PRODUCTION,
                                                       new LifecycleModule(ServiceDiscoveryLifecycle.class));

        final Lifecycle lifecycle = injector.getInstance(Lifecycle.class);

        Assert.assertEquals(ServiceDiscoveryLifecycle.class, lifecycle.getClass());
    }
}

