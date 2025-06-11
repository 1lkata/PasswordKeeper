import java.io.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;

class PasswordManager {
    private final ArrayList<PasswordEntry> entries;
    private final HashMap<String, PasswordEntry>  passwordMap;
    private final String masterPassword;
    private static final String FILE_PATH = "passwords.dat";

    public PasswordManager(String masterPassword) {
        this.masterPassword = masterPassword;
        entries = new ArrayList<>();
        passwordMap = new HashMap<>();
    }

    public void addEntry(PasswordEntry entry) {
        entries.add(entry);
        passwordMap.put(entry.getWebsite().toLowerCase(), entry);
    }

    public PasswordEntry getEntry(String website) {
        return passwordMap.get(website.toLowerCase());
    }

    public void updateEntry(String website, PasswordEntry newEntry) {
        PasswordEntry existing = passwordMap.get(website.toLowerCase());
        if (existing != null) {
            int index = entries.indexOf(existing);
            entries.set(index, newEntry);
            passwordMap.put(newEntry.getWebsite().toLowerCase(), newEntry);
        }
    }

    public void deleteEntry(String website) {
        PasswordEntry entry = passwordMap.remove(website.toLowerCase());
        if (entry != null) {
            entries.remove(entry);
        }
    }

    public ArrayList<PasswordEntry> getAllEntries() {
        return new ArrayList<>(entries);
    }

    // Encryption using XOR with master password(ChatGPT taught me how to implement it)
    public String encrypt(String data, String key) {
        StringBuilder encrypted = new StringBuilder();
        for (int i = 0; i < data.length(); i++) {
            encrypted.append((char) (data.charAt(i) ^ key.charAt(i % key.length())));
        }
        return encrypted.toString();
    }

    // Decryption
    public String decrypt(String data, String key) {
        // XOR decryption is the same as encryption
        return encrypt(data, key);
    }

    public void saveToFile() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write(encrypt("PasswordKeeperData\n",masterPassword);//*
            for (PasswordEntry entry : entries) {
                String encryptedPassword = encrypt(entry.getPassword(), masterPassword);
                writer.write(entry.getWebsite() + "," + entry.getUsername() + "," + encryptedPassword + "\n");
            }
        }
    }
    public void loadFromFile() throws IOException {
        entries.clear();
        passwordMap.clear();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return; // No file yet, start fresh
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String header = reader.readLine();
            //Check correctness of file with *
            if (header == null || !header.equals(encrypt("PasswordKeeperData",masterPassword))) {
                throw new IOException("Invalid file format or corrupted data");
            }
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 3);
                if (parts.length == 3) {
                    String website = parts[0];
                    String username = parts[1];
                    String decryptedPassword = decrypt(parts[2], masterPassword);
                    PasswordEntry entry = new PasswordEntry(website, username, decryptedPassword);
                    entries.add(entry);
                    passwordMap.put(website.toLowerCase(), entry);
                }
            }
        } catch (Exception e) {
            throw new IOException("Failed to decrypt data. Incorrect master password?");
        }
    }
    public String generatePassword(int length, boolean includeLetters, boolean includeNumbers, boolean includeSymbols) {
        StringBuilder charPool = getCharPool(includeLetters, includeNumbers, includeSymbols);

        SecureRandom random = new SecureRandom();//Use of SecureRandom instead of Random, as it is cryptographically stronger
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(charPool.length());
            password.append(charPool.charAt(index));
        }
        return password.toString();
    }
    //IntelliJ suggested creating a static getCharPool method instead of implementing its logic directly into generatePassword
    private static StringBuilder getCharPool(boolean includeLetters, boolean includeNumbers, boolean includeSymbols) {
        String letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String numbers = "0123456789";
        String symbols = "!@#$%^&*()-_=+";
        StringBuilder charPool = new StringBuilder();
        if (includeLetters) charPool.append(letters);
        if (includeNumbers) charPool.append(numbers);
        if (includeSymbols) charPool.append(symbols);

        if (charPool.isEmpty()) {
            throw new IllegalArgumentException("At least one character type must be selected");
        }
        return charPool;
    }
}
