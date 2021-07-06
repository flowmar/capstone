package com.inventory;

/**
 * @author Omar Imam
 */

public class Outsourced extends Part {

    /*
     * Fields
     */

/**
 * The name of the company that the {@link com.inventory.Part} was outsourced from
 */
private String companyName;

    /**
     * Constructor
     *
     * @param id          the id number of the item
     * @param name        the name of the part
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
 * Sets the <code>companyName</code> parameter of an Outsourced Object
 *
 * @param companyName the name of the company that the outsourced product was purchased from
 */
    public void setCompanyName(String companyName){
        this.companyName = companyName;
    }

    /**
     * Retrieves the name of the <code>companyName</code> of the Outsourced {@link com.inventory.Part}
     * @return the <code>companyName</code> of the Outsourced {@link com.inventory.Part}
     */
    public String getCompanyName(){
      return this.companyName;
    }
    

}
