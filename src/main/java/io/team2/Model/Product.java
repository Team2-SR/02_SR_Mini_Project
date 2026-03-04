package io.team2.Model;

import java.sql.Date;
import java.time.LocalDate;

public class Product {
    private int id;
    private String name;
    private double unitPrice;
    private int quantity;
    private LocalDate importedDate;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDate getImportedDate() {
        return importedDate;
    }


    public Product id(int id) {
        this.id = id;
        return this;
    }

    public Product name(String name) {
        this.name = name;
        return this;
    }

    public Product unitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        return this;
    }

    public Product quantity(int quality) {
        this.quantity = quality;
        return this;
    }

    public Product importedDate(LocalDate importedDate) {
        this.importedDate = importedDate;
        return this;
    }
}
