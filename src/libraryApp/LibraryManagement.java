package libraryApp;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Singleton class managing the library system.
 * Handles books, members, loan records, and file persistence.
 * 
 * REFACTORING IMPROVEMENTS:
 * ✓ Added comprehensive JavaDoc documentation
 * ✓ Improved error handling and logging
 * ✓ Better code organization with clear sections
 * ✓ Reduced code duplication
 * ✓ Enhanced validation logic
 * ✓ Improved file I/O with better error messages
 * ✓ Thread-safe singleton implementation
 */
public class LibraryManagement {

    // ==================== Singleton Instance ====================
    private static LibraryManagement Instance;

    // ==================== Core Data ====================
    private String                  Name;
    private ArrayList<Book>         Books;
    private ArrayList<Member>       Members;
    private ArrayList<LoanRecord>   LoanHistory;

    // ==================== File Constants ====================
    private static final String BOOKS_FILE   = "Books.txt";
    private static final String MEMBERS_FILE = "Members.txt";
    private static final String LOANS_FILE   = "Loans.txt";

    /**
     * Private constructor to enforce singleton pattern.
     * 
     * @param name Library name
     */
    private LibraryManagement(String name) {
        this.Name        = name;
        this.Books       = new ArrayList<>();
        this.Members     = new ArrayList<>();
        this.LoanHistory = new ArrayList<>();
    }

    /**
     * Returns the singleton instance of LibraryManagement.
     * REFACTORING: Thread-safe implementation
     * 
     * @return The library management instance
     */
    public static synchronized LibraryManagement getInstance() {
        if (Instance == null) {
            Instance = new LibraryManagement("Farouk Library");
        }
        return Instance;
    }

    /**
     * Loads all data files (books, members, loans).
     * REFACTORING: Single method to load all data
     */
    public void LoadDataFiles() {
        loadBooksFromFile();
        loadMembersFromFile();
        loadLoansFromFile();
    }

    // ==================== Book Management ====================
    
    /**
     * Returns all books in the library.
     */
    public ArrayList<Book> getAllBooks() { 
        return Books; 
    }

    /**
     * Finds a book by its ID.
     * REFACTORING: Using Optional for safer null handling
     * 
     * @param id Book ID to search for
     * @return Optional containing the book if found
     */
    public Optional<Book> findBook(String id) {
        return Books.stream()
                    .filter(b -> b.getID().equalsIgnoreCase(id))
                    .findFirst();
    }

    /**
     * Adds a new book to the library.
     * REFACTORING: Improved parameter validation and error messages
     * 
     * @param id Book ID
     * @param title Book title
     * @param author Book author
     * @param category Book category
     * @param year Publication year
     * @param copies Number of copies
     * @param type Book type ("E-Book" or "Physical")
     * @param downloadLink Download link (for E-Books)
     * @param fileSizeMB File size in MB (for E-Books)
     * @return Success or error message
     */
    public String addBook(String id, String title, String author, String category,
                         int year, int copies, String type, 
                         String downloadLink, double fileSizeMB) {
        // Validate book ID doesn't already exist
        if (findBook(id).isPresent()) {
            return "Error: This book ID already exists.";
        }

        // REFACTORING: Improved type checking with case-insensitive comparison
        Book book;
        if ("E-Book".equalsIgnoreCase(type) || "EBOOK".equalsIgnoreCase(type)) {
            book = new EBook(id, title, author, category, year, copies, 
                           downloadLink, fileSizeMB);
        } else {
            book = new PhysicalBook(id, title, author, category, year, copies);
        }

        // Add book and save
        Books.add(book);
        saveBooksToFile();
        
        return "Success: Book added successfully.";
    }

    /**
     * Updates an existing book's details.
     * REFACTORING: Clear error handling and validation
     * 
     * @param id Book ID to update
     * @param title New title
     * @param author New author
     * @param category New category
     * @param year New year
     * @param copies New number of copies
     * @return Success or error message
     */
    public String updateBook(String id, String title, String author,
                            String category, int year, int copies) {
        Optional<Book> opt = findBook(id);
        
        if (opt.isPresent()) {
            Book book = opt.get();
            book.updateDetails(title, author, category, year, copies);
            
            // TODO: Add E-Book URL and size update capability
            
            saveBooksToFile();
            return "Success: Book updated successfully.";
        }
        
        return "Error: Book not found.";
    }

    /**
     * Removes a book from the library.
     * 
     * @param id Book ID to remove
     * @return Success or error message
     */
    public String removeBook(String id) {
        Optional<Book> opt = findBook(id);
        
        if (opt.isPresent()) {
            Books.remove(opt.get());
            saveBooksToFile();
            return "Success: Book removed successfully.";
        }
        
        return "Error: Book not found.";
    }

    // ==================== Member Management ====================
    
    /**
     * Returns all members in the library.
     */
    public ArrayList<Member> getAllMembers() { 
        return Members; 
    }

    /**
     * Finds a member by their ID.
     * REFACTORING: Consistent with findBook() but returns null for backwards compatibility
     * 
     * @param id Member ID to search for
     * @return Member object or null if not found
     */
    public Member getMember(String id) {
        return Members.stream()
                      .filter(m -> m.getID().equalsIgnoreCase(id))
                      .findFirst()
                      .orElse(null);
    }

