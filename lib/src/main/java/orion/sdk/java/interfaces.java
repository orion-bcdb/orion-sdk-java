package orion.sdk.java;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.Duration;

import com.google.protobuf.Message;

import orion.sdk.java.config.SessionConfig;
import types.BlockAndTransaction.AccessControl;
import types.BlockAndTransaction.KVWithMetadata;
import types.BlockAndTransaction.ValueWithMetadata;
import types.Response.TxReceiptResponseEnvelope;

// BCDB Blockchain Database interface, defines set of APIs
// required to operate with BCDB instance
interface BCDB {
	// Session instantiates session to the database
    DBSession session(SessionConfig sessionConfig) throws Exception;
}

interface DBSession {
    // UserTxContext createUserTx() throws Exception;
    DataTxContext createDataTx() throws Exception;
    // DBsTxContex createDBsTx() throws Exception;
    // ConfigTxContext createConfigTx() throws Exception;
    // Provenance provenance() throws Exception;
    // Ledger ledger() throws Exception;
    // Query query() throws Exception;
    // Replica[] replicaSet(boolean refresh) throws Exception;
}

interface DataTxContext extends TxContext {
	// Put new value to key
    void put(String dbName, String key, byte[] value, AccessControl acl);
	// Get existing key value
    ValueWithMetadata get(String dbName, String key);
	// Delete value for key
    void delete(String dbName, String key);
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
