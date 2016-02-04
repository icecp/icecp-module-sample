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
