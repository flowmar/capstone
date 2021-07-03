package com.inventory;

/**
 *
 * @author Omar Imam
 */

public class Outsourced extends Part {

    /**
     * Fields
     */

    private String companyName;

    /**
     * Constructor
     *
     * @param price       the cost of the item
     * @param stock       the quantity in stock
     * @param min         the minimum number on hand
     * @param max         the maximum number on hand
     * @param companyName the name of the company the product was outsourced from
     */

    public Outsourced( int id, String name, double price, int stock, int min, int max, String companyName ) {
      super( id, name, price, stock, min, max );
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
      return this.companyName;
    }
    

}
