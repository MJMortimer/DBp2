package main;
/*

 * LibraryModel.java
 * Author:
 * Created on:
 */



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.*;

public class LibraryModel {

	// For use in creating dialogs and making them modal
	private JFrame dialogParent;
	private Connection con;

	public LibraryModel(JFrame parent, String userid, String password) {
		dialogParent = parent;
		try {
			con = DriverManager.getConnection(getURL(), userid, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String bookLookup(int isbn) {
		String select = "SELECT * FROM Book WHERE ISBN ="+isbn;
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(select);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "Lookup Book Stub";
	}

	public String showCatalogue() {
		return "Show Catalogue Stub";
	}

	public String showLoanedBooks() {
		return "Show Loaned Books Stub";
	}

	public String showAuthor(int authorID) {
		return "Show Author Stub";
	}

	public String showAllAuthors() {
		return "Show All Authors Stub";
	}

	public String showCustomer(int customerID) {
		return "Show Customer Stub";
	}

	public String showAllCustomers() {
		return "Show All Customers Stub";
	}

	public String borrowBook(int isbn, int customerID,
			int day, int month, int year) {
		return "Borrow Book Stub";
	}

	public String returnBook(int isbn, int customerid) {
		return "Return Book Stub";
	}

	public void closeDBConnection() {
	}

	public String deleteCus(int customerID) {
		return "Delete Customer";
	}

	public String deleteAuthor(int authorID) {
		return "Delete Author";
	}

	public String deleteBook(int isbn) {
		return "Delete Book";
	}

	private String getURL() {
		return "jdbc:postgresql://db.ecs.vuw.ac.nz/mortimmatt_jdbc";
	}
}