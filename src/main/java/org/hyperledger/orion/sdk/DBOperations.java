package org.hyperledger.orion.sdk;

import java.util.HashMap;

import types.BlockAndTransaction.DataDelete;
import types.BlockAndTransaction.DataWrite;
import types.Response.GetDataResponse;

public class DBOperations {
    HashMap<String, GetDataResponse> dataReads;
    HashMap<String, DataWrite> dataWrites;
    HashMap<String, DataDelete> dataDeletes;

    public void setDataRead(String key, GetDataResponse dataRead) {
        dataReads.put(key, dataRead);
    }

    public void setDataWrite(String key, DataWrite dataWrite) {
        dataWrites.put(key, dataWrite);
    }

    public void setDataDelete(String key, DataDelete dataDelete) {
        dataDeletes.put(key, dataDelete);
    }
}
