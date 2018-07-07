/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.nibeuplink.internal.model;

/**
 * list of all available channels
 *
 * @author Alexander Friese - initial contribution
 *
 */
public enum CustomChannels implements Channel {

    // Custom Channels
    CH_CH01("00000", "CH01", ChannelGroup.CUSTOM, ValueType.NUMBER),
    CH_CH02("00000", "CH02", ChannelGroup.CUSTOM, ValueType.NUMBER),
    CH_CH03("00000", "CH03", ChannelGroup.CUSTOM, ValueType.NUMBER),
    CH_CH04("00000", "CH04", ChannelGroup.CUSTOM, ValueType.NUMBER),
    CH_CH05("00000", "CH05", ChannelGroup.CUSTOM, ValueType.NUMBER),
    CH_CH06("00000", "CH06", ChannelGroup.CUSTOM, ValueType.NUMBER),
    CH_CH07("00000", "CH07", ChannelGroup.CUSTOM, ValueType.NUMBER),
    CH_CH08("00000", "CH08", ChannelGroup.CUSTOM, ValueType.NUMBER),

    /* END */
    ;

    private String id;
    private final String name;
    private final ChannelGroup channelGroup;
    private final ValueType valueType;
    private final String writeApiUrl;
    private final String validationExpression;

    /**
     * constructor for channels with wrote access enabled
     *
     * @param id
     * @param name
     * @param channelType
     * @param channelGroup
     * @param javaType
     * @param writeApiUrl
     * @param validationExpression
     */
    CustomChannels(String id, String name, ChannelGroup channelGroup, ValueType valueType, String writeApiUrl,
            String validationExpression) {
        this.id = id;
        this.name = name;
        this.channelGroup = channelGroup;
        this.valueType = valueType;
        this.writeApiUrl = writeApiUrl;
        this.validationExpression = validationExpression;
    }

    /**
     * constructor for channels without write access
     *
     * @param id
     * @param name
     * @param channelType
     * @param channelGroup
     * @param javaType
     */
    private CustomChannels(String id, String name, ChannelGroup channelGroup, ValueType valueType) {
        this(id, name, channelGroup, valueType, null, null);
    }

    public static CustomChannels fromId(String id) {
        for (CustomChannels channel : CustomChannels.values()) {
            if (channel.id != null && channel.id.equals(id)) {
                return channel;
            }
        }
        return null;
    }

    @Override
    public final String getName() {
        return name;
    }

    public final void setId(Integer id) {
        this.id = id == null ? null : id.toString();
    }

    @Override
    public final String getId() {
        return id;
    }

    @Override
    public ChannelGroup getChannelGroup() {
        return channelGroup;
    }

    @Override
    public final ValueType getValueType() {
        return valueType;
    }

    @Override
    public String getFQName() {
        return getChannelGroup().toString().toLowerCase() + "#" + getName();
    }

    @Override
    public String getWriteApiUrlSuffix() {
        return writeApiUrl;
    }

    @Override
    public boolean isReadOnly() {
        return writeApiUrl == null || writeApiUrl.isEmpty();
    }

    @Override
    public String getValidationExpression() {
        return validationExpression;
    }

}