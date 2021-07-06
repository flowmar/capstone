package com.inventory;

/**
 * @author Omar Imam
 */

public class InHouse extends Part {

/**
 * Fields
 */
private int machineId;

/**
 * Constructor
 *
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
 * @function setMachineId(String machineId) - sets the machineId parameter of an InHouse Object
 * @param machineId the name of the company that the outsourced product was purchased from
 */
public void setMachineId( int machineId ) {
  this.machineId = machineId;
}

/**
 * @function getMachineId() - retrieves the name of the machineId of the InHouse part
 * @return the desired value
 */
public int getMachineId( ) {
  return this.machineId;
}

}

