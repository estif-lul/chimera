package com.chimera.skill;

/**
 * Checked exception thrown when a media generation request would exceed
 * the agent's or campaign's allocated budget.
 */
public class BudgetExceededException extends Exception {

    public BudgetExceededException(String message) {
        super(message);
    }

    public BudgetExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}
