package io.team2.Controller;

import io.team2.Config.DbConn;
import io.team2.Model.Product;
import io.team2.Model.constant.ChangeType;
import io.team2.Model.constant.CommitStatus;
import io.team2.Service.ProductService;
import io.team2.Service.ProductServiceImpl;
import io.team2.Utils.Color;
import io.team2.Utils.InputValidation;
import io.team2.View.ProductView;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProductController {
    private final ProductService service;
    private final ProductView view;
    private final String MENU_DISPLAY = " ".repeat(40) + "_".repeat(10) + " Menu " + "_".repeat(10);
    private int currentPage = 1;

    public ProductController() throws SQLException {
        this.service = new ProductServiceImpl(DbConn.getConnection());
        this.view = new ProductView();
    }


    public void display() {
        showCurrentPage();
        displayMenu();
    }

    private void showCurrentPage() {
        int totalProducts = service.getProductSize();
        int totalPages = getTotalPages(totalProducts);

        List<Product> products = service.showProduct(currentPage, service.getRow());
        view.displayProducts(products, currentPage, totalPages, totalProducts);
    }

    public void displayMenu() {
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
            System.out.print(" ".repeat(5) + Color.ANSI_GREEN + key + "." + Color.ANSI_RESET + " " + value + " ".repeat(5));
        });

        System.out.println("\n");

        crudOperation.forEach((key, value) -> {
            if (key.equals("Sa")) {
                System.out.println();
                System.out.print(" ".repeat(5) + Color.ANSI_GREEN + key + ")" + Color.ANSI_RESET + " " + value + " ".repeat(5));
            } else {
                System.out.print(" ".repeat(5) + Color.ANSI_GREEN + key + ")" + Color.ANSI_RESET + " " + value + " ".repeat(5));
            }
        });
        System.out.println();
        System.out.println(" ".repeat(40) + "_".repeat(26));
        String input = InputValidation.readMenu(Color.ANSI_YELLOW + "=> Choose an option() : " + Color.ANSI_RESET);
        handleOperation(input);
    }

    private void handleOperation(String input) {
        switch (input.toUpperCase()) {
            case "N" -> handleNextPage();
            case "P" -> handlePreviousPage();
            case "F" -> handleFirstPage();
            case "L" -> handleLastPage();
            case "G" -> handleGoToPage();
            case "W" -> handleAddProduct() ;
//            case "R" -> ;
//            case "U" -> ;
//            case "D" -> ;
//            case "S" -> ;
            case "SE" -> handleSetRow();
            case "SA" -> handleSaveData();
            case "UN" -> handleUnSave();
//            case "Ba" -> ;
//            case "Re" -> ;
            case "E" -> handleExit();
            default -> {
                System.out.println(Color.ANSI_RED + "Invalid Option" + Color.ANSI_RESET);
                showCurrentPage();
                displayMenu();
            }
        }
    }

    private void handleNextPage() {
        int totalPages = getTotalPages(service.getProductSize());
        if (currentPage < totalPages) currentPage++;
        else System.out.println(Color.ANSI_RED + "You are on the last page" + Color.ANSI_RESET);
        showCurrentPage();
        displayMenu();
    }

    private void handlePreviousPage() {
        if (currentPage > 1) currentPage--;
        else System.out.println(Color.ANSI_RED + "You are on the first page" + Color.ANSI_RESET);
        showCurrentPage();
        displayMenu();
    }

    private void handleFirstPage() {
        currentPage = 1;
        showCurrentPage();
        displayMenu();
    }

    private void handleLastPage() {
        currentPage = getTotalPages(service.getProductSize());
        showCurrentPage();
        displayMenu();
    }

    private void handleGoToPage() {
        int totalPages = getTotalPages(service.getProductSize());
        int page = InputValidation.readNumber(Color.ANSI_YELLOW + "Enter page number: " + Color.ANSI_RESET);
        if (page >= 1 && page <= totalPages) currentPage = page;
        else System.out.println(Color.ANSI_RED + "Invalid page!" + Color.ANSI_RESET);
        showCurrentPage();
        displayMenu();
    }

    private void handleSetRow() {
        service.setRow(InputValidation.readNumber(Color.ANSI_YELLOW + "Set row to display per page: " + Color.ANSI_RESET));
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
    private void handleAddProduct(){
        service.addProduct(new Product());
        display();
    }

    private void handleSaveData() {
        ChangeType changeType = readChangeType();
        if (changeType == null) {
            showCurrentPage();
            displayMenu();
            return;
        }
        try {
            Map<CommitStatus, List<Product>> result = service.saveChange(changeType);

            if (result.values().stream().allMatch(List::isEmpty)) {
                System.out.println(Color.ANSI_YELLOW + "No pending changes to save." + Color.ANSI_RESET);
                showCurrentPage();
                displayMenu();
                return;
            }

            result.forEach((status, products) -> {
                if (!products.isEmpty()) {
                    String color = status == CommitStatus.CONFLICTED ? Color.ANSI_RED : Color.ANSI_GREEN;
                    System.out.println(color + status + ": " + Color.ANSI_RESET);
                    view.displayProducts(products, 1, 1, products.size());
                }
            });

            showCurrentPage();
            displayMenu();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ChangeType readChangeType() {
        System.out.println(Color.ANSI_GREEN.concat("'ui'").concat(Color.ANSI_RESET) + " for viewing insert products and" + Color.ANSI_GREEN.concat(" 'uu' ").concat(Color.ANSI_RESET) + "for viewing update products or " + Color.ANSI_RED.concat("'b'") + Color.ANSI_RESET + " for back to menu");
        String input = InputValidation.readMenu("Enter your option : ").toLowerCase();
        while (!input.equals("ui") && !input.equals("uu") && !input.equals("b")) {
            System.out.println(Color.ANSI_RED + "Invalid input! Please enter 'ui', 'uu', or 'b'." + Color.ANSI_RESET);
            input = InputValidation.readMenu("Enter save type: ").toLowerCase();
        }

        return switch (input) {
            case "ui" -> ChangeType.ADDED;
            case "uu" -> ChangeType.MODIFIED;
            default -> null;
        };
    }

    private void handleUnSave() {
        ChangeType changeType = readChangeType();
        if (changeType == null) {
            displayMenu();
            return;
        }
        List<Product> pendingProducts = service.getPendingChanges(changeType);
        view.displayProducts(pendingProducts, 1, 1, pendingProducts.size());
        displayMenu();
    }
}
