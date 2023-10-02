package io.angularpay.cipher.domain.service;

import io.angularpay.cipher.models.CipherEntryModel;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

@Service
public class CipherService {

    public static final String SIGNATURE_ALGORITHM = "SHA1withRSA";
    public static final String KEY_ALGORITHM = "RSA";

    public CipherEntryModel createCipherEntry() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());

        return CipherEntryModel.builder().privateKey(privateKey).publicKey(publicKey).build();
    }

    public ParsedCipherEntry fromCipherEntry(CipherEntryModel cipherEntry) throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] privateKeyBytes = Base64.getDecoder().decode(cipherEntry.getPrivateKey());
        PrivateKey privateKey = getPrivateKey(privateKeyBytes);

        byte[] publicKeyBytes = Base64.getDecoder().decode(cipherEntry.getPublicKey());
        RSAPublicKey publicKey = getPublicKey(publicKeyBytes);

        return ParsedCipherEntry.builder().privateKey(privateKey).publicKey(publicKey).build();
    }

    private static RSAPublicKey getPublicKey(byte[] publicKeyBytes) throws InvalidKeySpecException, NoSuchAlgorithmException {
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(publicKeyBytes);
        return (RSAPublicKey) keyFactory.generatePublic(keySpecX509);
    }

    private static PrivateKey getPrivateKey(byte[] privateKeyBytes) throws InvalidKeySpecException, NoSuchAlgorithmException {
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(privateKeyBytes);
        return keyFactory.generatePrivate(keySpecPKCS8);
    }

    public String encrypt(CipherEntryModel cipherEntry, String plaintext) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        // parse cipherEntry into an actual Java public/private key pair
        ParsedCipherEntry pair = fromCipherEntry(cipherEntry);

        // instantiate encryption cipher with public key
        PublicKey publicKey = pair.getPublicKey();
        javax.crypto.Cipher encryptCipher = javax.crypto.Cipher.getInstance(KEY_ALGORITHM);
        encryptCipher.init(javax.crypto.Cipher.ENCRYPT_MODE, publicKey);

        // now encrypt the data
        byte[] plainTextDataBytes = plaintext.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedMessageBytes = encryptCipher.doFinal(plainTextDataBytes);

        // return base64 encoded string
        return Base64.getEncoder().encodeToString(encryptedMessageBytes);
    }

    public String decrypt(CipherEntryModel cipherEntry, String encryptedData) throws IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        // parse cipherEntry into an actual Java public/private key pair
        ParsedCipherEntry pair = fromCipherEntry(cipherEntry);

        // instantiate decryption cipher with private key
        PrivateKey privateKey = pair.getPrivateKey();
        javax.crypto.Cipher decryptCipher = javax.crypto.Cipher.getInstance(KEY_ALGORITHM);
        decryptCipher.init(javax.crypto.Cipher.DECRYPT_MODE, privateKey);

        // decode the base64 string to bytes
        byte[] base64Decoded = Base64.getDecoder().decode(encryptedData);

        // now decrypt the data
        byte[] decryptedMessageBytes = decryptCipher.doFinal(base64Decoded);

        // return decrypted string
        return new String(decryptedMessageBytes, StandardCharsets.UTF_8);
    }

    public String sign(CipherEntryModel cipherEntry, String plaintext) throws InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        // parse cipherEntry into an actual Java public/private key pair
        ParsedCipherEntry pair = fromCipherEntry(cipherEntry);

        byte[] plainTextDataBytes = plaintext.getBytes(StandardCharsets.UTF_8);

        // instantiate signature with private key
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(pair.getPrivateKey());
        signature.update(plainTextDataBytes);

        byte[] signatureBytes = signature.sign();

        // return base64 encoded string
        return Base64.getEncoder().encodeToString(signatureBytes);
    }

    public boolean verifySignature(VerifySignatureRequest verifySignatureRequest) throws SignatureException, InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException {
        // parse cipherEntry into an actual Java public/private key pair
        ParsedCipherEntry pair = fromCipherEntry(verifySignatureRequest.getCipherEntry());

        byte[] plainTextDataBytes = verifySignatureRequest.getPlaintext().getBytes(StandardCharsets.UTF_8);

        // instantiate signature with public key
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(pair.getPublicKey());
        signature.update(plainTextDataBytes);

        // decode the base64 signature string to bytes
        byte[] signatureBytes = Base64.getDecoder().decode(verifySignatureRequest.getSignatureString());

        return signature.verify(signatureBytes);
    }
}
