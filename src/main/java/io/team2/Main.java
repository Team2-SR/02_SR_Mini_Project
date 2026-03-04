package io.team2;

import io.team2.Controller.ProductController;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws Exception {
        ProductController product = new ProductController();
        product.display();
    }
}