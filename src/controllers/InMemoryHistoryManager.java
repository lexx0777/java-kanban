package controllers;

import model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager{

    final private HashMap<Integer, Node> historyMap = new HashMap<>(); //id, Node
    private Node head;
    private Node tail;

    private void removeNode(Node node){
        if (node != null) {
            if (node == tail
                    && node == head) {
                tail = null;
                head = null;
                node.setData(null);
                node.setPrev(null);
                node.setNext(null);
            } else if (node == tail) {
                tail = tail.getPrev();
            } else if (node == head) {
                head = head.getNext();
            } else {
                node.getPrev().setNext(node.getNext());
                node.getNext().setPrev(node.getPrev());
                node.setData(null);
                node.setPrev(null);
                node.setNext(null);
            }
        }
    }

    private Node linkLast(Task task) {
        Node node = new Node(null, task, null);
        if (task != null){
            if (tail == null) {
                head = node;
                tail = node;
            } else {
                node.setPrev(tail);
                tail.setNext(node);
                tail = node;
            }
        }
        historyMap.remove(node.getData().getId());
        return node;
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            if (historyMap.containsKey(task.getId())) {
                remove(task.getId());
                historyMap.put(task.getId(), linkLast(task));
            } else {
                historyMap.put(task.getId(), linkLast(task));
            }

        }
    }

    @Override
    public void remove(int taskId) {
        if (historyMap.containsKey(taskId)) {
            removeNode(historyMap.get(taskId));
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> tasksList = new ArrayList<>();
        Node node = head;
        while (node != null) {
            tasksList.add(node.getData());
            node = node.getNext();
        }
        return tasksList;
    }
}

