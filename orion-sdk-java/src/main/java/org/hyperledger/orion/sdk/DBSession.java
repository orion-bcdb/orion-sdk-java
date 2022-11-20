// Copyright IBM Corp. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package org.hyperledger.orion.sdk;

/**
 * DBSession defines the methods that allow the creation of different types of
 * transaction contexts.
 */
public interface DBSession {
	/**
	 * Creates a DataTxContext implementation.
	 * 
	 * @return
	 * @throws Exception
	 */
	DataTxContext createDataTx() throws Exception;

	/**
	 * Creates a DataTxContext implementation with an externally supplied
	 * transaction ID.
	 * 
	 * @return
	 * @throws Exception
	 */
	DataTxContext createDataTx(String txID) throws Exception;

	/**
	 * Creates a DBsTxContex implementation.
	 * 
	 * @return
	 * @throws Exception
	 */
	DBsTxContex createDBsTx() throws Exception;

	/**
	 * Creates a UsersTxContext implementation.
	 * 
	 * @return
	 * @throws Exception
	 */
	UsersTxContext createUsersTx() throws Exception;

	// ConfigTxContext createConfigTx() throws Exception;
	// Provenance provenance() throws Exception;
	// Ledger ledger() throws Exception;
	// Query query() throws Exception;
	// Replica[] replicaSet(boolean refresh) throws Exception;
}