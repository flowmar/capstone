package com.inventory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Omar Imam
 */

public class Product {

    /*
     * Fields
     */

    /**
     * an ObservableList object containing each {@link com.inventory.Part} that is associated with the product
     */
    private ObservableList<Part> associatedParts;
    /**
     * the identification number of the product
     */
    private int                  id;
    /**
     * that name of the product
     */
    private String               name;
    /**
     * the price of the product
     */
    private double               price;
    /**
     * the quantity of the product available on hand
     */
    private int                  stock;
    /**
     * the minimum quantity of the product required to be on hand
     */
    private int                  min;
    /**
     * the maximum quantity of the product required to be on hand
     */
    private int                  max;

    /**
     * Constructor
     *
     * @param id              the numerical identification number of the item
     * @param name            the name of the product
     * @param price           the cost of the item
     * @param stock           the quantity in stock
     * @param min             the minimum number on hand
     * @param max             the maximum number on hand
     * @param associatedParts the parts associated with the product
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
     * Returns the <code>id</code> of the product
     *
     * @return the <code>id</code>
     */
    public int getId( ) {
        return id;
    }

    /**
     * Sets the <code>id</code> of the product
     *
     * @param id the identification number of the product
     */
    private void setId( int id ) {
        this.id = id;
    }

    /**
     * Returns the <code>name</code> of the product
     *
     * @return the <code>name</code>
     */
    public String getName( ) {
        return name;
    }

    /**
     * Sets the <code>name</code> of the product
     *
     * @param name the name of the product
     */
    public void setName( String name ) {
        this.name = name;
    }

    /**
     * Returns the <code>price</code> of the product
     *
     * @return the <code>price</code>
     */
    public double getPrice( ) {
        return price;
    }

    /**
     * Sets the <code>price</code> of the product
     *
     * @param price the price of the product
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Returns the <code>stock</code> of the product
     *
     * @return the <code>stock</code>
     */
    public int getStock() {
        return stock;
    }

    /**
     * Sets the <code>stock</code> of the product
     *
     * @param stock the number of items on hand
     */
    public void setStock(int stock) {
        this.stock = stock;
    }

    /**
     * Returns the <code>min</code> of the product
     *
     * @return the <code>min</code> quantity
     */
    public int getMin() {
        return min;
    }

    /**
     * Sets the <code>min</code> of the product
     *
     * @param min the minimum quantity on hand
     */
    public void setMin(int min) {
        this.min = min;
    }

    /**
     * Returns the <code>max</code> of the product
     *
     * @return the max quantity
     */
    public int getMax() {
        return max;
    }

    /**
     * Sets the <code>max</code> of the product
     *
     * @param max the maximum quantity on hand
     */
    public void setMax(int max) {
        this.max = max;
    }

    /**
     * adds an associated {@link com.inventory.Part} to the product
     * @param part the {@link com.inventory.Part} to be associated with the product
     */
    public void addAssociatedPart( Part part ) {
        this.associatedParts.add( part );
    }

    /**
     * Deletes the selected associated part
     * @param selectedAssociatedPart the selected {@link com.inventory.Part} which will have its association removed
     * @return true if the {@link com.inventory.Part} is deleted, false if not
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
     * Retrieves a list of the associated parts
     * @return an <code>ObservableList</code> of the associated parts
     */
    public ObservableList<Part> getAllAssociatedParts() {
        return associatedParts;
    }
}