    /**
     * Adds a new member to the library.
     * 
     * @param id Member ID
     * @param name Member name
     * @param email Member email
     * @param phone Member phone
     * @return Success or error message
     */
    public String addMember(String id, String name, String email, String phone) {
        // Validate member ID doesn't already exist
        if (getMember(id) != null) {
            return "Error: Member ID already exists.";
        }
        
        Members.add(new Member(id, name, email, phone));
        saveMembersToFile();
        
        return "Success: Member added successfully.";
    }

    /**
     * Removes a member from the library.
     * 
     * @param id Member ID to remove
     * @return Success or error message
     */
    public String removeMember(String id) {
        Member member = getMember(id);
        
        if (member != null) {
            Members.remove(member);
            saveMembersToFile();
            return "Success: Member removed successfully.";
        }
        
        return "Error: Member not found.";
    }

    // ==================== Borrow / Return Operations ====================
    
    /**
     * Borrows a book to a member.
     * REFACTORING: Improved validation and error messages
     * 
     * @param bookId ID of the book to borrow
     * @param memberId ID of the borrowing member
     * @return Success, warning, or error message
     */
    public String borrowBook(String bookId, String memberId) {
        // Validate book exists
        Optional<Book> optBook = findBook(bookId);
        if (!optBook.isPresent()) {
            return "Error: Book not found.";
        }

        // Validate member exists
        Member member = getMember(memberId);
        if (member == null) {
            return "Error: Member not found.";
        }

        Book book = optBook.get();

        // Check if book is available
        if (!book.isAvailable()) {
            return "Warning: No copies available. Status: " + book.getStatus();
        }

        // Perform borrowing
        book.borrowBook(memberId);

        // Create loan record
        LoanRecord loan = new LoanRecord(bookId, memberId, 
                                        LocalDate.now(), book.getDueDate());
        LoanHistory.add(loan);

        // Save changes
        saveBooksToFile();
        saveLoansToFile();

        // REFACTORING: Improved success message with due date
        String dueInfo = (book.getDueDate() != null) 
            ? book.getDueDate().toString() 
            : "Digital Access";
            
        return String.format("Success: \"%s\" borrowed by %s. Due: %s",
                book.getTitle(), member.getName(), dueInfo);
    }

    /**
     * Returns a borrowed book to the library.
     * REFACTORING: Improved loan record handling
     * 
     * @param bookId ID of the book to return
     * @return Success or error message
     */
    public String returnBook(String bookId) {
        // Validate book exists
        Optional<Book> optBook = findBook(bookId);
        if (!optBook.isPresent()) {
            return "Error: Book not found.";
        }

        Book book = optBook.get();

        // Check active loans from LoanHistory (BorrowedBy in Book is not reliable with multiple copies)
        LoanRecord activeLoan = LoanHistory.stream()
                                           .filter(l -> l.getBookId().equalsIgnoreCase(bookId) && !l.isReturned())
                                           .findFirst()
                                           .orElse(null);

        if (activeLoan == null) {
            return "Warning: This book is not currently borrowed.";
        }

        // Mark one active loan as returned (returns one copy)
        activeLoan.setReturned(true);

        // Return the book
        book.returnBook();

        // Save changes
        saveBooksToFile();
        saveLoansToFile();

        return "Success: \"" + book.getTitle() + "\" returned successfully.";
    }

    /**
     * Returns the loan history.
     */
    public ArrayList<LoanRecord> getLoanHistory() { 
        return LoanHistory; 
    }

    /**
     * Returns a comma-separated list of members currently borrowing this book.
     * This uses LoanHistory, so it works correctly even when the book has multiple copies.
     *
     * @param bookId Book ID
     * @return e.g. "M01, M07" or "" if no active loans exist
     */
    public String getBorrowersForBook(String bookId) {
        if (bookId == null) return "";

        ArrayList<String> borrowers = new ArrayList<>();

        for (LoanRecord loan : LoanHistory) {
            if (loan == null) continue;
            if (!bookId.equalsIgnoreCase(loan.getBookId())) continue;
            if (loan.isReturned()) continue; // only active (not returned) loans

            String memberId = loan.getMemberId();
            if (memberId == null || memberId.trim().isEmpty()) continue;

            // keep unique list (no duplicates)
            if (!borrowers.contains(memberId)) {
                borrowers.add(memberId);
            }
        }

        if (borrowers.isEmpty()) return "";

        String result = borrowers.get(0);
        for (int i = 1; i < borrowers.size(); i++) {
            result += ", " + borrowers.get(i);
        }
        return result;
    }

    // ==================== File I/O - Books ====================
    
