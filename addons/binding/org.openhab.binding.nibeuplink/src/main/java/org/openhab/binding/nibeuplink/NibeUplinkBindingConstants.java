/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.nibeuplink;

import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.smarthome.core.thing.ThingTypeUID;

import com.google.common.collect.ImmutableSet;

/**
 * The {@link NibeUplinkBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Alexander Friese - initial contribution
 *
 */
public final class NibeUplinkBindingConstants {

    private static final @NonNull String BINDING_ID = "nibeuplink";

    // List of main device types
    public static final @NonNull String DEVICE_VVM320 = "vvm320";
    public static final @NonNull String DEVICE_VVM310 = "vvm310";
    public static final @NonNull String DEVICE_F730 = "f730";
    public static final @NonNull String DEVICE_F750 = "f750";
    public static final @NonNull String DEVICE_F1145 = "f1145";
    public static final @NonNull String DEVICE_F1155 = "f1155";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_VVM320 = new ThingTypeUID(BINDING_ID, DEVICE_VVM320);
    public static final ThingTypeUID THING_TYPE_VVM310 = new ThingTypeUID(BINDING_ID, DEVICE_VVM310);
    public static final ThingTypeUID THING_TYPE_F730 = new ThingTypeUID(BINDING_ID, DEVICE_F730);
    public static final ThingTypeUID THING_TYPE_F750 = new ThingTypeUID(BINDING_ID, DEVICE_F750);
    public static final ThingTypeUID THING_TYPE_F1145 = new ThingTypeUID(BINDING_ID, DEVICE_F1145);
    public static final ThingTypeUID THING_TYPE_F1155 = new ThingTypeUID(BINDING_ID, DEVICE_F1155);

    // List of all Channel ids ==> see UplinkDataChannels

    // URLs
    public static final String LOGIN_URL = "https://www.nibeuplink.com/LogIn";
    public static final String DATA_API_URL = "https://www.nibeuplink.com/PrivateAPI/QueueValues";
    public static final String MANAGE_API_BASE_URL = "https://www.nibeuplink.com/System/";

    // login field names
    public static final String LOGIN_FIELD_PASSWORD = "Password";
    public static final String LOGIN_FIELD_EMAIL = "Email";
    public static final String LOGIN_FIELD_RETURN_URL = "returnUrl";

    // other field names
    public static final String DATA_API_FIELD_LAST_DATE = "currentWebDate";
    public static final String DATA_API_FIELD_LAST_DATE_DEFAULT_VALUE = "01.01.2017 13:37:42";
    public static final String DATA_API_FIELD_ID = "hpid";
    public static final String DATA_API_FIELD_DATA = "variables";
    public static final String DATA_API_FIELD_DATA_DEFAULT_VALUE = "0";

    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = ImmutableSet.of(THING_TYPE_VVM320,
            THING_TYPE_VVM310, THING_TYPE_F730, THING_TYPE_F750, THING_TYPE_F1145, THING_TYPE_F1155);

}
