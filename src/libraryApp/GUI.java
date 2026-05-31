
package libraryApp;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.*;

public class GUI extends JFrame {

    // ── Backend ───────────────────────────────────────────────────────────────
    private final LibraryManagement MyLibrary = LibraryManagement.getInstance();

    // ── Palette ───────────────────────────────────────────────────────────────
    private final Color C_DARK   = new Color(62,  39,  35);
    private final Color C_MED    = new Color(93,  64,  55);
    private final Color C_LIGHT  = new Color(141, 110, 99);
    private final Color C_PAPER  = new Color(255, 253, 231);
    private final Color C_PAPER2 = new Color(245, 240, 220);
    private final Color C_GOLD   = new Color(212, 175, 55);
    //private final Color C_GOLDB  = new Color(255, 215, 0);
    private final Color C_INK    = new Color(33,  33,  33);
    private final Color C_SHADOW = new Color(40,  25,  20);
    private final Color C_OK     = new Color(76,  175, 80);
    private final Color C_ERR    = new Color(244, 67,  54);
    private final Color C_WARN   = new Color(255, 152, 0);
    private final Color C_BLUE   = new Color(33,  150, 243);

    // ── Core UI ───────────────────────────────────────────────────────────────
    private JPanel       cardPanel;
    private CardLayout   cardLayout;
    private JLabel       lblWelcome;
    private ToastManager toastMgr;
    private boolean      isArabic = false;

    // Sidebar
    private JButton btnDash, btnManage, btnCirc, btnMembers,btnLang;

    // ── Dashboard ─────────────────────────────────────────────────────────────
    private JLabel            lblDashTitle, lblSearch;
    private JTextField        txtSearch;
    private JTable            bookTable;
    private DefaultTableModel bookModel;
    private JLabel            lblStatBooks, lblStatMembers;

    // ── Manage Books ──────────────────────────────────────────────────────────
    private JLabel     lblManTitle, lblID, lblTitleF, lblAuthor, lblCat, lblYear, lblCopies;
    private JTextField txtID, txtTitle, txtAuthor, txtCategory, txtYear, txtCopies;
    private JButton    btnAdd, btnEdit, btnDel, btnClear;
    private JToggleButton tglEBook;
    private JLabel        lblLink, lblSize;
    private JTextField    txtLink, txtSize;
    private JPanel        ebookPanel;

    // ── Circulation ───────────────────────────────────────────────────────────
    private JLabel     lblCircTitle;
    private JTextField txtBorrowBookID, txtBorrowMemID, txtRetBookID;
    private JButton    btnDoBorrow, btnDoReturn;
    private JTextArea  txtCircLog;

    // ── Members ───────────────────────────────────────────────────────────────
    private JLabel            lblMemTitle, lblRegNewMem;
    private JTextField        txtNewMemID, txtNewMemName, txtNewMemEmail;
    private JButton           btnRegMem, btnDelMem, btnMemLoans;
    private JTable            membersTable;
    private DefaultTableModel membersModel;

    // ==========================================================================
    //  Constructor
    // ==========================================================================
    public GUI() {
        applyLookAndFeel();
        MyLibrary.LoadDataFiles();
        buildUI();
        setVisible(true);
       // fadeIn();
    }

