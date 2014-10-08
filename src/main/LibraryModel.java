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
			/*
			 * Select b.isbn, b.title, b.edition_no, b.NumOfCop, b.numLeft, array_to_string(array_agg(trim(BOTH ' ' from a.surname) ORDER BY ba.authorseqno ASC),', ') as authors  
			 * From Book b, Author a, Book_Author ba 
			 * WHERE b.isbn = ba.isbn AND a.authorid = ba.authorid 
			 * group by b.isbn 
			 * ORDER BY b.isbn ASC;
			 * 
			 */
			
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
			//CHECK FOR BADNESS
			StringBuilder sb = new StringBuilder();
			sb.append("Show Catalogue:"+"\n");
			/*
			 * Select b.isbn, b.title, b.edition_no, b.NumOfCop, b.numLeft, array_to_string(array_agg(trim(BOTH ' ' from a.surname) ORDER BY ba.authorseqno ASC),', ') as authors  
			 * From Book b, Author a, Book_Author ba 
			 * WHERE b.isbn = ba.isbn AND a.authorid = ba.authorid 
			 * group by b.isbn 
			 * ORDER BY b.isbn ASC;
			 * 
			 */
			
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

	private String getUrl() {
		return "jdbc:postgresql://localhost:5432/mortimmatt_jdbc";
	}
}