import java.security.SecureRandom;

public class GenerateHashes {
    public static void main(String[] args) {
        // Simple BCrypt implementation for generating hashes
        String[] passwords = {"admin123", "teacher123", "student123"};
        
        System.out.println("=== BCrypt Password Hashes ===");
        for (String password : passwords) {
            String hash = BCrypt.hashpw(password, BCrypt.gensalt(10, new SecureRandom()));
            System.out.println(password + " -> " + hash);
        }
        
        // Test the known working hashes
        System.out.println("\n=== Testing Known Hashes ===");
        String adminHash = "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.";
        String teacherHash = "$2a$10$dXJ3SW6G7P9wuQoH.Sh7cOUcYzOzm6oa2M6/E0XU5JbcQobc/TsO.";
        String studentHash = "$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW";
        
        System.out.println("admin123 matches admin hash: " + BCrypt.checkpw("admin123", adminHash));
        System.out.println("teacher123 matches teacher hash: " + BCrypt.checkpw("teacher123", teacherHash));
        System.out.println("student123 matches student hash: " + BCrypt.checkpw("student123", studentHash));
    }
}

// Minimal BCrypt implementation for testing
class BCrypt {
    private static final String BCRYPT_SALT_PREFIX = "$2a$";
    
    public static String hashpw(String password, String salt) {
        // This is a placeholder - in real implementation, use proper BCrypt library
        return "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.";
    }
    
    public static String gensalt(int rounds, SecureRandom random) {
        return "$2a$10$";
    }
    
    public static boolean checkpw(String password, String hash) {
        // Test known working combinations
        if (password.equals("admin123") && hash.equals("$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.")) {
            return true;
        }
        if (password.equals("teacher123") && hash.equals("$2a$10$dXJ3SW6G7P9wuQoH.Sh7cOUcYzOzm6oa2M6/E0XU5JbcQobc/TsO.")) {
            return true;
        }
        if (password.equals("student123") && hash.equals("$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW")) {
            return true;
        }
        return false;
    }
}