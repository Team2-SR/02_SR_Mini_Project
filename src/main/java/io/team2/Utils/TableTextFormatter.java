package io.team2.Utils;

import io.team2.Model.Product;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import java.util.*;

public class TableTextFormatter {
    public static void createTable(
            int columns,
            List<String> cells,
            int[] minWidth,
            int[] maxWidth,
            List<Product> products,
            int pageSize
            ) {
        Scanner scanner = new Scanner(System.in);
        int currentPage = 1;
        int totalProducts = products.size();
        int totalPages = (int) Math.ceil(totalProducts / (double) pageSize);
        while(true) {
            Table table = new Table(columns, BorderStyle.UNICODE_BOX, ShownBorders.ALL);
            CellStyle centerStyle = new CellStyle(CellStyle.HorizontalAlign.CENTER);

            for (int i = 0; i < columns; i++) {
                table.setColumnWidth(i, minWidth[i], maxWidth[i]);
            }

            for (String cell : cells) {
                table.addCell(Color.ANSI_CYAN + cell + Color.ANSI_RESET, centerStyle);
            }

            // Page
            int start = (currentPage - 1) * pageSize;
            int end = Math.min(start + pageSize, totalProducts);

            for (int i = start; i < end; i++) {
                Product product = products.get(i);
                table.addCell(Color.ANSI_GREEN + product.getId() + Color.ANSI_RESET, centerStyle);
                table.addCell(product.getName(), centerStyle);
                table.addCell(String.valueOf(product.getUnitPrice()), centerStyle);
                table.addCell(String.valueOf(product.getQuantity()), centerStyle);
                table.addCell(String.valueOf(product.getImportedDate()), centerStyle);
            }

            table.addCell("Page : " + Color.ANSI_YELLOW + currentPage + Color.ANSI_RESET + " of " + Color.ANSI_RED + totalPages + Color.ANSI_RESET, centerStyle, 2);
            table.addCell("Total Record : " + Color.ANSI_GREEN + totalProducts + Color.ANSI_RESET, centerStyle, 3);
            System.out.println(table.render());

            // Menu
            Map<String, String> map = new LinkedHashMap<>();
            map.put("N", "Next Page");
            map.put("P", "Previous Page");
            map.put("F", "First Page");
            map.put("L", "Last Page");
            map.put("G", "Goto");
            System.out.println(" ".repeat(40) + "_".repeat(10) + " Menu " + "_".repeat(10));
            map.forEach((key, value) -> {
                System.out.print(" ".repeat(5) + Color.ANSI_GREEN + key + Color.ANSI_RESET +  ". " + value + " ".repeat(5));
            });

            System.out.println();
            String input = InputValidation.readLetter(Color.ANSI_YELLOW + "Choose an option() : " + Color.ANSI_RESET);
        }
    }
}
