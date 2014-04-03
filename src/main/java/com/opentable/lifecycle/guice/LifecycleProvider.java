/**
 * Copyright (C) 2012 Ness Computing, Inc.
 *
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


import com.google.inject.Provider;

import com.opentable.lifecycle.LifecycleStage;

/**
 * Describes a guice provider that support lifecycle actions.
 */
public interface LifecycleProvider<T> extends Provider<T>
{
    /**
     * Add a lifecycle Action to this provider. The action will called back when the lifecycle stage is
     * hit and contain an object that was created by the provider.
     */
    void addAction(LifecycleStage stage, LifecycleAction<T> action);
}