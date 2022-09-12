package com.bobocode.bibernate.action;

import java.util.Comparator;

public interface Action {
    void execute();
    ActionPriority getPriority();

    static Comparator<Action> comparingPriority() {
        return Comparator.comparing(action -> action.getPriority().getPriorityNumber());
    }
}
