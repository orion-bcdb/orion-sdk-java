package org.hyperledger.orion.sdk;

import java.util.ArrayList;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;

import org.hyperledger.orion.sdk.exception.TransactionSpentException;

import types.BlockAndTransaction.AccessControl;
import types.BlockAndTransaction.UserAdministrationTx;
import types.BlockAndTransaction.UserAdministrationTxEnvelope;
import types.BlockAndTransaction.UserDelete;
import types.BlockAndTransaction.UserRead;
import types.BlockAndTransaction.UserWrite;
import types.Configuration.User;
import types.Query.GetUserQuery;
import types.Response.GetUserResponseEnvelope;
import types.Response.TxReceiptResponseEnvelope;

public class UserTx implements UsersTxContext {
    CommonTxContext cTxContext;
    ArrayList<UserRead> userReads = new ArrayList<UserRead>();
    ArrayList<UserWrite> userWrites = new ArrayList<UserWrite>();
    ArrayList<UserDelete> userDeletes = new ArrayList<UserDelete>();
    UserAdministrationTxEnvelope envelope;
    boolean txSpent;

    public UserTx(CommonTxContext cTxContext) {
        this.cTxContext = cTxContext;
    }

    // PutUser introduce new user into database
    public void putUser(User user, AccessControl acl) throws Exception {
        if (txSpent) {
            throw new TransactionSpentException(
                    "Transaction is already consumed by either calling commit or abort operation");
        }

        UserWrite.Builder uw = UserWrite.newBuilder();
        uw.setUser(user);
        uw.setAcl(acl);
        userWrites.add(uw.build());
    }

    // GetUser obtain user's record from database
    public User getUser(String userID) throws Exception {
        if (txSpent) {
            throw new TransactionSpentException(
                    "Transaction is already consumed by either calling commit or abort operation");
        }

        GetUserQuery.Builder userQuery = GetUserQuery.newBuilder();
        userQuery.setUserId(cTxContext.userID);
        userQuery.setTargetUserId(userID);

        String jsonString = JsonFormat.printer()
                .preservingProtoFieldNames()
                .omittingInsignificantWhitespace()
                .print(userQuery.build());
        System.out.println(jsonString);
        var sig = cTxContext.crypto.sign(cTxContext.privateKey, jsonString.getBytes());

        GetUserResponseEnvelope.Builder resp = GetUserResponseEnvelope.newBuilder();
        cTxContext.handleGetPostRequest("/user/" + userID, "GET", sig, resp);

        var userResp = resp.build().getResponse();
        UserRead.Builder ur = UserRead.newBuilder();
        ur.setUserId(userResp.getUser().getId());
        ur.setVersion(userResp.getMetadata().getVersion());

        userReads.add(ur.build());

        return userResp.getUser();
    }

    // RemoveUser delete existing user from the database
    public void removeUser(String userID) throws Exception {
        if (txSpent) {
            throw new TransactionSpentException(
                    "Transaction is already consumed by either calling commit or abort operation");
        }

        UserDelete.Builder u = UserDelete.newBuilder();
        u.setUserId(userID);

        userDeletes.add(u.build());
    }

    public TxReceiptResponseEnvelope commit(boolean sync) throws Exception {
        UserAdministrationTxEnvelope.Builder txEnvelope = UserAdministrationTxEnvelope.newBuilder();

        UserAdministrationTx.Builder tx = UserAdministrationTx.newBuilder();
        tx.setUserId(cTxContext.userID);
        tx.setTxId(cTxContext.txID);

        for (UserRead u : userReads)
            tx.addUserReads(u);

        for (UserWrite w : userWrites)
            tx.addUserWrites(w);

        for (UserDelete d : userDeletes)
            tx.addUserDeletes(d);

        var txPayload = tx.build();
        txEnvelope.setPayload(txPayload);

        String jsonString = JsonFormat.printer()
            .preservingProtoFieldNames()
            .omittingInsignificantWhitespace()
            .print(txPayload);

        var sig = cTxContext.crypto.sign(cTxContext.privateKey, jsonString.getBytes());
        txEnvelope.setSignature(ByteString.copyFrom(sig));

        envelope = txEnvelope.build();

        return cTxContext.commit("/user/tx", sync, envelope);
    }

    public void abort() {

    }

    public Message committedTxEnvelope() {
        return envelope;
    }
}
