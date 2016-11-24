/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beeceebakery.Models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author Anne Gitau
 */
public class Database {
    private static final Database databaseInstance = new Database();
    
    public static Database getDatabaseInstance() {
        return databaseInstance;
    }
    
    Connection conn = null;
    public void connectToDatabse(){        
        final String USER = "root";
        final String PASSWORD = "";
        
        String dbURL = "jdbc:mysql://localhost/";
        Statement statement;
        
        try{
            // Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            // Open a connection
            conn = DriverManager.getConnection(dbURL, USER, PASSWORD);
            
            statement = conn.createStatement();
            
            // Creating a database
            String createBakeryDatabase = "CREATE DATABASE IF NOT EXISTS "
                    + "BeeCeeBakery";
            statement.executeUpdate(createBakeryDatabase);
            
            // Make the program use BeeCeeBakery database
            String useDatabaseStatement = "USE BeeCeeBakery";
            statement.executeUpdate(useDatabaseStatement);
            
            // Creating Bakery Table
            String createTableSqlStatement = "CREATE TABLE IF NOT EXISTS "
                    + "Bakery("
                    + "ID VARCHAR INT NOT NULL, "
                    + "cake VARCHAR(255),"
                    + "size VARCHAR(255),"
                    + "quantity INT,"
                    + "price FLOAT,"
                    + "PRIMARY KEY (ID))";
            statement.executeUpdate(createTableSqlStatement);
                    
        }catch (Exception e) {
            System.err.println("Sorry could not connect to "
                    + "BeeCeeBakery Database");            
        }        
    }
    
    
    public void addProduct(int id, String cake, String size, 
            int quantity, float price){
        /**
         * Adds a new book to the database
         */
        try{
            String addToProductsSqlStatement = "INSERT INTO bakery "
                + "(ID, cake, size, quantity, price) "
                + "VALUES (?, ?, ?, ?, ?)";
            
            PreparedStatement statement = 
                    conn.prepareStatement(addToProductsSqlStatement);
            statement.setInt(1, id);
            statement.setString(2, cake);
            statement.setString(3, size);
            statement.setInt(4, quantity);
            statement.setFloat(5, price);
            statement.execute();
            
        }catch(Exception ex){
            System.err.println("Sorry could not add new product to bakery "
                    + "table");
        }        
    }
    
    public void updateProduct(int id, String cake, String size, 
             int quantity, float price){
        /**
         * Changes the attributes of an existing product in Products table
         */
        try{
            String updateProductSqlStatement = "UPDATE bakery "
                    + "SET productID=?, cake=?, size=?,"
                    + "quantity=?, price=? "
                    + "WHERE ID=?";
            PreparedStatement statement = 
                    conn.prepareStatement(updateProductSqlStatement);
            statement.setInt(1, id);
            statement.setString(2, cake);
            statement.setString(3, size);
            statement.setInt(5, quantity);
            statement.setFloat(6, price);
            statement.setInt(7, id);
            statement.execute();
        }catch(Exception ex){
            System.err.println("Could not update record with id: " + id +
                    "in the bakery table");
        }
    }
    
    public String[] getArray(int id, String cake, 
            String size, int quantity, float price){
        /**
         * Returns a a string array based on parameters given
         */
        String[] product = new String[6];
        product[0] = Integer.toString(id);
        product[1] = cake;
        product[3] =size ;
        product[4] = Integer.toString(quantity);
        product[5] = Float.toString(price);
        return product;
    }
    
    public String[] getProduct(int ID){
        /**
         * Returns a product whose ID is provided from Products table
         */
        String[] product = new String[5];
        try{
            String getProductQuery = "SELECT * FROM bakery WHERE ID = ?";
            PreparedStatement statement = 
                    conn.prepareStatement(getProductQuery);
            statement.setInt(1, ID);
            ResultSet rs = statement.executeQuery();
            while(rs.next())
                product = getArray(rs.getInt("ID"), 
                        rs.getString("cake"), rs.getString("size"),
                        rs.getInt("quantity"),
                        rs.getFloat("price"));
        }catch(Exception ex){
            System.err.println("Could not get a product with id: " + ID);
        } 
        return product;       
    }
    
    public String[][] getAllProducts(){
        /**
         * Returns a Two dimensional array of 
         * Products in the database
         */
        ArrayList<String[]> products = productsM();
        String[][] productsArray = new String[products.size()][6];
        for(int i = 0; i < products.size();i++){
            productsArray[i] = products.get(i);
        }
        return productsArray;
    }
    
    public ArrayList<String[]> productsM(){
        /**
         * Returns an array list of all products in the database
         */
        ArrayList<String[]> products = new ArrayList<>();
        String getAllProductsQuery = "SELECT * FROM bakery";
        try{
            PreparedStatement statement = 
                    conn.prepareStatement(getAllProductsQuery);
            ResultSet result = statement.executeQuery();
            while(result.next())
                products.add(getArray(result.getInt("ID"), 
                        result.getString("cake"), result.getString("size"),
                         result.getInt("quantity"),
                        result.getFloat("price")));
        }catch(Exception ex){
            System.err.println("Sorry could not fetch all products");
        }       
        return products;
    }
    
    
    public void deleteProduct(int ID){
        /*
        * Removes a product record from the database
        */
        try{
            String sqlString = "DELETE FROM bakery WHERE ID=?";
            PreparedStatement sqlStatement =conn.prepareStatement(sqlString);
            sqlStatement.setInt(1, ID);
            sqlStatement.executeUpdate();
        }catch(Exception ex){
            System.err.println("Could not update record with id: " + ID +
                    "in the bakery table");
        }
    }
    
    public int getProductQuantity(int ID){
        /**
         * Returns the quantity of the products in the database
         */
        
        int quantity = 0;
        String sqlString = "SELECT quantity FROM bakery WHERE ID=?";
        try{
            PreparedStatement sqlStatement =conn.prepareStatement(sqlString);
            sqlStatement.setInt(1, ID);
            sqlStatement.executeUpdate();
            ResultSet rs = sqlStatement.executeQuery();
            while(rs.next())
                quantity = rs.getInt("quantity");            
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return quantity;
    }
    
    public String[] columnNames(){
        String[] columns = {"ID", "cake", "size", 
            "quantity", "price"};
        return columns;
    }
    
    public void closeDbConnection() {
        /**
         * Close Database connection
         */
        try{
            conn.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        
    }
    
    
}
