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

import com.intel.icecp.core.Channel;
import com.intel.icecp.core.Message;
import com.intel.icecp.core.Module;
import com.intel.icecp.core.Node;
import com.intel.icecp.core.messages.BytesMessage;
import com.intel.icecp.core.metadata.Persistence;
import com.intel.icecp.core.misc.ChannelIOException;
import com.intel.icecp.core.misc.ChannelLifetimeException;
import com.intel.icecp.core.misc.Configuration;
import com.intel.icecp.core.misc.PropertyNotFoundException;
import com.intel.icecp.core.modules.ModuleProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;

/**
 * Sample a subset of messages from an input channel and re-publish them on an
 * output channel
 *
 */
@ModuleProperty(name = "sample")
public class SampleModule implements Module {

    private static final Logger logger = LogManager.getLogger();
    private Channel outputChannel;
    private Channel inputChannel;

    @Override
    public void run(Node node, Configuration moduleConfiguration, Channel<State> moduleStateChannel, long moduleId) {
        try {
            // configure channels
            int persistFor = moduleConfiguration.getOrDefault(10000, "persistence");
            inputChannel = openChannel(node, moduleConfiguration.get("input"), persistFor);
            outputChannel = openChannel(node, moduleConfiguration.get("input"), persistFor);
            int every = moduleConfiguration.get("every");

            // observe and publish sampled messages
            Sampler<Message> sampler = new EveryNSampler(every);
            sampler.out((Message m) -> {
                try {
                    outputChannel.publish(m);
                } catch (ChannelIOException ex) {
                    logger.error("Failed to forward on sampled message", ex);
                }
            });

            // start observing input channel messages
            inputChannel.subscribe((Message m) -> {
                sampler.in(m);
            });
        } catch (PropertyNotFoundException | ChannelIOException | ChannelLifetimeException ex) {
            logger.error(ex);
            node.modules().stop(moduleId, StopReason.USER_DIRECTED);
        }
    }

    @Override
    public void stop(StopReason reason) {
        try {
            inputChannel.close();
            outputChannel.close();
        } catch (ChannelLifetimeException ex) {
            logger.error(ex);
        }
    }

    /**
     * Convenience method for opening necessary channels
     *
     * @param node Node to open the channel on
     * @param channelName Name of the channel
     * @param persistFor Period of time in milliseconds to persist the data for
     * @return the opened {@link Channel}
     * @throws ChannelLifetimeException
     */
    private Channel openChannel(Node node, String channelName, int persistFor) throws ChannelLifetimeException {
        URI uri = URI.create(channelName);
        Persistence persistence = new Persistence(persistFor);
        return node.openChannel(uri, BytesMessage.class, persistence);
    }
}
