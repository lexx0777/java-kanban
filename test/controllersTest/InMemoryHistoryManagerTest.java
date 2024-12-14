package controllersTest;

import model.*;
import controllers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private InMemoryTaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    public void setUp() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    public void shouldAddAndGetHistory() {
        Task task1 = new Task(0,"Убраться в комнате", "Протереть пыль и пр", TaskStatus.NEW);
        Epic epic1 = new Epic(0,"Сделать дипломную работу", "успеть за месяц");
        Subtask subtask1 = new Subtask(0,"Сделать дело1", "10 микродел",
                TaskStatus.NEW, epic1.getId());
        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(subtask1);
        assertEquals(3, historyManager.getHistory().size());
        assertTrue(historyManager.getHistory().contains(task1));
        assertTrue(historyManager.getHistory().contains(epic1));
        assertTrue(historyManager.getHistory().contains(subtask1));
    }

    @Test
    void shouldRemoveFirstTaskWhenAddNewTaskAndListAreFull() {
        Task task11 = new Task(0,"Задача 11", "Описание", TaskStatus.NEW);
        for (int i = 0; i < 10; i++) {
            historyManager.add(new Task(0,"Задача " + i, "Описание", TaskStatus.NEW));
        }
        historyManager.add(task11);

        assertEquals(10, historyManager.getHistory().size());
        assertEquals(task11, historyManager.getHistory().get(9));

    }
}