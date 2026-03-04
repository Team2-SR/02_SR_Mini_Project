package io.team2.Service;


import io.team2.Model.Product;
import io.team2.Model.constant.ChangeType;
import io.team2.Model.constant.CommitStatus;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface ProductService {
    List<Product> showProduct(int currentPage, int pageSize);

    int getRow();

    void setRow(int input);

    int getProductSize();

    List<Product> getPendingChanges(ChangeType changeType);

    boolean checkIfNameExists(String name) throws SQLException;
    Map<CommitStatus, List<Product>> saveChange(ChangeType type) throws SQLException;
}
