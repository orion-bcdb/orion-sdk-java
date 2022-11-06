package org.hyperledger.orion.sdk;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.HashMap;

import com.google.protobuf.Message;

import org.hyperledger.orion.sdk.config.SessionConfig;
import types.BlockAndTransaction.AccessControl;
import types.BlockAndTransaction.IndexAttributeType;
import types.BlockAndTransaction.KVWithMetadata;
import types.BlockAndTransaction.ValueWithMetadata;
import types.Configuration.User;
import types.Response.TxReceiptResponseEnvelope;

// BCDB Blockchain Database interface, defines set of APIs
// required to operate with BCDB instance
interface BCDB {
	// Session instantiates session to the database
    DBSession session(SessionConfig sessionConfig) throws Exception;
}

interface DBSession {
    DataTxContext createDataTx() throws Exception;
    DataTxContext createDataTx(String txID) throws Exception;
    DBsTxContex createDBsTx() throws Exception;
    // UsersTxContext createUserTx() throws Exception;
    // ConfigTxContext createConfigTx() throws Exception;
    // Provenance provenance() throws Exception;
    // Ledger ledger() throws Exception;
    // Query query() throws Exception;
    // Replica[] replicaSet(boolean refresh) throws Exception;
}


// TxContext is an abstract API to capture general purpose functionality for all types of transactions context.
interface TxContext {
	// Commit submits transaction to the server, can be sync or async.
	// Sync option returns tx id and tx receipt envelope and
	// in case of error, commitTimeout error is one of possible errors to return.
	// Async returns tx id, always nil as tx receipt or error
    TxReceiptResponseEnvelope commit(boolean sync) throws Exception;
	// Abort cancel submission and abandon all changes
	// within given transaction context
    void abort() throws Exception;
	// CommittedTxEnvelope returns transaction envelope, can be called only after Commit(), otherwise will return nil
    Message committedTxEnvelope() throws Exception;
}

interface DataTxContext extends TxContext {
	// Put new value to key
    void put(String dbName, String key, byte[] value, AccessControl acl) throws Exception;
	// Get existing key value
    ValueWithMetadata get(String dbName, String key) throws Exception;
	// Delete value for key
    void delete(String dbName, String key);
}

// UsersTxContext transaction context to operate with
// user management related transactions:
// 1. Add user's record
// 2. Get user's record
// 3. Delete user's record
// 4. Alternate user's ACLs
interface UsersTxContext extends TxContext {
	// PutUser introduce new user into database
	void putUser(User user, AccessControl acl) throws Exception;
	// GetUser obtain user's record from database
	User getUser(String userID) throws Exception;
	// RemoveUser delete existing user from the database
	void removeUser(String userID) throws Exception;
}

interface DBsTxContex extends TxContext {
	// createDB creates new database along with index definition for the query.
	// The index is a map of attributes/fields in json document, i.e., value associated
	// with the key, to its value type. For example, map["name"]types.IndexAttributeType_STRING
	// denotes that "name" attribute in all json documents to be stored in the given
	// database to be indexed for queries. Note that only indexed attributes can be
	// used as predicates in the query string. Currently, we support the following three
	// value types: STRING, BOOLEAN, and INT64
    void createDB(String dbName, HashMap<String, IndexAttributeType> index) throws Exception;
	// deleteDB deletes database
    void deleteDB(String dbName) throws Exception;
	// exists checks whenever database is already created
    boolean exists(String dbName) throws Exception;
	// getDBIndex returns the index definition associated with the given database.
	// The index definition is of form map["name"]types.IndexAttributeType where
	// name denotes the field name in the JSON document and types.IndexAttributeType
	// denotes one of the three value types: STRING, BOOLEAN, and INT64. When a database
	// does not have an index definition, GetDBIndex would return a nil map
    HashMap<String, IndexAttributeType> getDBIndex(String dbName) throws Exception;
}

// Query provides method to execute json query and range query on a
// given database.
interface Query {
	// ExecuteJSONQuery executes a given JSON query on a given database.
	// The JSON query is a json string which must contain predicates under the field
	// selector. The first field in the selector can be a combinational operator
	// such as "$and" or "$or" followed by a list of attributes and a list of
	// conditions per attributes. A query example is shown below
	//
	// {
	//   "selector": {
	// 		"$and": {            -- top level combinational operator
	// 			"attr1": {          -- a field in the json document
	// 				"$gte": "a",    -- value criteria for the field
	// 				"$lt": "b"      -- value criteria for the field
	// 			},
	// 			"attr2": {          -- a field in the json document
	// 				"$eq": true     -- value criteria for the field
	// 			},
	// 			"attr3": {          -- a field in the json document
	// 				"$lt": "a2"     -- a field in the json document
	// 			}
	// 		}
	//   }
	// }
	KVWithMetadata[] executeJSONQuery(String dbName, String query) throws Exception;
	// GetDataByRange executes a range query on a given database. The startKey is
	// inclusive but endKey is not. When the startKey is an empty string, it denotes
	// `fetch keys from the beginning` while an empty endKey denotes `fetch keys till the
	// the end`. The limit denotes the number of records to be fetched in total. However,
	// when the limit is set to 0, it denotes no limit. The iterator returned by
	// GetDataByRange is used to retrieve the records.
	Iterator GetDataByRange(String dbName, String startKey, String endKey, int limit) throws Exception;
}

// Iterator implements methods to iterate over a set records
interface Iterator {
	// Next returns the next record. If there is no more records, it would return a nil value
	// and a false value.
	void next() throws Exception;
}


// RestClient encapsulates http client with user identity
// signing capabilities to generalize ability to send requests
// to BCDB server
interface RestClient {
	// Query sends REST request with query semantics.
	// SDK will wait for `queryTimeout` for response from server and return error if no response received.
	// If commitTimeout set to 0, sdk will wait for http commitTimeout.
    HttpResponse<String> Query(String endpoint, String httpMethod, byte[] postData, byte[] signature) throws InterruptedException, IOException;

	// Submit send REST request with transaction submission semantics and optional commitTimeout.
	// If commitTimeout set to 0, server will return immediately, without waiting for transaction processing
	// pipeline to complete and response will not contain transaction receipt, otherwise, server will wait
	// up to commitTimeout for transaction processing to complete and will return tx receipt as result.
	// In case of commitTimeout, http.StatusAccepted returned.
	HttpResponse<String> Submit(String endpoint, Message msg) throws InterruptedException, IOException;
}
