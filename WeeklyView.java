import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridLayout;
import java.util.Calendar;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Time;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WeeklyView {
    private static final String url = "jdbc:postgresql://127.0.0.1:5432/sangryeol";
    private static final String user = "dbms_practice";
    private static final String dbPassword = "dbms_practice";
    static int userId;

    public static void showWeeklyCalendar() {
    JFrame weeklyFrame = new JFrame("Weekly Calendar");
    weeklyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    weeklyFrame.setSize(1600, 800);
    weeklyFrame.setLayout(new BorderLayout());
    weeklyFrame.setLocationRelativeTo(null);

    JTable weeklyTable = new JTable();
    JPanel weekNavPanel = new JPanel();

    JPanel weekPanel = new JPanel(new BorderLayout());
    weekPanel.setBackground(Color.WHITE);

    JButton prevWeekButton = new JButton("Previous Week");
    prevWeekButton.setBackground(Color.BLACK);
    prevWeekButton.setForeground(Color.WHITE);
    prevWeekButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            SwingCalendar.cal.add(Calendar.DATE, -7);
            updateWeeklyCalendar(weeklyTable, SwingCalendar.cal.getTime());
        }
    });

    JButton nextWeekButton = new JButton("Next Week");
    nextWeekButton.setBackground(Color.BLACK);
    nextWeekButton.setForeground(Color.WHITE);
    nextWeekButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            SwingCalendar.cal.add(Calendar.DATE, 7);
            updateWeeklyCalendar(weeklyTable, SwingCalendar.cal.getTime());
        }
    });

    weekNavPanel.add(prevWeekButton);
    weekNavPanel.add(nextWeekButton);

    weeklyTable.setGridColor(Color.LIGHT_GRAY);

    DefaultTableModel weeklyModel = new DefaultTableModel(new Object[]{"날짜", "일정1", "일정2", "일정3", "일정4", "일정5", "일정6"}, 0);
    weeklyTable.setModel(weeklyModel);

    weekPanel.add(weekNavPanel, BorderLayout.NORTH);
    weekPanel.add(new JScrollPane(weeklyTable), BorderLayout.CENTER);

    weeklyFrame.add(weekPanel, BorderLayout.CENTER);
    weeklyFrame.setVisible(true);

    SwingCalendar.cal = Calendar.getInstance();
    updateWeeklyCalendar(weeklyTable, SwingCalendar.cal.getTime());
}

    public static void updateWeeklyCalendar(JTable weeklyTable, java.util.Date date) {
        DefaultTableModel weeklyModel = (DefaultTableModel) weeklyTable.getModel();
        weeklyModel.setRowCount(0);

        int rowHeight = 100;
        weeklyTable.setRowHeight(rowHeight);

        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(date);
        for (int i = 0; i < 7; i++) {
            int year = SwingCalendar.cal.get(Calendar.YEAR);
            int dayOfMonth = currentCalendar.get(Calendar.DAY_OF_MONTH);

            String formattedDate = String.format("%d-%02d-%02d", year, currentCalendar.get(Calendar.MONTH) + 1, dayOfMonth);

            Date sqlDate = Date.valueOf(formattedDate);
            
            userId = Login.getUserID();

            List<String> events = getEventContentsFromDatabase(sqlDate, userId);

            String dayName = currentCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US);
            String cellValue = dayName + " " + dayOfMonth;

            if (dayOfMonth == MonthlyView.today && currentCalendar.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH)) {
                cellValue = "Today: " + cellValue;
            }


            Object[] row = new Object[events.size() + 1]; 
            row[0] = "<html>" + formattedDate + "</html>"; // 줄바꿈을 위해 html 태그 사용

        for (int j = 0; j < events.size(); j++) {
            String[] splitEvent = events.get(j).split("\n");
            StringBuilder htmlFormattedEvent = new StringBuilder("<html>");
            for (String eventPart : splitEvent) {
            htmlFormattedEvent.append(eventPart).append("<br>"); 
            }
            htmlFormattedEvent.append("</html>");
            row[j + 1] = htmlFormattedEvent.toString();
        }
        weeklyModel.addRow(row);
        currentCalendar.add(Calendar.DATE, 1); 
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
	    cal.set(Calendar.MONTH, CreateEvent.getMonthNumber(month) - 1);
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

                        String eventName = resultSet.getString("eventname");
                        Date eventDate = resultSet.getDate("day");
                        String eventPlace = resultSet.getString("place");
                        Time eventStarttime = resultSet.getTime("starttime");
                        Time eventEndtime = resultSet.getTime("endtime");

                        eventStringBuilder.append("Event Name: ").append(eventName).append("\n");
                        eventStringBuilder.append("Event Date: ").append(eventDate).append("\n");
                        eventStringBuilder.append("Event Place: ").append(eventPlace).append("\n");
                        eventStringBuilder.append("Event starts: ").append(eventStarttime).append("\n");
                        eventStringBuilder.append("Event ends: ").append(eventEndtime).append("\n");

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
            eventTextArea.setEditable(false);
            panel.add(eventTextArea);
            panel.add(modifyButton);
            panel.add(deleteButton); 

            modifyButton.addActionListener(e -> {
                JOptionPane.showMessageDialog(null, "Implement modification logic for: \n" + event);
                String[] splitEvent = event.split("Event");
                // get eventid
                int splitEventId = Integer.valueOf((splitEvent[1].substring(5, splitEvent[1].length())).trim());
                
                UpdateEvent.showUpdateEventDialog(splitEventId);
                MonthlyView.updateMonth();
            });
            deleteButton.addActionListener(e -> {
                    String[] splitEvent = event.split("Event");
                    
                    int splitEventId = Integer.valueOf((splitEvent[1].substring(5, splitEvent[1].length())).trim());
                    DeleteEvent.showDeleteEventDialog(splitEventId);
                    SwingCalendar.eventsMap.remove(event); 
                    MonthlyView.updateMonth();
                
            });
        }
        JOptionPane.showMessageDialog(null, panel, "Events", JOptionPane.INFORMATION_MESSAGE);
    }
} 
