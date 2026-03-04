package io.team2.Service;


import io.team2.Model.Product;

import java.util.List;

public interface ProductService {
    List<Product> showProduct(int currentPage, int pageSize);
    void setRow(int input);
    int getRow();
    int getProductSize();
}
