package libraryApp;

import java.time.LocalDate;

/**
 * Represents an electronic book (E-Book) in the library system.
 * Extends Book and implements IMedia for digital content properties.
 * 
 * REFACTORING IMPROVEMENTS:
 * ✓ Fixed field naming (was "Degitalmedia" → now "Media")
 * ✓ Added comprehensive JavaDoc documentation
 * ✓ Improved code organization and readability
 * ✓ Added proper status handling for digital books
 * ✓ Consistent formatting across all methods
 */
public class EBook extends Book implements IMedia {

    // ==================== Digital Media Component ====================
    // REFACTORING: Fixed typo - was "Degitalmedia"
    private final DigitalMedia Media;

    /**
     * Constructor for creating an E-Book.
     * 
     * @param id Unique book identifier
     * @param title Book title
     * @param author Book author
     * @param category Book category
     * @param year Publication year
     * @param copies Number of digital copies available
     * @param downloadLink URL for downloading the e-book
     * @param fileSizeMB File size in megabytes
     */
    public EBook(String id, String title, String author, String category,
                 int year, int copies, String downloadLink, double fileSizeMB) {
        super(id, title, author, category, year, copies);
        // REFACTORING: Initialize digital media component
        this.Media = new DigitalMedia(downloadLink, fileSizeMB);
    }
    
    // ==================== Book Abstract Methods Implementation ====================
    
    /**
     * E-Books have a 7-day borrowing period.
     * REFACTORING: Added clear constant value
     */
    @Override
    public int getBorrowDays() { 
        return 7; 
    }
    
    @Override
    public String getBookType() { 
        return "E-Book"; 
    }

    /**
     * Checks if the e-book has available digital copies.
     * REFACTORING: Simplified logic
     */
    @Override
    public boolean isAvailable() { 
        return this.Copies > 0; 
    }

    /**
     * Borrows the e-book to a member.
     * REFACTORING: Added clear step-by-step logic with comments
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
     * Returns the e-book to the library.
     * REFACTORING: Improved code organization
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
     * Returns the current status of the e-book.
     * REFACTORING: Improved status message clarity
     */
    @Override
    public String getStatus() {
        // Keep borrowing logic the same; only the status string changes.
        // Prefix with "Digital" so the GUI can color E-Books differently.
        if (IsBorrowed) {
            return "Digital — Borrowed";
        }
        if (isAvailable()) {
            return String.format("Digital — Available (%d cop.)", Copies);
        }
        return "Digital — Out of copies";
    }

    // ==================== IMedia Interface Implementation ====================
    
    @Override
    public String getMediaID() { 
        return Media.getMediaID(); 
    }
    
    @Override
    public String getURL() { 
        return Media.getURL(); 
    }

    @Override
    public double getSizeMB() { 
        return Media.getSizeMB(); 
    }

    // ==================== File I/O Methods ====================
    
    /**
     * Returns a semicolon-delimited string for file storage.
     * REFACTORING: Improved formatting and readability
     */
    @Override
    public String getToSave() {
        return String.format("EBOOK;%s;%s;%s;%s;%d;%d;%s;%.2f",
                ID, Title, Author, Category, Year, Copies,
                Media.getURL(), Media.getSizeMB());
    }

    /**
     * Returns a formatted string representation of the e-book.
     * REFACTORING: Consistent formatting with Book class
     */
    @Override
    public String asString() {
        return String.format("ID: %s | Title: %s | Type: %s | URL: %s | Size: %.2f MB",
                ID, Title, getBookType(), getURL(), getSizeMB());
    }
}
