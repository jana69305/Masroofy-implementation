package com.controller;

import com.model.SecurityConfig;

import java.io.*;
import java.time.LocalDateTime;

public class AuthController {


private static final int MAX_ATTEMPTS = 3;

private static final int LOCKOUT_SECS = 30;


private static final String SECURITY_FILE = "data/security.txt";



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
public boolean isPinSet() {
    SecurityConfig sc = loadSecurity();
    return sc.getHashedPIN() != null && !sc.getHashedPIN().isEmpty();
}

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

// ── saves a new PIN ───────────────────────────────────────────────────────
public void updatePIN(String newPIN) {
    SecurityConfig sc = loadSecurity();

  
    sc.setHashedPIN(SecurityConfig.hashPIN(newPIN));


    sc.setFailedAttempts(0);
    sc.setLockoutEndTime(null);

    saveSecurity(sc);
    System.out.println("PIN updated successfully.");
}

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

// ── checks if user is currently locked out ────────────────────────────────
public boolean isLockedOut() {
    return loadSecurity().isLockedOut();
}

public void togglePrivacyLock(boolean enabled) {
    SecurityConfig sc = loadSecurity();
    sc.setLockState(enabled); 
    saveSecurity(sc);
}
}