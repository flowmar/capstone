package com.inventory;
/**
 * Supplied class Part.java
 */

import java.util.Random;

/**
 *
 * @author Omar Imam
 */
public abstract class Part {
protected        int    id;
protected static String name;
protected        double price;
protected        int    stock;
protected        int    min;
protected        int    max;

public Part( String name, double price, int stock, int min, int max ) {
    int randomId = getRandomNumber( );
    System.out.println( "RANDOMID: " + randomId );
    id         = randomId;
    this.id    = randomId;
    this.name  = name;
    this.price = price;
    this.stock = stock;
    this.min   = min;
    this.max   = max;
}

/**
 * @return the id
 */
    public int getId() {
        return id;
    }


/**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
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
    public void setMin(int min) {
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

protected int getRandomNumber( ) {
    Random randomNumbers = new Random( );
    int    randomNumber  = Math.abs( randomNumbers.nextInt( 1000 ) );
    return randomNumber;
}
    
}