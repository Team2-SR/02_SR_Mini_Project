package io.team2.Service;


import io.team2.Model.Product;
import io.team2.Model.constant.ChangeType;
import io.team2.Model.constant.CommitStatus;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface ProductService {
    List<Product> showProduct(int currentPage, int pageSize);
    void setRow(int input);
    int getRow();
    int getProductSize();
    void addProduct(Product product);
    Map<CommitStatus, List<Product>> saveChange(ChangeType type) throws SQLException;
    boolean checkIfNameExists(String name) throws SQLException;
    List<Product> getPendingChanges(ChangeType changeType);
    List<Product> searchByName(String name);
}
