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
}
