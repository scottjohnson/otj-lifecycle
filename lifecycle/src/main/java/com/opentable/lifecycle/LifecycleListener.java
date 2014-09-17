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
package com.opentable.lifecycle;

/**
 * Implemented by classes that want to get a callback from the lifecycle. An instance can be registered in multiple stages.
 */
@FunctionalInterface
public interface LifecycleListener
{
    /**
     * Called when a stage is hit.
     * @param lifecycleStage A LifecycleStage object representing the stage that was hit.
     */
    void onStage(final LifecycleStage lifecycleStage);
}