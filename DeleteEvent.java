import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class DeleteEvent {
    // 이벤트 삭제
    public static void showDeleteEventDialog(int eventId) {
        int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this event?", "Confirmation", JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            try {
                String url = "jdbc:postgresql://127.0.0.1:5432/sangryeol";
                String user = "dbms_practice";
                String dbPassword = "dbms_practice";
                Connection connection = DriverManager.getConnection(url, user, dbPassword);
                String deleteQuery = "DELETE FROM event WHERE event_id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                    preparedStatement.setInt(1, eventId);
                    
                    preparedStatement.executeUpdate();
                }

                JOptionPane.showMessageDialog(null, "Event is successfully deleted!");
                ReminderManager.setupRemindersForAllEvents();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error while deleting event data from the database");
            }
        }
    }
}
