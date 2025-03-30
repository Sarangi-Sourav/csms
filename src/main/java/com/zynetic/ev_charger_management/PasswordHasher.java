package com.zynetic.ev_charger_management;

import com.zynetic.ev_charger_management.entity.ChargerStatusEnum;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class PasswordHasher {
    /*
    * class to create BCrypt password for testing
    * this class is used to generate the passKey
    * to store to the charge_point_cred table for testing purpose
    * */
    public static void main(String[] args) {
        String rawPassKey = "NewPassKey14";//"SuperSecretKey123";
        String salt = BCrypt.gensalt(10);//BCrypt.gensalt();
        String hashedPassKey = BCrypt.hashpw(rawPassKey, BCrypt.gensalt(10));
        String storedHashedKey = "$2a$10$QlHaApKLhD9xqh0txG886u0K.ZZfgjMNB.A4wdYEGV4aDSLQLFtgC";

        System.out.println("Hashed Pass-Key: " + hashedPassKey + "\nsalt: " + salt);
        System.out.println(BCrypt.checkpw(rawPassKey, storedHashedKey));
    }
}