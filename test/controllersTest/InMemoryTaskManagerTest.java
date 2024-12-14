package controllersTest;

import model.*;
import controllers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private InMemoryTaskManager taskManager;
    private Task task1;
    private Task task2;
    private Epic epic1;
    private Epic epic2;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
        task1 = new Task(1, "убраться в комнате", "пыль и пр " , TaskStatus.NEW);
        task2 = new Task(2, "Сходить в маг", "купить броколи", TaskStatus.NEW);
        epic1 = new Epic(3, "Дело", "неск подходов");
        epic2 = new Epic(4, "Сделать тз", "успеть");

    }

    @Test
    void shouldAddAndGetNewTasks() {
        taskManager.add(task1);
        Task task = taskManager.getTaskById(task1.getId());
        assertNotNull(task);
        assertEquals(task1, task);
    }

    @Test
    void shouldGetAllTasks() {
        taskManager.add(task1);
        taskManager.add(task2);
        List<Task> tasks = taskManager.getTasks();
        assertNotNull(tasks);
        assertEquals(2, tasks.size());
        assertTrue(tasks.contains(task1));
        assertTrue(tasks.contains(task2));
    }

    @Test
    void shouldUpdateTaskToNewTask() {
        taskManager.add(task1);
        Task updateTask1 = new Task(1,"Не забыть убраться в комнате", "Можно без влажной уборки",
                TaskStatus.IN_PROGRESS);
        taskManager.update(updateTask1);
        List<Task> tasks = taskManager.getTasks();
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        assertEquals("Не забыть убраться в комнате", tasks.getFirst().getTitle());

    }

    @Test
    void shouldRemoveAllTasks() {
        taskManager.add(task1);
        taskManager.add(task2);
        taskManager.removeAllTasks();
        List<Task> tasks = taskManager.getTasks();
        assertTrue(tasks.isEmpty());
    }

    @Test
    void shouldRemoveTaskById() {
        taskManager.add(task1);
        taskManager.add(task2);
        taskManager.removeTaskById(task1.getId());
        List<Task> tasks = taskManager.getTasks();
        assertFalse(tasks.contains(task1));
        assertTrue(tasks.contains(task2));
    }

    @Test
    void shouldAddAndGetNewEpics() {
        taskManager.add(epic1);
        Epic epic = taskManager.getEpicById(epic1.getId());
        assertNotNull(epic);
        assertEquals(epic1, epic);


    }

    @Test
    void shouldGetAllEpics() {
        taskManager.add(epic1);
        taskManager.add(epic2);
        List<Epic> epics = taskManager.getEpics();
        assertNotNull(epics);
        assertEquals(2, epics.size());
        assertTrue(epics.contains(epic1));
        assertTrue(epics.contains(epic2));
    }

    @Test
    void shouldUpdateEpicToNewEpic() {
        taskManager.add(epic1);
        Epic updateEpic1 = new Epic(1,"Съездить в отпуск в июне", "В приоритете в Германию, попить нормального пива");
        taskManager.update(updateEpic1);
        List<Epic> epics = taskManager.getEpics();
        assertNotNull(epics);
        assertEquals(1, epics.size());
        assertEquals("Съездить в отпуск в июне", epics.getFirst().getTitle());

    }

    @Test
    void shouldRemoveAllEpicsAlsoShouldRemoveAllSubtasks() {
        taskManager.add(epic1);
        taskManager.add(epic2);
        Subtask subtask1 = new Subtask(5,"Сделать презентацию", "12 слайдов",
                TaskStatus.NEW, epic1.getId());
        Subtask subtask2 = new Subtask(6,"Подготовить речь", "На 5-7 минут выступления",
                TaskStatus.NEW, epic1.getId());
        taskManager.add(subtask1);
        taskManager.add(subtask2);
        taskManager.removeAllEpics();
        List<Epic> epics = taskManager.getEpics();
        List<Subtask> subtasks = taskManager.getSubtasks();
        assertTrue(epics.isEmpty());
        assertTrue(subtasks.isEmpty());
    }

    @Test
    void shouldRemoveEpicByIdAlsoShouldRemoveAllSubtasks() {
        taskManager.add(epic1);
        taskManager.add(epic2);
        Subtask subtask1 = new Subtask(5,"Сделать презентацию", "12 слайдов",
                TaskStatus.NEW, epic1.getId());
        Subtask subtask2 = new Subtask(6,"Подготовить речь", "На 5-7 минут выступления",
                TaskStatus.NEW, epic1.getId());
        taskManager.add(subtask1);
        taskManager.add(subtask2);
        taskManager.removeEpicById(epic1.getId());
        List<Epic> epics = taskManager.getEpics();
        List<Subtask> subtasks = taskManager.getSubtasks();
        assertFalse(epics.contains(epic1));
        assertTrue(subtasks.isEmpty());
    }

    @Test
    void shouldAddAndGetNewSubtasks() {
        taskManager.add(epic1);
        Subtask subtask1 = new Subtask(5,"Сделать презентацию", "12 слайдов",
                TaskStatus.NEW, epic1.getId());
        taskManager.add(subtask1);
        Subtask subtask = taskManager.getSubtaskById(subtask1.getId());
        assertNotNull(subtask1);
        assertEquals(subtask1, subtask);
    }

    @Test
    void shouldGetAllSubtasks() {
        taskManager.add(epic1);
        Subtask subtask1 = new Subtask(epic1.getId(),"Сделать презентацию", "12 слайдов",
                TaskStatus.NEW, epic1.getId());
        Subtask subtask2 = new Subtask(epic1.getId(),"Подготовить речь", "На 5-7 минут выступления",
                TaskStatus.NEW, epic1.getId());
        taskManager.add(subtask1);
        taskManager.add(subtask2);
        List<Subtask> subtasks = taskManager.getSubtasks();
        assertNotNull(subtasks);
        assertEquals(2, subtasks.size());
        assertTrue(subtasks.contains(subtask1));
        assertTrue(subtasks.contains(subtask2));
    }

    @Test
    void shouldUpdateSubtaskShouldChangeEpicStatus() {
        taskManager.add(epic1);
        Subtask subtask1 = new Subtask(5,"Сделать презентацию", "небольшую",
                TaskStatus.IN_PROGRESS, epic1.getId());
        Subtask subtask2 = new Subtask(6,"написать речь", "краткую",
                TaskStatus.DONE, epic1.getId());
        taskManager.add(subtask1);
        taskManager.add(subtask2);
        TaskStatus epic1Status = epic1.getStatus();
        Subtask subtask3 = new Subtask(7,"Погладить шнурки", "аккуратно",
                TaskStatus.IN_PROGRESS, epic1.getId());
        taskManager.update(subtask3);
        TaskStatus actualEpicStatus = epic1.getStatus();
        assertEquals(epic1Status, actualEpicStatus);
    }

    @Test
    void shouldRemoveSubtaskByIdAlsoShouldChangeEpicStatus() {
        taskManager.add(epic1);
        Subtask subtask1 = new Subtask(5,"Сделать презентацию", "12 слайдов",
                TaskStatus.NEW, epic1.getId());
        Subtask subtask2 = new Subtask(6,"Подготовить речь", "На 5-7 минут выступления",
                TaskStatus.DONE, epic1.getId());
        taskManager.add(subtask1);
        taskManager.add(subtask2);
        TaskStatus epic1Status = epic1.getStatus();
        taskManager.removeSubtaskById(subtask1.getId());
        List<Subtask> subtasks = taskManager.getSubtasks();
        TaskStatus actualEpicStatus = epic1.getStatus();
        assertNotNull(subtasks);
        assertEquals(1, subtasks.size());
        assertTrue(subtasks.contains(subtask2));
        assertNotEquals(epic1Status, actualEpicStatus);

    }
}