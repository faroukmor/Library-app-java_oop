/*
 * Login.java - Authentication Screen with Legendary Theme
 * Fixed: proper header layout, enlarged input fields
 */
package libraryApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Login screen with the Legendary Wooden Library theme.
 */
public class Login extends JFrame {

    // ==================== Theme Colors ====================
    private final Color COLOR_WOOD_DARK   = new Color(62, 39, 35);
    private final Color COLOR_WOOD_LIGHT  = new Color(141, 110, 99);
    private final Color COLOR_WOOD_MEDIUM = new Color(93, 64, 55);
    //private final Color COLOR_PAPER       = new Color(255, 253, 231);
    private final Color COLOR_GOLD        = new Color(212, 175, 55);
    private final Color COLOR_GOLD_BRIGHT = new Color(255, 215, 0);
    private final Color COLOR_SHELF_SHADOW= new Color(40, 25, 20);
    private final Color COLOR_ERROR       = new Color(192, 57, 43);
    private final Color COLOR_SUCCESS     = new Color(76, 175, 80);

    // ==================== Components ====================
    private JTextField    txtUsername;
    private JPasswordField txtPassword;
    private JLabel lblUser, lblPass, lblTitle, lblSubtitle, lblMessage;
    private JButton btnLogin, btnSignup, btnLang;

    private boolean isArabic = false;

    public Login() {
        initializeLogin();
    }

    private void initializeLogin() {
        setTitle("Library Access - The Grand Library");
        // MUST be called before the frame is made displayable (before setVisible / pack)
        setUndecorated(false);
        setSize(900, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(false);

        LibraryBackgroundPanel bgPanel = new LibraryBackgroundPanel();
        bgPanel.setLayout(new GridBagLayout());

        JPanel loginCard = createLoginCard();
        bgPanel.add(loginCard, new GridBagConstraints());

        add(bgPanel);

        updateLanguageTexts();

        // ESC closes
        getRootPane().registerKeyboardAction(
            e -> dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        // Show window with OS title bar buttons
        setVisible(true);
    }

    private JPanel createLoginCard() {
        JPanel loginCard = new JPanel(new BorderLayout(0, 10)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 45));
                g2.fillRoundRect(8, 10, getWidth() - 16, getHeight() - 16, 22, 22);
                g2.setColor(new Color(255, 253, 231, 245));
                g2.fillRoundRect(0, 0, getWidth() - 16, getHeight() - 16, 22, 22);
                g2.setColor(new Color(COLOR_GOLD.getRed(), COLOR_GOLD.getGreen(), COLOR_GOLD.getBlue(), 210));
                g2.setStroke(new BasicStroke(3f));
                g2.drawRoundRect(0, 0, getWidth() - 16, getHeight() - 16, 22, 22);
                g2.dispose();
            }
        };
        loginCard.setOpaque(false);
        loginCard.setBorder(BorderFactory.createEmptyBorder(35, 55, 35, 55));
        loginCard.setPreferredSize(new Dimension(500, 520));

        // ---- HEADER: FIX — use a dedicated JPanel for title + subtitle ----
        // Previously both lblTitle and lblSubtitle were added to BorderLayout.NORTH,
        // causing lblSubtitle to overwrite lblTitle. Now they share a BoxLayout header.
        lblTitle = new JLabel("THE GRAND LIBRARY", SwingConstants.CENTER);
        lblTitle.setForeground(COLOR_WOOD_DARK);
        lblTitle.setFont(new Font("Serif", Font.BOLD, 36));

        lblSubtitle = new JLabel("Legendary Edition", SwingConstants.CENTER);
        lblSubtitle.setForeground(COLOR_WOOD_MEDIUM);
        lblSubtitle.setFont(new Font("Serif", Font.ITALIC, 16));

