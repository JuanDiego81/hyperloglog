package hyperloglog;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

public class HyperLogLogTest {

    private HyperLogLog hll;

    @Before
    public void setUp() {
        hll = new HyperLogLog(1024); 
    }

    @Test
    public void test_hashing_small() throws FileNotFoundException {
        String basePath = "data/01-hash/01-SmallTests/";
        File inputFile = new File(basePath + "Test02.in");
        File outputFile = new File(basePath + "Test02.ans");

        try (Scanner inScanner = new Scanner(inputFile);
            Scanner outScanner = new Scanner(outputFile)) {

            while (inScanner.hasNextLine() && outScanner.hasNextLine()) {
                String inputLine = inScanner.nextLine().trim();
                String expectedHex = outScanner.nextLine().trim();

                // Remove "0x" prefix if present and parse as hex
                String cleanInput = inputLine.replaceFirst("^0x", "");
                int inputValue = (int) Long.parseLong(cleanInput, 16);

                // Get hash and convert to hex string
                int actualHash = hll.h(inputValue);
                String actualHex = String.format("%08x", actualHash);
                
                // Compare hex strings
                assertEquals("Failed for: " + inputLine, expectedHex, actualHex);
            }
        }
    }

    @Test
    public void test_hashing_big() throws FileNotFoundException {
        String basePath = "data/01-hash/02-BigTests/";
        File inputFile = new File(basePath + "Test01.in");
        File outputFile = new File(basePath + "Test01.ans");

        try (Scanner inScanner = new Scanner(inputFile);
            Scanner outScanner = new Scanner(outputFile)) {

            while (inScanner.hasNextLine() && outScanner.hasNextLine()) {
                String inputLine = inScanner.nextLine().trim();
                String expectedHex = outScanner.nextLine().trim();

                // Remove "0x" prefix if present and parse as hex
                String cleanInput = inputLine.replaceFirst("^0x", "");
                int inputValue = (int) Long.parseLong(cleanInput, 16);

                // Get hash and convert to hex string
                int actualHash = hll.h(inputValue);
                String actualHex = String.format("%08x", actualHash);
                
                // Compare hex strings
                assertEquals("Failed for: " + inputLine, expectedHex, actualHex);
            }
        }
    }

    @Test
    public void test_rho_small() throws FileNotFoundException {
        String basePath = "data/02-rho/01-SmallTests/";
        File inputFile = new File(basePath + "Test01.in");
        File outputFile = new File(basePath + "Test01.ans");

        try (Scanner inScanner = new Scanner(inputFile);
            Scanner outScanner = new Scanner(outputFile)) {

            while (inScanner.hasNextLine() && outScanner.hasNextLine()) {
                String inputLine = inScanner.nextLine().trim();
                String expectedResult = outScanner.nextLine().trim();

                // Remove "0x" prefix if present and parse as hex
                String cleanInput = inputLine.replaceFirst("^0x", "");
                int inputValue = (int) Long.parseLong(cleanInput, 16);

                
                int actual_rho = hll.rho(inputValue);

                int expectedRho = Integer.parseInt(expectedResult);
                
                
                assertEquals("Failed for: " + inputLine, expectedRho, actual_rho);
            }
        }
    }

    @Test
    public void test_rho_big() throws FileNotFoundException {
        String basePath = "data/02-rho/02-BigTests/";
        File inputFile = new File(basePath + "Test01.in");
        File outputFile = new File(basePath + "Test01.ans");

        try (Scanner inScanner = new Scanner(inputFile);
            Scanner outScanner = new Scanner(outputFile)) {

            while (inScanner.hasNextLine() && outScanner.hasNextLine()) {
                String inputLine = inScanner.nextLine().trim();
                String expectedResult = outScanner.nextLine().trim();

                // Remove "0x" prefix if present and parse as hex
                String cleanInput = inputLine.replaceFirst("^0x", "");
                int inputValue = (int) Long.parseLong(cleanInput, 16);

                
                int actual_rho = hll.rho(inputValue);

                int expectedRho = Integer.parseInt(expectedResult);
                
                
                assertEquals("Failed for: " + inputLine, expectedRho, actual_rho);
            }
        }
    }

    @Test
    public void test_registers_small() throws FileNotFoundException {
        String basePath = "data/03-registers/01-SmallTests/";
        File inputFile = new File(basePath + "Test02.in");
        File outputFile = new File(basePath + "Test02.ans");


        try (Scanner inScanner = new Scanner(inputFile);
            Scanner outScanner = new Scanner(outputFile)) {
            
            int [] count = new int[1024];
            
            while (inScanner.hasNextLine()) {
                String inputLine = inScanner.nextLine().trim();
                String cleanInput = inputLine.replaceFirst("^0x", "");
                int inputValue = (int) Long.parseLong(cleanInput, 16);
                
                int index = hll.f(inputValue);
                int hashed = hll.h(inputValue);
                int number_leading_zeros = hll.rho(hashed);

                if (number_leading_zeros > count[index]) {
                    count[index] = number_leading_zeros;
                }
            }
            
            int i = 0;
            while (outScanner.hasNextLine()) {
                String expectedResult = outScanner.nextLine().trim();

                int expectedCount = Integer.parseInt(expectedResult);
                
                
                assertEquals("Mismatch at index " + i, expectedCount, count[i]);
                i++;
            }
        }
    }

