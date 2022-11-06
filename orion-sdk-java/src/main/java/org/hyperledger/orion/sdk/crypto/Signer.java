package org.hyperledger.orion.sdk.crypto;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

public class Signer {
    String identity;
    String keyFilePath;
    PrivateKey privateKey;

    public Signer(String identity, String keyFilePath) throws FileNotFoundException, IOException {
        this.identity = identity;
        this.keyFilePath = keyFilePath;

        File file = new File(keyFilePath);

        String key = new String(Files.readAllBytes(file.toPath()), Charset.defaultCharset());
        PemReader pr = new PemReader(new StringReader(key));
        PemObject po = pr.readPemObject();
        PEMParser pem = new PEMParser(new StringReader(key));

        if (po.getType().equals("PRIVATE KEY")) {
            privateKey = new JcaPEMKeyConverter().getPrivateKey((PrivateKeyInfo) pem.readObject());
        } else {
            PEMKeyPair kp = (PEMKeyPair) pem.readObject();
            privateKey = new JcaPEMKeyConverter().getPrivateKey(kp.getPrivateKeyInfo());
        }
    }

    public byte[] Sign(byte[] msg) {
        String sig = "";
        try {
            Signature ecdsaSign = Signature.getInstance("SHA256withECDSA");
            ecdsaSign.initSign(privateKey);
            ecdsaSign.update(msg);
            return ecdsaSign.sign();
            // sig = Base64.getEncoder().encodeToString(signature);
        } catch (NoSuchAlgorithmException e) {
        } catch (InvalidKeyException e) {
        } catch (SignatureException e) {
        }

        return sig.getBytes();

    }

    public String Identity() {
        return this.identity;
    }
}
