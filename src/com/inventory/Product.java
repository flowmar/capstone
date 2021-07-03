package com.inventory;


import javafx.collections.ObservableList;

/**
 * @author Omar Imam
 */

public class Product {

/*
 * Fields
     */
    private ObservableList<Part> associatedParts;
    private int id;
    private String name;
    private double price;
    private int stock;
    private int min;
    private int max;

    /**
     * Constructor
     *
     * @param id    the numerical identification number of the item
     * @param price the cost of the item
     * @param stock the quantity in stock
     * @param min   the minimum number on hand
     * @param max   the maximum number on hand
     */
    public Product( int id, String name, double price, int stock, int min, int max,
                    ObservableList<Part> associatedParts ) {
        this.id              = id;
        this.name            = name;
        this.price           = price;
        this.stock           = stock;
        this.min             = min;
        this.max             = max;
        this.associatedParts = associatedParts;
    }
    

/**
 * @return the id
 */
public int getId( ) {
    return id;
}

private void setId( int id ) {
    this.id = id;
}

/**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name of the set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the price
     */
    public double getPrice() {
        return price;
    }

    /**
     * @param price the price of the product
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * @return the stock
     */
    public int getStock() {
        return stock;
    }

    /**
     * @param stock the number of items on hand
     */
    public void setStock(int stock) {
        this.stock = stock;
    }

    /**
     * @return the min quantity
     */
    public int getMin() {
        return min;
    }

    /**
     * @param min the minimum quantity on hand
     */
    public void setMin(int min) {
        this.min = min;
    }

    /**
     * @return the max quantity
     */
    public int getMax() {
        return max;
    }

    /**
     * @param max the maximum quantity on hand
     */
    public void setMax(int max) {
        this.max = max;
    }

    /**
     * @function addAssociatedPart(Part part)
     */
    public void addAssociatedPart( Part part ) {
        this.associatedParts.add( part );
    }

/**
 * @function deleteAssociatedPart(Part selectedAssociatedPart ())
 * @param selectedAssociatedPart Deletes the part that is passed into the function.
 * @return true if the part is deleted, false if not
 */
public boolean deleteAssociatedPart( Part selectedAssociatedPart ) {
    // Delete Associated Part
    if ( associatedParts.contains( selectedAssociatedPart ) ) {
        this.associatedParts.remove( selectedAssociatedPart );
        return true;
    }
    else {
        return false;
    }
}

/**
 * @function getAllAssociatedParts()
 */
public ObservableList<Part> getAllAssociatedParts() {
        return associatedParts;
    }
}
