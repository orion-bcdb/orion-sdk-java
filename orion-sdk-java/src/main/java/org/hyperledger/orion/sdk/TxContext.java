// Copyright IBM Corp. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package org.hyperledger.orion.sdk;

import com.google.protobuf.Message;

import types.Response.TxReceiptResponseEnvelope;

/**
 * TxContext is an abstract API to capture general purpose functionality for all
 * types of transactions context.
 * 
 */
interface TxContext {
	/**
	 * Commit submits transaction to the server, can be sync or async.
	 * 
	 * Sync option returns tx id and tx receipt envelope and in case of error,
	 * commitTimeout error is one of possible errors to return. Async returns tx id,
	 * always nil as tx receipt or error
	 * 
	 * @param sync
	 * @return
	 * @throws Exception
	 */
	TxReceiptResponseEnvelope commit(boolean sync) throws Exception;

	/**
	 * Abort cancel submission and abandon all changes within given transaction
	 * context.
	 * 
	 * @throws Exception
	 */
	void abort() throws Exception;

	/**
	 * CommittedTxEnvelope returns transaction envelope, can be called only after
	 * Commit(), otherwise will return nil
	 * 
	 * @return
	 * @throws Exception
	 */
	Message committedTxEnvelope() throws Exception;
}