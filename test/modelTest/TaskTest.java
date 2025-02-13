package modelTest;

import model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {
    Task task1;
    Task task2;
    Task task3;

    @BeforeEach
    void beforeEach() {
        task1 = new Task(1, "Задача1", "Описание задачи1", TaskStatus.NEW);
        task2 = new Task(2, "Задача2", "Описание задачи2", TaskStatus.NEW);
        task3 = new Task(1, "Задача3", "Описание задачи3", TaskStatus.NEW);
     }

    @Test
    void shouldEqualsIsSameIds() {
        assertEquals(task1, task3);
    }

    @Test
    void shouldNotEqualsIsDifferentIds() {
        assertNotEquals(task1, task2);
    }
}
