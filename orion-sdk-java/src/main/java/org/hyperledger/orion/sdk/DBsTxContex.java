// Copyright IBM Corp. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package org.hyperledger.orion.sdk;

import java.util.HashMap;

import types.BlockAndTransaction.IndexAttributeType;

/**
 * DBsTxContex defines the methods available on a database administration
 * transaction.
 * 
 */
interface DBsTxContex extends TxContext {

	/**
	 * createDB creates new database along with index definition for the query.
	 * 
	 * The index is a map of attributes/fields in json document, i.e., value
	 * associated with the key, to its value type. For example,
	 * map["name"]types.IndexAttributeType_STRING denotes that "name" attribute in
	 * all json documents to be stored in the given database to be indexed for
	 * queries. Note that only indexed attributes can be used as predicates in the
	 * query string. Currently, we support the following three value types: STRING,
	 * BOOLEAN, and INT64
	 * 
	 * @param dbName
	 * @param index
	 * @throws Exception
	 */
	void createDB(String dbName, HashMap<String, IndexAttributeType> index) throws Exception;

	/**
	 * deleteDB deletes database.
	 * 
	 * @param dbName
	 * @throws Exception
	 */
	void deleteDB(String dbName) throws Exception;

	/**
	 * exists checks whenever database is already created.
	 * 
	 * @param dbName
	 * @return
	 * @throws Exception
	 */
	boolean exists(String dbName) throws Exception;

	/**
	 * getDBIndex returns the index definition associated with the given database.
	 * 
	 * The index definition is of form map["name"]types.IndexAttributeType where
	 * name denotes the field name in the JSON document and types.IndexAttributeType
	 * denotes one of the three value types: STRING, BOOLEAN, and INT64. When a
	 * database does not have an index definition, GetDBIndex would return a nil map
	 * 
	 * @param dbName
	 * @return
	 * @throws Exception
	 */
	HashMap<String, IndexAttributeType> getDBIndex(String dbName) throws Exception;
}