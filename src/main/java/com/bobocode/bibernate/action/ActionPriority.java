package com.bobocode.bibernate.action;

public enum ActionPriority {
    INSERT_PRIORITY(1),
    UPDATE_PRIORITY(2),
    DELETE_PRIORITY(3);

    private final int priorityNumber;

    ActionPriority(int priorityNumber) {
        this.priorityNumber = priorityNumber;
    }

    public int getPriorityNumber() {
        return priorityNumber;
    }
}
