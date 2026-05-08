package com.controller;

import com.model.SecurityConfig;

import java.io.*;
import java.time.LocalDateTime;

/**
 * Manages user authentication and PIN security for the Masroofy application.
 * Handles PIN validation, lockout logic, and persisting security data to file.
 */

public class AuthController {

    /** Maximum number of failed PIN attempts before the user is locked out. */

private static final int MAX_ATTEMPTS = 3;
    /** Duration in seconds the user remains locked out after exceeding max attempts. */

private static final int LOCKOUT_SECS = 30;
    /** Path to the file where security configuration data is stored. */


private static final String SECURITY_FILE = "data/security.txt";


/**
     * Loads the security configuration from the data file.
     * Returns a default {@link SecurityConfig} if the file does not exist or is unreadable.
     *
     * @return the loaded {@link SecurityConfig} object
     */

private SecurityConfig loadSecurity() {
    File f = new File(SECURITY_FILE);


    if (!f.exists()) return new SecurityConfig();

    try (BufferedReader br = new BufferedReader(new FileReader(f))) {
        String line = br.readLine();
        if (line == null || line.isBlank()) return new SecurityConfig();


        String[] parts   = line.split(",", 4);
        String hashedPIN = parts[0];
        boolean locked   = Boolean.parseBoolean(parts[1]);
        int attempts     = Integer.parseInt(parts[2]);
        LocalDateTime lockoutEnd = parts[3].equals("null")
                                 ? null
                                 : LocalDateTime.parse(parts[3]);

        return new SecurityConfig(hashedPIN, locked, attempts, lockoutEnd);

    } catch (IOException e) {
        return new SecurityConfig();
    }
}
/**
     * Saves the given security configuration to the data file.
     *
     * @param sc the {@link SecurityConfig} object to persist
     */

// ── saves security data to file ──────────────────────────────────────────
private void saveSecurity(SecurityConfig sc) {
    new File("data").mkdirs();
    try (PrintWriter pw = new PrintWriter(new FileWriter(SECURITY_FILE))) {
        pw.println(sc.getHashedPIN()      + "," +
                   sc.isLockEnabled()     + "," +
                   sc.getFailedAttempts() + "," +
                   (sc.getLockoutEndTime() != null
                       ? sc.getLockoutEndTime()
                       : "null"));
    } catch (IOException e) {
        System.out.println("Error saving security data.");
    }
}
 /**
     * Checks whether a PIN has been set by the user.
     *
     * @return {@code true} if a PIN exists, {@code false} otherwise
     */

public boolean isPinSet() {
    SecurityConfig sc = loadSecurity();
    return sc.getHashedPIN() != null && !sc.getHashedPIN().isEmpty();
}

    /**
     * Validates the user's input PIN against the stored hashed PIN.
     * Returns "SUCCESS" if correct, "LOCKED" if the account is locked out,
     * or "INVALID" if the PIN is wrong.
     *
     * @param input the raw PIN string entered by the user
     * @return a status string: {@code "SUCCESS"}, {@code "LOCKED"}, or {@code "INVALID"}
     */

public String validatePIN(String input) {

    SecurityConfig sc = loadSecurity();

 
    if (sc.isLockedOut()) return "LOCKED";

  
    boolean valid = sc.verifyPIN(input);

    if (valid) {
    
        sc.setFailedAttempts(0);
        sc.setLockoutEndTime(null);
        saveSecurity(sc);
        return "SUCCESS";
    } else {
       
        recordFailedAttempt();
     
        if (loadSecurity().isLockedOut()) {
            return "LOCKED";
        }
        return "INVALID";
    }
}
/**
     * Updates the user's PIN by hashing the new value and saving it.
     * Also resets any failed attempts and lockout state.
     *
     * @param newPIN the new plain-text PIN to hash and store
     */

// ── saves a new PIN ───────────────────────────────────────────────────────
public void updatePIN(String newPIN) {
    SecurityConfig sc = loadSecurity();

  
    sc.setHashedPIN(SecurityConfig.hashPIN(newPIN));


    sc.setFailedAttempts(0);
    sc.setLockoutEndTime(null);

    saveSecurity(sc);
    System.out.println("PIN updated successfully.");
}
/**
     * Records a single failed PIN attempt and triggers a lockout
     * if the maximum number of attempts has been reached.
     */

// ── records one wrong attempt ─────────────────────────────────────────────
public void recordFailedAttempt() {
    SecurityConfig sc = loadSecurity();


    sc.recordFailedAttempt();

   
    if (sc.getFailedAttempts() >= MAX_ATTEMPTS) {
        sc.setLockoutEndTime(LocalDateTime.now().plusSeconds(LOCKOUT_SECS));
        System.out.println("Too many wrong attempts. Locked for "
                + LOCKOUT_SECS + " seconds.");
    }

    saveSecurity(sc);
}
 /**
     * Checks whether the user is currently locked out.
     *
     * @return {@code true} if the user is locked out, {@code false} otherwise
     */
// ── checks if user is currently locked out ────────────────────────────────
public boolean isLockedOut() {
    return loadSecurity().isLockedOut();
}
 /**
     * Enables or disables the privacy lock feature.
     *
     * @param enabled {@code true} to enable the lock, {@code false} to disable it
     */
    
public void togglePrivacyLock(boolean enabled) {
    SecurityConfig sc = loadSecurity();
    sc.setLockState(enabled); 
    saveSecurity(sc);
}
}