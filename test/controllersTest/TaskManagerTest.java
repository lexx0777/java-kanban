package controllersTest;

import controllers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    public abstract T createTaskManager();

    @BeforeEach
    void setUp() {
        taskManager = createTaskManager();
    }

    protected Task addTask() {
        return new Task(1,"Task1", "Task description1", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now());
    }

    protected Epic addEpic() {
        return new Epic(10,"Epic1", "Epic description1");
    }

    protected Subtask addSubtask(Epic epic) {
        return new Subtask(20,"Subtask1", "Subtask description1", TaskStatus.NEW,
                epic.getId(), Duration.ofMinutes(30), LocalDateTime.now());
    }

    @Test
    public void shouldAddTask() {
        Task task = addTask();
        task.setId(1);
        taskManager.add(task);
        assertEquals(task, taskManager.getTaskById(task.getId()));
        assertEquals(TaskStatus.NEW, taskManager.getTaskById(task.getId()).getStatus());
    }

    @Test
    public void shouldAddEpic() {
        Epic epic = addEpic();
        taskManager.add(epic);
        assertEquals(epic, taskManager.getEpicById(epic.getId()));
        assertEquals(TaskStatus.NEW, taskManager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    public void shouldAddSubtask() {
        Epic epic = addEpic();
        taskManager.add(epic);
        Subtask subtask = addSubtask(epic);
        taskManager.add(subtask);
        assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()));
        assertEquals(TaskStatus.NEW, taskManager.getSubtaskById(subtask.getId()).getStatus());
        assertEquals(epic.getId(), subtask.getEpicId());
    }

    @Test
    public void shouldUpdateTask() {
        Task task = addTask();
        taskManager.add(task);
        taskManager.getTaskById(task.getId()).setStatus(TaskStatus.DONE);
        taskManager.update(task);
        assertEquals(TaskStatus.DONE, taskManager.getTaskById(task.getId()).getStatus());
        assertEquals(TaskStatus.DONE, task.getStatus());
    }

    @Test
    public void shouldUpdateEpic() {
        Epic epic = addEpic();
        taskManager.add(epic);
        taskManager.getEpicById(epic.getId()).setStatus(TaskStatus.DONE);
        taskManager.update(epic);
        assertEquals(TaskStatus.DONE, taskManager.getEpicById(epic.getId()).getStatus());
        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    public void ShouldUpdateSubtask() {
        Epic epic = addEpic();
        taskManager.add(epic);
        Subtask subtask = addSubtask(epic);
        taskManager.add(subtask);
        taskManager.getSubtaskById(subtask.getId()).setStatus(TaskStatus.DONE);
        taskManager.update(subtask);
        assertEquals(TaskStatus.DONE, taskManager.getSubtaskById(subtask.getId()).getStatus());
        assertEquals(TaskStatus.DONE, subtask.getStatus());
    }

    @Test
    public void shouldGetTaskById() {
        Task task = addTask();
        taskManager.add(task);
        Task savedTask = taskManager.getTaskById(task.getId());
        assertEquals(task, savedTask);
        assertNotNull(savedTask);
    }

    @Test
    public void shouldGetEpicById() {
        Epic epic = addEpic();
        taskManager.add(epic);
        Epic savedEpic = taskManager.getEpicById(epic.getId());
        assertEquals(epic, savedEpic);
        assertNotNull(savedEpic);
    }

    @Test
    public void shouldGetSubtaskById() {
        Epic epic = addEpic();
        taskManager.add(epic);
        Subtask subtask = addSubtask(epic);
        taskManager.add(subtask);
        Subtask savedSubtask = taskManager.getSubtaskById(subtask.getId());
        assertEquals(subtask, savedSubtask);
        assertNotNull(savedSubtask);
    }

    @Test
    public void shouldGetAllTasks() {
        Task task = addTask();
        Task task2 = new Task(2,"Task2", "Task description2", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now().plus(Duration.ofMinutes(30)));
        taskManager.add(task);
        taskManager.add(task2);
        List<Task> tasks = taskManager.getTasks();
        assertNotNull(tasks);
        assertEquals(2, tasks.size());
    }

    @Test
    public void shouldGetAllEpics() {
        Epic epic = addEpic();
        Epic epic2 = new Epic(11,"Epic2", "Epic description2");
        taskManager.add(epic);
        taskManager.add(epic2);
        List<Epic> epics = taskManager.getEpics();
        assertNotNull(epics);
        assertEquals(2, epics.size());
    }

    @Test
    public void shouldGetAllSubtasks() {
        Epic epic = addEpic();
        taskManager.add(epic);
        Subtask subtask = addSubtask(epic);
        Subtask subtask2 = new Subtask(21, "Subtask2", "Subtask description2", TaskStatus.NEW, epic.getId()
                , Duration.ofMinutes(30), LocalDateTime.now().plus(Duration.ofMinutes(30)));
        taskManager.add(subtask);
        taskManager.add(subtask2);
        List<Subtask> subtasks = taskManager.getSubtasks();
        assertNotNull(subtasks);
        assertEquals(2, subtasks.size());
    }

    @Test
    public void shouldRemoveTaskById() {
        Task task = addTask();
        taskManager.add(task);
        taskManager.removeTaskById(task.getId());
        assertNull(taskManager.getTaskById(task.getId()));
    }

    @Test
    public void shouldRemoveEpicById() {
        Epic epic = addEpic();
        taskManager.add(epic);
        taskManager.removeEpicById(epic.getId());
        assertNull(taskManager.getEpicById(epic.getId()));
    }

    @Test
    public void shouldRemoveSubtaskById() {
        Epic epic = addEpic();
        taskManager.add(epic);
        Subtask subtask = addSubtask(epic);
        taskManager.add(subtask);
        taskManager.removeSubtaskById(subtask.getId());
        assertNull(taskManager.getSubtaskById(subtask.getId()));
    }

    @Test
    public void shouldRemoveAllTasks() {
        Task task = addTask();
        Task task2 = new Task(2,"Task2", "Task description2", TaskStatus.NEW,
                Duration.ofMinutes(30), LocalDateTime.now().plus(Duration.ofMinutes(30)));
        taskManager.add(task);
        taskManager.add(task2);
        taskManager.removeAllTasks();
        assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    public void shouldRemoveAllEpics() {
        Epic epic = addEpic();
        Epic epic2 = new Epic(11,"Epic2", "Epic description2");
        Subtask subtask = addSubtask(epic);
        taskManager.add(subtask);
        taskManager.add(epic);
        taskManager.add(epic2);
        taskManager.removeAllEpics();
        assertEquals(0, taskManager.getEpics().size());
        assertEquals(0, taskManager.getSubtasks().size());
    }

    @Test
    public void shouldRemoveAllSubtasks() {
        Epic epic = addEpic();
        taskManager.add(epic);
        Subtask subtask = addSubtask(epic);
        Subtask subtask2 = new Subtask(21, "Subtask2", "Subtask description2", TaskStatus.NEW, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.now().plus(Duration.ofMinutes(30)));
        taskManager.add(subtask);
        taskManager.add(subtask2);
        taskManager.removeAllSubtasks();
        assertEquals(0, taskManager.getSubtasks().size());
    }

    @Test
    public void shouldGetSubtasksForEpic() {
        Epic epic = addEpic();
        taskManager.add(epic);
        Subtask subtask = addSubtask(epic);
        Subtask subtask2 = new Subtask(21,"Subtask2", "Subtask description2", TaskStatus.NEW, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.now().plus(Duration.ofMinutes(30)));
        taskManager.add(subtask);
        taskManager.add(subtask2);
        List<Subtask> subtasks = taskManager.getSubtasks(epic.getId());
        assertNotNull(subtasks);
        assertEquals(2, subtasks.size());
    }

}