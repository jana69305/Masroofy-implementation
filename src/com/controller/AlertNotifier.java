package com.controller;

import com.model.BudgetCycle;

/**
 * Handles budget alert notifications for the Masroofy application.
 * Monitors spending thresholds and cycle deadlines, and sends push alerts to the user.
 */

public class AlertNotifier {
/**
     * Checks if the user has spent 80% or more of their budget allowance.
     * Sends a warning alert at 80% and a critical alert at 100%.
     *
     * @param cycle the current {@link BudgetCycle} containing allowance and balance data
     */

public void check80Percent(BudgetCycle cycle) {

double spent = cycle.getTotalAllowance() - cycle.getRemainingBalance();

double percentage = (spent / cycle.getTotalAllowance()) * 100;

if (percentage >= 100) {

sendPushAlert("CRITICAL: You have exhausted your entire budget!");

} else if (percentage >= 80) {

sendPushAlert(String.format(

"WARNING: You have used %.1f%% of your allowance.", percentage));

}

}
 /**
     * Sends a push alert message to the user via console output.
     *
     * @param message the alert message to display
     */
public void sendPushAlert(String message) {

System.out.println("\\n* ALERT: " + message + " *\\n");

}
  /**
     * Checks if today is the final day of the current budget cycle.
     * Sends a warning alert if only one day remains.
     *
     * @param cycle the current {@link BudgetCycle} to check remaining days for
     */
public void checkFinalDay(BudgetCycle cycle) {

if (cycle.getRemainingDays() == 1) {

sendPushAlert("WARNING: This is your final day of the budget cycle!");

}

}

}