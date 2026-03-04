package io.team2;

import io.team2.Config.DbConn;
import io.team2.Service.ProductService;
import io.team2.Service.ProductServiceImpl;
import io.team2.View.ProductView;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        ProductView productView = new ProductView();
        productView.displayProducts(
                new ProductServiceImpl(DbConn.getConnection())
        );
    }
}