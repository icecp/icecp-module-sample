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

import java.security.SecureRandom;
import java.util.Random;
import java.util.function.Consumer;

public class RandomBatchSampler implements Sampler<Object> {

    public static final int DEFAULT_BATCH_SIZE = 10;
    private final int batchSize;
    private int nextPick = -1;
    private Consumer<Object> onSampleReady;
    private int sampleCount;
    private Random random;

    /**
     * Constructor
     *
     * @param batchSize
     *            batch size, will use DEFAULT_BATCH_SIZE if invalid value used.
     */
    public RandomBatchSampler(int batchSize) {
        this.batchSize = batchSize <= 0 ? DEFAULT_BATCH_SIZE : batchSize;
        this.sampleCount = 0;
        this.random = new SecureRandom();
    }

    @Override
    public void in(Object sample) {
        // sampleCount of 0 means we are starting a new batch
        if (sampleCount == 0) {
            nextPick = random.nextInt(this.batchSize) + 1;
        }
        sampleCount++;
        if (sampleCount == nextPick) {
            onSampleReady.accept(sample);
        }

        sampleCount = (sampleCount == batchSize) ? 0 : sampleCount;
    }

    @Override
    public void out(Consumer<Object> onSampleReady) {
        this.onSampleReady = onSampleReady;
    }

    /**
     * Gets the batch size for this sampler
     *
     * @return Sampler batch size
     */
    public int getBatchSize() {
        return batchSize;
    }
}
