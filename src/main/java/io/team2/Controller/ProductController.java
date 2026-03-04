package io.team2.Controller;

import io.github.cdimascio.dotenv.Dotenv;

import io.team2.Config.DbConn;
import io.team2.Model.Product;
import io.team2.Service.ProductService;
import io.team2.Service.ProductServiceImpl;
import io.team2.Utils.Color;
import io.team2.Utils.InputValidation;
import io.team2.Utils.TableTextFormatter;
import io.team2.View.ProductView;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import static io.team2.Config.DbConn.dotenv;
import static io.team2.Utils.Color.*;
//import static io.team2.Utils.InputValidation.scanner;

public class ProductController {
    private final ProductService service;
    private final ProductView view;

    Scanner scanner = new Scanner(System.in);

    private int currentPage = 1;

    private final String MENU_DISPLAY = " ".repeat(40) + "_".repeat(10) + " Menu " + "_".repeat(10);

    public ProductController() throws SQLException {
        this.service = new ProductServiceImpl(DbConn.getConnection());
        this.view = new ProductView();
    }

    public void display() throws Exception {
        showCurrentPage();
        displayMenu();
    }

    private void showCurrentPage() {
        int totalProducts = service.getProductSize();
        int totalPages = getTotalPages(totalProducts);

        List<Product> products = service.showProduct(currentPage, service.getRow());
        view.displayProducts(products, currentPage, totalPages, totalProducts);
    }

    public void displayMenu() throws Exception {
        Map<String, String> paginationMenu = new LinkedHashMap<>();
        paginationMenu.put("N", " Next Page");
        paginationMenu.put("P", " Previous Page");
        paginationMenu.put("F", " First Page");
        paginationMenu.put("L", " Last Page");
        paginationMenu.put("G", " Go to");

        Map<String, String> crudOperation = new LinkedHashMap<>();
        crudOperation.put("W", "Write");
        crudOperation.put("R", "Read (id)");
        crudOperation.put("U", "Update");
        crudOperation.put("D", "Delete");
        crudOperation.put("S", "Search (name)");
        crudOperation.put("Se", "Set rows");
        crudOperation.put("Sa", "Save");
        crudOperation.put("Un", "Unsaved");
        crudOperation.put("Ba", "Backup");
        crudOperation.put("Re", "Restore");
        crudOperation.put("E", "Exit");

        System.out.println(MENU_DISPLAY);
        paginationMenu.forEach((key, value) -> {
            System.out.print(" ".repeat(5) + ANSI_GREEN + key + "." + ANSI_RESET + " " + value + " ".repeat(5));
        });

        System.out.println("\n");

        crudOperation.forEach((key, value) -> {
            if(key.equals("Sa")) {
                System.out.println();
                System.out.print(" ".repeat(5) + ANSI_GREEN + key + ")" + ANSI_RESET + " " + value + " ".repeat(5));
            } else {
                System.out.print(" ".repeat(5) + ANSI_GREEN + key + ")" + ANSI_RESET + " " + value + " ".repeat(5));
            }
        });
        System.out.println();
        System.out.println(" ".repeat(40) + "_".repeat(26));
        String input = InputValidation.readMenu(Color.ANSI_YELLOW + "=> Choose an option() : " + ANSI_RESET);
        handleOperation(input);
    }

    private void handleOperation(String input) throws Exception {
        switch (input.toUpperCase()) {
            case "N" -> handleNextPage();
            case "P" -> handlePreviousPage();
            case "F" -> handleFirstPage();
            case "L" -> handleLastPage();
            case "G" -> handleGoToPage();
//            case "W" -> ;
//            case "R" -> ;
//            case "U" -> ;
//            case "D" -> ;
//            case "S" -> ;
            case "SE" -> handleSetRow();
//            case "Sa" -> ;
//            case "Un" -> ;
            case "BA" -> backupDatabase();
            case "RE" -> restoreMenu(DbConn.getConnection(), scanner);
            case "E" -> handleExit();
            default -> {
                System.out.println(Color.ANSI_RED + "Invalid Option" + ANSI_RESET);
                showCurrentPage();
                displayMenu();
            }
        }
    }

    private void handleNextPage() throws Exception {
        int totalPages = getTotalPages(service.getProductSize());
        if (currentPage < totalPages) currentPage++;
        else System.out.println(Color.ANSI_RED + "You are on the last page" + ANSI_RESET);
        showCurrentPage();
        displayMenu();
    }

    private void handlePreviousPage() throws Exception {
        if (currentPage > 1) currentPage--;
        else System.out.println(Color.ANSI_RED + "You are on the first page" + ANSI_RESET);
        showCurrentPage();
        displayMenu();
    }

