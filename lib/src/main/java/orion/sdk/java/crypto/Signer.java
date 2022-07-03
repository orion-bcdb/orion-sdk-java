package orion.sdk.java.crypto;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;


public class Signer {
    String identity;
    String keyFilePath;
    PrivateKey privateKey;

    public Signer(String identity, String keyFilePath) throws FileNotFoundException, IOException {
        this.identity = identity;
        this.keyFilePath = keyFilePath;

        File file = new File(keyFilePath);

        String key = new String(Files.readAllBytes(file.toPath()), Charset.defaultCharset());

        String privateKeyPEM = key
                .replace("-----BEGIN EC PRIVATE KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END EC PRIVATE KEY-----", "");

        try {
            KeyFactory kf = KeyFactory.getInstance("EC");
            EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyPEM.getBytes());
            privateKey = kf.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Could not reconstruct the private key, the given algorithm could not be found.");
        } catch (InvalidKeySpecException e) {
            System.out.println("Could not reconstruct the private key");
        }
    }

    public byte[] Sign(byte[] msg) {
        String sig = "";
        try {
            Signature ecdsaSign = Signature.getInstance("SHA256withECDSA");
            ecdsaSign.initSign(privateKey);
            ecdsaSign.update(msg);
            byte[] signature = ecdsaSign.sign();
            sig = Base64.getEncoder().encodeToString(signature);
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
