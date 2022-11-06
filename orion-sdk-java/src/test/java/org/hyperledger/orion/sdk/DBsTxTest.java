package org.hyperledger.orion.sdk;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Paths;
import java.time.Duration;

import com.google.protobuf.util.JsonFormat;

import org.hyperledger.orion.sdk.config.ConnectionConfig;
import org.hyperledger.orion.sdk.config.Replica;
import org.hyperledger.orion.sdk.config.SessionConfig;
import org.hyperledger.orion.sdk.config.UserConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
class DBsTxTest {
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
	void checkDBExistance() throws Exception {
		DBsTxContex tx = session.createDBsTx();
		assertTrue(tx.exists("bdb"));
		assertFalse(tx.exists("db1"));
	}

	@Test
	void createDBsAndDeleteDBs() throws Exception {
		DBsTxContex tx = session.createDBsTx();
		assertFalse(tx.exists("db1"));
		assertFalse(tx.exists("db2"));

		tx.createDB("db1", null);
		tx.createDB("db2", null);
		var receipt = tx.commit(true);
		String jsonString = JsonFormat.printer().print(receipt);
		System.out.println(jsonString);

		assertTrue(tx.exists("db1"));
		assertTrue(tx.exists("db2"));

		tx = session.createDBsTx();
		tx.deleteDB("db1");
		tx.deleteDB("db2");
		receipt = tx.commit(true);
		jsonString = JsonFormat.printer().print(receipt);
		System.out.println(jsonString);

		assertFalse(tx.exists("db1"));
		assertFalse(tx.exists("db2"));

	}
}
