// Copyright IBM Corp. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package org.hyperledger.orion.sdk;

import types.BlockAndTransaction.AccessControl;
import types.BlockAndTransaction.ValueWithMetadata;

/**
 * DataTxContext defines the methods available on a data transaction.
 */
public interface DataTxContext extends TxContext {
	/**
	 * Put new value to key.
	 * 
	 * @param dbName
	 * @param key
	 * @param value
	 * @param acl
	 * @throws Exception
	 */
	void put(String dbName, String key, byte[] value, AccessControl acl) throws Exception;

	/**
	 * Get existing key value
	 * 
	 * @param dbName
	 * @param key
	 * @return
	 * @throws Exception
	 */
	ValueWithMetadata get(String dbName, String key) throws Exception;

	/**
	 * Delete value for key
	 * 
	 * @param dbName
	 * @param key
	 */
	void delete(String dbName, String key);
}