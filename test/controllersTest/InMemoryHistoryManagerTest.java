package controllersTest;

import model.*;
import controllers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    //private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    public void setUp() {
        //taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    public void shouldAddAndGetHistory() {
        Task task1 = new Task(1,"Убраться в комнате", "Протереть пыль и пр", TaskStatus.NEW);
        Epic epic1 = new Epic(2,"Сделать дипломную работу", "успеть за месяц");
        Subtask subtask1 = new Subtask(3,"Сделать дело1", "10 микродел",
                TaskStatus.NEW, epic1.getId());
        historyManager.add(task1);
        assertEquals(1, historyManager.getHistory().size());
        historyManager.add(task1);
        assertEquals(1, historyManager.getHistory().size());
        historyManager.add(epic1);
        assertEquals(2, historyManager.getHistory().size());
        historyManager.add(subtask1);
        assertEquals(3, historyManager.getHistory().size());
        historyManager.add(task1);
        assertEquals(3, historyManager.getHistory().size());
        assertTrue(historyManager.getHistory().contains(task1));
        assertTrue(historyManager.getHistory().contains(epic1));
        assertTrue(historyManager.getHistory().contains(subtask1));

        historyManager.remove(subtask1.getId());
        assertEquals(2, historyManager.getHistory().size());
    }

    @Test
    void shouldRemoveFirstTaskWhenAddNewTaskAndListAreFull() {
        Task task11 = new Task(101,"Задача 101", "Описание", TaskStatus.NEW);
        for (int i = 0; i < 100; i++) {
            historyManager.add(new Task(i,"Задача " + i, "Описание", TaskStatus.NEW));
        }
        historyManager.add(task11);

        assertEquals(101, historyManager.getHistory().size());
        assertEquals(task11, historyManager.getHistory().get(100));

        historyManager.add(task11);
        assertEquals(task11, historyManager.getHistory().getLast());

        Task task5 = new Task(5,"Задача 5", "Описание", TaskStatus.NEW);
        for (int i = 0; i < 25; i++) {
            historyManager.add(task5);
        }
        assertEquals(101, historyManager.getHistory().size());
        assertEquals(task5, historyManager.getHistory().getLast());
    }
}