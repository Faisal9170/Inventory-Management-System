
import java.sql.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        String url = "jdbc:mysql://localhost:3306/retail_db";
        String user = "root";
        String password = "Faisal1234?!"; // Apna MySQL password likhna

        try (Connection con = DriverManager.getConnection(url, user, password)) {

            System.out.println("Database Connected Successfully!");

            int choice;

            do {
                System.out.println("\n===== INVENTORY MANAGEMENT SYSTEM =====");
                System.out.println("1. Insert 3 Product Records");
                System.out.println("2. Conditional Retrieval and Sorting");
                System.out.println("3. Update Product Stock");
                System.out.println("4. Search and Modify Product Price");
                System.out.println("5. Aggregate Query and Conditional Deletion");
                System.out.println("0. Exit");

                System.out.print("Enter your choice: ");
                choice = Integer.parseInt(sc.nextLine());

                switch (choice) {

                    // (a) INSERT PRODUCT RECORDS
                    case 1:

                        String insertSQL = "INSERT INTO Product VALUES (?, ?, ?, ?, ?)";

                        for (int i = 1; i <= 3; i++) {

                            System.out.println("\nEnter Product " + i + " Details:");

                            System.out.print("Product ID: ");
                            int id = Integer.parseInt(sc.nextLine());

                            System.out.print("Product Name: ");
                            String name = sc.nextLine();

                            System.out.print("Category: ");
                            String category = sc.nextLine();

                            System.out.print("Quantity: ");
                            int quantity = Integer.parseInt(sc.nextLine());

                            System.out.print("Price: ");
                            double price = Double.parseDouble(sc.nextLine());

                            try (PreparedStatement ps = con.prepareStatement(insertSQL)) {

                                ps.setInt(1, id);
                                ps.setString(2, name);
                                ps.setString(3, category);
                                ps.setInt(4, quantity);
                                ps.setDouble(5, price);

                                ps.executeUpdate();

                                System.out.println("Product Inserted Successfully!");

                            } catch (SQLException e) {

                                if (e.getErrorCode() == 1062) {
                                    System.out.println("Duplicate Product ID Not Allowed!");
                                } else {
                                    System.out.println(e.getMessage());
                                }
                            }
                        }
                        break;


                    // (b) CONDITIONAL RETRIEVAL AND SORTING
                    case 2:

                        System.out.print("Enter Category: ");
                        String cat = sc.nextLine();

                        String selectSQL = "SELECT * FROM Product WHERE category = ? AND quantity < 20 ORDER BY price DESC";

                        try (PreparedStatement ps = con.prepareStatement(selectSQL)) {

                            ps.setString(1, cat);

                            try (ResultSet rs = ps.executeQuery()) {

                                boolean found = false;

                                while (rs.next()) {

                                    found = true;

                                    System.out.println("\nProduct ID: " + rs.getInt("product_id"));
                                    System.out.println("Name: " + rs.getString("product_name"));
                                    System.out.println("Category: " + rs.getString("category"));
                                    System.out.println("Quantity: " + rs.getInt("quantity"));
                                    System.out.println("Price: " + rs.getDouble("price"));
                                }

                                if (!found) {
                                    System.out.println("No Matching Products Found!");
                                }
                            }
                        }
                        break;


                    // (c) UPDATE PRODUCT STOCK
                    case 3:

                        System.out.print("Enter Product ID: ");
                        int pid = Integer.parseInt(sc.nextLine());

                        System.out.print("Enter Quantity to Add: ");
                        int addQty = Integer.parseInt(sc.nextLine());

                        if (addQty <= 0) {
                            System.out.println("Quantity must be greater than zero!");
                            break;
                        }

                        String updateStock = "UPDATE Product SET quantity = quantity + ? WHERE product_id = ?";

                        try (PreparedStatement ps = con.prepareStatement(updateStock)) {

                            ps.setInt(1, addQty);
                            ps.setInt(2, pid);

                            int rows = ps.executeUpdate();

                            if (rows > 0) {
                                System.out.println("Product Stock Updated Successfully!");
                            } else {
                                System.out.println("Product Does Not Exist!");
                            }
                        }
                        break;


                    // (d) SEARCH AND MODIFY PRODUCT DETAILS
                    case 4:

                        System.out.print("Enter Product ID: ");
                        int searchId = Integer.parseInt(sc.nextLine());

                        System.out.print("Enter New Price: ");
                        double newPrice = Double.parseDouble(sc.nextLine());

                        if (newPrice <= 0) {
                            System.out.println("Price Must Be Greater Than Zero!");
                            break;
                        }

                        String searchSQL = "SELECT * FROM Product WHERE product_id = ?";

                        boolean exists = false;

                        try (PreparedStatement ps = con.prepareStatement(searchSQL)) {

                            ps.setInt(1, searchId);

                            try (ResultSet rs = ps.executeQuery()) {

                                if (rs.next()) {
                                    exists = true;
                                }
                            }
                        }

                        if (exists) {

                            String updatePrice = "UPDATE Product SET price = ? WHERE product_id = ?";

                            try (PreparedStatement ps = con.prepareStatement(updatePrice)) {

                                ps.setDouble(1, newPrice);
                                ps.setInt(2, searchId);

                                ps.executeUpdate();

                                System.out.println("Price Updated Successfully!");
                            }

                            // Display Updated Product Details
                            try (PreparedStatement ps = con.prepareStatement(searchSQL)) {

                                ps.setInt(1, searchId);

                                try (ResultSet rs = ps.executeQuery()) {

                                    if (rs.next()) {

                                        System.out.println("\nUpdated Product Details:");
                                        System.out.println("ID: " + rs.getInt("product_id"));
                                        System.out.println("Name: " + rs.getString("product_name"));
                                        System.out.println("Category: " + rs.getString("category"));
                                        System.out.println("Quantity: " + rs.getInt("quantity"));
                                        System.out.println("Price: " + rs.getDouble("price"));
                                    }
                                }
                            }

                        } else {
                            System.out.println("Product Does Not Exist!");
                        }
                        break;


                    // (e) AGGREGATE QUERY AND CONDITIONAL DELETION
                    case 5:

                        String sumSQL = "SELECT COALESCE(SUM(quantity * price), 0) AS total FROM Product";

                        try (Statement st = con.createStatement();
                             ResultSet rs = st.executeQuery(sumSQL)) {

                            if (rs.next()) {
                                System.out.println("Total Inventory Value: " + rs.getDouble("total"));
                            }
                        }

                        System.out.print("Enter Product ID to Delete: ");
                        int deleteId = Integer.parseInt(sc.nextLine());

                        String deleteSQL = "DELETE FROM Product WHERE product_id = ? AND quantity = 0";

                        try (PreparedStatement ps = con.prepareStatement(deleteSQL)) {

                            ps.setInt(1, deleteId);

                            int rows = ps.executeUpdate();

                            if (rows > 0) {
                                System.out.println("Product Deleted Successfully!");
                            } else {

                                String checkSQL = "SELECT quantity FROM Product WHERE product_id = ?";

                                try (PreparedStatement check = con.prepareStatement(checkSQL)) {

                                    check.setInt(1, deleteId);

                                    try (ResultSet rs = check.executeQuery()) {

                                        if (rs.next()) {
                                            System.out.println("Cannot Delete! Product Quantity Is Not Zero.");
                                        } else {
                                            System.out.println("Product Does Not Exist!");
                                        }
                                    }
                                }
                            }
                        }
                        break;


                    case 0:
                        System.out.println("Thank You! Application Closed.");
                        break;

                    default:
                        System.out.println("Invalid Choice! Try Again.");
                }

            } while (choice != 0);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        sc.close();
    }
}
