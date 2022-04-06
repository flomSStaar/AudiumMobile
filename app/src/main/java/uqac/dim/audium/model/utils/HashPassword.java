package uqac.dim.audium.model.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashPassword {
    /**
     * Hashs the input password with SHA-256
     *
     * @param password Password to hash
     * @return Password hash
     */
    public static String hashPassword(String password) {
        try {
            if (password != null) {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(password.getBytes());

                StringBuilder hexString = new StringBuilder();
                for (byte datum : md.digest()) {
                    hexString.append(Integer.toString((datum & 0xff) + 0x100, 16).substring(1));
                }

                return hexString.toString();
            } else {
                return null;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
