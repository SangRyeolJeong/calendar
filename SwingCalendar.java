import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.util.*;
import java.util.List;

public class SwingCalendar extends JFrame {

    static DefaultTableModel model;
    static Calendar cal = new GregorianCalendar();
    static JLabel label;
    JFrame loginFrame;
    JTextField usernameField;
    JPasswordField passwordField;
    JPanel calendarPanel;
    JTextField eventField;
    JButton createEventButton;
    static String username;
    static String password;
    public static Map<String, List<String>> eventsMap = new HashMap<>();

    SwingCalendar() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Monthly Calendar");
        this.setSize(1600, 1000);
        this.setLayout(new BorderLayout());
        this.setVisible(true);
        this.setLocationRelativeTo(null);

        createLoginFrame();
    }

    private void createLoginFrame() {
        loginFrame = new JFrame("Login");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(400, 200);
        loginFrame.setLayout(new BorderLayout());
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setVisible(true);
        loginFrame.getContentPane().setBackground(Color.BLACK);

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(null);
        loginPanel.setBackground(Color.white);

        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setSize(130, 30);
        usernameLabel.setFont(new Font("Serif", Font.BOLD, 20));
        usernameLabel.setLocation(50,30);
        usernameLabel.setHorizontalAlignment(JLabel.CENTER);

        usernameField = new JTextField();
        usernameField.setSize(130, 30);
        usernameField.setLocation(180,30);
        usernameField.setBackground(Color.LIGHT_GRAY);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setSize(130, 30);
        passwordLabel.setFont(new Font("Serif", Font.BOLD, 20));
        passwordLabel.setLocation(50,70);
        passwordLabel.setHorizontalAlignment(JLabel.CENTER);
        
        passwordField = new JPasswordField();
        passwordField.setSize(130, 30);
        passwordField.setLocation(180,70);
        passwordField.setBackground(Color.LIGHT_GRAY);

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Serif", Font.BOLD, 15));
        loginButton.setSize(100, 40);
        loginButton.setLocation(80, 120);
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(Color.BLACK);
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                username = usernameField.getText();
                password = new String(passwordField.getPassword());

                if (Login.authenticateUserAndGetID(username, password) > 0) {
                    JOptionPane.showMessageDialog(null, "Login Successfully!");
                    loginFrame.dispose(); 
                    initializeCalendar(); 
                } else {
                    JOptionPane.showMessageDialog(null, "Login failed. Please try again.");
                }
            }
        });

        JButton accessButton = new JButton("register");
        accessButton.setFont(new Font("Serif", Font.BOLD, 15));
        accessButton.setSize(100, 40);
        accessButton.setLocation(200, 120);
        accessButton.setForeground(Color.WHITE);
        accessButton.setBackground(Color.BLACK);
        accessButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                Login.showRegisterDialog();
            }
        });

        loginPanel.add(usernameLabel);
        loginPanel.add(usernameField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        loginPanel.add(loginButton);
        loginPanel.add(accessButton);

        loginFrame.add(loginPanel, BorderLayout.CENTER);
    }

    private void initializeCalendar() {
        calendarPanel = new JPanel();
        calendarPanel.setLayout(new BorderLayout());
        calendarPanel.setVisible(false);

        label = new JLabel();
        label.setHorizontalAlignment(SwingConstants.CENTER);

        JButton button1 = new JButton("Previous Month");
        button1.setBackground(Color.black);
        button1.setForeground(Color.WHITE);
        button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                cal.add(Calendar.MONTH, -1);
                MonthlyView.updateMonth();
            }
        });

        JButton button2 = new JButton("Next Month");
        button2.setBackground(Color.black);
        button2.setForeground(Color.WHITE);
        button2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                cal.add(Calendar.MONTH, +1);
                MonthlyView.updateMonth();
            }
        });

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.add(button1, BorderLayout.WEST);
        headerPanel.add(label, BorderLayout.CENTER);
        headerPanel.add(button2, BorderLayout.EAST);
      
        String[] columns = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
        model = new DefaultTableModel(null, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        JTable table = new JTable(model) {
            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                return new TableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                        JButton button = new JButton();
                        if (value != null) {
                            button.setText(value.toString());
                        }
                        button.setBackground(Color.WHITE);
                        return button;
                    }
                };
            }
        };

        table.setRowHeight(140);

        JScrollPane pane = new JScrollPane(table);
        
        JButton updateUserButton = new JButton("Update user");
        updateUserButton.setSize(150, 80);
        updateUserButton.setBackground(Color.WHITE);
        updateUserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                Login.updateUserDialog();
            }
        });

        JButton createEventButton = new JButton("Create Event");
        createEventButton.setSize(150, 80);
        createEventButton.setBackground(Color.WHITE);
        createEventButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                CreateEvent.showCreateEventDialog();
            }
        });

        JButton eventReminderButton = new JButton("Search Event");
        eventReminderButton.setSize(300, 80);
        eventReminderButton.setBackground(Color.WHITE);
        eventReminderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                SearchEvent.searchEventDialog();
            }
        });
        
        JButton DailyButton = new JButton("Daily View");
        DailyButton.setSize(300, 80);
        DailyButton.setBackground(Color.WHITE);
        DailyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                DailyView.showDailyEvents();
            }
        });

        JButton weeklyButton = new JButton("Weekly View");
        weeklyButton.setSize(300, 80);
        weeklyButton.setBackground(Color.WHITE);
        weeklyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                WeeklyView.showWeeklyCalendar();
            }
        });

        calendarPanel.add(headerPanel, BorderLayout.NORTH);
        calendarPanel.add(pane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(updateUserButton);
        buttonPanel.add(createEventButton);
        buttonPanel.add(eventReminderButton);
        buttonPanel.add(DailyButton);
        buttonPanel.add(weeklyButton);

        calendarPanel.add(buttonPanel, BorderLayout.SOUTH);

        this.add(calendarPanel, BorderLayout.CENTER);
        MonthlyView.updateMonth();

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                int row = table.rowAtPoint(e.getPoint());
		String month = SwingCalendar.cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
        	int year = SwingCalendar.cal.get(Calendar.YEAR);
        	int startDay = SwingCalendar.cal.get(Calendar.DAY_OF_WEEK);
                if (col >= 0 && row >= 0) {
                    String formattedDate = MonthlyView.getFormattedDateForColumn(year, month, startDay, row, col);
                    MonthlyView.showEventsForDate(formattedDate);
                }
            }
        });
        ReminderManager.setupRemindersForAllEvents();
        showCalendar();
    }
    

    private void showCalendar() {
        this.setVisible(true);
        calendarPanel.setVisible(true);
    }
    
    public static String getUsername() {
    	return username;
    }
    
    public static String getPassword() {
    	return password;
    }

    public static void main(String[] arguments) {
        SwingUtilities.invokeLater(() -> {
            JFrame.setDefaultLookAndFeelDecorated(true);
            SwingCalendar sc = new SwingCalendar();
        });
    }
}

