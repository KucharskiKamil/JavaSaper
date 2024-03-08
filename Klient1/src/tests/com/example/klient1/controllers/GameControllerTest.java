package com.example.klient1.controllers;
import com.example.klient1.klientController;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameControllerTest {
    // Tworzymy obiekt KlientController dla testów
    private klientController klientController = new klientController();

    @Test
    void testIsValidUsername() {
        // Testowanie poprawnego username
        assertTrue(klientController.isValidUsername("ValidUsername123"));

        // Testowanie błędnego username (zawiera niedozwolone znaki)
        assertFalse(klientController.isValidUsername("Invalid$Username"));

        // Testowanie błędnego username (puste)
        assertFalse(klientController.isValidUsername(""));

        // Testowanie błędnego username (null)
        assertFalse(klientController.isValidUsername(null));
    }

    @Test
    void testIsValidPassword()
    {
        // Testowanie poprawnego hasła
        assertTrue(klientController.isValidPassword("ValidPassword123!@#"));

        // Testowanie błędnego hasła (zawiera niedozwolone znaki)
        assertTrue(klientController.isValidPassword("InvalidPassword$%^"));

        // Testowanie błędnego hasła (puste)
        assertFalse(klientController.isValidPassword(""));

        // Testowanie błędnego hasła (null)
        assertFalse(klientController.isValidPassword(null));
    }
    @Test
    void testCountAdjacentBombs() {
        int[][] gameBoard = {
                {0, 1, 10},
                {10, 1, 0},
                {1, 0, 10}
        };

        // Testowanie, że metoda nie wyrzuca wyjątku dla prawidłowych wartości row i col
        assertDoesNotThrow(() -> klientController.countAdjacentBombs(gameBoard, 1, 1));

        // Testowanie, że metoda wyrzuca wyjątek dla wartości row < 0
        assertThrows(IllegalArgumentException.class, () -> klientController.countAdjacentBombs(gameBoard, -1, 1));

        // Testowanie, że metoda wyrzuca wyjątek dla wartości col < 0
        assertThrows(IllegalArgumentException.class, () -> klientController.countAdjacentBombs(gameBoard, 1, -1));

        // Testowanie, że metoda wyrzuca wyjątek dla wartości row >= gameBoard.length
        assertThrows(IllegalArgumentException.class, () -> klientController.countAdjacentBombs(gameBoard, 5, 1));

        // Testowanie, że metoda wyrzuca wyjątek dla wartości col >= gameBoard.length
        assertThrows(IllegalArgumentException.class, () -> klientController.countAdjacentBombs(gameBoard, 1, 5));
    }
}
