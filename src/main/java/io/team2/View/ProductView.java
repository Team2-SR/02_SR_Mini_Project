package io.team2.View;

import io.team2.Model.Product;
import io.team2.Service.ProductService;
import io.team2.Utils.TableTextFormatter;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProductView {
    public void displayProducts(ProductService service) {
        String[] columns = {"ID", "Name", "Unit Price", "Qty", "Import Date"};
        List<Product> products = service.showProduct();
        int[] minWidths = {15, 30, 20, 15, 25};
        int[] maxWidths = {15, 30, 20, 15, 25};

        List<String> attributes = new ArrayList<>(Arrays.asList(columns));
        TableTextFormatter.createTable(attributes.size(), attributes, minWidths, maxWidths, products, service.getRow());

    }
}
