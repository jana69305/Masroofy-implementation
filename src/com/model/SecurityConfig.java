package com.model;

import java.time.LocalDateTime;
/**
 * Stores and manages the security configuration for the Masroofy application.
 * Handles PIN hashing and verification, lockout state, and failed attempt tracking.
 */
public class SecurityConfig {
    /** The hashed PIN used for authentication. */
    private String hashedPIN;

    /** Whether the privacy lock feature is currently enabled. */
    private boolean isLockEnabled;

    /** The number of consecutive failed PIN attempts. */
    private int failedAttempts;

    /** The date and time when the lockout period ends, or {@code null} if not locked. */
    private LocalDateTime lockoutEndTime;
/**
     * Constructs a default SecurityConfig with no PIN set,
     * lock disabled, zero failed attempts, and no lockout.
     */
    public SecurityConfig() {
        this.hashedPIN = "";
        this.isLockEnabled = false;
        this.failedAttempts = 0;
        this.lockoutEndTime = null;
    }
 /**
     * Constructs a SecurityConfig with the specified security state.
     *
     * @param hashedPIN      the stored hashed PIN string
     * @param isLockEnabled  whether the privacy lock is enabled
     * @param failedAttempts the number of consecutive failed attempts
     * @param lockoutEndTime the time when the lockout ends, or {@code null} if not locked
     */
    public SecurityConfig(String hashedPIN, boolean isLockEnabled,
                          int failedAttempts, LocalDateTime lockoutEndTime) {
        this.hashedPIN = hashedPIN;
        this.isLockEnabled = isLockEnabled;
        this.failedAttempts = failedAttempts;
        this.lockoutEndTime = lockoutEndTime;
    }
/**
     * Verifies whether the given plain-text PIN matches the stored hashed PIN.
     *
     * @param input the plain-text PIN entered by the user
     * @return {@code true} if the input matches the stored PIN, {@code false} otherwise
     */
    public boolean verifyPIN(String input) {
        return hashedPIN.equals(hashPIN(input));
    }
/**
     * Enables or disables the privacy lock.
     *
     * @param enabled {@code true} to enable the lock, {@code false} to disable it
     */
    public void setLockState(boolean enabled) {
        this.isLockEnabled = enabled;
    }
/**
     * Increments the failed attempt counter by one.
     */
    public void recordFailedAttempt() {
        this.failedAttempts++;
    }
/**
     * Checks whether the user is currently locked out.
     * The user is locked out if the current time is before the lockout end time.
     *
     * @return {@code true} if the user is locked out, {@code false} otherwise
     */
    public boolean isLockedOut() {
        if (lockoutEndTime == null) return false;
        return LocalDateTime.now().isBefore(lockoutEndTime);
    }
 /**
     * Hashes a plain-text PIN using Java's {@code hashCode()} method.
     *
     * @param pin the plain-text PIN to hash
     * @return the hashed PIN as a string
     */
    public static String hashPIN(String pin) {
        return String.valueOf(pin.hashCode());
    }

/** @return the stored hashed PIN */
    public String getHashedPIN() { return hashedPIN; }

    /** @param hashedPIN the hashed PIN to set */
    public void setHashedPIN(String hashedPIN) { this.hashedPIN = hashedPIN; }

    /** @return {@code true} if the privacy lock is enabled */
    public boolean isLockEnabled() { return isLockEnabled; }

    /** @param lockEnabled {@code true} to enable the lock, {@code false} to disable it */
    public void setLockEnabled(boolean lockEnabled) { isLockEnabled = lockEnabled; }

    /** @return the number of consecutive failed PIN attempts */
    public int getFailedAttempts() { return failedAttempts; }

    /** @param failedAttempts the failed attempt count to set */
    public void setFailedAttempts(int failedAttempts) { this.failedAttempts = failedAttempts; }

    /** @return the lockout end time, or {@code null} if not locked */
    public LocalDateTime getLockoutEndTime() { return lockoutEndTime; }

    /** @param lockoutEndTime the lockout end time to set, or {@code null} to clear it */
    public void setLockoutEndTime(LocalDateTime lockoutEndTime) { this.lockoutEndTime = lockoutEndTime; }
}