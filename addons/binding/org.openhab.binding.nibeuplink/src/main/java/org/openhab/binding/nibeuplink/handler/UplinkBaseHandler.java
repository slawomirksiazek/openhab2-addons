/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.nibeuplink.handler;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.eclipse.smarthome.core.types.State;
import org.openhab.binding.nibeuplink.config.NibeUplinkConfiguration;
import org.openhab.binding.nibeuplink.internal.command.UpdateSetting;
import org.openhab.binding.nibeuplink.internal.connector.UplinkWebInterface;
import org.openhab.binding.nibeuplink.internal.model.Channel;
import org.openhab.binding.nibeuplink.internal.model.CustomChannels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link UplinkBaseHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Alexander Friese - initial contribution
 */
@NonNullByDefault
public abstract class UplinkBaseHandler extends BaseThingHandler implements NibeUplinkHandler {
    private final Logger logger = LoggerFactory.getLogger(UplinkBaseHandler.class);

    private Set<Channel> deadChannels = new HashSet<>(100);

    /**
     * Refresh interval which is used to poll values from the NibeUplink web interface (optional, defaults to 60 s)
     */
    private int refreshInterval;

    /**
     * Refresh interval which is used clean the dead channel list (optional, defaults to 1 h)
     */
    private int houseKeepingInterval = 1;

    /**
     * Interface object for querying the FRITZ!Box web interface
     */
    private UplinkWebInterface webInterface;

    /**
     * Schedule for polling
     */
    private AtomicReference<@Nullable ScheduledFuture<?>> pollingJobReference = new AtomicReference<@Nullable ScheduledFuture<?>>(
            null);

    /**
     * Schedule for periodic cleaning dead channel list
     */
    private AtomicReference<@Nullable ScheduledFuture<?>> deadChannelHouseKeepingReference = new AtomicReference<@Nullable ScheduledFuture<?>>(
            null);;

    public UplinkBaseHandler(Thing thing, HttpClient httpClient) {
        super(thing);
        this.webInterface = new UplinkWebInterface(getConfiguration(), scheduler, this, httpClient);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (!(command instanceof RefreshType)) {
            logger.debug("command for {}: {}", channelUID.getIdWithoutGroup(), command.toString());
            Channel channel = getSpecificChannel(channelUID.getIdWithoutGroup());
            if (channel != null && !channel.isReadOnly()) {
                webInterface.enqueueCommand(new UpdateSetting(this, channel, command));
            }
        }
    }

    @Override
    public void initialize() {
        logger.debug("About to initialize NibeUplink");
        NibeUplinkConfiguration config = getConfiguration();

        logger.debug("NibeUplink initialized with configuration: {}", config);

        setupCustomChannels(config);
        this.refreshInterval = config.getPollingInterval();
        this.houseKeepingInterval = config.getHouseKeepingInterval();

        startPolling();
        webInterface.start();
    }

    /**
     * initialize the custom channels out of the configuration
     *
     * @param config
     */
    private void setupCustomChannels(NibeUplinkConfiguration config) {
        CustomChannels.CH_CH01.setCode(config.getCustomChannel01());
        CustomChannels.CH_CH02.setCode(config.getCustomChannel02());
        CustomChannels.CH_CH03.setCode(config.getCustomChannel03());
        CustomChannels.CH_CH04.setCode(config.getCustomChannel04());
        CustomChannels.CH_CH05.setCode(config.getCustomChannel05());
        CustomChannels.CH_CH06.setCode(config.getCustomChannel06());
        CustomChannels.CH_CH07.setCode(config.getCustomChannel07());
        CustomChannels.CH_CH08.setCode(config.getCustomChannel08());
    }

    /**
     * Start the polling.
     */
    private void startPolling() {
        ScheduledFuture<?> job = pollingJobReference.get();
        if (job == null || job.isCancelled()) {
            logger.debug("start polling job at intervall {}", refreshInterval);
            ScheduledFuture<?> newJob = scheduler.scheduleWithFixedDelay(new UplinkPolling(this), 30, refreshInterval,
                    TimeUnit.SECONDS);
            if (pollingJobReference.compareAndSet(job, newJob)) {
                logger.debug("updated 'pollingJobReference'");
            } else {
                logger.info("detected concurrent modification of job 'pollingJobReference'");
                newJob.cancel(true);
            }
        } else {
            logger.debug("pollingJob already active");
        }

        job = deadChannelHouseKeepingReference.get();
        if (job == null || job.isCancelled()) {
            logger.debug("start deadChannelHouseKeeping job at intervall {}", houseKeepingInterval);
            ScheduledFuture<?> newJob = scheduler.scheduleWithFixedDelay(deadChannels::clear, 300, houseKeepingInterval,
                    TimeUnit.SECONDS);
            if (deadChannelHouseKeepingReference.compareAndSet(job, newJob)) {
                logger.debug("updated 'pollingJobReference'");
            } else {
                logger.info("detected concurrent modification of job 'pollingJobReference'");
                newJob.cancel(true);
            }

        } else {
            logger.debug("deadChannelHouseKeeping already active");
        }
    }

    /**
     * Disposes the bridge.
     */
    @Override
    public void dispose() {
        logger.debug("Handler disposed.");

        ScheduledFuture<?> job = pollingJobReference.get();
        if (job != null && !job.isCancelled()) {
            logger.debug("stop polling job");
            job.cancel(true);
            pollingJobReference.compareAndSet(job, null);
        }
        job = deadChannelHouseKeepingReference.get();
        if (job != null && !job.isCancelled()) {
            logger.debug("stop polling job");
            job.cancel(true);
            deadChannelHouseKeepingReference.compareAndSet(job, null);
        }

        // the webinterface also makes use of the scheduler and must stop it's jobs
        webInterface.dispose();
    }

    @Override
    public UplinkWebInterface getWebInterface() {
        return webInterface;
    }

    /**
     * will update all channels provided in the map
     */
    @Override
    public void updateChannelStatus(Map<Channel, State> values) {
        logger.debug("Handling channel update. ({} Channels)", values.size());

        for (Channel channel : values.keySet()) {
            if (getChannels().contains(channel)) {
                State value = values.get(channel);
                logger.debug("Channel is to be updated: {}: {}", channel.getFQName(), value);
                updateState(channel.getFQName(), value);
            } else {
                logger.debug("Could not identify channel: {} for model {}", channel.getFQName(),
                        getThing().getThingTypeUID().getAsString());
            }
        }
    }

    @Override
    public Set<Channel> getDeadChannels() {
        return deadChannels;
    }

    @Override
    public void setStatusInfo(ThingStatus status, ThingStatusDetail statusDetail, String description) {
        super.updateStatus(status, statusDetail, description);
    }

    @Override
    public NibeUplinkConfiguration getConfiguration() {
        return this.getConfigAs(NibeUplinkConfiguration.class);
    }

}
