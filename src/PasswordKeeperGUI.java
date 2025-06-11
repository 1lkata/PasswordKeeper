import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class PasswordKeeperGUI {
    private PasswordManager manager;
    private JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField websiteField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField searchField;
    private static final Color BG_COLOR = new Color(245, 245, 245);
    private static final Color ACCENT_COLOR = new Color(70, 130, 180);
    private static final Color PANEL_COLOR = Color.WHITE;
    private static final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);

    public PasswordKeeperGUI() {
        showLoginScreen();
    }

    // Method for creating a styled JLabel with the given text
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(Color.BLACK);
        return label;
    }

    // Method for creating a styled JTextField with specified columns
    private JTextField createStyledTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(LABEL_FONT);
        field.setBorder(new LineBorder(ACCENT_COLOR, 1));
        return field;
    }

    // Method for creating a styled JPasswordField with specified columns
    private JPasswordField createStyledPasswordField(int columns) {
        JPasswordField field = new JPasswordField(columns);
        field.setFont(LABEL_FONT);
        field.setBorder(new LineBorder(ACCENT_COLOR, 1));
        return field;
    }

    // Method for creating a styled JCheckBox with text and selection state
    private JCheckBox createStyledCheckBox(String text, boolean selected) {
        JCheckBox checkBox = new JCheckBox(text, selected);
        checkBox.setFont(LABEL_FONT);
        checkBox.setBackground(BG_COLOR);
        return checkBox;
    }

    // Method for creating a styled JButton with text and hover effect
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(ACCENT_COLOR);
        button.setForeground(Color.WHITE);
        button.setBorder(new LineBorder(ACCENT_COLOR, 2, true));
        button.setFocusPainted(false);
        // Add hover effect //ChatGPT suggested improvement
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(100, 149, 237)); // Lighter blue on hover
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(ACCENT_COLOR); // Revert to original color
            }
        });
        return button;
    }

    // Method for creating a styled JPanel with GridBagLayout
    private JPanel createStyledPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(new LineBorder(ACCENT_COLOR, 1));
        return panel;
    }

    // Method for adding a component to a GridBagLayout with specified constraints
    private void addToGridBag(Container container, Component comp, int gridx, int gridy, int gridwidth) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        container.add(comp, gbc);
    }

    private void showLoginScreen() {
        JDialog loginDialog = new JDialog((Frame) null, "Password Keeper - Login/Sign Up", true);
        loginDialog.setSize(300, 220);
        loginDialog.setLocationRelativeTo(null);
        loginDialog.setLayout(new GridBagLayout());
        loginDialog.getContentPane().setBackground(BG_COLOR);

        JLabel passwordLabel = createStyledLabel("Master Password:");
        JPasswordField passwordInput = createStyledPasswordField(15);
        JButton loginButton = createStyledButton("Login");
        JButton signupButton = createStyledButton("Sign Up");

        addToGridBag(loginDialog, passwordLabel, 0, 0, 1);
        addToGridBag(loginDialog, passwordInput, 1, 0, 1);
        addToGridBag(loginDialog, loginButton, 0, 1, 2);
        addToGridBag(loginDialog, signupButton, 0, 2, 2);

        loginButton.addActionListener(e -> {
            String inputPassword = new String(passwordInput.getPassword());
            try {
                manager = new PasswordManager(inputPassword);
                manager.loadFromFile();
                loginDialog.dispose();
                initializeMainGUI();
                
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(loginDialog, "Failed to load data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        signupButton.addActionListener(e -> {
            String inputPassword = new String(passwordInput.getPassword());
            if (inputPassword.isEmpty()) {
                JOptionPane.showMessageDialog(loginDialog, "Password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            manager = new PasswordManager(inputPassword);
            try {
                manager.saveToFile();
                loginDialog.dispose();
                initializeMainGUI();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(loginDialog, "Failed to create data file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        loginDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        loginDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        loginDialog.setVisible(true);
    }

    private void initializeMainGUI() {
        frame = new JFrame("Password Keeper");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout(10, 10));
        frame.getContentPane().setBackground(BG_COLOR);

        String[] columns = {"Website", "Username", "Password"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setGridColor(ACCENT_COLOR);
        table.setBackground(PANEL_COLOR);
        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        refreshTable();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = createStyledPanel();
        JLabel websiteLabel = createStyledLabel("Website:");
        websiteField = createStyledTextField(15);
        JLabel usernameLabel = createStyledLabel("Username:");
        usernameField = createStyledTextField(15);
        JLabel passwordLabel = createStyledLabel("Password:");
        passwordField = createStyledPasswordField(15);
        JLabel searchLabel = createStyledLabel("Search Website:");
        searchField = createStyledTextField(15);

        addToGridBag(inputPanel, websiteLabel, 0, 0, 1);
        addToGridBag(inputPanel, websiteField, 1, 0, 1);
        addToGridBag(inputPanel, usernameLabel, 0, 1, 1);
        addToGridBag(inputPanel, usernameField, 1, 1, 1);
        addToGridBag(inputPanel, passwordLabel, 0, 2, 1);
        addToGridBag(inputPanel, passwordField, 1, 2, 1);
        addToGridBag(inputPanel, searchLabel, 0, 3, 1);
        addToGridBag(inputPanel, searchField, 1, 3, 1);

        frame.add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setBackground(BG_COLOR);
        JButton addButton = createStyledButton("Add Entry");
        JButton updateButton = createStyledButton("Update Entry");
        JButton deleteButton = createStyledButton("Delete Entry");
        JButton searchButton = createStyledButton("Search");
        JButton generateButton = createStyledButton("Generate Password");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(generateButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addEntry());
        updateButton.addActionListener(e -> updateEntry());
        deleteButton.addActionListener(e -> deleteEntry());
        searchButton.addActionListener(e -> searchEntry());
        generateButton.addActionListener(e -> showPasswordGenerator());

        // Save data when window closes //ChatGPT suggested improvement
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    manager.saveToFile();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Failed to save data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        frame.setVisible(true);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (PasswordEntry entry : manager.getAllEntries()) {
            tableModel.addRow(new Object[]{entry.getWebsite(), entry.getUsername(), entry.getPassword()});
        }
    }

    private void addEntry() {
        String website = websiteField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        if (website.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "All fields must be filled.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        PasswordEntry entry = new PasswordEntry(website, username, password);
        manager.addEntry(entry);
        try {
            manager.saveToFile();
            refreshTable();
            clearFields();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Failed to save entry: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateEntry() {
        String website = websiteField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        if (website.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "All fields must be filled.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        PasswordEntry existing = manager.getEntry(website);
        if (existing == null) {
            JOptionPane.showMessageDialog(frame, "Website not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        PasswordEntry newEntry = new PasswordEntry(website, username, password);
        manager.updateEntry(website, newEntry);
        try {
            manager.saveToFile();
            refreshTable();
            clearFields();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Failed to update entry: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteEntry() {
        String website = websiteField.getText().trim();
        if (website.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Enter a website to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        PasswordEntry entry = manager.getEntry(website);
        if (entry == null) {
            JOptionPane.showMessageDialog(frame, "Website not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        manager.deleteEntry(website);
        try {
            manager.saveToFile();
            refreshTable();
            clearFields();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Failed to delete entry: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchEntry() {
        String website = searchField.getText().trim().toLowerCase();
        if (website.isEmpty()) {
            refreshTable();
            return;
        }
        PasswordEntry entry = manager.getEntry(website);
        tableModel.setRowCount(0);
        if (entry != null) {
            tableModel.addRow(new Object[]{entry.getWebsite(), entry.getUsername(), entry.getPassword()});
        } else {
            JOptionPane.showMessageDialog(frame, "No entry found for website.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showPasswordGenerator() {
        JDialog generatorDialog = new JDialog(frame, "Generate Password", true);
        generatorDialog.setSize(300, 250);
        generatorDialog.setLocationRelativeTo(frame);
        generatorDialog.setLayout(new GridBagLayout());
        generatorDialog.getContentPane().setBackground(BG_COLOR);

        JLabel lengthLabel = createStyledLabel("Length:");
        JTextField lengthField = createStyledTextField(10);
        lengthField.setText("12");
        JCheckBox lettersCheck = createStyledCheckBox("Include Letters", true);
        JCheckBox numbersCheck = createStyledCheckBox("Include Numbers", true);
        JCheckBox symbolsCheck = createStyledCheckBox("Include Symbols", true);
        JButton generateButton = createStyledButton("Generate");
        JTextField resultField = createStyledTextField(15);
        resultField.setEditable(false);

        addToGridBag(generatorDialog, lengthLabel, 0, 0, 1);
        addToGridBag(generatorDialog, lengthField, 1, 0, 1);
        addToGridBag(generatorDialog, lettersCheck, 0, 1, 2);
        addToGridBag(generatorDialog, numbersCheck, 0, 2, 2);
        addToGridBag(generatorDialog, symbolsCheck, 0, 3, 2);
        addToGridBag(generatorDialog, generateButton, 0, 4, 2);
        addToGridBag(generatorDialog, resultField, 0, 5, 2);

        generateButton.addActionListener(e -> {
            try {
                int length = Integer.parseInt(lengthField.getText());
                if (length <= 0) {
                    JOptionPane.showMessageDialog(generatorDialog, "Length must be positive.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String password = manager.generatePassword(length, lettersCheck.isSelected(), numbersCheck.isSelected(), symbolsCheck.isSelected());
                resultField.setText(password);
                passwordField.setText(password);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(generatorDialog, "Invalid length.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(generatorDialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        generatorDialog.setVisible(true);
    }

    private void clearFields() {
        websiteField.setText("");
        usernameField.setText("");
        passwordField.setText("");
        searchField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PasswordKeeperGUI::new);
    }
}
