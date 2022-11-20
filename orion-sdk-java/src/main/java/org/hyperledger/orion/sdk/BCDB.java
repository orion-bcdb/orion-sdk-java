// Copyright IBM Corp. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package org.hyperledger.orion.sdk;

import org.hyperledger.orion.sdk.config.SessionConfig;

/**
 * BCDB Blockchain Database instance interface.
 *
 */
public interface BCDB {
	/**
	 * Session instantiates session to the database.
	 * 
	 * @param sessionConfig
	 * @return
	 * @throws Exception
	 */
	DBSession session(SessionConfig sessionConfig) throws Exception;
}