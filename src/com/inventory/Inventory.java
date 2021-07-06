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
 */

public class Inventory extends Application {

/*
 * Fields
 */
/**
 * A list of every {@link com.inventory.Part} in the {@link com.inventory.Inventory}
 */
public static ObservableList<Part>    allParts    = FXCollections.observableArrayList( );
/**
 * A list of every {@link com.inventory.Product} in the {@link com.inventory.Inventory}
 */
public static ObservableList<Product> allProducts = FXCollections.observableArrayList( );
/**
 * Holds a {@link com.inventory.Part} anytime one is selected within the UI
 */
public static Part                    selectedPart;

/**
 * The index of the selected {@link com.inventory.Part} within its <code>ObservableList</code>
 */
public static int     selectedPartIndex;
/**
 * The <code>id</code> number of the selected {@link com.inventory.Part}
 */
public static int     selectedPartId;
/**
 * Holds a {@link com.inventory.Product} anytime one is selected within the UI
 */
public static Product selectedProduct;
/**
 * The index of the selected {@link com.inventory.Product} within its <code>ObservableList</code>
 */
public static int     selectedProductIndex;
/**
 * The <code>id</code> number of the selected {@link com.inventory.Product}
 */
public static int     selectedProductId;


/**
 * Launches the JavaFX application. <br><br> Javadocs are located at: OmarImamSoftware1PerformanceAssessment/Javadocs
 * <br><br> FUTURE EHANCEMENT In the future the application can be expanded to have different departments able to login
 * and access the parts and products that apply to them. <br><br> FUTURE ENHANCEMENT There can also be an ordering
 * system integrated to order new parts when the stock gets close to the minimum. <br><br> FUTURE ENHANCEMENT
 * Transactions when parts and products are bought and sold can also be integrated into their own database. <br><br>
 * FUTURE ENHANCEMENT If they are exchanging parts for parts or products for products with another company, instead of
 * buying and selling, that functionality can be added in as well.
 */
public static void main( String args[] ) {
    launch( args );
}

/**
 * Adds a new part to the {@link #allParts ObservableList&lt;Part&gt;allParts} object
 *
 * @param newPart the new {@link com.inventory.Part} object that is to be added to the <code>ObservableList&lt;Part&gt;
 *                </code>
 *                object
 */
public void addPart( Part newPart ) {
    allParts.add( newPart );
}

/**
 * Adds a new product to the {@link #allProducts ObservableList&lt;Product&gt;allProducts} object
 *
 * @param newProduct the new {@link com.inventory.Product} object that is to added to the <code>ObservableList&lt;
 *                   Product&gt;
 *                   </code> object
 */
public void addProduct( Product newProduct ) {
    allProducts.add( newProduct );
}


/**
 * Searches for a {@link com.inventory.Part}, identified by the <code>id</code> number of the {@link com.inventory.Part}
 *
 * @param partId the inventory identification number of the {@link com.inventory.Part}
 * @return the {@link com.inventory.Part} in the {@link #allParts} Observable list that matches the partId
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
 * Searches for a {@link com.inventory.Product}, identified by the <code>id</code> number of the
 * {@link com.inventory.Product}
 *
 * @param productId the inventory identification number of the {@link com.inventory.Product}
 * @return the {@link com.inventory.Product} that matches the <code>productId</code>
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
 * Searches for a {@link com.inventory.Part}, identified by by the <code>name</code> of the {@link com.inventory.Part}
 *
 * @param partName the name of the {@link com.inventory.Part} that is being searched for
 * @return the {@link com.inventory.Part} that matches the <code>partName</code>
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
 * Searches for a {@link com.inventory.Product}, identified by by the <code>name</code> of the
 * {@link com.inventory.Product}
 *
 * @param productName the name of the {@link com.inventory.Product} that is being searched for
 * @return the {@link com.inventory.Product} that matches the <code>productName</code>
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
 * Updates a {@link com.inventory.Part} at a specified index with a new {@link com.inventory.Part}
 *
 * @param index location in the list of the {@link com.inventory.Part} to be updated
 * @param newPart the {@link com.inventory.Part} that will be used to replace the old one
 */
public void updatePart( int index, Part newPart ) {
    // Find the part at the selected index and update the entry with newPart
    allParts.set(index, newPart );
}

/**
 * Updates a {@link com.inventory.Product} at a specified index with a {@link com.inventory.Product}
 *
 * @param index location of the  {@link com.inventory.Product} to be updated
 * @param newProduct the {@link com.inventory.Product} that will be used to replace the old one
 */
public void updateProduct( int index, Product newProduct ) {
    // Find the  product at the selected index and update the entry with new product
    allProducts.set(index, newProduct );
}

/**
 * Deletes the selected {@link com.inventory.Part} from the {@link com.inventory.Inventory}
 *
 * @param selectedPart the {@link com.inventory.Part} to be deleted
 * @return <code>deleteCompleted</code> a boolean value that confirms deletion
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
 * Deletes the selected {@link com.inventory.Product} from the {@link com.inventory.Inventory}
 *
 * @param selectedProduct the {@link com.inventory.Product} to be deleted
 * @return <code>deleteCompleted</code> a boolean value that confirms deletion
 *
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
 * Returns a sorted list of every {@link com.inventory.Part} in the {@link com.inventory.Inventory}
 *
 * @return a <code>SortedList&lt;Part&gt;</code> object
 */
public ObservableList<Part> getAllParts( ) {
    return allParts.sorted( );
}

/**
 * Returns a sorted list of all {@link com.inventory.Product} in the {@link com.inventory.Inventory}
 *
 * @return a <code>SortedList&lt;Product&gt;</code> object
 */
public ObservableList<Product> getAllProducts( ) {
    return allProducts.sorted( );
}

/**
 * Creates the loader, the {@link javafx.scene.Scene} and the {@link javafx.stage.Stage}
 *
 * @param stage the window where the scene and the components of the scene are loaded and displayed
 * @throws Exception if the fxml file is not found, an exception is thrown
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