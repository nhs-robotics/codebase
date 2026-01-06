package codebase.actions;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class SimultaneousAction implements Action {

    private final ArrayList<Action> actions;

    public SimultaneousAction(Action first, Action... rest) {
        this.actions = new ArrayList<>();

        this.actions.add(first);

        for (Action action : rest) {
            this.add(action, false);
        }
    }

    @Override
    public void init() {
        for (Action action : actions) {
            action.init();
        }
    }

    @Override
    public boolean isComplete() {
        for (Action action : actions) {
            if (!action.isComplete()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void loop() {
        for (Action action : actions) {
            if (!action.isComplete()) {
                action.loop();
            }
        }
    }

    public void add(@NonNull Action action, boolean init) {
        String actionName = action.getClass().getName();

        for (Action a : actions) {
            if (a.getClass().getName().equals(actionName)) {
                throw new IllegalArgumentException("You can't add multiple of the same class of Action to SimultaneousAction.");
            }
        }
        actions.add(action);

        if (init) {
            action.init();
        }
    }

    public List<Action> getActions() {
        return actions;
    }
}