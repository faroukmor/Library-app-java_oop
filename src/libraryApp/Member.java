package libraryApp;

/**
 * Represents a library member with contact information.
 * 
 * REFACTORING IMPROVEMENTS:
 * ✓ Added comprehensive JavaDoc documentation
 * ✓ Added input validation in constructor
 * ✓ Improved null handling
 * ✓ Better code organization
 * ✓ Added validation in setters
 */
public class Member {
    
    // ==================== Fields ====================
    private String ID;
    private String Name;
    private String Email;
    private String Phone;

    /**
     * Full constructor for creating a member with all details.
     * REFACTORING: Added null checks and default values
     * 
     * @param id Member unique identifier
     * @param name Member name
     * @param email Member email address
     * @param phone Member phone number
     */
    public Member(String id, String name, String email, String phone) {
        this.ID    = (id != null) ? id : "";
        this.Name  = (name != null) ? name : "";
        this.Email = (email != null) ? email : "";
        this.Phone = (phone != null) ? phone : "";
    }

    /**
     * Simplified constructor for creating a member with minimal details.
     * Email and phone will be set to empty strings.
     * 
     * @param id Member unique identifier
     * @param name Member name
     */
    public Member(String id, String name) {
        this(id, name, "", "");
    }
    
    // ==================== Getters and Setters ====================
    
    public String getID() { 
        return ID; 
    }
    
    public void setID(String ID) { 
        if (ID != null && !ID.trim().isEmpty()) {
            this.ID = ID; 
        }
    }

    public String getName() { 
        return Name; 
    }
    
    // REFACTORING: Added validation to prevent null names
    public void setName(String n) { 
        if (n != null && !n.trim().isEmpty()) {
            this.Name = n; 
        }
    }

    public String getEmail() { 
        return Email; 
    }
    
    // REFACTORING: Allow empty email but prevent null
    public void setEmail(String e) { 
        this.Email = (e != null) ? e : ""; 
    }

    public String getPhone() { 
        return Phone; 
    }
    
    // REFACTORING: Allow empty phone but prevent null
    public void setPhone(String p) { 
        this.Phone = (p != null) ? p : ""; 
    }

    // ==================== Utility Methods ====================
    
    /**
     * Returns a semicolon-delimited string for file storage.
     * REFACTORING: Improved formatting consistency
     */
    public String getToSave() { 
        return String.join(";", ID, Name, Email, Phone); 
    }
    
    /**
     * Returns a human-readable string representation.
     * REFACTORING: Improved formatting and null handling
     */
    public String asString() {
        // REFACTORING: Use N/A for empty fields to improve readability
        String displayEmail = (Email == null || Email.isEmpty()) ? "N/A" : Email;
        String displayPhone = (Phone == null || Phone.isEmpty()) ? "N/A" : Phone;
        
        return String.format("Member ID: %s | Name: %s | Email: %s | Phone: %s",
                ID, Name, displayEmail, displayPhone);
    }
}
