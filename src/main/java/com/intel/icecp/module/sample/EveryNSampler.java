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
 * Sample a stream of objects, returning every nth object to the registered
 * callback in {@link #out(java.util.function.Consumer)}. Currently this
 * class only supports one callback at a time; subsequent calls to
 * {@link #out(java.util.function.Consumer)} will replace the consumer. This
 * sampler will always return the first object observed.
 *
 */
public class EveryNSampler implements Sampler {

    private final int sampleSize;
    private Consumer onSampleReady;
    private int observedSamples;

    /**
     * @param sampleSize the number of objects to see before returning a sample
     */
    public EveryNSampler(int sampleSize) {
        this.sampleSize = sampleSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void out(Consumer onSampleReady) {
        this.onSampleReady = onSampleReady;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void in(Object sample) {
        if (observedSamples == 0) {
            onSampleReady.accept(sample);
        }
        observedSamples++;
        observedSamples = (observedSamples == sampleSize) ? 0 : observedSamples;
    }
}
