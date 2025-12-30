package com.example.finance.backend;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public class PasswordUtils {

    private static final String ALGORITHM = "SHA-256";

    public static byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    public static byte[] getHashedPassword(String password, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            return md.digest(password.getBytes());
        } catch (NoSuchAlgorithmException e) {
            // This should not happen
            throw new RuntimeException(e);
        }
    }

    public static boolean verifyPassword(String enteredPassword, byte[] storedHash, byte[] storedSalt) {
        byte[] enteredHash = getHashedPassword(enteredPassword, storedSalt);
        return Arrays.equals(enteredHash, storedHash);
    }
}


