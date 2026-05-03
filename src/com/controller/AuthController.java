package com.controller;

import com.model.SecurityConfig;

import java.io.*;
import java.time.LocalDateTime;

public class AuthController {

// max wrong attempts before lockout
private static final int MAX_ATTEMPTS = 3;

// lockout duration in seconds
private static final int LOCKOUT_SECS = 30;

// where security data is saved
private static final String SECURITY_FILE = "data/security.txt";


// ── reads security data from file ────────────────────────────────────────
private SecurityConfig loadSecurity() {
    File f = new File(SECURITY_FILE);

    // if file does not exist yet, return empty security config
    if (!f.exists()) return new SecurityConfig();

    try (BufferedReader br = new BufferedReader(new FileReader(f))) {
        String line = br.readLine();
        if (line == null || line.isBlank()) return new SecurityConfig();

        // file stores: hashedPIN,lockEnabled,failedAttempts,lockoutEndTime
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

// ── checks if entered PIN is correct ─────────────────────────────────────
public boolean validatePIN(String input) {

    SecurityConfig sc = loadSecurity();

    // if user is locked out, do not even check the PIN
    if (sc.isLockedOut()) return false;

    // check if entered PIN matches stored PIN
    boolean valid = sc.verifyPIN(input);

    if (valid) {
        // correct PIN — reset failed attempts
        sc.setFailedAttempts(0);
        sc.setLockoutEndTime(null);
        saveSecurity(sc);
    } else {
        // wrong PIN — record the failed attempt
        recordFailedAttempt();
    }

    return valid;
}

// ── saves a new PIN ───────────────────────────────────────────────────────
public void updatePIN(String newPIN) {
    SecurityConfig sc = loadSecurity();

    // hash the new PIN before saving (never save plain text)
    sc.setHashedPIN(SecurityConfig.hashPIN(newPIN));

    // reset any lockout
    sc.setFailedAttempts(0);
    sc.setLockoutEndTime(null);

    saveSecurity(sc);
    System.out.println("PIN updated successfully.");
}

// ── records one wrong attempt ─────────────────────────────────────────────
public void recordFailedAttempt() {
    SecurityConfig sc = loadSecurity();

    // add one to the counter
    sc.recordFailedAttempt();

    // if reached max attempts, start the lockout timer
    if (sc.getFailedAttempts() >= MAX_ATTEMPTS) {
        sc.setLockoutEndTime(LocalDateTime.now().plusSeconds(LOCKOUT_SECS));
        System.out.println("Too many wrong attempts. Locked for "
                + LOCKOUT_SECS + " seconds.");
    }

    saveSecurity(sc);
}

// ── checks if user is currently locked out ────────────────────────────────
public boolean isLockedOut() {
    return loadSecurity().isLockedOut();
}
}