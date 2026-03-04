package io.team2.Service;


import io.team2.Config.DbConn;
import io.team2.Model.Product;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ProductServiceImpl implements ProductService {
    private final Connection con;
    public ProductServiceImpl(Connection connection) {
        this.con = connection;
    }

    public void setRow(int inputRow) {
        try(FileOutputStream output = new FileOutputStream("set_row.txt");) {
            output.write(inputRow);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getRow() {
        int rows = 0;
        try(FileReader row = new FileReader("set_row.txt")) {
            rows = row.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rows;
    }

    @Override
    public List<Product> showProduct() {
        int rows = getRow();
        int currentPage = 0;
        List<Product> products = new ArrayList<>();
        try(

                Connection con = DbConn.getConnection();
                PreparedStatement ps = con.prepareStatement("SELECT * " +
                        "FROM products" +
                        "ORDER BY ? OFFSET (page_number - 1) * page_sizes ROWS" +
                        "FETCH NEXT ? ROWS ONLY");

                ResultSet rs = ps.executeQuery()
        ) {
            while(rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("id"));
                product.setName(rs.getString("name"));
                product.setUnitPrice(rs.getDouble("unit_price"));
                product.setQuantity(rs.getInt("quantity"));
                product.importedDate(rs.getDate("imported_date").toLocalDate());
                products.add(product);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }
}
