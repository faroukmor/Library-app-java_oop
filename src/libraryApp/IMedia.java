package libraryApp;

/**
 * Interface defining contracts for digital media properties.
 * Implemented by classes that represent digital content (e.g., E-Books).
 * 
 * REFACTORING IMPROVEMENTS:
 * ✓ Added comprehensive JavaDoc documentation
 * ✓ Removed redundant 'public' modifiers (interfaces are public by default)
 * ✓ Improved method naming clarity
 */
public interface IMedia {
    
    /**
     * Returns the unique media identifier.
     * 
     * @return Media ID
     */
    String getMediaID();
    
    /**
     * Returns the URL or download link for the digital media.
     * 
     * @return Media URL
     */
    String getURL();
    
    /**
     * Returns the file size in megabytes.
     * 
     * @return File size in MB
     */
    double getSizeMB();
}
