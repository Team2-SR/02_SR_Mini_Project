package io.team2.Service;


import io.team2.Model.Product;
import io.team2.Model.constant.ChangeType;
import io.team2.Model.constant.CommitStatus;
import io.team2.Utils.Color;
import io.team2.Utils.InputValidation;

import java.io.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import java.util.*;

public class ProductServiceImpl implements ProductService {
    private final Connection con;
    private final Map<Product, ChangeType> stagedProduct = new LinkedHashMap<>();
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
    public List<Product> searchByName(String name)  {
        List<Product> foundProduct = new ArrayList<>();
        try (PreparedStatement stmt = con.prepareStatement("SELECT id,name,unit_price,quantity,imported_date FROM products WHERE name ILIKE CONCAT('%',?,'%') LIMIT 5")) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product()
                            .id(rs.getInt(1))
                            .name(rs.getString(2))
                            .unitPrice(rs.getDouble(3))
                            .quantity(rs.getInt(4))
                            .importedDate(rs.getDate(5).toLocalDate());
                    foundProduct.add(product);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return foundProduct;
    }


    // Add product by Chhun Panha
    @Override
    public void addProduct(Product product) {
        String sql = "SELECT count(id) FROM products";
        try(
            PreparedStatement ps = con.prepareStatement(sql);
        ) {
           ResultSet rs = ps.executeQuery();
           if(rs.next()) {
               int id = rs.getInt(1);

               System.out.println("ID " + (id + 1));
               String productName = InputValidation.readProductName(Color.ANSI_YELLOW + " => Enter Product Name: " + Color.ANSI_RESET);
               double unitPrice = InputValidation.readProductPrice(Color.ANSI_YELLOW + "=> Enter Product price: " + Color.ANSI_RESET);
               int qty = InputValidation.readNumber(Color.ANSI_YELLOW + "=> Enter Product qty: " + Color.ANSI_RESET);
               product.setName(productName);
               product.setUnitPrice(unitPrice);
               product.setQuantity(qty);
               stagedProduct.put(product, ChangeType.ADDED);

           }

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<Product> getPendingChanges(ChangeType changeType) {
        return stagedProduct.entrySet()
                .stream()
                .filter(e -> e.getValue() == changeType)
                .map(Map.Entry::getKey)
                .toList();
    }


    @Override
    public boolean checkIfNameExists(String name) throws SQLException {
        try (PreparedStatement stmt = con.prepareStatement("SELECT EXISTS(SELECT 1 FROM products WHERE name = ?)")) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean(1);
                }
            }
        }
        return false;
    }



    @Override
    public Map<CommitStatus, List<Product>> saveChange(ChangeType type) throws SQLException {

        Map<CommitStatus, List<Product>> result = new EnumMap<>(CommitStatus.class);
        result.put(CommitStatus.INSERTED, new ArrayList<>());
        result.put(CommitStatus.UPDATED, new ArrayList<>());
        result.put(CommitStatus.CONFLICTED, new ArrayList<>());

        String inComm = """
                INSERT INTO products(name, unit_price, quantity)
                VALUES (?, ?, ?)
                ON CONFLICT (name) DO NOTHING
                RETURNING name
                """;

        String upComm = """
                UPDATE products SET name = ?, unit_price = ?, quantity = ?, imported_date = ?
                WHERE id = ?
                """;

        try {
            con.setAutoCommit(false);
            switch (type) {
                case ADDED -> {
                    try (PreparedStatement inStmt = con.prepareStatement(inComm)) {

                        for (Map.Entry<Product, ChangeType> entry : stagedProduct.entrySet()) {
                            if (entry.getValue() != ChangeType.ADDED) continue;

                            Product prod = entry.getKey();

                            inStmt.setString(1, prod.getName());
                            inStmt.setDouble(2, prod.getUnitPrice());
                            inStmt.setInt(3, prod.getQuantity());
                            inStmt.executeQuery();

                            try (ResultSet rs = inStmt.getResultSet()) {
                                if (rs != null && rs.next()) {
                                    result.get(CommitStatus.INSERTED).add(prod);
                                } else {
                                    result.get(CommitStatus.CONFLICTED).add(prod);
                                }
                            }
                        }
                    }
                }
                case MODIFIED -> {
                    try (PreparedStatement upStmt = con.prepareStatement(upComm)) {
                        for (Map.Entry<Product, ChangeType> entry : stagedProduct.entrySet()) {
                            if (entry.getValue() != ChangeType.MODIFIED) continue;

                            Product prod = entry.getKey();
                            upStmt.setString(1, prod.getName());
                            upStmt.setDouble(2, prod.getUnitPrice());
                            upStmt.setInt(3, prod.getQuantity());
                            upStmt.setDate(4, Date.valueOf( prod.getImportedDate()));
                            upStmt.setInt(5, prod.getId());
                            int affected = upStmt.executeUpdate();

                            if (affected > 0) {
                                result.get(CommitStatus.UPDATED).add(prod);
                            } else {
                                result.get(CommitStatus.CONFLICTED).add(prod);
                            }
                        }
                    }
                }
            }

            con.commit();
            stagedProduct.entrySet()
                    .removeIf(e -> e.getValue() == type);

        } catch (SQLException e) {
            con.rollback();
            throw e;
        }
        return result;
    }
}
