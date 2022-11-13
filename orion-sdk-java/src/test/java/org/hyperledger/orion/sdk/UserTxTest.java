package org.hyperledger.orion.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.time.Duration;

import com.google.protobuf.ByteString;
import com.google.protobuf.util.JsonFormat;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.hyperledger.orion.sdk.config.ConnectionConfig;
import org.hyperledger.orion.sdk.config.Replica;
import org.hyperledger.orion.sdk.config.SessionConfig;
import org.hyperledger.orion.sdk.config.UserConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import types.BlockAndTransaction.AccessControl;
import types.Configuration.User;

@TestInstance(Lifecycle.PER_CLASS)
class UsersTxTest {
    DBSession session;
    SessionConfig sConfig;

    @BeforeAll
    void setUp() throws Exception {
        String adminCertFilePath = "src/test/resources/crypto/admin/admin.pem";
        String adminKeyFilePath = "src/test/resources/crypto/admin/admin.key";

        Replica r = new Replica("node1", "http://127.0.0.1:6001");
        ConnectionConfig cConfig = new ConnectionConfig(new Replica[] { r }, null);
        DB db = new DB(cConfig);

        UserConfig user = new UserConfig("admin", adminCertFilePath, adminKeyFilePath);
        this.sConfig = new SessionConfig(user, Duration.ofSeconds(5), Duration.ofSeconds(5));
        this.session = db.session(sConfig);
    }

    @Test
    void addNewUser() throws Exception {
        UsersTxContext tx = session.createUsersTx();
        var user = tx.getUser("alice");
        assertEquals("", user.toString());

        var aliceCertFilePath = "src/test/resources/crypto/alice/alice.pem";
        File file = new File(aliceCertFilePath);

        String key = new String(Files.readAllBytes(file.toPath()), Charset.defaultCharset());
        PemReader pr = new PemReader(new StringReader(key));
        PemObject po = pr.readPemObject();

        User.Builder u = User.newBuilder();
        u.setId("alice");
        u.setCertificate(ByteString.copyFrom(po.getContent()));

        tx.putUser(u.build(), null);
        var receipt = tx.commit(true);

        String jsonString = JsonFormat.printer()
            .print(receipt);
        System.out.println(jsonString);
    }
}
