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
