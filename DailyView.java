import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.List; 

public class DailyView {
    private static int userId;
    private static final String url = "jdbc:postgresql://127.0.0.1:5432/sangryeol";
    private static final String user = "dbms_practice";
    private static final String dbPassword = "dbms_practice";

    public static void showDailyEvents() {
        String selectedDate = promptForDate();

        if (selectedDate == null) {
            return; 
        }

        Date sqlDate = Date.valueOf(selectedDate);
        userId = Login.getUserID();

        java.util.List<String> events = getEventContentsFromDatabase(sqlDate, userId);

        if (events == null || events.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No events for this date.");
            return;
        }

        displayDailyEvents(events);
    }

    private static String promptForDate() {
        String selectedDate = JOptionPane.showInputDialog("Enter date (yyyy-MM-dd):");

        // Validate the date format
        if (selectedDate != null && !selectedDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(null, "Invalid date format. Please enter a date in the format yyyy-MM-dd.");
            return null;
        }

        return selectedDate;
    }

    private static void displayDailyEvents(List<String> events) {
        JPanel panel = new JPanel(new GridLayout(0, 1));

        for (String event : events) {
            JButton modifyButton = new JButton("Modify Event");
            JButton deleteButton = new JButton("Delete Event");
            JTextArea eventTextArea = new JTextArea(event);

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(modifyButton);
            buttonPanel.add(deleteButton);
            eventTextArea.setEditable(false);
            panel.add(eventTextArea);
            panel.add(buttonPanel);

            modifyButton.addActionListener(e -> {
                JOptionPane.showMessageDialog(null, "Implement modification logic for: \n" + event);
                String[] splitEvent = event.split("Event");
                int splitEventId = Integer.valueOf((splitEvent[1].substring(5, splitEvent[1].length())).trim());
                UpdateEvent.showUpdateEventDialog(splitEventId);
            });

            deleteButton.addActionListener(e -> {
                String[] splitEvent = event.split("Event");
                int splitEventId = Integer.valueOf((splitEvent[1].substring(5, splitEvent[1].length())).trim());
                DeleteEvent.showDeleteEventDialog(splitEventId);
            });
        }

        JOptionPane.showMessageDialog(null, panel, "Events for " + events.get(0).split("\n")[2].substring(14), JOptionPane.INFORMATION_MESSAGE);
    }

    public static java.util.List<String> getEventContentsFromDatabase(Date sqlDate, int userId) {
        try (Connection connection = DriverManager.getConnection(url, user, dbPassword)) {
            String query = "SELECT * FROM event WHERE day = ? AND user_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setDate(1, sqlDate);
                preparedStatement.setInt(2, userId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {

                    // Explicitly specify java.util.List here
                    java.util.List<String> events = new ArrayList<>();

                    while (resultSet.next()) {
                        StringBuilder eventStringBuilder = new StringBuilder();

                        int eventId = resultSet.getInt("event_id");
                        String eventName = resultSet.getString("eventname");
                        Date eventDate = resultSet.getDate("day");
                        String eventPlace = resultSet.getString("place");
                        Time eventStarttime = resultSet.getTime("starttime");
                        Time eventEndtime = resultSet.getTime("endtime");
                        int interval = resultSet.getInt("interval");
                        int timeframe = resultSet.getInt("timeframe");

                        eventStringBuilder.append("Event Id: ").append(eventId).append("\n");
                        eventStringBuilder.append("Event Name: ").append(eventName).append("\n");
                        eventStringBuilder.append("Event Date: ").append(eventDate).append("\n");
                        eventStringBuilder.append("Event Place: ").append(eventPlace).append("\n");
                        eventStringBuilder.append("Event starts: ").append(eventStarttime).append("\n");
                        eventStringBuilder.append("Event ends: ").append(eventEndtime).append("\n");
                        eventStringBuilder.append("Interval: ").append(interval).append("\n");
                        eventStringBuilder.append("Timeframe: ").append(timeframe);

                        events.add(eventStringBuilder.toString());
                    }
                    return events;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

