package com.inventory;

/**
 * @author Omar Imam
 */

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.Random;

import static com.inventory.InventoryController.*;

public class Inventory extends Application {
    
    /**
     * Fields
     */
    private static final ObservableList<Part>    allParts      = FXCollections.observableArrayList( );
    private static final ObservableList<Product> allProducts   = FXCollections.observableArrayList( );
//    private final        Part[]                  parts         = { part1, part2 };

    
//    private final Product[] products = { product1, product2 };
    
    
    /**
     * @param newPart the new Part object that is to be added to the ObservableList<Part> object
     * @function addPart(Part newPart) Add a new part to the ObservableList<Part> object
     */
    public static void addPart( Part newPart ) {
        allParts.add( newPart );
    }
    
    /**
     * @param newProduct the new Part object that is to added to the ObservableList<Product> object
     * @function addProduct(Product newProduct) Add a new product to the ObservableList<Product> object
     */
    public static void addProduct( Product newProduct ) {
        allProducts.add( newProduct );
    }
    
    
    /**
     * @param partId the catalog number of the part
     * @return the part that matches the partId
     *
     * @function lookupPart(int partId) Searches for a part, identified by
     */
    public static Part lookupPart( int partId ) {
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
      public static Product lookupProduct( int productId ) {
        return allProducts.get( productId );
    }
    
    /**
     * @param partName the catalog number of the part
     * @return the part that matches the partName
     *
     * @function lookupPart(String partName) Searches for a part, identified by
     */
      public static Part lookupPart( String partName ) {
//            Part searchedPart = allParts.get(partId);
//            System.out.println("Searching...");
//            if (!foundPart) {
//                return foundPart;
//            }
//            System.out.println("Found matching part: foundPart");
//            return foundPart
        // TODO: Search for index of partName, then use the index to return the actual part
        SortedList<Part> sortedList = allParts.sorted( );
        return sortedList.get( 1 );
    }
    
    /**
     * @param productName the catalog number of the part
     * @return the part that matches the productName
     *
     * @function lookupPart(String productName) Searches for a part, identified by
     */
      public static Product lookupProduct( String productName ) {
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
     public static void updatePart( int index, Part newPart ) {
        // Find the part at the selected index and update the entry with newPart
    }
    
    /**
     * @param index      location of the  product to be updated
     * @param newProduct the entry that will be used to replace the old one
     * @function updateProduct(int index, Product newProduct) updates an entry at a specified index with a new
     *         entry
     */
    public static void updateProduct( int index, Product newProduct ) {
        // Find the  product at the selected index and update the entry with new product
        int indexThing = 1;
    }
    
    /**
     * @param selectedPart the part to be deleted
     * @return deleteCompleted a boolean value that confirms deletion
     *
     * @function deletePart(Part selectedPart) deletes a part from the inventory
     */
    public static boolean deletePart( Part selectedPart ) {
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
    public static boolean deletePart( Product selectedProduct ) {
        boolean deleteCompleted = false;
        // Find the part that is passed into the function, delete it and then change deleteCompleted to true
        return true;
    }
    
    /**
     * @return a SortedList<Part> object
     *
     * @function getAllParts() returns a sorted list of all parts in the inventory
     */
    public static ObservableList<Part> getAllParts( ) {
        ObservableList<Part> returnedInventory = allParts;
        return allParts.sorted( );
    }
    
    /**
     * @return a SortedList<Product> object
     *
     * @function getAllProducts() returns a sorted list of all products in the inventory
     */
    public static ObservableList<Product> getAllProducts( ) {
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
    
       Parent parent = FXMLLoader.load(getClass().getResource( "mainScene.fxml" ));
//       loader.load(parent);
//       Parent parent = FXMLLoader.load(getClass().getResource( "mainScene.fxml" ));
        // Build the scene graph
    Scene scene = new Scene( parent,1000, 1000);
    
    // Create the controller
    InventoryController controller = new InventoryController();
    
    
    //FXMLLoader loader2 = new FXMLLoader(getClass().getResource("mainScene.fxml"));
    
//    loader2.setController( controller );
    // Create variables for the nodes
    Node partsTableNode    = parent.lookup( "#partsTableView" );
    Node productsTableNode = parent.lookup( "#productsTableView" );
    
    // Create TableViews for parts and products
    TableView<Part> partsTableView = new TableView<>( );
    allParts.addAll(part1);
    partsTableView.setItems( allParts );
    
    TableView<Product> productTableView = new TableView<>( );
    allProducts.addAll( product1, product2 );
    productTableView.setItems( allProducts );
    controller.setData( allParts, partsTableView );
    controller.setProductData( allProducts, productTableView );
    
    
    // Create the TableColumns for the Parts and Products tables
//        InventoryController TableColumn<Part, Integer> = ;
    TableColumn<Part, Integer> b = partsIdCol;
    partsIdCol.setCellValueFactory( new PropertyValueFactory<Part, Integer>( ( "Id" ) ) );
    TableColumn<Part, String> c = partsNameCol;
    partsNameCol.setCellValueFactory( new PropertyValueFactory<Part, String>( ( "Name" ) ) );
    TableColumn<Part, Integer> a = partsStockCol;
    partsStockCol.setCellValueFactory( new PropertyValueFactory<Part, Integer>( ( "Stock" ) ) );
    TableColumn<Part, Double> d = partsPriceCol;
    partsPriceCol.setCellValueFactory( new PropertyValueFactory<Part, Double>( ( "Price" ) ) );
    

        
        // Display our window, using the scene graph.
        stage.setTitle( "Inventory System" );
        stage.setScene( scene );
        stage.show();
    }

 
           
           
    public static void main( String args[] ) {
        launch( args );
    }
}