    // ==========================================================================
    //  Look & Feel
    // ==========================================================================
    private void applyLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo lf : UIManager.getInstalledLookAndFeels())
                if ("Nimbus".equals(lf.getName())) { UIManager.setLookAndFeel(lf.getClassName()); break; }
        } catch (Exception ignored) {}
        UIManager.put("Label.font",     new Font("Serif", Font.PLAIN, 14));
        UIManager.put("Button.font",    new Font("Serif", Font.BOLD,  13));
        UIManager.put("TextField.font", new Font("Serif", Font.PLAIN, 13));
        UIManager.put("Table.font",     new Font("SansSerif", Font.PLAIN, 13));
    }

    
    // ==========================================================================
    //  Build Main UI
    // ==========================================================================
    private void buildUI() {
        setTitle("The Grand Library System ");
        setSize(1200, 780);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        JPanel bg = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(C_DARK); g2.fillRect(0,0,getWidth(),getHeight());
                g2.setColor(C_SHADOW);
                for (int y=0;y<getHeight();y+=180){
                    g2.fillRect(0,y,getWidth(),10);
                    g2.setColor(new Color(0,0,0,35));
                    g2.fillRect(0,y+10,getWidth(),4);
                    g2.setColor(C_SHADOW);
                }
            }
        };

        JPanel sidebar = buildSidebar();
        cardPanel = new JPanel(cardLayout = new CardLayout());
        cardPanel.setOpaque(false);

        cardPanel.add(buildDashCard(),    "DASH");
        cardPanel.add(buildManageCard(),  "MANAGE");
        cardPanel.add(buildCircCard(),    "CIRC");
        cardPanel.add(buildMembersCard(), "MEMBERS");

        bg.add(sidebar,   BorderLayout.WEST);
        bg.add(cardPanel, BorderLayout.CENTER);

        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT,14,5));
        statusBar.setBackground(C_DARK);
        statusBar.setBorder(BorderFactory.createMatteBorder(1,0,0,0,C_GOLD));
        statusBar.setPreferredSize(new Dimension(0,30));
        JPanel root = new JPanel(new BorderLayout());
        root.add(bg,        BorderLayout.CENTER);
        root.add(statusBar, BorderLayout.SOUTH);
        setContentPane(root);

        toastMgr = new ToastManager();
        wireSidebar();
        updateTexts();
        updateSidebarWidth();
        refreshAll();
    }

    // ==========================================================================
    //  Sidebar
    // ==========================================================================
    private JPanel buildSidebar() {
        JPanel s = new JPanel();
        s.setLayout(new BoxLayout(s, BoxLayout.Y_AXIS));
        s.setBackground(C_DARK);
        // Sidebar width will be computed to fit the longest label (EN/AR).
        s.setPreferredSize(new Dimension(220,0));
        s.setBorder(BorderFactory.createMatteBorder(0,0,0,3,C_GOLD));

        lblWelcome = new JLabel("THE LIBRARY", SwingConstants.CENTER);
        lblWelcome.setForeground(C_GOLD);
        lblWelcome.setFont(new Font("Serif",Font.BOLD,22));
        lblWelcome.setAlignmentX(CENTER_ALIGNMENT);
        lblWelcome.setBorder(new EmptyBorder(22,0,22,0));
        lblWelcome.setMaximumSize(new Dimension(220,70));

        btnDash         = sideBtn("📋  Dashboard");
        btnManage       = sideBtn("📚  Manage Books");
        btnCirc         = sideBtn("🔄  Circulation");
        btnMembers      = sideBtn("👥  Members");
        btnLang         = sideBtn("🌐  EN / عربي");
        s.add(lblWelcome);
        for (JButton b : new JButton[]{btnDash,btnManage,btnCirc,
                                        btnMembers})
            { s.add(b); s.add(Box.createVerticalStrut(5)); }
        s.add(Box.createVerticalGlue());
        s.add(btnLang);  s.add(Box.createVerticalStrut(10));
        return s;
    }

    private JButton sideBtn(String txt) {
        JButton b = new JButton(txt) {
            boolean hov = false;
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                if (hov) { g2.setColor(C_MED); g2.fillRoundRect(4,2,getWidth()-8,getHeight()-4,8,8); }
                g2.setColor(hov?C_GOLD:C_LIGHT);
                g2.setFont(new Font("SansSerif",hov?Font.BOLD:Font.PLAIN,13));
                FontMetrics fm=g2.getFontMetrics();
                g2.drawString(getText(),14,(getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
            @Override public void addNotify(){
                super.addNotify();
                addMouseListener(new MouseAdapter(){
                    @Override public void mouseEntered(MouseEvent e){hov=true;repaint();}
                    @Override public void mouseExited (MouseEvent e){hov=false;repaint();}
                });
            }
        };
        b.setFont(new Font("SansSerif",Font.PLAIN,13));
        b.setFocusPainted(false); b.setBorderPainted(false); b.setContentAreaFilled(false);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setMaximumSize(new Dimension(220,40)); b.setAlignmentX(LEFT_ALIGNMENT);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void updateSidebarWidth() {
        if (btnDash == null) return;
        Container sidebar = btnDash.getParent();
        if (!(sidebar instanceof JPanel)) return;

        int pad = 46; // left padding + breathing room
        int max = 0;

        for (JButton b : new JButton[]{btnDash, btnManage, btnCirc, btnMembers, btnLang}) {
            if (b == null) continue;
            FontMetrics fm = b.getFontMetrics(b.getFont());
            max = Math.max(max, fm.stringWidth(b.getText()));
        }
        if (lblWelcome != null) {
            FontMetrics fm = lblWelcome.getFontMetrics(lblWelcome.getFont());
            max = Math.max(max, fm.stringWidth(lblWelcome.getText()));
        }

        int w = Math.max(180, max + pad);
        sidebar.setPreferredSize(new Dimension(w, 0));
        if (lblWelcome != null) lblWelcome.setMaximumSize(new Dimension(w, 70));
        for (JButton b : new JButton[]{btnDash, btnManage, btnCirc, btnMembers, btnLang}) {
            if (b != null) b.setMaximumSize(new Dimension(w, 40));
        }
        sidebar.revalidate();
        sidebar.repaint();
    }

    // ==========================================================================
    //  Toast
    // ==========================================================================
    private class ToastManager {
        void show(String msg, boolean ok) {
            new Thread(()->{
                JWindow t = new JWindow(GUI.this);
                JPanel p = new JPanel(new BorderLayout());
                p.setBackground(ok?C_OK:C_ERR);
                p.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
                JLabel l = new JLabel(msg,SwingConstants.CENTER);
                l.setForeground(Color.WHITE); l.setFont(new Font("SansSerif",Font.BOLD,13));
                p.add(l); t.setContentPane(p); t.pack();
                t.setLocation(GUI.this.getX()+GUI.this.getWidth()-t.getWidth()-18,
                              GUI.this.getY()+GUI.this.getHeight()-t.getHeight()-50);
                t.setOpacity(0f); t.setVisible(true);
                for(float v=0;v<=1f;v+=0.15f){final float op=Math.min(1f,v);
                    SwingUtilities.invokeLater(()->t.setOpacity(op));try{Thread.sleep(18);}catch(Exception ignored){}}
                try{Thread.sleep(2200);}catch(Exception ignored){}
                for(float v=1f;v>=0;v-=0.15f){final float op=Math.max(0f,v);
                    SwingUtilities.invokeLater(()->t.setOpacity(op));try{Thread.sleep(18);}catch(Exception ignored){}}
                SwingUtilities.invokeLater(t::dispose);
            }).start();
        }
    }
    private void toast(String m, boolean ok){ SwingUtilities.invokeLater(()->toastMgr.show(m,ok)); }

    // ✅ مُضاف: كانت مُعلَّقة في التعليق — الـ log() مطلوبة لـ actionBorrow/Return
    private void log(String m){ SwingUtilities.invokeLater(()->txtCircLog.append(m+"\n")); }

    // ==========================================================================
    //  Shared UI helpers
    // ==========================================================================
    private JPanel card() {
        JPanel p = new JPanel(new BorderLayout(0,10));
        p.setBackground(C_PAPER);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_GOLD,2),
            BorderFactory.createEmptyBorder(16,16,16,16)));
        return p;
    }

    private JTextField field(int cols) {
        JTextField f = new JTextField(cols);
        f.setBackground(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_LIGHT,1),
            BorderFactory.createEmptyBorder(5,7,5,7)));
        f.setFont(new Font("SansSerif",Font.PLAIN,13));
        return f;
    }

    private JButton btn(String txt, Color bg) {
        JButton b = new JButton(txt) {
            boolean hov = false;
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color top = hov ? bg.brighter() : bg;
                Color bot = hov ? bg : bg.darker();
                g2.setPaint(new GradientPaint(0, 0, top, 0, getHeight(), bot));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(255, 255, 255, hov ? 130 : 90));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override public void addNotify() {
                super.addNotify();
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hov = true; repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hov = false; repaint(); }
                });
            }
        };
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif",Font.BOLD,12));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(9,16,9,16));
        return b;
    }

    private JLabel hdr(String t){
        JLabel l=new JLabel(t);
        l.setFont(new Font("Serif",Font.BOLD,22));
        l.setForeground(C_DARK); return l;
    }

    private JLabel lbl(String t){
        JLabel l=new JLabel(t);
        l.setFont(new Font("SansSerif",Font.BOLD,13));
        l.setForeground(C_DARK); return l;
    }

    private JTable styledTable(DefaultTableModel m){
        JTable t = new JTable(m){
            @Override public boolean isCellEditable(int r,int c){return false;}
        };
        t.setRowHeight(26);
        t.setFont(new Font("SansSerif",Font.PLAIN,13));
        t.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        t.getTableHeader().setReorderingAllowed(false);
        t.getTableHeader().setBackground(C_MED);
        t.getTableHeader().setForeground(Color.WHITE);
        t.getTableHeader().setFont(new Font("SansSerif",Font.BOLD,13));
        t.setGridColor(new Color(200,190,170));
        t.setSelectionBackground(C_GOLD);
        t.setSelectionForeground(C_DARK);
        t.setShowHorizontalLines(true);
        t.setShowVerticalLines(false);
        DefaultTableCellRenderer stripe = new DefaultTableCellRenderer(){
            @Override public Component getTableCellRendererComponent(
                    JTable tbl,Object v,boolean sel,boolean foc,int r,int c){
                Component comp=super.getTableCellRendererComponent(tbl,v,sel,foc,r,c);
                if(!sel){ comp.setBackground(r%2==0?C_PAPER:C_PAPER2); comp.setForeground(C_INK);}
                else    { comp.setBackground(C_GOLD); comp.setForeground(C_DARK);}
                setToolTipText(v==null?"":v.toString());
                return comp;
            }
        };
        stripe.setHorizontalAlignment(SwingConstants.LEFT);
        for(int i=0;i<m.getColumnCount();i++) t.getColumnModel().getColumn(i).setCellRenderer(stripe);
        return t;
    }

    private JScrollPane scrollOf(JTable t){
        JScrollPane s=new JScrollPane(t);
        s.setBorder(BorderFactory.createLineBorder(C_LIGHT,1));
        s.getViewport().setBackground(C_PAPER);
        styleScrollBar(s.getVerticalScrollBar());
        styleScrollBar(s.getHorizontalScrollBar());
        return s;
    }

    private JScrollPane scrollOfText(JTextArea a){
        JScrollPane s=new JScrollPane(a);
        s.setBorder(BorderFactory.createLineBorder(C_LIGHT,1));
        s.getViewport().setBackground(C_PAPER);
        styleScrollBar(s.getVerticalScrollBar());
        styleScrollBar(s.getHorizontalScrollBar());
        return s;
    }

    private void styleScrollBar(JScrollBar bar) {
        if (bar == null) return;
        bar.setUI(new BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                thumbColor = new Color(141, 110, 99);       // wood light
                trackColor = new Color(255, 253, 231);      // paper
                thumbDarkShadowColor = new Color(62, 39, 35);
                thumbHighlightColor = new Color(212, 175, 55);
                thumbLightShadowColor = new Color(93, 64, 55);
            }
            @Override protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            @Override protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            private JButton createZeroButton() {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(0, 0));
                b.setMinimumSize(new Dimension(0, 0));
                b.setMaximumSize(new Dimension(0, 0));
                b.setOpaque(false);
                b.setContentAreaFilled(false);
                b.setBorderPainted(false);
                return b;
            }
        });
        bar.setPreferredSize(new Dimension(10, 10));
        bar.setUnitIncrement(16);
    }

    // ==========================================================================
    //  DASHBOARD
    // ==========================================================================
    private JPanel buildDashCard(){
        JPanel p = card();

        JPanel top = new JPanel(new BorderLayout(10,0));
        top.setOpaque(false);
        lblDashTitle = hdr("Library Catalog");
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0));
        searchRow.setOpaque(false);
        lblSearch = lbl("🔍 Search:");
        txtSearch = field(22);
        txtSearch.getDocument().addDocumentListener(new DocumentListener(){
            @Override public void insertUpdate(DocumentEvent e){performSearch();}
            @Override public void removeUpdate(DocumentEvent e){performSearch();}
            @Override public void changedUpdate(DocumentEvent e){performSearch();}
        });
        txtSearch.setToolTipText("Live search by ID, title, author or type");
        searchRow.add(lblSearch); searchRow.add(txtSearch);
        top.add(lblDashTitle, BorderLayout.WEST);
        top.add(searchRow,    BorderLayout.EAST);

        JPanel stats = new JPanel(new GridLayout(1,2,12,0));
        stats.setOpaque(false); stats.setBorder(new EmptyBorder(6,0,8,0));
        lblStatBooks   = new JLabel("0");
        lblStatMembers = new JLabel("0");
        stats.add(tile("📚 Total Books",  lblStatBooks,   C_MED));
        stats.add(tile("👥 Members",       lblStatMembers, C_GOLD));

        bookModel = new DefaultTableModel(
            new String[]{"ID","Title","Author","Category","Year","Copies","Type","Status","Borrowers"},0);
        bookTable = styledTable(bookModel);
        bookTable.setAutoCreateRowSorter(true);
        bookTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        // Simple, readable column widths
        int[] w = {80, 220, 160, 140, 70, 70, 90, 120, 240};
        for (int i = 0; i < w.length && i < bookTable.getColumnCount(); i++) {
            bookTable.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        }

        bookTable.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer(){
            @Override public Component getTableCellRendererComponent(
                    JTable tbl,Object v,boolean sel,boolean foc,int r,int c){
                Component comp=super.getTableCellRendererComponent(tbl,v,sel,foc,r,c);
                if(!sel){
                    String s=(v==null?"":v.toString()).toLowerCase();
                    comp.setBackground(r%2==0?C_PAPER:C_PAPER2);
                    if(s.startsWith("available"))     comp.setForeground(C_OK);
                    else if(s.startsWith("reserved")) comp.setForeground(C_WARN);
                    else if(s.startsWith("borrowed")) comp.setForeground(C_ERR);
                    else if(s.startsWith("digital"))  comp.setForeground(C_BLUE);
                    else                              comp.setForeground(C_INK);
                } else { comp.setBackground(C_GOLD); comp.setForeground(C_DARK);}
                setToolTipText(v==null?"":v.toString());
                return comp;
            }
        });

        // Type coloring: Physical vs E-Book
        bookTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer(){
            @Override public Component getTableCellRendererComponent(
                    JTable tbl,Object v,boolean sel,boolean foc,int r,int c){
                Component comp=super.getTableCellRendererComponent(tbl,v,sel,foc,r,c);
                if(!sel){
                    String s=(v==null?"":v.toString()).toLowerCase();
                    comp.setBackground(r%2==0?C_PAPER:C_PAPER2);
                    if(s.contains("e-book") || s.contains("ebook")) comp.setForeground(C_BLUE);
                    else comp.setForeground(C_MED);
                } else { comp.setBackground(C_GOLD); comp.setForeground(C_DARK); }
                setToolTipText(v==null?"":v.toString());
                return comp;
            }
        });

        bookTable.addMouseListener(new MouseAdapter(){
            @Override public void mouseClicked(MouseEvent e){
                int row = bookTable.getSelectedRow();
                if(row<0) return;
                txtID.setText(      (String)bookModel.getValueAt(row,0));
                txtTitle.setText(   (String)bookModel.getValueAt(row,1));
                txtAuthor.setText(  (String)bookModel.getValueAt(row,2));
                txtCategory.setText((String)bookModel.getValueAt(row,3));
                txtYear.setText(    bookModel.getValueAt(row,4).toString());
                txtCopies.setText(  bookModel.getValueAt(row,5).toString());
                String type = (String)bookModel.getValueAt(row,6);
                tglEBook.setSelected("E-Book".equalsIgnoreCase(type));
                ebookPanel.setVisible(tglEBook.isSelected());
            }
        });

        p.add(top,                 BorderLayout.NORTH);
        p.add(stats,               BorderLayout.SOUTH);
        p.add(scrollOf(bookTable), BorderLayout.CENTER);
        return p;
    }

    private JPanel tile(String title, JLabel valueLabel, Color accent){
        JPanel p = new JPanel(new BorderLayout(0,4));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0,3,0,0,accent),
            BorderFactory.createEmptyBorder(10,12,10,12)));
        JLabel lt = new JLabel(title);
        lt.setFont(new Font("SansSerif",Font.PLAIN,12)); lt.setForeground(C_MED);
        valueLabel.setFont(new Font("SansSerif",Font.BOLD,24));
        valueLabel.setForeground(accent);
        valueLabel.setHorizontalAlignment(SwingConstants.LEFT);
        p.add(lt,         BorderLayout.NORTH);
        p.add(valueLabel, BorderLayout.CENTER);
        return p;
    }

    // ==========================================================================
    //  MANAGE BOOKS
    // ==========================================================================
    private JPanel buildManageCard(){
        JPanel p = card();
        lblManTitle = hdr("Book Registry");

        tglEBook = new JToggleButton("E-Book") {
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isSelected()?C_BLUE:new Color(200,195,185));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),getHeight(),getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif",Font.BOLD,12));
                FontMetrics fm=g2.getFontMetrics();
                g2.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,
                    (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        tglEBook.setPreferredSize(new Dimension(90,30));
        tglEBook.setFocusPainted(false); tglEBook.setBorderPainted(false);
        tglEBook.setContentAreaFilled(false);
        tglEBook.setFont(new Font("SansSerif",Font.BOLD,12));
        tglEBook.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tglEBook.setToolTipText("Toggle to add an E-Book instead of Physical");

        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setOpaque(false);
        JPanel hdrLeft = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
        hdrLeft.setOpaque(false); hdrLeft.add(lblManTitle);
        JPanel hdrRight = new JPanel(new FlowLayout(FlowLayout.RIGHT,0,6));
        hdrRight.setOpaque(false);
        hdrRight.add(lbl("Type: ")); hdrRight.add(tglEBook);
        hdr.add(hdrLeft, BorderLayout.WEST);
        hdr.add(hdrRight,BorderLayout.EAST);
        p.add(hdr, BorderLayout.NORTH);

        JPanel formWrap = new JPanel(new GridBagLayout());
        formWrap.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets=new Insets(7,8,7,8); g.fill=GridBagConstraints.HORIZONTAL; g.anchor=GridBagConstraints.WEST;

        lblID=lbl("Book ID:");    txtID=field(18);
        lblTitleF=lbl("Title:");  txtTitle=field(18);
        lblAuthor=lbl("Author:"); txtAuthor=field(18);
        lblCat=lbl("Category:"); txtCategory=field(18);
        lblYear=lbl("Year:");     txtYear=field(18);
        lblCopies=lbl("Copies:"); txtCopies=field(18);

        JLabel[]     labs = {lblID,lblTitleF,lblAuthor,lblCat,lblYear,lblCopies};
        JTextField[] flds = {txtID,txtTitle,txtAuthor,txtCategory,txtYear,txtCopies};
        for(int i=0;i<labs.length;i++){
            g.gridx=0; g.gridy=i; g.weightx=0; formWrap.add(labs[i],g);
            g.gridx=1; g.weightx=1;             formWrap.add(flds[i],g);
        }

        lblLink=lbl("Download URL:"); txtLink=field(18);
        lblSize=lbl("Size (MB):");    txtSize=field(18);
        ebookPanel = new JPanel(new GridBagLayout());
        ebookPanel.setOpaque(false);
        ebookPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(C_BLUE,1),"Digital Info",
            javax.swing.border.TitledBorder.LEFT,javax.swing.border.TitledBorder.TOP,
            new Font("SansSerif",Font.BOLD,11),C_BLUE));
        GridBagConstraints eg=new GridBagConstraints();
        eg.insets=new Insets(6,8,6,8); eg.fill=GridBagConstraints.HORIZONTAL; eg.anchor=GridBagConstraints.WEST;
        eg.gridx=0;eg.gridy=0;eg.weightx=0; ebookPanel.add(lblLink,eg);
        eg.gridx=1;eg.weightx=1;            ebookPanel.add(txtLink,eg);
        eg.gridx=0;eg.gridy=1;eg.weightx=0; ebookPanel.add(lblSize,eg);
        eg.gridx=1;eg.weightx=1;            ebookPanel.add(txtSize,eg);
        ebookPanel.setVisible(false);

        GridBagConstraints ep=new GridBagConstraints();
        ep.gridx=0;ep.gridy=6;ep.gridwidth=2;ep.fill=GridBagConstraints.HORIZONTAL;
        ep.insets=new Insets(4,0,0,0);
        formWrap.add(ebookPanel,ep);

        tglEBook.addActionListener(e->{
            ebookPanel.setVisible(tglEBook.isSelected());
            formWrap.revalidate(); formWrap.repaint();
        });

        JPanel btns = new JPanel(new GridLayout(4,1,0,8));
        btns.setOpaque(false); btns.setBorder(new EmptyBorder(0,16,0,0));
        btnAdd   = btn("➕ Add Book",    C_OK);
        btnEdit  = btn("✏️ Edit Book",   new Color(180,140,30));
        btnDel   = btn("🗑 Delete Book", C_ERR);
        btnClear = btn("✖ Clear Form",  C_MED);
        btnAdd.addActionListener(e->actionAddBook());
        btnEdit.addActionListener(e->actionEditBook());
        btnDel.addActionListener(e->actionDelBook());
        btnClear.addActionListener(e->clearForm());
        btns.add(btnAdd); btns.add(btnEdit); btns.add(btnDel); btns.add(btnClear);

        JPanel centre = new JPanel(new BorderLayout(0,0));
        centre.setOpaque(false);
        centre.add(formWrap, BorderLayout.CENTER);
        centre.add(btns,     BorderLayout.EAST);
        p.add(centre, BorderLayout.CENTER);
        return p;
    }

    // ==========================================================================
    //  CIRCULATION
    // ==========================================================================
    private JPanel buildCircCard(){
        JPanel p = card();
        lblCircTitle = hdr("Circulation Desk");
        JPanel hd = new JPanel(new FlowLayout(FlowLayout.LEFT));
        hd.setOpaque(false); hd.add(lblCircTitle);
        p.add(hd, BorderLayout.NORTH);

        JPanel forms = new JPanel(new GridLayout(2,1,0,14));
        forms.setOpaque(false);

        JPanel bBox = titledCard("📖 Borrow & Return");
        JPanel bForm = formGrid(2);
        txtBorrowBookID = circField(15);
        txtBorrowMemID  = circField(15);
        txtRetBookID    = circField(15);
        bForm.add(lbl("Book ID (borrow):")); bForm.add(txtBorrowBookID);
        bForm.add(lbl("Member ID:"));        bForm.add(txtBorrowMemID);
        bForm.add(lbl("Book ID (return):")); bForm.add(txtRetBookID);
        btnDoBorrow = btn("✔ Borrow", C_MED);
        btnDoReturn = btn("↩ Return", new Color(180,140,30));
        btnDoBorrow.addActionListener(e->actionBorrow());
        btnDoReturn.addActionListener(e->actionReturn());
        JPanel bBtns = new JPanel(new FlowLayout(FlowLayout.CENTER,10,4));
        bBtns.setOpaque(false); bBtns.add(btnDoBorrow); bBtns.add(btnDoReturn);
        bBox.add(bForm, BorderLayout.CENTER); bBox.add(bBtns, BorderLayout.SOUTH);

        
       
       
        

        forms.add(bBox);

        txtCircLog = new JTextArea();
        txtCircLog.setFont(new Font("Monospaced",Font.PLAIN,12));
        txtCircLog.setBackground(C_PAPER); txtCircLog.setForeground(C_INK);
        txtCircLog.setEditable(false); txtCircLog.setLineWrap(true);
        JScrollPane ls = scrollOfText(txtCircLog);
        ls.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(C_GOLD,1),"Activity Log",
            javax.swing.border.TitledBorder.LEFT,javax.swing.border.TitledBorder.TOP,
            new Font("SansSerif",Font.BOLD,12),C_DARK));

        JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,forms,ls);
        sp.setDividerLocation(460); sp.setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder());
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private JTextField circField(int cols){
        JTextField f = new JTextField(cols);
        f.setBackground(Color.WHITE);
        f.setFont(new Font("SansSerif",Font.PLAIN,13));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_LIGHT,1,true),
            BorderFactory.createEmptyBorder(8,10,8,10)));
        f.setPreferredSize(new Dimension(180, 34));
        return f;
    }

    // ==========================================================================
    //  MEMBERS
    // ==========================================================================
    private JPanel buildMembersCard(){
        JPanel p = card();
        lblMemTitle = hdr("Membership Office");
        JPanel hd = new JPanel(new FlowLayout(FlowLayout.LEFT));
        hd.setOpaque(false); hd.add(lblMemTitle);
        p.add(hd, BorderLayout.NORTH);

        membersModel = new DefaultTableModel(
            new String[]{"Member ID","Name","Borrowed Books"},0);
        membersTable = styledTable(membersModel);
        membersTable.setAutoCreateRowSorter(true);
        membersTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        membersTable.addMouseListener(new MouseAdapter(){
            @Override public void mouseClicked(MouseEvent e){
                int r=membersTable.getSelectedRow();
                if(r<0) return;
                txtNewMemID.setText((String)membersModel.getValueAt(r,0));
                txtNewMemName.setText((String)membersModel.getValueAt(r,1));
                txtNewMemEmail.setText("");
                if(e.getClickCount()==2) showMemberLoans((String)membersModel.getValueAt(r,0));
            }
        });
        JScrollPane ms = new JScrollPane(membersTable);
        styleScrollBar(ms.getVerticalScrollBar());
        styleScrollBar(ms.getHorizontalScrollBar());
        ms.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(C_GOLD,1),"Members  (double-click for loan history)",
            javax.swing.border.TitledBorder.LEFT,javax.swing.border.TitledBorder.TOP,
            new Font("SansSerif",Font.BOLD,12),C_DARK));

        JPanel reg = new JPanel(new GridBagLayout());
        reg.setOpaque(false);
        reg.setPreferredSize(new Dimension(310,0));
        reg.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(C_GOLD,1),"Register / Manage",
            javax.swing.border.TitledBorder.LEFT,javax.swing.border.TitledBorder.TOP,
            new Font("SansSerif",Font.BOLD,12),C_DARK));

        GridBagConstraints g=new GridBagConstraints();
        g.insets=new Insets(9,9,9,9); g.fill=GridBagConstraints.HORIZONTAL; g.anchor=GridBagConstraints.WEST;
        txtNewMemID=field(15); txtNewMemName=field(15); txtNewMemEmail=field(15);
        lblRegNewMem = lbl("Register New Member");
        String[] fl={"Member ID:","Full Name:","Email (opt.):"};
        JTextField[] ff={txtNewMemID,txtNewMemName,txtNewMemEmail};
        for(int i=0;i<fl.length;i++){
            g.gridx=0;g.gridy=i;g.weightx=0; reg.add(lbl(fl[i]),g);
            g.gridx=1;g.weightx=1;            reg.add(ff[i],g);
        }
        btnRegMem   = btn("✔ Register",   C_OK);
        btnDelMem   = btn("🗑 Delete",    C_ERR);
        btnMemLoans = btn("📋 View Loans",C_MED);
        btnRegMem.addActionListener(e->actionAddMember());
        btnDelMem.addActionListener(e->actionDelMember());
        btnMemLoans.addActionListener(e->{
            String id=txtNewMemID.getText().trim();
            if(id.isEmpty()){toast("Select a member first.",false);return;}
            showMemberLoans(id);
        });

        JPanel rb = new JPanel(new GridLayout(3,1,0,7));
        rb.setOpaque(false); rb.setBorder(new EmptyBorder(10,0,0,0));
        rb.add(btnRegMem); rb.add(btnDelMem); rb.add(btnMemLoans);
        g.gridx=0;g.gridy=3;g.gridwidth=2;g.anchor=GridBagConstraints.CENTER;
        reg.add(rb,g);
        JLabel hint=new JLabel("<html><i>Click once to select · Double-click for loans</i></html>");
        hint.setFont(new Font("SansSerif",Font.ITALIC,11)); hint.setForeground(C_MED);
        g.gridy=4;g.anchor=GridBagConstraints.WEST;
        reg.add(hint,g);

        JSplitPane sp=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,ms,reg);
        sp.setDividerLocation(580); sp.setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder());
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private void showMemberLoans(String memberId){
        ArrayList<Book> books = MyLibrary.getAllBooks();
        DefaultTableModel m = new DefaultTableModel(
            new String[]{"Book ID","Title","Author","Due Date","Days Overdue"},0);

        ArrayList<LoanRecord> allLoans = MyLibrary.getLoanHistory();

        for (LoanRecord loan : allLoans) {
            if (!memberId.equalsIgnoreCase(loan.getMemberId())) continue;
            if (loan.isReturned()) continue; // show current active loans only

            Book book = null;
            for (Book b : books) {
                if (b.getID().equalsIgnoreCase(loan.getBookId())) { book = b; break; }
            }
            if (book == null) continue;

            java.time.LocalDate due = loan.getDueDate();
            long overdue = (due != null)
                ? java.time.temporal.ChronoUnit.DAYS.between(due, java.time.LocalDate.now())
                : 0;
            overdue = Math.max(0, overdue);

            m.addRow(new Object[]{
                book.getID(),
                book.getTitle(),
                book.getAuthor(),
                (due != null ? due.toString() : "N/A"),
                (overdue > 0 ? overdue + " days" : "On time")
            });
        }

        JTable tbl = styledTable(m);
        JScrollPane sc = scrollOf(tbl);
        sc.setPreferredSize(new Dimension(640,220));
        JDialog dlg = new JDialog(this,"Loan History — "+memberId,true);
        dlg.setLayout(new BorderLayout(0,8));
        JLabel ttl=new JLabel("  Books currently borrowed by member: "+memberId);
        ttl.setFont(new Font("SansSerif",Font.BOLD,14)); ttl.setBorder(new EmptyBorder(10,8,4,0));
        JLabel note=new JLabel("  ("+m.getRowCount()+" active loan(s))");
        note.setFont(new Font("SansSerif",Font.ITALIC,12)); note.setForeground(C_MED);
        note.setBorder(new EmptyBorder(0,8,8,0));
        dlg.add(ttl,  BorderLayout.NORTH);
        dlg.add(sc,   BorderLayout.CENTER);
        dlg.add(note, BorderLayout.SOUTH);
        dlg.pack(); dlg.setLocationRelativeTo(this); dlg.setVisible(true);
    }
    // ==========================================================================
    //  Panel helpers
    // ==========================================================================
    private JPanel titledCard(String title){
        JPanel p=new JPanel(new BorderLayout(0,6));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(C_GOLD,1),title,
            javax.swing.border.TitledBorder.LEFT,javax.swing.border.TitledBorder.TOP,
            new Font("SansSerif",Font.BOLD,13),C_DARK));
        return p;
    }

    private JPanel formGrid(int cols){
        JPanel fp=new JPanel(new GridLayout(0,cols*2,8,8));
        fp.setOpaque(false); fp.setBorder(new EmptyBorder(4,8,4,8));
        return fp;
    }

    // ==========================================================================
    //  Sidebar wiring
    // ==========================================================================
    private void wireSidebar(){
        btnDash.addActionListener(e->show("DASH"));
        btnManage.addActionListener(e->show("MANAGE"));
        btnCirc.addActionListener(e->show("CIRC"));
        btnMembers.addActionListener(e->{refreshMembersTable();show("MEMBERS");});
        btnLang.addActionListener(e->toggleLang());
    }

    private void show(String name){ cardLayout.show(cardPanel,name); refreshAll(); }

    // ==========================================================================
    //  Language toggle
    // ==========================================================================
    private void toggleLang(){ isArabic=!isArabic; updateTexts(); }

    private void updateTexts(){
        if(isArabic){
            lblWelcome.setText("المكتبة المركزية");
            btnDash.setText("📋  الرئيسية"); btnManage.setText("📚  إدارة الكتب");
            btnCirc.setText("🔄  الاستعارة"); btnMembers.setText("👥  الأعضاء");
            btnLang.setText("🌐  EN / عربي");
            lblDashTitle.setText("فهرس المكتبة"); lblSearch.setText("🔍 بحث:");
            lblManTitle.setText("سجل الكتب"); tglEBook.setText("كتاب رقمي");
            lblID.setText("الرقم:"); lblTitleF.setText("العنوان:"); lblAuthor.setText("المؤلف:");
            lblCat.setText("الفئة:"); lblYear.setText("السنة:"); lblCopies.setText("النسخ:");
            lblLink.setText("رابط:"); lblSize.setText("الحجم:");
            btnAdd.setText("➕ إضافة"); btnEdit.setText("✏️ تعديل");
            btnDel.setText("🗑 حذف"); btnClear.setText("✖ مسح");
            lblCircTitle.setText("مكتب الاستعارة");
            btnDoBorrow.setText("✔ استعارة"); btnDoReturn.setText("↩ إرجاع");
            
            lblMemTitle.setText("مكتب العضوية"); lblRegNewMem.setText("تسجيل عضو");
            btnRegMem.setText("✔ تسجيل"); btnDelMem.setText("🗑 حذف");
            btnMemLoans.setText("📋 كتبه المستعارة");
        } else {
            lblWelcome.setText("THE LIBRARY");
            btnDash.setText("📋  Dashboard"); btnManage.setText("📚  Manage Books");
            btnCirc.setText("🔄  Circulation"); btnMembers.setText("👥  Members");
            //btnReservations.setText("📌  Reservations");
            btnLang.setText("🌐  EN / عربي");
            lblDashTitle.setText("Library Catalog"); lblSearch.setText("🔍 Search:");
            lblManTitle.setText("Book Registry"); tglEBook.setText("E-Book");
            lblID.setText("Book ID:"); lblTitleF.setText("Title:"); lblAuthor.setText("Author:");
            lblCat.setText("Category:"); lblYear.setText("Year:"); lblCopies.setText("Copies:");
            lblLink.setText("Download URL:"); lblSize.setText("Size (MB):");
            btnAdd.setText("➕ Add Book"); btnEdit.setText("✏️ Edit Book");
            btnDel.setText("🗑 Delete Book"); btnClear.setText("✖ Clear Form");
            lblCircTitle.setText("Circulation Desk");
            btnDoBorrow.setText("✔ Borrow"); btnDoReturn.setText("↩ Return");
            
            lblMemTitle.setText("Membership Office"); lblRegNewMem.setText("Register New Member");
            btnRegMem.setText("✔ Register"); btnDelMem.setText("🗑 Delete");
            btnMemLoans.setText("📋 View Loans");
            
        }
        updateSidebarWidth();
    }

    // ==========================================================================
    //  Actions — Books
    // ==========================================================================
    private void actionAddBook(){
        try{
            if(txtID.getText().trim().isEmpty()||txtTitle.getText().trim().isEmpty()){
                toast("ID and Title are required!",false); return;
            }
            String type=tglEBook.isSelected()?"E-Book":"Physical";
            String msg;
            if(tglEBook.isSelected()){
                double sz=0;
                try{sz=Double.parseDouble(txtSize.getText().trim());}catch(Exception ignored){}
                msg=MyLibrary.addBook(txtID.getText().trim(),txtTitle.getText().trim(),
                    txtAuthor.getText().trim(),txtCategory.getText().trim(),
                    Integer.parseInt(txtYear.getText().trim()),
                    Integer.parseInt(txtCopies.getText().trim()),
                    type,txtLink.getText().trim(),sz);
            } else {
                msg=MyLibrary.addBook(txtID.getText().trim(),txtTitle.getText().trim(),
                    txtAuthor.getText().trim(),txtCategory.getText().trim(),
                    Integer.parseInt(txtYear.getText().trim()),
                    Integer.parseInt(txtCopies.getText().trim()),type,"",0.0);
            }
            if(msg.contains("Success")){MyLibrary.saveBooksToFile();refreshAll();clearForm();}
            toast(msg,msg.contains("Success"));
        }catch(Exception ex){toast("Error: "+ex.getMessage(),false);}
    }

    private void actionEditBook(){
        try{
            String msg=MyLibrary.updateBook(txtID.getText().trim(),txtTitle.getText().trim(),
                txtAuthor.getText().trim(),txtCategory.getText().trim(),
                Integer.parseInt(txtYear.getText().trim()),
                Integer.parseInt(txtCopies.getText().trim()));
            if(msg.contains("Success")){MyLibrary.saveBooksToFile();refreshAll();clearForm();}
            toast(msg,msg.contains("Success"));
        }catch(Exception ex){toast("Error: "+ex.getMessage(),false);}
    }

    private void actionDelBook(){
        String id=txtID.getText().trim();
        if(id.isEmpty()){toast("Enter or select a Book ID first.",false);return;}
        int ok=JOptionPane.showConfirmDialog(this,"Delete book \""+id+"\"?",
            "Confirm",JOptionPane.YES_NO_OPTION);
        if(ok==JOptionPane.YES_OPTION){
            String msg=MyLibrary.removeBook(id);
            if(msg.contains("Success")){MyLibrary.saveBooksToFile();refreshAll();clearForm();}
            toast(msg,msg.contains("Success"));
        }
    }

    private void clearForm(){
        for(JTextField f:new JTextField[]{txtID,txtTitle,txtAuthor,txtCategory,
                                          txtYear,txtCopies,txtLink,txtSize})
            f.setText("");
        tglEBook.setSelected(false); ebookPanel.setVisible(false);
    }

    // ==========================================================================
    //  Actions — Circulation
    // ==========================================================================
    private void actionBorrow(){
        String msg=MyLibrary.borrowBook(
            txtBorrowBookID.getText().trim(), txtBorrowMemID.getText().trim());
        log(msg); toast(msg,msg.contains("Success")); refreshAll();
    }

    private void actionReturn(){
        String msg=MyLibrary.returnBook(txtRetBookID.getText().trim());
        log(msg); toast(msg,!msg.contains("Error")); refreshAll();
    }

    // ✅ مُضاف: كانتا مُعلَّقتين — أُزيل التعليق
    

    // ==========================================================================
    //  Actions — Members
    // ==========================================================================
    private void actionAddMember(){
        String msg=MyLibrary.addMember(
            txtNewMemID.getText().trim(), txtNewMemName.getText().trim(),"","");
        if(msg.contains("Success")){
            MyLibrary.saveMembersToFile();
            txtNewMemID.setText(""); txtNewMemName.setText(""); txtNewMemEmail.setText("");
            refreshMembersTable();
        }
        toast(msg,msg.contains("Success"));
    }

    private void actionDelMember(){
        String id=txtNewMemID.getText().trim();
        if(id.isEmpty()){toast("Select a member from the table first.",false);return;}
        int ok=JOptionPane.showConfirmDialog(this,
            "Delete member \""+id+"\"?","Confirm",JOptionPane.YES_NO_OPTION);
        if(ok==JOptionPane.YES_OPTION){
            String msg=MyLibrary.removeMember(id);
            if(msg.contains("Success")){
                MyLibrary.saveMembersToFile();
                txtNewMemID.setText(""); txtNewMemName.setText("");
                refreshMembersTable();
            }
            toast(msg,msg.contains("Success"));
        }
    }

    // ==========================================================================
    //  Actions — Members
    // ==========================================================================
    // ==========================================================================
    //  Refresh
    // ==========================================================================
    private void refreshAll(){
        refreshBooksTable();
        refreshMembersTable();
    }

    private void refreshBooksTable(){
        if(bookModel==null) return;
        bookModel.setRowCount(0);
        ArrayList<Book> books=MyLibrary.getAllBooks();
        if(books==null) return;
        int ebooks=0;
        for(Book b:books){
            if(b instanceof EBook) ebooks++;
            String st;
            if(b instanceof PhysicalBook) st=((PhysicalBook)b).getStatus();
            else if(b instanceof EBook)   st=((EBook)b).getStatus(); // ✅ getStatus() أُضيفت في EBook
            else                          st="Available";
            String borrowers = MyLibrary.getBorrowersForBook(b.getID());
            bookModel.addRow(new Object[]{b.getID(),b.getTitle(),b.getAuthor(),
                b.getCategory(),b.getYear(),b.getCopies(),b.getBookType(),st,borrowers});
        }
        if(lblStatBooks  !=null) lblStatBooks.setText(books.size()+" ("+ebooks+" digital)");
        if(lblStatMembers!=null) lblStatMembers.setText(String.valueOf(MyLibrary.getAllMembers().size()));
        
    }

    private void refreshMembersTable() {
    membersModel.setRowCount(0);
    ArrayList<Member> members = MyLibrary.getAllMembers();
    ArrayList<LoanRecord> allLoans = MyLibrary.getLoanHistory();
    for (Member m : members) {
        // نقوم بحساب عدد السجلات النشطة (التي لم تُرجع بعد) لهذا العضو بالتحديد
        long cnt = allLoans.stream()
            .filter(loan -> loan.getMemberId().equalsIgnoreCase(m.getID()) && !loan.isReturned())
            .count();

        membersModel.addRow(new Object[]{
            m.getID(), 
            m.getName(),
            cnt + " book(s)" // سيظهر العدد الصحيح لكل مستعير بشكل مستقل
        });
    }
}

    private void performSearch(){
        if(bookModel==null) return;
        String q=txtSearch.getText().toLowerCase().trim();
        bookModel.setRowCount(0);
        ArrayList<Book> books=MyLibrary.getAllBooks();
        if(books==null) return;
        for(Book b:books){
            if(b.getID().toLowerCase().contains(q)||
               b.getTitle().toLowerCase().contains(q)||
               b.getAuthor().toLowerCase().contains(q)||
               b.getBookType().toLowerCase().contains(q)||
               b.getCategory().toLowerCase().contains(q)){
                String st;
                if(b instanceof PhysicalBook) st=((PhysicalBook)b).getStatus();
                else if(b instanceof EBook)   st=((EBook)b).getStatus();
                else                          st="Available";
                String borrowers = MyLibrary.getBorrowersForBook(b.getID());
                bookModel.addRow(new Object[]{b.getID(),b.getTitle(),b.getAuthor(),
                    b.getCategory(),b.getYear(),b.getCopies(),b.getBookType(),st,borrowers});
            }
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) { }
            new GUI();
        });
    
}
}