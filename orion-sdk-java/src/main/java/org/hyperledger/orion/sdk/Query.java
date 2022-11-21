// Copyright IBM Corp. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package org.hyperledger.orion.sdk;

import types.BlockAndTransaction.KVWithMetadata;

/**
 * Query provides method to execute json query and range query on a given
 * database.
 * 
 */
interface Query {
	/**
	 * ExecuteJSONQuery executes a given JSON query on a given database.
	 * 
	 * The JSON query is a json string which must contain predicates under the field
	 * selector. The first field in the selector can be a combinational operator
	 * such as "$and" or "$or" followed by a list of attributes and a list of
	 * conditions per attributes. A query example is shown below
	 * 
	 * <p>
	 * <blockquote>
	 * 
	 * <pre>
	 *
	 * {
	 *  "selector": {
	 *    "$and": { -- top level combinational operator
	 *      "attr1": { -- a field in the json document
	 *        "$gte": "a", -- value criteria for the field
	 *        "$lt": "b" -- value criteria for the field
	 *      },
	 *      "attr2": { -- a field in the json document
	 *        "$eq": true -- value criteria for the field
	 *      },
	 *      "attr3": { -- a field in the json document
	 *        "$lt": "a2" -- a field in the json document
	 *        }
	 *      }
	 *   }
	 * }
	 * 
	 * </pre>
	 * 
	 * </blockquote>
	 * </p>
	 * 
	 * @param dbName
	 * @param query
	 * @return
	 * @throws Exception
	 */
	KVWithMetadata[] executeJSONQuery(String dbName, String query) throws Exception;

	/**
	 * GetDataByRange executes a range query on a given database.
	 * 
	 * The startKey is inclusive but endKey is not. When the startKey is an empty
	 * string, it denotes `fetch keys from the beginning` while an empty endKey
	 * denotes `fetch keys till the the end`. The limit denotes the number of
	 * records to be fetched in total. However, when the limit is set to 0, it
	 * denotes no limit. The iterator returned by GetDataByRange is used to retrieve
	 * the records.
	 * 
	 * @param dbName
	 * @param startKey
	 * @param endKey
	 * @param limit
	 * @return
	 * @throws Exception
	 */
	Iterator GetDataByRange(String dbName, String startKey, String endKey, int limit) throws Exception;
}