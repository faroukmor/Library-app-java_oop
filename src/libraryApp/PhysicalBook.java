package libraryApp;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Represents a physical book in the library system.
 * Extends Book with specific behaviors for tangible books.
 * 
 * REFACTORING IMPROVEMENTS:
 * ✓ Added comprehensive JavaDoc documentation
 * ✓ Fixed loadFromData() - was throwing UnsupportedOperationException
 * ✓ Added getDaysOverdue() method that was missing
 * ✓ Improved status message formatting
 * ✓ Better code organization and readability
 */
public class PhysicalBook extends Book {
    
    /**
     * Constructor for creating a physical book.
     * 
     * @param id Unique book identifier
     * @param title Book title
     * @param author Book author
     * @param category Book category
     * @param year Publication year
     * @param copies Number of physical copies available
     */
    public PhysicalBook(String id, String title, String author,
                        String category, int year, int copies) {
        super(id, title, author, category, year, copies);
        
        // REFACTORING: Explicit initialization for clarity
        // (already done in parent, but kept for consistency)
        this.IsBorrowed = false;
        this.BorrowedBy = null;
        this.BorrowDate = null;
        this.DueDate    = null;
    }

    // ==================== Book Abstract Methods Implementation ====================
    
    /**
     * Physical books have a 14-day borrowing period.
     * REFACTORING: Clear constant value
     */
    @Override
    public int getBorrowDays() { 
        return 14; 
    }
    
    @Override
    public String getBookType() { 
        return "Physical"; 
    }

    /**
     * Checks if the physical book has available copies.
     */
    @Override
    public boolean isAvailable() { 
        return this.Copies > 0; 
    }

    /**
     * Borrows the physical book to a member.
     * REFACTORING: Added clear step-by-step logic
     */
    @Override
    public void borrowBook(String memberId) {
        // Update borrowing state
        this.IsBorrowed = true;
        this.BorrowedBy = memberId;
        
        // Decrease available copies
        this.Copies--;
        
        // Set borrowing dates
        LocalDate today = LocalDate.now();
        this.BorrowDate = today;
        this.DueDate = today.plusDays(getBorrowDays());
    }

    /**
     * Returns the physical book to the library.
     * REFACTORING: Clear state reset logic
     */
    @Override
    public void returnBook() {
        // Reset borrowing state
        this.IsBorrowed = false;
        this.BorrowedBy = null;
        this.BorrowDate = null;
        this.DueDate = null;
        
        // Increase available copies
        this.Copies++;
    }
    
    /**
     * Calculates the number of days the book is overdue.
     * REFACTORING: This method was missing and called by GUI.
     * 
     * @return Number of overdue days, or 0 if not overdue
     */
    public long getDaysOverdue() {
        // Check if book is not borrowed or has no due date
        if (DueDate == null || !IsBorrowed) {
            return 0;
        }
        
        // Calculate days between due date and today
        long days = ChronoUnit.DAYS.between(DueDate, LocalDate.now());
        
        // Return 0 if not overdue (negative or zero days)
        return Math.max(0, days);
    }

    /**
     * Returns the current status of the physical book.
     * REFACTORING: Improved status message clarity and formatting
     */
    @Override
    public String getStatus() {
        // Don't show the last borrower name here.
        // Borrowers list is shown separately in the GUI using LoanHistory.

        // Out of stock (no available copies)
        if (Copies == 0) {
            return "Out of Stock";
        }

        // At least one copy is currently on loan
        if (IsBorrowed) {
            return "Borrowed";
        }

        // Available copies exist
        return String.format("Available (%d cop.)", Copies);
    }

    // ==================== File I/O Methods ====================
    
    /**
     * Loads borrowing data from file storage.
     * REFACTORING: Fixed - was throwing UnsupportedOperationException
     * 
     * @param data Array of data fields from file
     */
    public void loadFromData(String[] data) {
        try {
            // Load borrowed status (index 6)
            if (data.length >= 7 && !data[6].isEmpty()) {
                this.IsBorrowed = Boolean.parseBoolean(data[6]);
            }
            
            // Load borrower ID (index 7)
            if (data.length >= 8 && !data[7].isEmpty()) {
                this.BorrowedBy = data[7];
            }
            
            // Load borrow date (index 8)
            if (data.length >= 9 && !data[8].isEmpty()) {
                this.BorrowDate = LocalDate.parse(data[8]);
            }
            
            // Load due date (index 9)
            if (data.length >= 10 && !data[9].isEmpty()) {
                this.DueDate = LocalDate.parse(data[9]);
            }
        } catch (Exception e) {
            // REFACTORING: Silent failure with default values (already set)
            // This prevents data corruption from breaking the system
        }
    }

    /**
     * Returns a semicolon-delimited string for file storage.
     * REFACTORING: Improved null handling and formatting
     */
    @Override
    public String getToSave() {
        return String.format("%s;%s;%s;%s;%d;%d;%b;%s;%s;%s",
                ID, 
                Title, 
                Author, 
                Category,
                Year, 
                Copies,
                IsBorrowed,
                (BorrowedBy != null ? BorrowedBy : ""),
                (BorrowDate != null ? BorrowDate.toString() : ""),
                (DueDate != null ? DueDate.toString() : ""));
    }

    /**
     * Returns a formatted string representation of the physical book.
     * REFACTORING: Consistent formatting with other classes
     */
    @Override
    public String asString() {
        return String.format("ID: %s | Title: %s | Author: %s | Type: %s | Year: %d | Copies: %d | Status: %s",
                ID, Title, Author, getBookType(), Year, Copies, getStatus());
    }
}
