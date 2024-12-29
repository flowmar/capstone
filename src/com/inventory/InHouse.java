package com.inventory;

/**
 * @author Omar Imam
 */

public class InHouse extends com.inventory.Part {

/*
 * Fields
 */

/**
 * The id number of the machine used to create the part
 */
private int machineId;

/**
 * Constructor
 *
 * @param id        the identification number of the part
 * @param name      the name of the part
 * @param price     the cost of the item
 * @param stock     the quantity in stock
 * @param min       the minimum number on hand
 * @param max       the maximum number on hand
 * @param machineId the id number of the machine that was used to create the part
 */
public InHouse( int id, String name, double price, int stock, int min, int max, int machineId ) {
  // Calls the superclass constructor
  super( id, name, price, stock, min, max );
  this.machineId = machineId;
}

/**
 * Sets the <code>machineId</code> parameter of an InHouse Object
 *
 * @param machineId the id number of the machine used to create the part
 */
public void setMachineId( int machineId ) {
  this.machineId = machineId;
}

/**
 * Retrieves the name of the <code>machineId</code> of the InHouse {@link com.inventory.Part}
 * @return the <code>machineId</code> of the InHouse {@link com.inventory.Part}
 */
public int getMachineId( ) {
  return this.machineId;
}

}

