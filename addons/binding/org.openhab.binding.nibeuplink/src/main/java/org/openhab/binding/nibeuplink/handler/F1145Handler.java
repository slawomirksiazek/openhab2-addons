/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.nibeuplink.handler;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.Thing;
import org.openhab.binding.nibeuplink.internal.model.Channel;
import org.openhab.binding.nibeuplink.internal.model.CustomChannels;
import org.openhab.binding.nibeuplink.internal.model.F1145Channels;

/**
 * F1145 specific implementation part of handler logic
 *
 * @author Alexander Friese - initial contribution
 *
 */
public class F1145Handler extends GenericUplinkHandler {

    public F1145Handler(@NonNull Thing thing) {
        super(thing);
    }

    @Override
    protected @NonNull List<Channel> getAllSpecificChannels(String id) {
        List<Channel> channels = new ArrayList<>(2);

        Channel channel = F1145Channels.fromId(id);
        if (channel != null) {
            channels.add(channel);
        }

        channel = CustomChannels.fromId(id);
        if (channel != null) {
            channels.add(channel);
        }

        return channels;
    }

    @Override
    protected @Nullable Channel getSpecificChannel(String id) {
        return F1145Channels.fromId(id);
    }

    @Override
    public List<Channel> getChannels() {
        return getChannels(F1145Channels.values());
    }

}