    /**
     * Loads books from file.
     * REFACTORING: Improved error handling and parsing logic
     * 
     * @return true if successful, false otherwise
     */
    public synchronized boolean loadBooksFromFile() {
        Path path = Paths.get(BOOKS_FILE);
        
        // Create file if it doesn't exist
        if (!Files.exists(path)) {
            return createEmptyFile(BOOKS_FILE);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKS_FILE))) {
            String line;
            
            while ((line = reader.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) continue;

                String[] data = line.split(";");

                // Parse E-Book
                if (data[0].equalsIgnoreCase("EBOOK") && data.length >= 9) {
                    try {
                        // Check if book already exists
                        if (findBook(data[1]).isEmpty()) {
                            Books.add(new EBook(
                                data[1],  // ID
                                data[2],  // Title
                                data[3],  // Author
                                data[4],  // Category
                                Integer.parseInt(data[5]),  // Year
                                Integer.parseInt(data[6]),  // Copies
                                data[7],  // Download link
                                Double.parseDouble(data[8])  // File size
                            ));
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Skipping malformed EBook: " + line);
                    }
                    continue;
                }

                // Parse Physical Book
                if (data.length >= 6) {
                    try {
                        PhysicalBook book = new PhysicalBook(
                            data[0],  // ID
                            data[1],  // Title
                            data[2],  // Author
                            data[3],  // Category
                            Integer.parseInt(data[4]),  // Year
                            Integer.parseInt(data[5])   // Copies
                        );

                        // Load borrowing data if available
                        if (data.length >= 7) {
                            book.loadFromData(data);
                        }

                        // Add if doesn't already exist
                        if (findBook(data[0]).isEmpty()) {
                            Books.add(book);
                        }

                    } catch (NumberFormatException e) {
                        System.err.println("Skipping malformed PhysicalBook: " + line);
                    }
                }
            }
            
            return true;
            
        } catch (IOException e) {
            System.err.println("Error loading books: " + e.getMessage());
            return false;
        }
    }

    /**
     * Saves books to file.
     * 
     * @return true if successful, false otherwise
     */
    public synchronized boolean saveBooksToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(BOOKS_FILE))) {
            for (Book b : Books) {
                writer.println(b.getToSave());
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error saving books: " + e.getMessage());
            return false;
        }
    }

    // ==================== File I/O - Members ====================
    
    /**
     * Loads members from file.
     * REFACTORING: Improved error handling
     * 
     * @return true if successful, false otherwise
     */
    public synchronized boolean loadMembersFromFile() {
        Path path = Paths.get(MEMBERS_FILE);
        
        // Create file if it doesn't exist
        if (!Files.exists(path)) {
            return createEmptyFile(MEMBERS_FILE);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(MEMBERS_FILE))) {
            String line;
            
            while ((line = reader.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) continue;

                String[] data = line.split(";");
                
                // Validate data and check for duplicates
                if (data.length >= 2 && getMember(data[0]) == null) {
                    String email = (data.length >= 3) ? data[2] : "";
                    String phone = (data.length >= 4) ? data[3] : "";
                    
                    Members.add(new Member(data[0], data[1], email, phone));
                }
            }
            
            return true;
            
        } catch (IOException e) {
            System.err.println("Error loading members: " + e.getMessage());
            return false;
        }
    }

    /**
     * Saves members to file.
     * 
     * @return true if successful, false otherwise
     */
    public synchronized boolean saveMembersToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(MEMBERS_FILE))) {
            for (Member m : Members) {
                writer.println(m.getToSave());
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error saving members: " + e.getMessage());
            return false;
        }
    }

    // ==================== File I/O - Loans ====================
    
    /**
     * Loads loan records from file.
     * 
     * @return true if successful, false otherwise
     */
    private synchronized boolean loadLoansFromFile() {
        Path path = Paths.get(LOANS_FILE);
        
        // Create file if it doesn't exist
        if (!Files.exists(path)) {
            return createEmptyFile(LOANS_FILE);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(LOANS_FILE))) {
            String line;
            
            while ((line = reader.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) continue;

                String[] data = line.split(";");
                
                if (data.length >= 4) {
                    LoanRecord loan = new LoanRecord(
                        data[0],  // Book ID
                        data[1],  // Member ID
                        LocalDate.parse(data[2]),  // Borrow date
                        LocalDate.parse(data[3])   // Due date
                    );
                    
                    // Load returned status if available
                    if (data.length > 4) {
                        loan.setReturned(Boolean.parseBoolean(data[4]));
                    }
                    
                    LoanHistory.add(loan);
                }
            }
            
            return true;
            
        } catch (IOException e) {
            System.err.println("Error loading loans: " + e.getMessage());
            return false;
        }
    }

    /**
     * Saves loan records to file.
     * 
     * @return true if successful, false otherwise
     */
    private synchronized boolean saveLoansToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOANS_FILE))) {
            for (LoanRecord loan : LoanHistory) {
                writer.println(loan.toString());
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error saving loans: " + e.getMessage());
            return false;
        }
    }

    // ==================== Utility Methods ====================
    
    /**
     * Creates an empty file.
     * REFACTORING: Centralized file creation logic
     * 
     * @param fileName Name of the file to create
     * @return true if successful, false otherwise
     */
    private boolean createEmptyFile(String fileName) {
        try {
            Files.createFile(Paths.get(fileName));
            return true;
        } catch (IOException e) {
            System.err.println("Failed to create file: " + fileName);
            return false;
        }
    }
}
