package com.controller;

import com.model.BudgetCycle;

public class AlertNotifier {

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

public void sendPushAlert(String message) {

System.out.println("\\n* ALERT: " + message + " *\\n");

}

public void checkFinalDay(BudgetCycle cycle) {

if (cycle.getRemainingDays() == 1) {

sendPushAlert("WARNING: This is your final day of the budget cycle!");

}

}

}