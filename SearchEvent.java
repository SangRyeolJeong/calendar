import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SearchEvent {
    private static int event_userId;
	private static final String url = "jdbc:postgresql://127.0.0.1:5432/sangryeol";
    private static final String user = "dbms_practice";
    private static final String dbPassword = "dbms_practice";

    public static void searchEventDialog() {
        String eventName = JOptionPane.showInputDialog("Enter event name:");
        String eventPlace = JOptionPane.showInputDialog("Enter event place:");

        List<String> events = searchEvents(eventName, eventPlace);

        if (events.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No events found for the specified criteria.");
        } else {
            StringBuilder resultMessage = new StringBuilder("Found events:\n\n");
            for (String event : events) {
                resultMessage.append(event).append("\n\n");
            }
            JOptionPane.showMessageDialog(null, resultMessage.toString());
        }
    }

    private static List<String> searchEvents(String eventName, String eventPlace) {
	    event_userId = Login.getUserID();
	    List<String> events = new ArrayList<>();

	    try {
		Connection connection = DriverManager.getConnection(url, user, dbPassword);

		StringBuilder queryBuilder = new StringBuilder("SELECT * FROM event WHERE user_id = ?");

		if (eventName != null && !eventName.isEmpty()) {
		    queryBuilder.append(" AND eventname LIKE ?");
		}

		if (eventPlace != null && !eventPlace.isEmpty()) {
		    queryBuilder.append(" AND place LIKE ?");
		}

		try (PreparedStatement preparedStatement = connection.prepareStatement(queryBuilder.toString())) {
		    int parameterIndex = 1;

		    preparedStatement.setInt(parameterIndex++, event_userId);

		    if (eventName != null && !eventName.isEmpty()) {
		        preparedStatement.setString(parameterIndex++, "%" + eventName + "%");
		    }

		    if (eventPlace != null && !eventPlace.isEmpty()) {
		        preparedStatement.setString(parameterIndex, "%" + eventPlace + "%");
		    }

		    try (ResultSet resultSet = preparedStatement.executeQuery()) {
		        while (resultSet.next()) {
		            int eventId = resultSet.getInt("event_id");
		            String resultEventName = resultSet.getString("eventname");
		            String resultEventPlace = resultSet.getString("place");
		            events.add("Event Id: " + eventId + "\nEvent Name: " + resultEventName + "\nEvent Place: " + resultEventPlace);
		        }
		    }
		}

		connection.close();

	    } catch (SQLException e) {
		e.printStackTrace();
		JOptionPane.showMessageDialog(null, "Error while searching for events in the database");
	    }
	    return events;
	}

}
