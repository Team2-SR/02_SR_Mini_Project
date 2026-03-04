package io.team2.Utils;

import io.team2.Model.Product;
import io.team2.Service.ProductService;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import java.util.*;

public class TableTextFormatter {

    private static final String MENU_SEPARATOR = " ".repeat(40) + "_".repeat(10) + " Menu " + "_".repeat(10);
    private static final String MENU_SPACING = " ".repeat(5);
    private static final String UPDATE_ROW = " ".repeat(40) + "_".repeat(10) + " Set Rows to Display " + "_".repeat(10);

    public static void createTable(
            int columns,
            List<String> cells,
            int[] minWidth,
            int[] maxWidth,
            List<Product> products,
            int currentPage,
            int totalPages,
            int totalRecords
    ) {
        Table table = new Table(columns, BorderStyle.UNICODE_BOX, ShownBorders.ALL);
        CellStyle centerStyle = new CellStyle(CellStyle.HorizontalAlign.CENTER);

        for (int i = 0; i < columns; i++) {
            table.setColumnWidth(i, minWidth[i], maxWidth[i]);
        }

        for (String cell : cells) {
            table.addCell(Color.ANSI_CYAN + cell + Color.ANSI_RESET, centerStyle);
        }

        for (Product product : products) {
            table.addCell(Color.ANSI_GREEN + product.getId() + Color.ANSI_RESET, centerStyle);
            table.addCell(product.getName(), centerStyle);
            table.addCell(String.valueOf(product.getUnitPrice()), centerStyle);
            table.addCell(String.valueOf(product.getQuantity()), centerStyle);
            table.addCell(String.valueOf(product.getImportedDate()), centerStyle);
        }

        table.addCell(buildPageInfo(currentPage, totalPages), centerStyle, 2);
        table.addCell("Total Record : " + Color.ANSI_GREEN + totalRecords + Color.ANSI_RESET, centerStyle, 3);
        System.out.println(table.render());
    }

    private static String buildPageInfo(int currentPage, int totalPages) {
        return "Page : " + Color.ANSI_YELLOW + currentPage + Color.ANSI_RESET +
                " of " + Color.ANSI_RED + totalPages + Color.ANSI_RESET;
    }
}

