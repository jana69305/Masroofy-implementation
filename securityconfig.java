package com.model;

import java.time.LocalDateTime;

public class SecurityConfig {
    private String hashedPIN;
    private boolean isLockEnabled;
    private int failedAttempts;
    private LocalDateTime lockoutEndTime;

    public SecurityConfig() {
        this.hashedPIN = "";
        this.isLockEnabled = false;
        this.failedAttempts = 0;
        this.lockoutEndTime = null;
    }

    public SecurityConfig(String hashedPIN, boolean isLockEnabled,
                          int failedAttempts, LocalDateTime lockoutEndTime) {
        this.hashedPIN = hashedPIN;
        this.isLockEnabled = isLockEnabled;
        this.failedAttempts = failedAttempts;
        this.lockoutEndTime = lockoutEndTime;
    }

    public boolean verifyPIN(String input) {
        return hashedPIN.equals(hashPIN(input));
    }

    public void setLockState(boolean enabled) {
        this.isLockEnabled = enabled;
    }

    public void recordFailedAttempt() {
        this.failedAttempts++;
    }

    public boolean isLockedOut() {
        if (lockoutEndTime == null) return false;
        return LocalDateTime.now().isBefore(lockoutEndTime);
    }

    public static String hashPIN(String pin) {
        return String.valueOf(pin.hashCode());
    }

    public String getHashedPIN() { return hashedPIN; }
    public void setHashedPIN(String hashedPIN) { this.hashedPIN = hashedPIN; }
    public boolean isLockEnabled() { return isLockEnabled; }
    public void setLockEnabled(boolean lockEnabled) { isLockEnabled = lockEnabled; }
    public int getFailedAttempts() { return failedAttempts; }
    public void setFailedAttempts(int failedAttempts) { this.failedAttempts = failedAttempts; }
    public LocalDateTime getLockoutEndTime() { return lockoutEndTime; }
    public void setLockoutEndTime(LocalDateTime lockoutEndTime) { this.lockoutEndTime = lockoutEndTime; }
}