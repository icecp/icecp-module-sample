
/*
 * Copyright (c) 2017 Intel Corporation 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intel.icecp.module.sample;

import java.util.function.Consumer;

/**
 * Define an API for sampling objects from a stream; e.g.:
 *
 * <pre>{@code
 * sampler.observe((Object sample) -> process(sample));
 * sampler.add(m1);
 * sampler.add(m2); sampler.add(m3);
 * }</pre>
 *
 * @param <T> if necessary, define the type of objects observed
 */
public interface Sampler<T> {

    /**
     * Add objects to the stream observed by the sampler
     *
     * @param object the object to append to the stream
     */
    void in(T object);

    /**
     * Observe the sampler to receive notifications of samples pulled from the
     * stream of objects
     *
     * @param onSampleReady callback fired when a sample is pulled
     */
    void out(Consumer<T> onSampleReady);
}