        JPanel headerText = new JPanel();
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));
        headerText.setOpaque(false);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerText.add(lblTitle);
        headerText.add(Box.createVerticalStrut(6));
        headerText.add(lblSubtitle);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        headerPanel.add(headerText, BorderLayout.CENTER);

        // ---- FORM ----
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints fgbc = new GridBagConstraints();
        fgbc.insets = new Insets(12, 10, 12, 10);
        fgbc.fill = GridBagConstraints.HORIZONTAL;
        fgbc.anchor = GridBagConstraints.WEST;

        lblUser = new JLabel("Username:");
        lblUser.setForeground(COLOR_WOOD_DARK);
        lblUser.setFont(new Font("Serif", Font.BOLD, 16));
        // FIX: use wider columns (30) and explicit preferred size for visibility
        txtUsername = createPaperField(30);

        lblPass = new JLabel("Password:");
        lblPass.setForeground(COLOR_WOOD_DARK);
        lblPass.setFont(new Font("Serif", Font.BOLD, 16));
        txtPassword = new JPasswordField(30);
        stylePasswordField(txtPassword);

        fgbc.gridx = 0; fgbc.gridy = 0; fgbc.weightx = 0; form.add(lblUser, fgbc);
        fgbc.gridx = 1; fgbc.weightx = 1.0;                form.add(txtUsername, fgbc);
        fgbc.gridx = 0; fgbc.gridy = 1; fgbc.weightx = 0; form.add(lblPass, fgbc);
        fgbc.gridx = 1; fgbc.weightx = 1.0;                form.add(txtPassword, fgbc);

        // ---- BUTTONS ----
        JPanel btnRow = new JPanel(new GridLayout(1, 2, 20, 0));
        btnRow.setOpaque(false);
        btnRow.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        btnLogin  = createWoodenButton("Login");
        btnSignup = createWoodenButton("Sign Up");
        btnLogin.addActionListener(e  -> performLogin());
        btnSignup.addActionListener(e -> performSignup());
        btnRow.add(btnLogin);
        btnRow.add(btnSignup);

        // ---- CENTER WRAPPER ----
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(form,   BorderLayout.CENTER);
        centerWrapper.add(btnRow, BorderLayout.SOUTH);

        // ---- BOTTOM (lang + message) ----
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        btnLang = new JButton("English / العربية");
        btnLang.setBackground(COLOR_WOOD_DARK);
        btnLang.setForeground(COLOR_GOLD);
        btnLang.setFont(new Font("Serif", Font.PLAIN, 13));
        btnLang.setFocusPainted(false);
        btnLang.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnLang.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLang.addActionListener(e -> toggleLanguage());

        lblMessage = new JLabel("", SwingConstants.CENTER);
        lblMessage.setForeground(COLOR_ERROR);
        lblMessage.setFont(new Font("Serif", Font.ITALIC, 14));

        bottomPanel.add(btnLang,    BorderLayout.WEST);
        bottomPanel.add(lblMessage, BorderLayout.CENTER);

        // ---- ASSEMBLE ----
        loginCard.add(headerPanel,    BorderLayout.NORTH);
        loginCard.add(centerWrapper,  BorderLayout.CENTER);
        loginCard.add(bottomPanel,    BorderLayout.SOUTH);

        // Enter key submits
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) performLogin();
            }
        });

        return loginCard;
    }

    // ==================== Background Panel ====================

    private class LibraryBackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            GradientPaint gp = new GradientPaint(
                0, 0, COLOR_WOOD_DARK,
                getWidth(), getHeight(), COLOR_WOOD_LIGHT
            );
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            g2d.setColor(COLOR_SHELF_SHADOW);
            for (int y = 0; y < getHeight(); y += 200) {
                g2d.fillRect(0, y, getWidth(), 15);
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fillRect(0, y + 15, getWidth(), 5);
                g2d.setColor(COLOR_SHELF_SHADOW);
            }
        }
    }

    // ==================== Styling Helpers ====================

    private JTextField createPaperField(int cols) {
        JTextField f = new JTextField(cols);
        f.setBackground(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_WOOD_DARK),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        f.setFont(new Font("Serif", Font.PLAIN, 16));
        f.setPreferredSize(new Dimension(280, 42));
        return f;
    }

    private void stylePasswordField(JPasswordField f) {
        f.setBackground(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_WOOD_DARK),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        f.setFont(new Font("Serif", Font.PLAIN, 16));
        f.setEchoChar('●');
        f.setPreferredSize(new Dimension(280, 42));
    }

    private JButton createWoodenButton(String text) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = hovered
                    ? new GradientPaint(0, 0, COLOR_WOOD_MEDIUM, 0, getHeight(), COLOR_WOOD_LIGHT)
                    : new GradientPaint(0, 0, COLOR_WOOD_LIGHT,  0, getHeight(), COLOR_WOOD_DARK);
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                if (hovered) {
                    g2d.setColor(new Color(255, 215, 0, 100));
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
                }
                g2d.setColor(COLOR_GOLD);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2d.setColor(hovered ? COLOR_GOLD_BRIGHT : Color.WHITE);
                FontMetrics fm = g2d.getFontMetrics();
                g2d.setFont(new Font("Serif", Font.BOLD, 16));
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2d.drawString(getText(), x, y);
            }

            @Override
            public void addNotify() {
                super.addNotify();
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                });
            }
        };

        btn.setFont(new Font("Serif", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 48));
        return btn;
    }

    

    // ==================== Logic ====================

    private void toggleLanguage() {
        isArabic = !isArabic;
        updateLanguageTexts();
    }

    private void updateLanguageTexts() {
        if (isArabic) {
            setTitle("دخول المكتبة");
            lblTitle.setText("المكتبة المركزية");
            lblSubtitle.setText("الإصدار الأسطوري");
            lblUser.setText("اسم المستخدم:");
            lblPass.setText("كلمة المرور:");
            btnLogin.setText("دخول");
            btnSignup.setText("إنشاء حساب");
            btnLang.setText("English");
        } else {
            setTitle("Library Access - The Grand Library");
            lblTitle.setText("THE GRAND LIBRARY");
            lblSubtitle.setText("Legendary Edition");
            lblUser.setText("Username:");
            lblPass.setText("Password:");
            btnLogin.setText("Login");
            btnSignup.setText("Sign Up");
            btnLang.setText("العربية");
        }
    }

    private void performLogin() {
        String user = txtUsername.getText();
        String pass = new String(txtPassword.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            lblMessage.setText(isArabic ? "الحقول مطلوبة" : "Fields required");
            lblMessage.setForeground(COLOR_ERROR);
            return;
        }

        String storedPass = searchUser(user);
        if (storedPass != null && storedPass.equals(pass)) {
            lblMessage.setText(isArabic ? "جاري الدخول..." : "Logging in...");
            lblMessage.setForeground(COLOR_SUCCESS);
            // Move to main UI (no opacity animation; keep it simple and reliable)
            SwingUtilities.invokeLater(() -> {
                dispose();
                new GUI();
            });
        } else {
            lblMessage.setText(isArabic ? "بيانات خاطئة" : "Invalid Credentials");
            lblMessage.setForeground(COLOR_ERROR);
        }
    }

    private void performSignup() {
        String user = txtUsername.getText();
        String pass = new String(txtPassword.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            lblMessage.setText(isArabic ? "الحقول مطلوبة" : "Fields required");
            lblMessage.setForeground(COLOR_ERROR);
            return;
        }

        if (searchUser(user) != null) {
            lblMessage.setText(isArabic ? "المستخدم موجود مسبقاً" : "User already exists");
            lblMessage.setForeground(COLOR_ERROR);
            return;
        }

        try (FileWriter writer = new FileWriter("Users.txt", true)) {
            writer.write(user + "\n" + encrypt(pass) + "\n");
            lblMessage.setText(isArabic ? "تم التسجيل بنجاح" : "Signed up successfully!");
            lblMessage.setForeground(COLOR_SUCCESS);
            txtUsername.setText("");
            txtPassword.setText("");
        } catch (IOException e) {
            lblMessage.setText("Error: " + e.getMessage());
            lblMessage.setForeground(COLOR_ERROR);
        }
    }

    private String searchUser(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader("Users.txt"))) {
            String u;
            while ((u = reader.readLine()) != null) {
                if (u.equalsIgnoreCase(username)) {
                    return decrypt(reader.readLine());
                }
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    private String encrypt(String password) {
        int key = 9;
        StringBuilder encrypted = new StringBuilder();
        for (int i = 0; i < password.length(); i++) {
            encrypted.append((int) password.charAt(i) * key);
            if (i < password.length() - 1) encrypted.append("-");
        }
        return encrypted.toString();
    }

    private String decrypt(String encrypted) {
        if (encrypted == null) return "";
        int key = 9;
        StringBuilder decrypted = new StringBuilder();
        for (String part : encrypted.split("-")) {
            try { decrypted.append((char) (Integer.parseInt(part) / key)); }
            catch (Exception e) { return ""; }
        }
        return decrypted.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) { }
            new Login();
        });
    }
}
