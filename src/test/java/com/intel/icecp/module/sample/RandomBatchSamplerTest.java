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

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RandomBatchSamplerTest {
    public int count;

    @Test
    public void testConstructorWithNegativeNumber() {
        RandomBatchSampler sampler = new RandomBatchSampler(-1);
        assertEquals(sampler.getBatchSize(), RandomBatchSampler.DEFAULT_BATCH_SIZE);
    }

    @Test
    public void testTestRandomItemPick() {
        count = 0;
        int batchSize = 10;
        RandomBatchSampler sampler = new RandomBatchSampler(batchSize);
        sampler.out(o -> count++);

        for (int i = 0; i < batchSize; i++) {
            sampler.in(i);
        }
        assertTrue(count == 1);

        for (int i = 0; i < batchSize; i++) {
            sampler.in(i);
        }
        assertTrue(count == 2);
    }
}
