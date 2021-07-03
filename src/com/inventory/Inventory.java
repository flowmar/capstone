package com.inventory;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Inventory extends Application {

/**
 * Fields
 */
public static ObservableList<Part>    allParts;
public static ObservableList<Product> allProducts;
public static int                     selectedPartId;
public static int                     selectedPartIndex;


/**
 * @param newPart the new Part object that is to be added to the ObservableList<Part> object
 * @function addPart(Part newPart) Add a new part to the ObservableList<Part >object
 */
public void addPart( Part newPart ) {
    allParts.add( newPart );
}

/**
 * @param newProduct the new Part object that is to added to the ObservableList<Product> object
 * @function addProduct(Product newProduct) Add a new product to the ObservableList<Product> object
 */
public void addProduct( Product newProduct ) {
    allProducts.add( newProduct );
}
    
    
    /**
     * @param partId the catalog number of the part
     * @return the part that matches the partId
     *
     * @function lookupPart(int partId) Searches for a part, identified by
     */
    public Part lookupPart( int partId ) {
//            Part searchedPart = allParts.get(partId);
//            System.out.println("Searching...");
//
//            if (!foundPart) {
//
//                return foundPart;
//            }
//            System.out.println("Found matching part: foundPart");
//            return foundPart
        // TODO: Search for index of partId, then use the index to return the actual part
        return allParts.get( partId );
    }
    
    /**
     * @param productId the catalog number of the product
     * @return the product that matches the productId
     *
     * @function lookupProduct(int productId) Searches for a product identified by
     */
    public Product lookupProduct( int productId ) {
        return allProducts.get( productId );
    }

/**
 * @param partName the catalog number of the part
 * @return the part that matches the partName
 *
 * @function lookupPart(String partName) Searches for a part, identified by
 */
//      public static Part lookupPart( String partName ) {
//            Part searchedPart = allParts.get(partId);
////            System.out.println("Searching...");
////            if (!foundPart) {
////                return foundPart;
////            }
////            System.out.println("Found matching part: foundPart");
////            return foundPart
//        // TODO: Search for index of partName, then use the index to return the actual part
//        SortedList<Part> sortedList = allParts.sorted( );
////        return sortedList;
////          return sortedList.get(0);
//          return searchedPart;
//    }

/**
 * @param productName the catalog number of the part
 * @return the part that matches the productName
 *
 * @function lookupPart(String productName) Searches for a part, identified by
 */
public Product lookupProduct( String productName ) {
//            Part searchedPart = allParts.get(partId);
//            System.out.println("Searching...");
//
//            if (!foundPart) {
//
//                return foundPart;
//            }
//            System.out.println("Found matching part: foundPart");
//            return foundPart
    // TODO: Search for index of productName, then use the index to return the actual part
    SortedList<Product> sortedList = allProducts.sorted( );
        return sortedList.get( 1 );
    }
    
    /**
     * @param index   location of the part to be updated
     * @param newPart the entry that will be used to replace the old one
     * @function updatePart(int index, Part newPart) updates an entry at a specified index with a new entry
     */
    public void updatePart( int index, Part newPart ) {
        // Find the part at the selected index and update the entry with newPart
    }

/**
 * @param index      location of the  product to be updated
 * @param newProduct the entry that will be used to replace the old one
 * @function updateProduct(int index, Product newProduct) updates an entry at a specified index with a new
 *         entry
 */
public void updateProduct( int index, Product newProduct ) {
    // Find the  product at the selected index and update the entry with new product
    int indexThing = 1;
}

/**
 * @param selectedPart the part to be deleted
 * @return deleteCompleted a boolean value that confirms deletion
 *
 * @function deletePart(Part selectedPart) deletes a part from the inventory
 */
public boolean deletePart( Part selectedPart ) {
    boolean deleteCompleted = false;
    // Find the part that is passed into the function, delete it and then change deleteCompleted to true
    return true;
}

/**
 * @param selectedProduct the part to be deleted
 * @return deleteCompleted a boolean value that confirms deletion
 *
 * @function deleteProduct(Product selectedProduct) deletes a part from the inventory
 */
public boolean deletePart( Product selectedProduct ) {
    boolean deleteCompleted = false;
    // Find the part that is passed into the function, delete it and then change deleteCompleted to true
    return true;
}

/**
 * @return a SortedList<Part> object
 *
 * @function getAllParts() returns a sorted list of all parts in the inventory
 */
public ObservableList<Part> getAllParts( ) {
    ObservableList<Part> returnedInventory = allParts;
    return allParts.sorted( );
}

/**
 * @return a SortedList<Product> object
 *
 * @function getAllProducts() returns a sorted list of all products in the inventory
 */
public ObservableList<Product> getAllProducts( ) {
    ObservableList<Product> returnedInventory = allProducts;
    return allProducts.sorted( );
}

/**
 * @function start(Stage Stage) creates the loader and the scene and
 * @param stage
 * @throws Exception
 */

    @Override
    public void start( Stage stage ) throws Exception {
    
    // Load the FXML file
        FXMLLoader loader = new FXMLLoader( );
//
        Parent parent = FXMLLoader.load( getClass( ).getResource( "mainScene.fxml" ) );
//        // Build the scene graph
        Scene scene = new Scene( parent );
//
//    // Create the controller
        InventoryController controller = new InventoryController( );
        loader.setController( controller );
    
        // Create variables for the nodes
        Node partsTableNode    = parent.lookup( "#partsTableView" );
        Node productsTableNode = parent.lookup( "#productsTableView" );
    
    
        // Display our window, using the scene graph.
        stage.setTitle( "Inventory System" );
        stage.setScene( scene );
        stage.show( );
    }


public static void main( String args[] ) {
        launch( args );
    }
}