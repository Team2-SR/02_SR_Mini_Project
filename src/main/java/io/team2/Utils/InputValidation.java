package io.team2.Utils;

import java.util.Scanner;
import java.util.function.Predicate;

public class InputValidation {
    private static final Scanner scanner = new Scanner(System.in);
    private static String readInput(String message, Predicate<String> validateInput, String errorMessage) {
        while(true) {
            System.out.print(message);
            String s = scanner.nextLine().trim();
            if(validateInput.test(s)) {
                return s;
            } else {
                System.out.println(Color.ANSI_RED + errorMessage + Color.ANSI_RESET);
            }
        }
    }

    public static int readNumber(String messsage) {
        return Integer.parseInt(readInput(messsage, s -> s.matches("^\\d+$"), "Invalid Input"));
    }

    public static String readLetter(String message) {
        return readInput(message, s -> s.matches("^[a-zA-Z]+$"), "Invalid Input");
    }

    public static String readProductName(String message) {
        return readInput(message, s -> s.matches("^[A-Za-z][A-Za-z0-9 +_-]*$"), "Invalid Input");
    }

    public static double readProductPrice(String message) {
        return Double.parseDouble(readInput(message, s -> s.matches("^\\d+(\\.\\d{1,2})?$"), "Invalid Input"));
    }

    
    public static String readMenu(String message) {

        // Needs to accept number or else the return string won't be passed into switch in method handleOperation() of ProductController
        return readInput(message, s -> s.matches("^[a-zA-Z|0-9]+$"), "Invalid Input");
    }
}
