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

public class TimeBasedSampler implements Sampler<Object> {

    public static final int DEFAULT_DURATION_SIZE = 10;
    private final long samplingDuration;
    private final long from;
    private Consumer<Object> onSampleReady;
    private long lastSampledPeriod;

    /**
     * Constructor
     *
     * @param samplingDuration
     *            samplingDuration, will use DEFAULT_DURATION_SIZE if invalid
     *            value used.
     */
    public TimeBasedSampler(long samplingDuration) {
        this.samplingDuration = samplingDuration <= 0 ? DEFAULT_DURATION_SIZE : samplingDuration;
        this.from = System.currentTimeMillis();
        lastSampledPeriod = -1;
    }

    @Override
    public void in(Object sample) {
        long elapsed = System.currentTimeMillis() - from;
        long period = elapsed / samplingDuration;
        if (period != lastSampledPeriod) {
            onSampleReady.accept(sample);
            lastSampledPeriod = period;
        }
    }

    @Override
    public void out(Consumer<Object> onSampleReady) {
        this.onSampleReady = onSampleReady;
    }

    /**
     * Gets the sample duration for this sampler
     *
     * @return Sampler sample duration
     */
    public long getSamplingDuration() {
        return samplingDuration;
    }

    /**
     * Get the starting point for the time periods
     *
     * @return Starting time in milliseconds for the time based sampler
     */
    public long getFrom() {
        return from;
    }

}
