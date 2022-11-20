// Copyright IBM Corp. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package org.hyperledger.orion.sdk;

import java.io.IOException;
import java.net.http.HttpResponse;

import com.google.protobuf.Message;

/**
 * RestClient encapsulates http client with user identity signing capabilities
 * to generalize ability to send requests to BCDB server.
 * 
 */

interface RestClient {
	/**
	 * Query sends REST request with query semantics.
	 * 
	 * SDK will wait for `queryTimeout` for response from server and return error if
	 * no response received. If commitTimeout set to 0, sdk will wait for http
	 * commitTimeout.
	 * 
	 * @param endpoint
	 * @param httpMethod
	 * @param postData
	 * @param signature
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	HttpResponse<String> Query(String endpoint, String httpMethod, byte[] postData, byte[] signature)
			throws InterruptedException, IOException;

	/**
	 * Submit send REST request with transaction submission semantics and optional
	 * commitTimeout.
	 * 
	 * If commitTimeout set to 0, server will return immediately, without waiting
	 * for transaction processing pipeline to complete and response will not contain
	 * transaction receipt, otherwise, server will wait up to commitTimeout for
	 * transaction processing to complete and will return tx receipt as result. In
	 * case of commitTimeout, http.StatusAccepted returned.
	 * 
	 * @param endpoint
	 * @param msg
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	HttpResponse<String> Submit(String endpoint, Message msg) throws InterruptedException, IOException;
}