    @Test
    public void test_registers_Big() throws FileNotFoundException {
        String basePath = "data/03-registers/02-BigTests/";
        File inputFile = new File(basePath + "Test02.in");
        File outputFile = new File(basePath + "Test02.ans");


        try (Scanner inScanner = new Scanner(inputFile);
            Scanner outScanner = new Scanner(outputFile)) {
            
            int [] count = new int[1024];
            
            while (inScanner.hasNextLine()) {
                String inputLine = inScanner.nextLine().trim();
                String cleanInput = inputLine.replaceFirst("^0x", "");
                int inputValue = (int) Long.parseLong(cleanInput, 16);
                
                int index = hll.f(inputValue);
                int hashed = hll.h(inputValue);
                int number_leading_zeros = hll.rho(hashed);

                if (number_leading_zeros > count[index]) {
                    count[index] = number_leading_zeros;
                }
            }
            
            int i = 0;
            while (outScanner.hasNextLine()) {
                String expectedResult = outScanner.nextLine().trim();

                int expectedCount = Integer.parseInt(expectedResult);
                
                
                assertEquals("Mismatch at index " + i, expectedCount, count[i]);
                i++;
            }
        }
    }

    @Test
    public void test_threshold_small() throws FileNotFoundException {
        String basePath = "data/04-threshold/01-SmallTests/";
        File inputFile = new File(basePath + "Test02.in");
        File outputFile = new File(basePath + "Test02.ans");

        try (
            Scanner inScanner = new Scanner(inputFile);
            Scanner outScanner = new Scanner(outputFile)
        ) {
           
            int threshold = 0;
            if (inScanner.hasNextInt()) {
                threshold = inScanner.nextInt();
            }

            // Collect remaining lines (data to process)
            StringBuilder remainingInput = new StringBuilder();
            while (inScanner.hasNextLine()) {
                String line = inScanner.nextLine().trim();
                if (!line.isEmpty()) {
                    remainingInput.append(line).append("\n");
                }
            }

            // Run processFromFile() on remaining data
            Scanner dataScanner = new Scanner(remainingInput.toString());
            hll.processFromFile(dataScanner);

            // Get estimated value
            long estimated = hll.cardinalityEstimation();

            //  Read expected output ("below" or "above")
            String expectedResult = "";
            if (outScanner.hasNextLine()) {
                expectedResult = outScanner.nextLine().trim().toLowerCase();
            }

            // Check the condition
            boolean isBelow = estimated < threshold;
            boolean isAbove = estimated > threshold;

            if (expectedResult.equals("below")) {
                assertTrue("Expected below threshold but got " + estimated, isBelow);
            } else if (expectedResult.equals("above")) {
                assertTrue("Expected above threshold but got " + estimated, isAbove);
            } else {
                fail("Unexpected answer value: " + expectedResult);
            }
        }
    }

    @Test
    public void test_threshold_big() throws FileNotFoundException {
        String basePath = "data/04-threshold/02-BigTests/";
        File inputFile = new File(basePath + "Test04.in");
        File outputFile = new File(basePath + "Test04.ans");

        try (
            Scanner inScanner = new Scanner(inputFile);
            Scanner outScanner = new Scanner(outputFile)
        ) {
           
            int threshold = 0;
            if (inScanner.hasNextInt()) {
                threshold = inScanner.nextInt();
            }

            // Collect remaining lines (data to process)
            StringBuilder remainingInput = new StringBuilder();
            while (inScanner.hasNextLine()) {
                String line = inScanner.nextLine().trim();
                if (!line.isEmpty()) {
                    remainingInput.append(line).append("\n");
                }
            }

            // Run processFromFile() on remaining data
            Scanner dataScanner = new Scanner(remainingInput.toString());
            hll.processFromFile(dataScanner);

            // Get estimated value
            long estimated = hll.cardinalityEstimation();

            //  Read expected output ("below" or "above")
            String expectedResult = "";
            if (outScanner.hasNextLine()) {
                expectedResult = outScanner.nextLine().trim().toLowerCase();
            }

            // Check the condition
            boolean isBelow = estimated < threshold;
            boolean isAbove = estimated > threshold;

            if (expectedResult.equals("below")) {
                assertTrue("Expected below threshold but got " + estimated, isBelow);
            } else if (expectedResult.equals("above")) {
                assertTrue("Expected above threshold but got " + estimated, isAbove);
            } else {
                fail("Unexpected answer value: " + expectedResult);
            }
        }
    }


}
