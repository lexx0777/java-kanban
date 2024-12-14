package modelTest;

import model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    Subtask subtask1;
    Subtask subtask2;
    Subtask subtask3;

    Epic epic3;


    @BeforeEach
    void beforeEach() {

        epic3 = new Epic(5, "Epic3", "Описание Epic3");
        subtask1 = new Subtask(7, "subtask3_1", "Описание subtask3_1", TaskStatus.DONE, epic3.getId());
        subtask2 = new Subtask(8, "subtask3_1", "Описание subtask3_1", TaskStatus.DONE, epic3.getId());
        subtask3 = new Subtask(7, "subtask3_1", "Описание subtask3_1", TaskStatus.DONE, epic3.getId());
    }

    @Test
    void shouldEqualsIsSameIds() {
        assertEquals(subtask1, subtask3);
    }

    @Test
    void shouldNotEqualsIsDifferentIds() {
        assertNotEquals(subtask1, subtask2);
    }
}