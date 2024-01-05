import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;


public class Login {
    private static int event_userId;
    private static String event_userName;
    private static String event_userPassword;
    private static final String url = "jdbc:postgresql://127.0.0.1:5432/sangryeol";
    private static final String user = "dbms_practice";
    private static final String dbPassword = "dbms_practice";

    public static int authenticateUserAndGetID(String username, String password) {
        String sql = "SELECT user_id FROM \"user\" WHERE name = ? AND password = ?";

        try (Connection connection = DriverManager.getConnection(url, user, dbPassword);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            setEventUsername(username);
            setEventUserPassword(password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("user_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    public static String getName() {
        return event_userName;  
    }
    
    public static void setEventUsername(String name) {
        event_userName = name;
    }
    
    public static void setEventUserPassword(String userPassword) {
        event_userPassword = userPassword;
    }
    
    public static int getUserID() {
        event_userId = authenticateUserAndGetID(event_userName, event_userPassword);
        
        return event_userId;
        
    }

    public static void showRegisterDialog() {
        JTextField nameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JTextField emailField = new JTextField(20);

        Object[] message = {
            "Name:", nameField,
            "Password:", passwordField,
            "Email:", emailField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Register", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String password = new String(passwordField.getPassword());
            String email = emailField.getText();
            
            registerUser(name, password, email);
        }
    }

    public static void updateUserDialog() {
        JTextField nameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JTextField emailField = new JTextField(20);

        Object[] message = {
            "Password:", passwordField,
            "Email:", emailField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Update", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = getName();
            String newPassword = new String(passwordField.getPassword());
            String newEmail = emailField.getText();
           
            try (Connection connection = DriverManager.getConnection(url, user, dbPassword)) {
                String sql = "UPDATE \"user\" SET password = ?, email = ? WHERE name = ?";

                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setString(1, newPassword);
                    preparedStatement.setString(2, newEmail);
                    preparedStatement.setString(3, name);

                    int rowsAffected = preparedStatement.executeUpdate();
		    
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "User update successful!\n Please restart the application and login again.");
                        restartSwingCalendar(); // Restart SwingCalendar after successful update
                    } else {
                        JOptionPane.showMessageDialog(null, "User updating failed.\n Please reenter.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void restartSwingCalendar() {
        SwingUtilities.invokeLater(() -> {
            JFrame.setDefaultLookAndFeelDecorated(true);
            new SwingCalendar();
            System.exit(0);
        });
    }

    public static void registerUser(String name, String password, String email) {
        if (isUsernameExists(name)) {
            JOptionPane.showMessageDialog(null, "Username already exists. Please choose a different username.");
            return; 
        }

        try (Connection connection = DriverManager.getConnection(url, user, dbPassword)) {
            String sql = "INSERT INTO \"user\" (name, password, email) VALUES (?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, password);
                preparedStatement.setString(3, email);

                preparedStatement.executeUpdate();
                JOptionPane.showMessageDialog(null, "Registration successful!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean isUsernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM \"user\" WHERE name = ?";

        try (Connection connection = DriverManager.getConnection(url, user, dbPassword);
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
