import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Date;
import java.sql.ResultSet;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.util.ArrayList;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class CreateEvent {

    private static final String url = "jdbc:postgresql://127.0.0.1:5432/sangryeol";
    private static final String user = "dbms_practice";
    private static final String dbPassword = "dbms_practice";

    public static void showCreateEventDialog() {

        Integer[] years = {2023, 2024, 2025}; 
        JComboBox<Integer> yearComboBox = new JComboBox<>(years);

        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        JComboBox<String> monthComboBox = new JComboBox<>(months);

        Integer[] days = new Integer[31];
        for (int i = 1; i <= 31; i++) {
            days[i - 1] = i;
        }
        JComboBox<Integer> dayComboBox = new JComboBox<>(days);

        Integer[] hours = new Integer[24];
        for (int i = 0; i < 24; i++) {
            hours[i] = i;
        }
        JComboBox<Integer> startHourComboBox = new JComboBox<>(hours);

        Integer[] minutes = new Integer[60];
        for (int i = 0; i < 12; i++) {
            minutes[i] = i * 5;
        }
        JComboBox<Integer> startMinuteComboBox = new JComboBox<>(minutes);

        JComboBox<Integer> endHourComboBox = new JComboBox<>(hours);

        JComboBox<Integer> endMinuteComboBox = new JComboBox<>(minutes);

        JTextField eventNameField = new JTextField(10);

        JTextField eventPlaceField = new JTextField(10);

        String[] reminders = {"0", "15", "30", "45", "60"};
        JComboBox<String> remindersComboBox = new JComboBox<>(reminders);

        String[] timeframe = {"15", "30", "45", "60"};
        JComboBox<String> timeframeComboBox = new JComboBox<>(timeframe);

        JPanel datePanel = new JPanel();
        datePanel.add(yearComboBox);
        datePanel.add(monthComboBox);

        datePanel.add(dayComboBox);

        JPanel startTimepanel = new JPanel();
        startTimepanel.add(startHourComboBox);
        startTimepanel.add(startMinuteComboBox);

        JPanel endTimepanel = new JPanel();
        endTimepanel.add(endHourComboBox);

        endTimepanel.add(endMinuteComboBox);

        JPanel eventNamePanel = new JPanel();
        eventNamePanel.add(eventNameField);

        JPanel eventPlacePanel = new JPanel();
        eventPlacePanel.add(eventPlaceField);

        JPanel remindersPanel = new JPanel();
        remindersPanel.add(remindersComboBox);

        JPanel timeframePanel = new JPanel();
        timeframePanel.add(timeframeComboBox);

        JPanel dialogPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;

        constraints.gridx = 0;
        constraints.gridy = 0;
        dialogPanel.add(new JLabel("Select a day you want:"), constraints);

        constraints.gridy = 1;
        dialogPanel.add(new JLabel("Start Time:"), constraints);

        constraints.gridy = 2;
        dialogPanel.add(new JLabel("End Time:"), constraints);

        constraints.gridy = 3;
        dialogPanel.add(new JLabel("Enter event name:"), constraints);

        constraints.gridy = 4;
        dialogPanel.add(new JLabel("Enter event place:"), constraints);

        constraints.gridy = 5;
        dialogPanel.add(new JLabel("Interval:"), constraints);

        constraints.gridy = 6;
        dialogPanel.add(new JLabel("Timeframe:"), constraints);

        constraints.gridx = 1;

        constraints.gridy = 0;
        dialogPanel.add(datePanel, constraints);

        constraints.gridy = 1;
        dialogPanel.add(startTimepanel, constraints);

        constraints.gridy = 2;
        dialogPanel.add(endTimepanel, constraints);

        constraints.gridy = 3;
        dialogPanel.add(eventNamePanel, constraints);

        constraints.gridy = 4;
        dialogPanel.add(eventPlacePanel, constraints);

        constraints.gridy = 5;
        dialogPanel.add(remindersComboBox, constraints);

        constraints.gridy = 6;
        dialogPanel.add(timeframeComboBox, constraints);

        Object[] message = {dialogPanel};

        int option = JOptionPane.showConfirmDialog(null, message, "Create Event", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            int selectedYear = (int) yearComboBox.getSelectedItem();
            String selectedMonth = (String) monthComboBox.getSelectedItem();
            int selectedDay = (int) dayComboBox.getSelectedItem();
            int startHour = (int) startHourComboBox.getSelectedItem();
            int startMinute = (int) startMinuteComboBox.getSelectedItem();
            int endHour = (int) endHourComboBox.getSelectedItem();
            int endMinute = (int) endMinuteComboBox.getSelectedItem();
            String event = eventNameField.getText();
            String place = eventPlaceField.getText();
            int interval = Integer.parseInt((String) remindersComboBox.getSelectedItem());
            int eventTimeframe = Integer.parseInt((String) timeframeComboBox.getSelectedItem());

            if (isEndTimeBeforeStartTime(startHour, startMinute, endHour, endMinute)) {
                JOptionPane.showMessageDialog(null, "시간을 다시 설정해주세요.");
                return;
            }
            
            int userId = getLoggedInUserId(); //get userid that logged in.
            String formattedDate = String.format("%d-%02d-%02d", selectedYear, getMonthNumber(selectedMonth), selectedDay);

            Date sqlDate = Date.valueOf(formattedDate);
            Time startTime = Time.valueOf(String.format("%02d:%02d:00", startHour, startMinute));
            Time endTime = Time.valueOf(String.format("%02d:%02d:00", endHour, endMinute));
            
            if (isEventTimeOverlap(userId, sqlDate, startTime, endTime)) {
                JOptionPane.showMessageDialog(null, "The selected time overlaps with an existing event. Please choose a different time.");
                return; // Do not proceed with event creation
            }

            try {
                Connection connection = DriverManager.getConnection(url, user, dbPassword);

                String insertQuery = "INSERT INTO event (user_id, eventname, day, starttime, endtime, place, interval, timeframe) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

                  
                    preparedStatement.setInt(1, userId);
                    preparedStatement.setString(2, event);
                    preparedStatement.setDate(3, sqlDate);
                    preparedStatement.setTime(4, startTime);
                    preparedStatement.setTime(5, endTime);
                    preparedStatement.setString(6, place);
                    preparedStatement.setInt(7, interval);
                    preparedStatement.setInt(8, eventTimeframe);

                    preparedStatement.executeUpdate();
                }
		        JOptionPane.showMessageDialog(null, "Event is successfully created!");
                connection.close();

                String dateKey = selectedMonth + " " + selectedDay + ", " + selectedYear;
                SwingCalendar.eventsMap.computeIfAbsent(dateKey, k -> new ArrayList<>()).add(event);

                MonthlyView.updateMonth();
                ReminderManager.setupRemindersForAllEvents();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error while storing event data in the database");
            }
        }
    }

    private static boolean isEndTimeBeforeStartTime(int startHour, int startMinute, int endHour, int endMinute) {
        return endHour < startHour || (endHour == startHour && endMinute <= startMinute);
    }
     
    public static int getMonthNumber(String month) {
    switch (month) {
        case "January":
            return 1;
        case "February":
            return 2;
        case "March":
            return 3;
        case "April":
            return 4;
        case "May":
            return 5;
        case "June":
            return 6;
        case "July":
            return 7;
        case "August":
            return 8;
        case "September":
            return 9;
        case "October":
            return 10;
        case "November":
            return 11;
        case "December":
            return 12;
        default:
            throw new IllegalArgumentException("Invalid month: " + month);
        }
    }


    private static int getLoggedInUserId() {
        int userId = -1;
        String eventusername = SwingCalendar.getUsername();
        String eventuserpassword = SwingCalendar.getPassword();

        try {
            Connection connection = DriverManager.getConnection(url, user, dbPassword);

            String query = "SELECT user_id FROM \"user\" WHERE name = ? AND password = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, eventusername);
                preparedStatement.setString(2, eventuserpassword);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        userId = resultSet.getInt("user_id");
                    }
                }
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userId;
    }
    
    private static boolean isEventTimeOverlap(int userId, Date sqlDate, Time startTime, Time endTime) {
        try {
            Connection connection = DriverManager.getConnection(url, user, dbPassword);
            String query = "SELECT COUNT(*) FROM event WHERE user_id = ? AND day = ? AND " +
                    "((starttime < ? AND endtime > ?) OR (starttime > ? AND endtime < ?))";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, userId);
                preparedStatement.setDate(2, sqlDate);
                preparedStatement.setTime(3, endTime);
                preparedStatement.setTime(4, startTime);
                preparedStatement.setTime(5, startTime);
                preparedStatement.setTime(6, endTime);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int overlapCount = resultSet.getInt(1);
                        return overlapCount > 0;
                    }
                }
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
