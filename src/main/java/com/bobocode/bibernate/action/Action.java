package com.bobocode.bibernate.action;

import com.bobocode.bibernate.session.Session;
import java.util.Comparator;

/**
 * Represents SQL query being queued in {@link Session}. Lazily executed during {@link Session#flush()} call
 */
public interface Action {

    /**
     * Calls SQL query execution
     */
    void execute();

    /**
     * Used to prioritize actions and execute them in order after {@link Session#flush()} is called
     * @return {@link ActionPriority action priority}
     */
    ActionPriority getPriority();

    static Comparator<Action> comparingPriority() {
        return Comparator.comparing(action -> action.getPriority().getPriorityNumber());
    }
}
