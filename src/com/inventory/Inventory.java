package com.inventory;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author Omar Imam
 * @version %I% %G%
 * @FUTURE_EHANCEMENT In the future the application can be expanded to have different departments able to login and
 *     access the parts and products that apply to them.
 * @FUTURE_ENHANCEMENT There can also be an ordering system integrated to order new parts when the stock gets close
 *     to the minimum.
 * @FUTURE_ENHANCEMENT Transactions when parts and products are bought and sold can also be integrated into their
 *     own database.
 * @FUTURE_ENHANCEMENT If they are exchanging parts for parts or products for products with another company, instead
 *     of buying and selling, that functionality can be added in as well.
 */

public class Inventory extends Application {

/**
 * Fields
 */
public static ObservableList<Part>    allParts    = FXCollections.observableArrayList( );
public static ObservableList<Product> allProducts = FXCollections.observableArrayList( );
public static int                     selectedPartId;
public static int                     selectedPartIndex;
public static Part                    selectedPart;
public static Product                 selectedProduct;
public static int                     selectedProductId;
public static int                     selectedProductIndex;


/**
 *
 * JAVADOCS ARE LOCATED AT OmarImamSoftware1PerformanceAssessment/Javadocs
 *
 */


/**
 * @function main launches the JavaFX application
 */
public static void main( String args[] ) {
    launch( args );
}

/**
 * @function addPart(Part newPart) Add a new part to the ObservableList<Part >object
 * @param newPart the new Part object that is to be added to the ObservableList<Part> object
 */
public void addPart( Part newPart ) {
    allParts.add( newPart );
}

/**
 * @function addProduct(Product newProduct) Add a new product to the ObservableList<Product> object
 * @param newProduct the new Part object that is to added to the ObservableList<Product> object
 */
public void addProduct( Product newProduct ) {
    allProducts.add( newProduct );
}


/**
 * @function lookupPart(int partId) Searches for a part, identified by the Id number of the part
 * @param partId the inventory identification number of the part
 * @return the part in the allParts Observable list that matches the partId
 */
public Part lookupPart( int partId ) {
    Part foundPart = null;
    // Iterate through the allParts ObservableList
    // If the partId matches, return the part
    for ( int i = 0; i < allParts.size( ); i++ ) {
        if ( allParts.get( i ).getId( ) == partId ) {
            foundPart = allParts.get( i );
        }
    }
    return foundPart;
}

/**
 * @function lookupProduct(int productId) Searches for a product identified by the id number of the product
 * @param productId the inventory identification number of the product
 * @return the product that matches the productId
 */
public Product lookupProduct( int productId ) {
    Product foundProduct = null;
    // Iterate through the allProducts ObservableList
    // If the productId matches, return the product
    for ( int j = 0; j < allProducts.size( ); j++ ) {
        if ( allProducts.get( j ).getId( ) == productId ) {
            foundProduct = allProducts.get( j );
        }
    }
    return foundProduct;
}

/**
 * @param partName the name of the part that is being searched for
 * @return the part that matches the partName
 *
 * @function lookupPart(String partName) Searches for a part, identified by by the Name of the part
 */
public ObservableList<Part> lookupPart( String partName ) {
    Part                 foundPart  = null;
    ObservableList<Part> foundParts = FXCollections.observableArrayList( );
    
    // Iterate through the allParts ObservableList
    // If the partName matches, return the part
    for ( int k = 0; k < allParts.size( ); k++ ) {
        if ( allParts.get( k ).getName( ).equals( partName ) ) {
            foundPart = allParts.get( k );
        }
    }
    
    if ( foundPart instanceof InHouse ) {
        foundParts.add( new InHouse( foundPart.getId( ), foundPart.getName( ), foundPart.getPrice( ),
            foundPart.getStock( ),
            foundPart.getMin( ), foundPart.getMax( ), ( ( InHouse ) foundPart ).getMachineId( ) ) );
    }
    else {
        foundParts.add( new Outsourced( foundPart.getId( ), foundPart.getName( ), foundPart.getPrice( ),
            foundPart.getStock( ),
            foundPart.getMin( ), foundPart.getMax( ), ( ( Outsourced ) foundPart ).getCompanyName( ) ) );
    }
    
    return foundParts;
}

/**
 * @param productName the name of the product that is being searched for
 * @return the product that matches the productName
 *
 * @function lookupProduct(String productName) Searches for a product, identified by by the Name of the product
 */
public ObservableList<Product> lookupProduct( String productName ) {
    Product                 foundProduct  = null;
    ObservableList<Product> foundProducts = FXCollections.observableArrayList( );
    
    // Iterate through the allParts ObservableList
    // If the partName matches, return the part
    for ( int l = 0; l < allProducts.size( ); l++ ) {
        if ( allProducts.get( l ).getName( ).equals( productName ) ) {
            foundProduct = allProducts.get( l );
        }
    }
    
    foundProducts.add( new Product( foundProduct.getId( ), foundProduct.getName( ), foundProduct.getPrice( ),
        foundProduct.getStock( ), foundProduct.getMin( ), foundProduct.getMax( ),
        foundProduct.getAllAssociatedParts( ) ) );
    
    return foundProducts;
}

/**
 * @function updatePart(int index, Part newPart) updates an entry at a specified index with a new entry
 * @param index location in the list of the part to be updated
 * @param newPart the entry that will be used to replace the old one
 */
public void updatePart( int index, Part newPart ) {
    // Find the part at the selected index and update the entry with newPart
    allParts.set(index, newPart );
}

/**
 * @function updateProduct(int index, Product newProduct) updates an entry at a specified index with a new entry
 * @param index location of the  product to be updated
 * @param newProduct the entry that will be used to replace the old one
 */
public void updateProduct( int index, Product newProduct ) {
    // Find the  product at the selected index and update the entry with new product
    allProducts.set(index, newProduct );
}

/**
 * @function deletePart(Part selectedPart) deletes the selected part from the inventory
 * @param selectedPart the part to be deleted
 * @return deleteCompleted a boolean value that confirms deletion
 */
public boolean deletePart( Part selectedPart ) {
    // Remove the selected part
    try {
        allParts.remove( selectedPart );
    }
    // Catch and print any errors
    catch ( Exception e ) {
        System.out.println( e.getMessage( ) );
    }
    return true;
}

/**
 * @param selectedProduct the part to be deleted
 * @return deleteCompleted a boolean value that confirms deletion
 *
 * @function deleteProduct(Product selectedProduct) deletes the selected part from the inventory
 */
public boolean deleteProduct( Product selectedProduct ) {
    // Remove the selected product
    try {
        allProducts.remove( selectedProduct );
    }
    // Catch and print any errors
    catch ( Exception e ) {
        System.out.println( e.getMessage( ) );
    }
    return true;
}

/**
 * @function getAllParts() returns a sorted list of all parts in the inventory
 * @return a SortedList<Part> object
 */
public ObservableList<Part> getAllParts( ) {
    return allParts.sorted( );
}

/**
 * @function getAllProducts() returns a sorted list of all products in the inventory
 * @return a SortedList<Product> object
 */
public ObservableList<Product> getAllProducts( ) {
    return allProducts.sorted( );
}

/**
 * @function start(Stage Stage) creates the loader and the scene and the stage
 * @param stage the window where the scene and the components of the scene are loaded and displayed
 * @throws Exception if the
 */

@Override
public void start( Stage stage ) throws Exception {
    Parent parent = null;
    // Load the FXML file
    FXMLLoader loader = new FXMLLoader( );
    
    // Create a new parent object and load the fxml document
    try {
        parent = FXMLLoader.load( getClass( ).getResource( "mainScene.fxml" ) );
    }
    catch ( IOException e ) {
        e.printStackTrace( );
    }
    
    // Build the scene graph
    Scene scene = new Scene( parent );
    
    // Create the controller
    InventoryController controller = new InventoryController( );
    loader.setController( controller );
    
    // Display our window, using the scene graph.
    stage.setTitle( "Inventory System" );
    stage.setScene( scene );
    stage.show( );
}


}