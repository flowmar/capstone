package com.inventory;
/**
 * Supplied class Part.java
 */

/**
 * @author Omar Imam
 */
public abstract class Part {

/*
 * Fields
 */

/**
 * The identification number of the part
 */
private int id;

/**
 * The name of the part
 */
private String name;

/**
 * The price of the part
 */
private double price;

/**
 * The quantity of the part on hand
 */
private int stock;

/**
 * The required minimum quantity of the part on hand
 */
private int min;

/**
 * The required maximum quantity of the part on hand
 */
private int max;

/**
 * Constructor
 *
 * @param id    identification number of the part
 * @param name  name of the part
 * @param price price of the part
 * @param stock quantity on hand of the part
 * @param min   the required minimum quantity of the part on hand
 * @param max   the maximum quantity of the part allowed on hand
 */
public Part( int id, String name, double price, int stock, int min, int max ) {
  this.id    = id;
  this.name  = name;
  this.price = price;
  this.stock = stock;
  this.min   = min;
  this.max   = max;
}

/**
 * @return the id
 */
public int getId( ) {
  return id;
}

/**
 * @param id the id to set
 */
public void setId( int id ) { this.id = id; }

/**
 * @return the name
 */
public String getName( ) {
  return name;
}

/**
 * @param name the name to set
 */
public void setName( String name ) {
  this.name = name;
}

    /**
     * @return the price
     */
    public double getPrice() {
        return price;
    }

    /**
     * @param price the price to set
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
     * @param stock the stock to set
     */
    public void setStock(int stock) {
        this.stock = stock;
    }

    /**
     * @return the min
     */
    public int getMin() {
        return min;
    }

    /**
     * @param min the min to set
     */
    public void setMin(int min ) {
      this.min = min;
    }

/**
 * @return the max
 */
public int getMax( ) {
  return max;
}

/**
 * @param max the max to set
 */
public void setMax( int max ) {
  this.max = max;
}

}