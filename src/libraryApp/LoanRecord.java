package libraryApp;

import java.time.LocalDate;

/**
 * Represents a loan record tracking book borrowing history.
 * Records which member borrowed which book, when, and when it's due.
 * 
 * REFACTORING IMPROVEMENTS:
 * ✓ Added comprehensive JavaDoc documentation
 * ✓ Changed from default to public access (for better encapsulation)
 * ✓ Added validation in constructor
 * ✓ Improved toString() method formatting
 * ✓ Better code organization
 */
public class LoanRecord {

    // ==================== Fields ====================
    private String    bookId;
    private String    memberId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private boolean   returned;

    /**
     * Constructor for creating a loan record.
     * 
     * @param bookId ID of the borrowed book
     * @param memberId ID of the borrowing member
     * @param borrowDate Date when the book was borrowed
     * @param dueDate Date when the book is due to be returned
     */
    public LoanRecord(String bookId, String memberId,
                      LocalDate borrowDate, LocalDate dueDate) {
        // REFACTORING: Added null checks for safety
        this.bookId     = (bookId != null) ? bookId : "";
        this.memberId   = (memberId != null) ? memberId : "";
        this.borrowDate = (borrowDate != null) ? borrowDate : LocalDate.now();
        this.dueDate    = (dueDate != null) ? dueDate : LocalDate.now();
        this.returned   = false;
    }

    // ==================== Getters and Setters ====================
    
    public String getBookId() { 
        return bookId; 
    }
    
    public String getMemberId() { 
        return memberId; 
    }
    
    public LocalDate getBorrowDate() { 
        return borrowDate; 
    }
    
    public LocalDate getDueDate() { 
        return dueDate; 
    }
    
    public boolean isReturned() { 
        return returned; 
    }
    
    public void setReturned(boolean returned) { 
        this.returned = returned; 
    }

    // ==================== Utility Methods ====================
    
    /**
     * Returns a semicolon-delimited string for file storage.
     * REFACTORING: Added clear formatting and documentation
     * 
     * @return Formatted string: bookId;memberId;borrowDate;dueDate;returned
     */
    @Override
    public String toString() {
        return String.format("%s;%s;%s;%s;%b",
                bookId, memberId, borrowDate, dueDate, returned);
    }
}
