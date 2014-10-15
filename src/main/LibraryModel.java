package main;


/*
 * LibraryModel.java
 * Author:
 * Created on:
 */



import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static javax.swing.JOptionPane.showMessageDialog;

import javax.swing.*;

public class LibraryModel {

	// For use in creating dialogs and making them modal
	private JFrame dialogParent;
	private Connection con;

	public LibraryModel(JFrame parent, String userid, String password) {
		dialogParent = parent;
		try {
			con = DriverManager.getConnection(getUrl(), userid, password);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String bookLookup(int isbn) {
		String select =String.format("SELECT b.isbn, b.title, b.edition_no, b.NumOfCop, b.numLeft, array_to_string(array_agg(trim(BOTH ' ' from a.surname) ORDER BY ba.authorseqno ASC),', ') as authors " 
				+"FROM Book b LEFT OUTER JOIN Book_Author ba ON (b.isbn = ba.isbn) LEFT OUTER JOIN Author a ON (a.authorid = ba.authorid)"
				+"WHERE b.isbn = %d "
				+"GROUP BY b.isbn "
				+"ORDER BY b.isbn ASC;", isbn);
		Statement stmt;
		try {
			stmt = con.createStatement();
			ResultSet res = stmt.executeQuery(select);
			//CHECK FOR BADNESS
			StringBuilder sb = new StringBuilder();
			sb.append("Book Lookup:"+"\n");
			if(!res.next()){
				System.out.println("No book exists with isbn "+ isbn);
				return "No book exists with isbn "+ isbn;
			}
			
			while(res.next()){				
				sb.append(String.format("\t%d: %s\n",res.getInt("ISBN"), res.getString("title")));
				sb.append(String.format("\tEdition: %d - Number of Copies: %d - Copies Left: %d\n", res.getInt("Edition_No"), res.getInt("NumOfCop"), res.getInt("NumLeft")));
				String authors = res.getString("authors");
				if(authors.length() == 0)  
					sb.append("\t(No Authors)"); 
				else
					sb.append(String.format("\t%s: %s", (authors.contains(",")? "Authors" : "Author"), authors));
			}
			return sb.toString();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return (e.getMessage());
		}		
	}

	public String showCatalogue() {
		String select =String.format("SELECT b.isbn, b.title, b.edition_no, b.NumOfCop, b.numLeft, array_to_string(array_agg(trim(BOTH ' ' from a.surname) ORDER BY ba.authorseqno ASC),', ') as authors " 
				+"FROM Book b LEFT OUTER JOIN Book_Author ba ON (b.isbn = ba.isbn) LEFT OUTER JOIN Author a ON (a.authorid = ba.authorid)"
				+"GROUP BY b.isbn "
				+"ORDER BY b.isbn ASC;");
		Statement stmt;
		try {
			stmt = con.createStatement();
			ResultSet res = stmt.executeQuery(select);
			StringBuilder sb = new StringBuilder();
			sb.append("Show Catalogue:"+"\n");
			while(res.next()){				
				sb.append(String.format("%d: %s\n",res.getInt("ISBN"), res.getString("title")));
				sb.append(String.format("\tEdition: %d - Number of Copies: %d - Copies Left: %d\n", res.getInt("Edition_No"), res.getInt("NumOfCop"), res.getInt("NumLeft")));
				String authors = res.getString("authors");
				if(authors.length() == 0)  
					sb.append("\t(No Authors)\n"); 
				else
					sb.append(String.format("\t%s: %s\n", (authors.contains(",")? "Authors" : "Author"), authors));			}
			
			return sb.toString();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return (e.getMessage());
		}		
	}

	public String showLoanedBooks() {
		return "loane books stub";
	}

	public String showAllAuthors() {
		String authors = String.format("Select AuthorId, trim(BOTH ' ' from Name) as Name, trim(BOTH ' ' from Surname) as Surname FROM Author a;");
		Statement stmt;
		try {
			stmt = con.createStatement();
			ResultSet res = stmt.executeQuery(authors);
			//CHECK FOR BADNESS
			StringBuilder sb = new StringBuilder();
			sb.append("Show All Authors:"+"\n");

			while(res.next()){				
				sb.append(String.format("\t%d: %s, %s\n",res.getInt("AuthorId"), res.getString("Surname"), res.getString("Name")));
			}
			return sb.toString();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return (e.getMessage());
		}		
	}

	public String showCustomer(int customerID) {
		String customer = String.format("Select * FROM Customer c Where c.customerId = %d;", customerID);
		String books = String.format("select * from Book b, Cust_Book bc  Where b.isbn = bc.isbn AND bc.customerId = %d;", customerID);
		Statement stmt;
		try {
			stmt = con.createStatement();
			ResultSet res = stmt.executeQuery(customer);
			//CHECK FOR BADNESS
			StringBuilder sb = new StringBuilder();
			sb.append("Show Customer:"+"\n");

			while(res.next()){				
				sb.append(String.format("\t%d: %s, %s\n",res.getInt("CustomerId"), res.getString("l_name").trim(), res.getString("f_name").trim()));
			}
			sb.append("\tBorrowed: \n");
			stmt = con.createStatement();
			res = stmt.executeQuery(books);
			boolean hasBooks = false;
			while(res.next()){
				hasBooks = true;
				sb.append(String.format("\t\t%d - %s\n", res.getInt("isbn"), res.getString("title")));
			}
			if(!hasBooks){
				sb.append("\t\t(No Books)\n");
			}
			return sb.toString();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return (e.getMessage());
		}	
	}

	public String showAllCustomers() {
		String customers = String.format("Select * FROM Customer c;");
		Statement stmt;
		try {
			stmt = con.createStatement();
			ResultSet res = stmt.executeQuery(customers);
			//CHECK FOR BADNESS
			StringBuilder sb = new StringBuilder();
			sb.append("Show All Customers:"+"\n");

			while(res.next()){	
				String city = res.getString("city");
				sb.append(String.format("\t%d: %s, %s - %s\n",res.getInt("CustomerId"), res.getString("l_name").trim(), res.getString("f_name").trim(), city == null ? "(no city)": city.trim()));
			}
			return sb.toString();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return (e.getMessage());
		}
	}

	public String borrowBook(int isbn, int customerID,
			int day, int month, int year) {
		try {
			con.setAutoCommit(false);
			Statement custStmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			Statement bookStmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			Statement constraint = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			Statement insertStmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

			ResultSet customer = custStmt.executeQuery("SELECT * FROM Customer WHERE CustomerID = "+ customerID +" FOR UPDATE;");
			if(!customer.next()){
				con.rollback();
				con.setAutoCommit(true);
				System.out.println("No customer exists with ID "+ customerID);
				return "No customer exists with ID "+ customerID;
			}

			ResultSet book = bookStmt.executeQuery("SELECT * FROM Book WHERE ISBN = "+ isbn +" FOR UPDATE;");
			if(!book.next()){
				con.rollback();
				con.setAutoCommit(true);
				System.out.println("No book exists with isbn "+ isbn);
				return "No book exists with isbn "+ isbn;
			}
			
			ResultSet constraintCheck = constraint.executeQuery("SELECT * FROM Cust_Book WHERE ISBN = "+isbn+" AND CustomerID = "+customerID+";");
			if(constraintCheck.next()){
				con.rollback();
				con.setAutoCommit(true);
				System.out.println("That customer has already loaned out this book.");
				return "The customer with ID "+customerID+ " has already loaned the book with ISBN "+ isbn;
			}
			book.first();
			if(book.getInt("numLeft") == 0){
				con.rollback();
				con.setAutoCommit(true);
				System.out.println("No copies of the book with ISBN "+ isbn+" is available");
				return "No copies of the book with ISBN "+ isbn+" is available";
			}
			
			String insert = String.format("INSERT INTO Cust_Book VALUES (%d, '%d-%d-%d', %d);", isbn, year, month, day, customerID);
			insertStmt.executeUpdate(insert);
			showMessageDialog(dialogParent, "The book is soon to be borrowed");
			book.first();			
			book.updateInt("numLeft", book.getInt("numleft")-1);
			book.updateRow();
			con.commit();
			con.setAutoCommit(true);

			book.first();
			customer.first();
			StringBuilder sb = new StringBuilder();
			sb.append("Borrow Book\n");
			sb.append(String.format("\tBook: %d (%s)\n", book.getInt("isbn"), book.getString("title").trim()));
			sb.append(String.format("\tLoaned to: %d (%s %s)\n",customer.getInt("customerId"),customer.getString("f_name").trim(), customer.getString("l_name").trim()));
			sb.append(String.format("\tDue Date: %d-%d-%d\n", year, month, day));
			return sb.toString();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		}
	}

	public String returnBook(int isbn, int customerID) {
		try {
			con.setAutoCommit(false);
			Statement custStmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			Statement bookStmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			Statement deleteStmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			Statement constraint = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			ResultSet customer = custStmt.executeQuery("SELECT * FROM Customer WHERE CustomerID = "+ customerID +" FOR UPDATE;");
			if(!customer.next()){
				con.rollback();
				con.setAutoCommit(true);
				System.out.println("No customer exists with ID "+ customerID);
				return "No customer exists with ID "+ customerID;
			}

			ResultSet book = bookStmt.executeQuery("SELECT * FROM Book WHERE ISBN = "+ isbn +" FOR UPDATE;");
			if(!book.next()){
				con.rollback();
				con.setAutoCommit(true);
				System.out.println("No book exists with isbn "+ isbn);
				return "No book exists with isbn "+ isbn;
			}
			
			ResultSet constraintCheck = constraint.executeQuery("SELECT * FROM Cust_Book WHERE ISBN = "+isbn+" AND CustomerID = "+customerID+";");
			if(!constraintCheck.next()){
				con.rollback();
				con.setAutoCommit(true);
				System.out.println("The customer has not previously loaned out this book");
				return "The customer with ID "+customerID+ " has not loaned the book with ISBN "+ isbn;
			}
			
			String delete = String.format("DELETE FROM Cust_Book WHERE isbn = %d AND customerId = %d;", isbn, customerID);
			deleteStmt.executeUpdate(delete);
			showMessageDialog(dialogParent, "The book is soon to be returned");
			book.first();			
			book.updateInt("numLeft", book.getInt("numleft")+1);
			book.updateRow();
			con.commit();
			con.setAutoCommit(true);

			book.first();
			customer.first();
			StringBuilder sb = new StringBuilder();
			sb.append("Return Book\n");
			sb.append(String.format("\tBook %d  returned for customer %d\n", book.getInt("isbn"), customer.getInt("customerId")));

			return sb.toString();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		}
	}

	public void closeDBConnection() {
		try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	private String getUrl() {
		return "jdbc:postgresql://localhost:5432/mortimmatt_jdbc";
	}
}