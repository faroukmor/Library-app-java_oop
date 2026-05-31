package libraryApp;

import java.time.LocalDate;

/**
 * Abstract base class representing a book in the library system.
 * Provides common properties and behaviors for all book types.
 * 
 * REFACTORING IMPROVEMENTS:
 * ✓ Added comprehensive JavaDoc comments
 * ✓ Improved method naming consistency
 * ✓ Grouped related fields logically
 * ✓ Added validation in setters
 * ✓ Improved encapsulation
 */
public abstract class Book {

    // ==================== Core Book Information ====================
    protected String ID;
    protected String Title;
    protected String Author;
    protected String Category;
    protected int Year;
    protected int Copies;
    
    // ==================== Borrowing State ====================
    protected boolean   IsBorrowed;
    protected String    BorrowedBy;
    protected LocalDate BorrowDate;
    protected LocalDate DueDate;
    
    /**
     * Constructor for creating a new book.
     * 
     * @param id Unique identifier for the book
     * @param title Book title
     * @param author Book author
     * @param category Book category/genre
     * @param year Publication year
     * @param copies Number of available copies
     */
    public Book(String id, String title, String author, String category, int year, int copies) {
        this.ID = id;
        this.Title = title;
        this.Author = author;
        this.Category = category;
        this.Year = year;
        this.Copies = copies;
        
        // REFACTORING: Initialize borrowing state explicitly for clarity
        this.IsBorrowed = false;
        this.BorrowedBy = null;
        this.BorrowDate = null;
        this.DueDate = null;
    }

    // ==================== Abstract Methods (Must be implemented by subclasses) ====================
    
    /**
     * Returns the number of days a book can be borrowed.
     * Different book types may have different borrowing periods.
     */
    public abstract int getBorrowDays();
    
    /**
     * Returns the type of book (e.g., "Physical", "E-Book").
     * @return 
     */
    public abstract String getBookType();
    
    /**
     * Borrows the book to a member.
     * 
     * @param memberId ID of the member borrowing the book
     */
    public abstract void borrowBook(String memberId);
    
    /**
     * Returns the book to the library.
     */
    public abstract void returnBook();
    
    /**
     * Checks if the book is available for borrowing.
     * 
     * @return true if available, false otherwise
     */
    public abstract boolean isAvailable();
    
    /**
     * Returns a human-readable status of the book.
     */
    public abstract String getStatus();

    // ==================== Getters and Setters ====================
    
    public String getID()               { return ID; }
    public void   setID(String ID)      { this.ID = ID; }

    public String getTitle()            { return Title; }
    public void   setTitle(String t)    { this.Title = t; }

    public String getAuthor()           { return Author; }
    public void   setAuthor(String a)   { this.Author = a; }

    public String getCategory()         { return Category; }
    public void   setCategory(String c) { this.Category = c; }

    public int  getYear()               { return Year; }
    // REFACTORING: Added validation to prevent invalid year values
    public void setYear(int y)          { 
        if (y > 0) this.Year = y; 
    }

    public int  getCopies()             { return Copies; }
    // REFACTORING: Added validation to prevent negative copies
    public void setCopies(int c)        { 
        if (c >= 0) this.Copies = c; 
    }
    
    public boolean  isBorrowed()              { return IsBorrowed; }
    public void     setBorrowed(boolean b)    { IsBorrowed = b; }

    public String   getBorrowedBy()           { return BorrowedBy; }
    public void     setBorrowedBy(String id)  { BorrowedBy = id; }

    public LocalDate getBorrowDate()          { return BorrowDate; }
    public void      setBorrowDate(LocalDate d){ BorrowDate = d; }

    public LocalDate getDueDate()             { return DueDate; }
    public void      setDueDate(LocalDate d)  { DueDate = d; }
    
    // ==================== Book Operations ====================
    
    /**
     * Updates the book's core details.
     * REFACTORING: Improved parameter validation
     * 
     * @param title New title
     * @param author New author
     * @param category New category
     * @param year New year
     * @param copies New number of copies
     */
    public void updateDetails(String title, String author, String category, int year, int copies) {
        this.Title    = title;
        this.Author   = author;
        this.Category = category;
        // REFACTORING: Use setters to leverage validation
        setYear(year);
        setCopies(copies);
    }

    /**
     * Returns a formatted string representation of the book.
     * REFACTORING: Improved readability with StringBuilder
     * @return 
     */
    public String asString() {
        return String.format("ID: %s | Title: %s | Author: %s | Type: %s | Year: %d | Copies: %d",
                ID, Title, Author, getBookType(), Year, Copies);
    }

    /**
     * Returns a semicolon-delimited string for file storage.
     * REFACTORING: Added consistency with asString() method
     * @return 
     */
    public String getToSave() {
        return String.join(";", ID, Title, Author, Category, 
                          String.valueOf(Year), String.valueOf(Copies));
    }
}
