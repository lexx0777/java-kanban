package controllers;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{

    final private ArrayList<Task> history = new ArrayList<>();
    final private int historySize;

    public InMemoryHistoryManager(int historySize) {
        this.historySize = historySize;
    }

    @Override
    public boolean add(Task task) {
        if (task == null) {
            return false;
        }
        history.addLast(task);
        if ( history.size() > historySize) {
            history.removeFirst();
        }
        return true;
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}

