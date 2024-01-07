import java.sql.Connection;
import java.sql.Date;
import java.sql.Time;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.swing.*;
import java.awt.GridLayout;



public class MonthlyView {
    private static final String url = "jdbc:postgresql://127.0.0.1:5432/sangryeol";
    private static final String user = "dbms_practice";
    private static final String dbPassword = "dbms_practice";

    static Calendar currentCalendar = Calendar.getInstance();
    static int today = currentCalendar.get(Calendar.DAY_OF_MONTH); 
    static int userId;
    static void updateMonth() {
        SwingCalendar.cal.set(Calendar.DAY_OF_MONTH, 1);

        String month = SwingCalendar.cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
        int year = SwingCalendar.cal.get(Calendar.YEAR);
        SwingCalendar.label.setText(month + " " + year);

        int today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int startDay = SwingCalendar.cal.get(Calendar.DAY_OF_WEEK);
        int numberOfDays = SwingCalendar.cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        int weeks = SwingCalendar.cal.getActualMaximum(Calendar.WEEK_OF_MONTH);

        SwingCalendar.model.setRowCount(0);
        SwingCalendar.model.setRowCount(weeks);

        int i = startDay - 1;
        for (int day = 1; day <= numberOfDays; day++) {
        
            String formattedDate = String.format("%d-%02d-%02d", year, CreateEvent.getMonthNumber(month), day);

            Date sqlDate = Date.valueOf(formattedDate);
            userId = Login.getUserID();

            List<String> events = getEventsFromDatabase(sqlDate, userId);

            StringBuilder cellValue = new StringBuilder(String.valueOf(day));

            if (events != null && !events.isEmpty()) {
                cellValue.append(" - ").append(String.join(", ", events));
            }

            if (day == today && SwingCalendar.cal.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH)) {
                cellValue.insert(0, "Today: ");
            }

            SwingCalendar.model.setValueAt(cellValue.toString(), i / 7, i % 7);
            i = i + 1;
        }
    }
    
    public static String getFormattedDateForColumn(int year, String month, int startDay, int row, int col) {
	    int day = (row * 7) + col - startDay + 2; // Adding 2 because day starts from 1

	    if (day < 1 || day > getNumberOfDaysInMonth(year, month)) {
		    return "";
	    }
	    return String.format("%d-%02d-%02d", year, CreateEvent.getMonthNumber(month), day);
    }
     
    public static int getNumberOfDaysInMonth(int year, String month) {
	    Calendar cal = Calendar.getInstance();
	    cal.set(Calendar.YEAR, year);
	    cal.set(Calendar.MONTH, CreateEvent.getMonthNumber(month) - 1); // Month is 0-based
	    return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    
    public static List<String> getEventsFromDatabase(Date sqlDate, int userId) {
        try (Connection connection = DriverManager.getConnection(url, user, dbPassword)) {
            String query = "SELECT eventname FROM event WHERE day = ? AND user_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setDate(1, sqlDate);
                preparedStatement.setInt(2, userId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {

                    List<String> events = new ArrayList<>();

                    while (resultSet.next()) {
                        events.add(resultSet.getString("eventname"));
                    }
                    return events;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> getEventContentsFromDatabase(Date sqlDate, int userId) {
        try (Connection connection = DriverManager.getConnection(url, user, dbPassword)) {
            String query = "SELECT * FROM event WHERE day = ? AND user_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setDate(1, sqlDate);
                preparedStatement.setInt(2, userId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {

                    List<String> events = new ArrayList<>();

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

    public static void showEventsForDate(String selectedDate) {
        Date sqlDate;
        try {
            sqlDate = Date.valueOf(selectedDate);
        } catch (IllegalArgumentException e) {
            return;
        }

        List<String> events = getEventContentsFromDatabase(sqlDate, userId);
        if (events == null || events.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No events for this date.");
            return;
        }

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
                updateMonth();
            });

            deleteButton.addActionListener(e -> {
                String[] splitEvent = event.split("Event");
                
                int splitEventId = Integer.valueOf((splitEvent[1].substring(5, splitEvent[1].length())).trim());
                DeleteEvent.showDeleteEventDialog(splitEventId);
                SwingCalendar.eventsMap.remove(event); 
                updateMonth();   
             });
        }
        JOptionPane.showMessageDialog(null, panel, "Events", JOptionPane.INFORMATION_MESSAGE);
    }
}
