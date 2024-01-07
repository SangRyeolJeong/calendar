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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class UpdateEvent {

    private static int userId; 
    private static Date eventDay; 
    private static final String url = "jdbc:postgresql://127.0.0.1:5432/sangryeol";
    private static final String user = "dbms_practice";
    private static final String dbPassword = "dbms_practice";

    public static void setUserId(int id) {
        userId = id;
    }
    
    public static void seteventDay(Date date) {
        eventDay = date;
    }
    
    public static void showUpdateEventDialog(int eventId) {
    
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

        String selectQuery = "SELECT * FROM event WHERE event_id = ?";
        try (Connection connection = DriverManager.getConnection(url, user, dbPassword);
             PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
            selectStatement.setInt(1, eventId);
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                if (resultSet.next()) {
                    
                    userId = resultSet.getInt("user_id"); // Store user ID
                    String existingEventname = resultSet.getString("eventname");
                    Date existingSqlDate = resultSet.getDate("day");
                    Time existingStartTime = resultSet.getTime("starttime");
                    Time existingEndTime = resultSet.getTime("endtime");
                    String existingPlace = resultSet.getString("place");
                    int existingInterval = resultSet.getInt("interval");
                    int existingEventTimeframe = resultSet.getInt("timeframe");

                    yearComboBox.setSelectedItem(existingSqlDate.toLocalDate().getYear());
                    monthComboBox.setSelectedItem(getMonthName(existingSqlDate.toLocalDate().getMonthValue()));
                    dayComboBox.setSelectedItem(existingSqlDate.toLocalDate().getDayOfMonth());
                    startHourComboBox.setSelectedItem(existingStartTime.toLocalTime().getHour());
                    startMinuteComboBox.setSelectedItem(existingStartTime.toLocalTime().getMinute());
                    endHourComboBox.setSelectedItem(existingEndTime.toLocalTime().getHour());
                    endMinuteComboBox.setSelectedItem(existingEndTime.toLocalTime().getMinute());
                    eventNameField.setText(existingEventname);
                    eventPlaceField.setText(existingPlace);
                    remindersComboBox.setSelectedItem(String.valueOf(existingInterval));
                    timeframeComboBox.setSelectedItem(String.valueOf(existingEventTimeframe));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int option = JOptionPane.showConfirmDialog(null, message, "Update Event", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            int selectedYear = (int) yearComboBox.getSelectedItem();
            String selectedMonth = (String) monthComboBox.getSelectedItem();
            int selectedDay = (int) dayComboBox.getSelectedItem();
            int startHour = (int) startHourComboBox.getSelectedItem();
            int startMinute = (int) startMinuteComboBox.getSelectedItem();
            int endHour = (int) endHourComboBox.getSelectedItem();
            int endMinute = (int) endMinuteComboBox.getSelectedItem();
            String eventname = eventNameField.getText();
            String place = eventPlaceField.getText();
            int interval = Integer.parseInt((String) remindersComboBox.getSelectedItem());
            int eventTimeframe = Integer.parseInt((String) timeframeComboBox.getSelectedItem());

            if (isEndTimeBeforeStartTime(startHour, startMinute, endHour, endMinute)) {
                JOptionPane.showMessageDialog(null, "시간을 다시 설정해주세요.");
                return;
            }

            Date sqlDate = Date.valueOf(String.format("%d-%02d-%02d", selectedYear, getMonthNumber(selectedMonth), selectedDay));
            Time startTime = Time.valueOf(String.format("%02d:%02d:00", startHour, startMinute));
            Time endTime = Time.valueOf(String.format("%02d:%02d:00", endHour, endMinute));

            if (isEventTimeOverlap(userId, sqlDate, startTime, endTime, eventId)) {
                JOptionPane.showMessageDialog(null, "The selected time overlaps with an existing event. Please choose a different time.");
                return; 
            }
            updateEventInDatabase(eventId, userId, eventname, sqlDate, startTime, endTime, place, interval, eventTimeframe);
            JOptionPane.showMessageDialog(null, "Event is successfully updated!");
            ReminderManager.setupRemindersForAllEvents();
        }
    }
    
    private static void updateEventInDatabase(int eventId, int userId, String eventname, Date sqlDate, Time startTime, Time endTime, String place, int interval, int eventTimeframe) {
        try (Connection connection = DriverManager.getConnection(url, user, dbPassword)) {
            String updateQuery = "UPDATE event SET user_id=?, eventname=?, day=?, starttime=?, endtime=?, place=?, interval=?, timeframe=? WHERE event_id=?";
            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                updateStatement.setInt(1, userId);
                updateStatement.setString(2, eventname);
                updateStatement.setDate(3, sqlDate);
                updateStatement.setTime(4, startTime);
                updateStatement.setTime(5, endTime);
                updateStatement.setString(6, place);
                updateStatement.setInt(7, interval);
                updateStatement.setInt(8, eventTimeframe);
                updateStatement.setInt(9, eventId);  

                updateStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error while updating event data in the database");
        }
    }

    private static boolean isEndTimeBeforeStartTime(int availstartHour, int availstartMinute, int availendHour, int availendMinute) {
        return availendHour < availstartHour || (availendHour == availstartHour && availendMinute <= availstartMinute);
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

	public static String getMonthName(int monthNumber) {
        switch (monthNumber) {
            case 1:
                return "January";
            case 2:
                return "February";
            case 3:
                return "March";
            case 4:
                return "April";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "July";
            case 8:
                return "August";
            case 9:
                return "September";
            case 10:
                return "October";
            case 11:
                return "November";
            case 12:
                return "December";
            default:
                throw new IllegalArgumentException("Invalid month");
        }
    }

    private static boolean isEventTimeOverlap(int userId, Date sqlDate, Time startTime, Time endTime, int eventId) {
        try (Connection connection = DriverManager.getConnection(url, user, dbPassword)) {
            String query = "SELECT COUNT(*) FROM event WHERE user_id = ? AND day = ? AND " +
                    "((starttime < ? AND endtime > ?) OR (starttime > ? AND endtime < ?)) AND event_id != ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, userId);
                preparedStatement.setDate(2, sqlDate);
                preparedStatement.setTime(3, endTime);
                preparedStatement.setTime(4, startTime);
                preparedStatement.setTime(5, startTime);
                preparedStatement.setTime(6, endTime);
                preparedStatement.setInt(7, eventId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int overlapCount = resultSet.getInt(1);
                        return overlapCount > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
} 
