package hu.amoba.io;

import hu.amoba.model.Board;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class BoardIOTest {

    @TempDir
    Path tempDir;

    @Test
    void save_createsFileAndWritesHeader() throws Exception {
        Board b = new Board(3, 3);

        b.getCells()[1][1] = 'X';
        b.getCells()[1][2] = 'O';

        Path file = tempDir.resolve("tabla.txt");

        BoardIO.save(b, file);

        assertTrue(Files.exists(file), "A mentésnek létre kell hoznia a fájlt.");
        assertTrue(Files.size(file) > 0, "A mentett fájl nem lehet üres.");

        String header = Files.readAllLines(file).get(0).trim();
        assertEquals("3 3", header, "A fejlécnek 'rows cols' formátumúnak kell lennie.");
    }

    @Test
    void loadOrEmpty_readsBackMarksCorrectly() {
        Board b = new Board(4, 4);
        b.getCells()[2][1] = 'X';
        b.getCells()[2][2] = 'O';

        Path file = tempDir.resolve("tabla.txt");
        BoardIO.save(b, file);

        Board loaded = BoardIO.loadOrEmpty(file, 10, 10);

        assertEquals(4, loaded.getRows());
        assertEquals(4, loaded.getCols());

        assertEquals('X', loaded.getCells()[2][1]);
        assertEquals('O', loaded.getCells()[2][2]);
    }

    @Test
    void loadOrEmpty_whenFileMissing_returnsDefaultSizeBoard() {
        Path missing = tempDir.resolve("nincs.txt");

        Board loaded = BoardIO.loadOrEmpty(missing, 5, 6);

        assertEquals(5, loaded.getRows());
        assertEquals(6, loaded.getCols());
    }
}

