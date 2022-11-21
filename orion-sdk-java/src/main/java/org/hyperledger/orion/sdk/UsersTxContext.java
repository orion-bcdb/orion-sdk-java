// Copyright IBM Corp. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package org.hyperledger.orion.sdk;

import types.BlockAndTransaction.AccessControl;
import types.Configuration.User;

/**
 * UsersTxContext transaction context to operate with user management related
 * transactions.
 * 
 */
interface UsersTxContext extends TxContext {
	/**
	 * PutUser introduce new user into database.
	 * 
	 * @param user
	 * @param acl
	 * @throws Exception
	 */
	void putUser(User user, AccessControl acl) throws Exception;

	/**
	 * GetUser obtain user's record from database.
	 * 
	 * @param userID
	 * @return
	 * @throws Exception
	 */
	User getUser(String userID) throws Exception;

	/**
	 * RemoveUser delete existing user from the database.
	 * 
	 * @param userID
	 * @throws Exception
	 */
	void removeUser(String userID) throws Exception;
}