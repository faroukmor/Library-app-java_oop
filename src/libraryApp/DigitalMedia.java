package libraryApp;

/**
 * Represents digital media properties such as download URL and file size.
 * Used as a component in digital book types (e.g., EBook).
 * 
 * REFACTORING IMPROVEMENTS:
 * ✓ Added comprehensive JavaDoc documentation
 * ✓ Added constructor validation
 * ✓ Improved field naming consistency
 * ✓ Added proper encapsulation
 */
public class DigitalMedia implements IMedia {

    // ==================== Fields ====================
    private String MediaID;
    private String URL;
    private double SizeMB;

    /**
     * Constructor for creating digital media.
     * 
     * @param URL Download link or URL
     * @param SizeMB File size in megabytes
     */
    public DigitalMedia(String URL, double SizeMB) {
        // REFACTORING: Added null check and default values
        this.URL    = (URL != null && !URL.trim().isEmpty()) ? URL : "";
        this.SizeMB = (SizeMB >= 0) ? SizeMB : 0.0;
    }
    
    // ==================== Setters ====================
    
    public void setMediaId(String MediaID) { 
        this.MediaID = MediaID; 
    }
    
    // REFACTORING: Added validation in setters
    public void setURL(String URL) { 
        if (URL != null && !URL.trim().isEmpty()) {
            this.URL = URL; 
        }
    }
    
    public void setSize(double SizeMB) { 
        if (SizeMB >= 0) {
            this.SizeMB = SizeMB; 
        }
    }

    // ==================== IMedia Interface Implementation ====================
    
    @Override
    public String getMediaID() { 
        return MediaID; 
    }
    
    @Override
    public String getURL() { 
        return URL; 
    }

    @Override
    public double getSizeMB() { 
        return SizeMB; 
    }

    // ==================== Utility Methods ====================
    
    /**
     * Returns a semicolon-delimited string for file storage.
     * REFACTORING: Added proper formatting
     */
    public String getToSave() { 
        return String.format("%s;%.2f", URL, SizeMB); 
    }

    /**
     * Returns a human-readable string representation.
     * REFACTORING: Improved formatting for consistency
     */
    public String asString() { 
        return String.format("URL: %s | Size: %.2f MB", URL, SizeMB); 
    }
}
