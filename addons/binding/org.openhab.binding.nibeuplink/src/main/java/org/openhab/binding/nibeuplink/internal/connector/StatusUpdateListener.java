/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.nibeuplink.internal.connector;

import org.eclipse.jetty.http.HttpStatus;

/**
 * callback interface to update the status of the {@link UplinkWebInterface}
 * 
 * @author afriese
 *
 */
public interface StatusUpdateListener {

    public void update(HttpStatus.Code status);

}
