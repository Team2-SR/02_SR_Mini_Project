package io.team2.Service;


import io.team2.Model.Product;
import io.team2.Utils.Color;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductServiceImpl implements ProductService {
    private final Connection con;
    private static final String ROW_FILE = "set_row.txt";
    private static int DEFAULT_ROW_SIZE = 10;
    private static final int MIN_ROW_SIZE = 1;
    private static final int MAX_ROW_SIZE = 100;
    public ProductServiceImpl(Connection connection) {
        this.con = connection;
        this.setRow(DEFAULT_ROW_SIZE);
    }

    @Override
    public void setRow(int inputRow) {
        if (inputRow < MIN_ROW_SIZE || inputRow > MAX_ROW_SIZE) {
            System.out.println(Color.ANSI_RED + "Invalid row size! Must be between " + MIN_ROW_SIZE + " and " + MAX_ROW_SIZE + Color.ANSI_RESET);
            return;
        }

        try (FileWriter writer = new FileWriter(ROW_FILE)) {
            writer.write(String.valueOf(inputRow));
            System.out.println(Color.ANSI_GREEN + "Row size saved successfully!" + Color.ANSI_RESET);
        } catch (IOException e) {
            System.err.println(Color.ANSI_RED + "Error saving row size: " + e.getMessage() + Color.ANSI_RESET);
        }
    }

    @Override
    public int getRow() {
        try (BufferedReader reader = new BufferedReader(new FileReader(ROW_FILE))) {
            String line = reader.readLine();
            if (line != null && !line.trim().isEmpty()) {
                int rowSize = Integer.parseInt(line.trim());
                if (rowSize >= MIN_ROW_SIZE && rowSize <= MAX_ROW_SIZE) {
                    return rowSize;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println(Color.ANSI_YELLOW + "Note: " + ROW_FILE + " not found, using default: " + DEFAULT_ROW_SIZE + Color.ANSI_RESET);
        } catch (IOException | NumberFormatException e) {
            System.err.println(Color.ANSI_YELLOW + "Warning: Could not read " + ROW_FILE + ", using default: " + DEFAULT_ROW_SIZE + Color.ANSI_RESET);
        }
        return DEFAULT_ROW_SIZE;
    }

    @Override
    public int getProductSize() {
        String sql = "SELECT count(*) FROM products";
        try {
            ResultSet rs = con.createStatement().executeQuery(sql);
            if(rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<Product> showProduct(int currentPage, int pageSize) {
        int offset = (currentPage - 1) * pageSize;
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products " +
                "ORDER BY id " +
                "OFFSET ? ROWS " +
                "FETCH NEXT ? ROWS ONLY";

        try(
                PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setInt(1, offset);
            ps.setInt(2, pageSize);
            ResultSet rs = ps.executeQuery();
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
    @Override
    public void deleteProduct(int id){
        String sql = "DELETE FROM products WHERE id =?";
        try(PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            if(ps.executeUpdate() == 0) {
                System.out.println(Color.ANSI_RED + "ID Not Found" + Color.ANSI_RESET);
            } else {
                System.out.println(Color.ANSI_GREEN + "Deleted Successfully" + Color.ANSI_RESET);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Product getProductByid(int id){
        String comm = "SELECT id, name,unit_price, quantity,imported_date FROM products WHERE products.id = ?";
        try (PreparedStatement stmt = con.prepareStatement(comm)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Product product = new Product().id(rs.getInt("id")).name(rs.getString("name")).unitPrice(rs.getDouble("unit_price")).quantity(rs.getInt("quantity")).importedDate(rs.getDate("imported_date").toLocalDate());
                return product;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