    private void handleFirstPage() throws Exception {
        currentPage = 1;
        showCurrentPage();
        displayMenu();
    }

    private void handleLastPage() throws Exception {
        currentPage = getTotalPages(service.getProductSize());
        showCurrentPage();
        displayMenu();
    }

    private void handleGoToPage() throws Exception {
        int totalPages = getTotalPages(service.getProductSize());
        int page = InputValidation.readNumber(Color.ANSI_YELLOW + "Enter page number: " + ANSI_RESET);
        if (page >= 1 && page <= totalPages) currentPage = page;
        else System.out.println(Color.ANSI_RED + "Invalid page!" + ANSI_RESET);
        showCurrentPage();
        displayMenu();
    }

    private void handleSetRow() throws Exception {
        service.setRow(InputValidation.readNumber(Color.ANSI_YELLOW  + "Set row to display per page: " + ANSI_RESET));
        showCurrentPage();
        displayMenu();
    }

    private Integer handleExit() {
        return null;
    }

    private int getTotalPages(int totalProducts) {
        int rows = service.getRow();
        return (int) Math.ceil((double) totalProducts / rows);
    }

    // BACKUP
    private void backupDatabase() throws Exception {
        String backupFolderPath = dotenv.get("BACKUP_FOLDER");

        // Create folder
        File folder = new File(backupFolderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // Count existing .backup files
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".backup"));
        int version = (files == null) ? 1 : files.length + 1;

        // Build filename
        String fileName = "Version" + version
                + "-product-backup-"
                + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date())
                + ".backup";

        // full path
        String fullPath = backupFolderPath + File.separator + fileName;

        ProcessBuilder pb = new ProcessBuilder(
                "C:\\Program Files\\PostgreSQL\\15\\bin\\pg_dump.exe",
                "-h", "localhost",
                "-p", "5432",
                "-U", dotenv.get("DB_USER"),
                "-Fc",
                "-d", "java_mini_project",
                "-f", fullPath
        );

        pb.environment().put("PGPASSWORD", dotenv.get("DB_PASSWORD"));
        pb.redirectErrorStream(true);

        Process process = pb.start();

        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(process.getInputStream()))) {

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }

        int exitCode = process.waitFor();

        if (exitCode == 0) {
            System.out.println(ANSI_GREEN + "Database backup successful: " + fileName + ANSI_RESET);
        } else {
            System.out.println("❌ Backup failed");
        }

        System.out.println();
        display();
    }

    // RESTORE MENU
    private void restoreMenu(Connection connection, Scanner scanner) throws Exception {

        File folder = new File(dotenv.get("BACKUP_FOLDER"));
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".backup"));

        if (files == null || files.length == 0) {
            System.out.println("No backup files found.");
            return;
        }


        System.out.println("+---------------------------------------------------------+");
        System.out.println("|                   List of Backup Data                   |");
        System.out.println("+----+----------------------------------------------------+");

        for (int i = 0; i < files.length; i++) {
            System.out.printf("| %-2d | %-40s |\n", (i + 1), files[i].getName());
        }

        System.out.println("+----+----------------------------------------------------+");

        int choice = -1;
        while (true) {
            System.out.print("=> Enter backup_id to restore: ");
            String input = scanner.nextLine();

            if (input.matches("\\d+")) {
                choice = Integer.parseInt(input);
                break;
            } else {
                System.out.println(ANSI_RED + "Invalid input! Please enter numbers only." + ANSI_RESET);
            }
        }

        if (choice < 1 || choice > files.length) {
            System.out.println(ANSI_YELLOW + "Invalid selection" + ANSI_RESET);
            System.out.println();
            display();
            return;
        }

        restoreFromFile(files[choice - 1].getPath());

        System.out.println();
        display();
    }

    private void restoreFromFile(String filePath) throws Exception {

        ProcessBuilder pb = new ProcessBuilder(
                "C:\\Program Files\\PostgreSQL\\15\\bin\\pg_restore.exe",
                "-h", "localhost",
                "-p", "5432",
                "-U", dotenv.get("DB_USER"),
                "-d", "java_mini_project",
                "--clean",
                "--if-exists",
                filePath
        );

        pb.environment().put("PGPASSWORD", dotenv.get("DB_PASSWORD"));
        pb.redirectErrorStream(true);

        Process process = pb.start();

        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }

        int exitCode = process.waitFor();
        System.out.println(exitCode == 0 ? ANSI_GREEN + "Database restore successful" + ANSI_RESET : ANSI_RED + "Restore failed" + ANSI_RESET);

        System.out.println();
        display();

    }

}
