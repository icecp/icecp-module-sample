/*
 * ******************************************************************************
 *
 *  INTEL CONFIDENTIAL
 *
 *  Copyright 2013 - 2016 Intel Corporation All Rights Reserved.
 *
 *  The source code contained or described herein and all documents related to the
 *  source code ("Material") are owned by Intel Corporation or its suppliers or
 *  licensors. Title to the Material remains with Intel Corporation or its
 *  suppliers and licensors. The Material contains trade secrets and proprietary
 *  and confidential information of Intel or its suppliers and licensors. The
 *  Material is protected by worldwide copyright and trade secret laws and treaty
 *  provisions. No part of the Material may be used, copied, reproduced, modified,
 *  published, uploaded, posted, transmitted, distributed, or disclosed in any way
 *  without Intel's prior express written permission.
 *
 *  No license under any patent, copyright, trade secret or other intellectual
 *  property right is granted to or conferred upon you by disclosure or delivery of
 *  the Materials, either expressly, by implication, inducement, estoppel or
 *  otherwise. Any license under such intellectual property rights must be express
 *  and approved by Intel in writing.
 *
 *  Unless otherwise agreed by Intel in writing, you may not remove or alter this
 *  notice or any other notice embedded in Materials by Intel or Intel's suppliers
 *  or licensors in any way.
 *
 * *******************************************************************************
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
