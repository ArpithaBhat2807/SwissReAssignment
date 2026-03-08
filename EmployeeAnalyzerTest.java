import org.example.EmployeeAnalyzer;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeeAnalyzerTest {

    @Test
    void testEmployeesTC005Output() throws Exception {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        System.setOut(new PrintStream(outputStream));

        try {
            EmployeeAnalyzer.main(new String[]{});
        } finally {
            System.setOut(originalOut);
        }

        String output = outputStream.toString();

        String[] lines = output
                .lines()
                .filter(line -> !line.trim().isEmpty())
                .toArray(String[]::new);

        assertEquals(9, lines.length);

        assertTrue(output.contains("Manager Joe earns MORE than allowed"));
        assertTrue(output.contains("Manager Martin earns LESS than required"));
        assertTrue(output.contains("Manager Bob earns LESS than required"));
        assertTrue(output.contains("Henry has reporting line too long"));

        assertEquals("Manager Joe earns MORE than allowed by 67500.0", lines[0]);
        assertEquals("Manager Martin earns LESS than required by 4000.0", lines[1]);
        assertEquals("Manager Bob earns LESS than required by 4800.0", lines[2]);
        assertEquals("Manager Alice earns LESS than required by 15000.0", lines[3]);
        assertEquals("Manager Eva earns LESS than required by 7600.0", lines[4]);
        assertEquals("Manager Frank earns LESS than required by 7200.0", lines[5]);
        assertEquals("Manager Gina earns LESS than required by 6800.0", lines[6]);
        assertEquals("Gina has reporting line too long by 1", lines[7]);
        assertEquals("Henry has reporting line too long by 2", lines[8]);
    }

    @Test
    void testFileNotFoundException() {

        Exception exception = assertThrows(Exception.class, () -> {
            EmployeeAnalyzer.main(new String[]{"wrong_file.csv"});
        });

        assertTrue(exception.getMessage().contains("File not found"));
    }
}
