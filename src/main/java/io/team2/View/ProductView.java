package io.team2.View;

import io.team2.Model.Product;
import io.team2.Utils.TableTextFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProductView {
    public void displayProducts(List<Product> products, int currentPage, int totalPages, int totalRecords) {
        String[] columns = {"ID", "Name", "Unit Price", "Qty", "Import Date"};
        int[] minWidths = {15, 30, 20, 15, 25};
        int[] maxWidths = {15, 30, 20, 15, 25};

        List<String> headers = new ArrayList<>(Arrays.asList(columns));
        TableTextFormatter.createTable(
                headers.size(),
                headers,
                minWidths,
                maxWidths,
                products,
                currentPage,
                totalPages,
                totalRecords);
    }

    public void displayProductById(Product product) {
        String[] columns = {"ID", "Name", "Unit Price", "Qty", "Import Date"};
        int[] minWidths = {15, 30, 20, 15, 25};
        int[] maxWidths = {15, 30, 20, 15, 25};

        List<String> headers = new ArrayList<>(Arrays.asList(columns));
        TableTextFormatter.createTableProductById(
                headers.size(),
                product,
                headers,
                minWidths,
                maxWidths);
    }
}
