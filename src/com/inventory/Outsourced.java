package com.inventory;

/**
 *
 * @author Omar Imam
 */

import com.inventory.*;

import java.util.Random;

public class Outsourced extends Part {

    /**
     * Fields
     */

    private String companyName;

    /**
     * Constructor
     * @param id the numerical identification number of the item
     * @param price the cost of the item
     * @param stock the quantity in stock
     * @param min the minimum number on hand
     * @param max the maximum number on hand
     * @param companyName the name of the company the product was outsourced from
     */

    public Outsourced(int id, String name, double price, double stock, int min, int max, String companyName)
    {
         super(id, name, price, stock, min, max, companyName);
         this.companyName = companyName;
 }
 

    /**
     * @function setCompanyName(String companyName) - sets the companyName parameter of an Outsourced Object
     * @param companyName the name of the company that the outsourced product was purchased from
     */
    public void setCompanyName(String companyName){
        this.companyName = companyName;
    }

    /**
     * @function getCompanyName() - retrieves the name of the companyName of the Outsourced part
     * @return the desired value
     */
    public String getCompanyName(){
        String retrievedName = this.companyName;
        return retrievedName;
    }
    

}
