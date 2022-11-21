// Copyright IBM Corp. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package org.hyperledger.orion.sdk;

/**
 * Iterator implements methods to iterate over a set records.
 * 
 */
public interface Iterator {
	/**
	 * Next returns the next record. If there is no more records, it would return a
	 * nil value and a false value.
	 * 
	 * @throws Exception
	 */
	void next() throws Exception;
}
