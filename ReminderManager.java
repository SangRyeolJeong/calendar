import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;
import java.util.List;
import java.util.ArrayList;


public class ReminderManager {
    private static final String url = "jdbc:postgresql://127.0.0.1:5432/sangryeol";
    private static final String user = "dbms_practice";
    private static final String dbPassword = "dbms_practice";
    
    private static class Event {
        int eventId;
        Date date;
        Time startTime;
        int interval;
        int timeframe;
        String eventName;
    }
    
    private static List<Timer> timers = new ArrayList<>();

    public static void setupRemindersForAllEvents() {
        clearReminders();

        try (Connection connection = DriverManager.getConnection(url, user, dbPassword)) {
            String query = "SELECT event_id, day, starttime, interval, timeframe, eventname FROM event";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Event event = new Event();
                    event.eventId = resultSet.getInt("event_id");
                    event.date = resultSet.getDate("day");
                    event.startTime = resultSet.getTime("starttime");
                    event.interval = resultSet.getInt("interval");
                    event.timeframe = resultSet.getInt("timeframe");
                    event.eventName = resultSet.getString("eventname");

                    if (isSameDate(event.date, getCurrentDateOfCountry())) {
                        setupReminder(event);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void clearReminders() {
        for (Timer timer : timers) {
            timer.cancel();
            timer.purge();
        }
        timers.clear();
    }

    public static void setupReminder(Event event) {
        long currentTimeMillis = System.currentTimeMillis();
        long eventTimeMillis = getEventTimeMillis(event.date, event.startTime);
        long[] initialReminderTime = {eventTimeMillis - (event.timeframe * 60 * 1000)};

        if (initialReminderTime[0] < currentTimeMillis) {
            long timeDifference = currentTimeMillis - initialReminderTime[0];
            long intervalsElapsed = timeDifference / (event.interval * 60 * 1000);
            initialReminderTime[0] += (intervalsElapsed + 1) * (event.interval * 60 * 1000);
        }

        Timer timer = new Timer();
        timers.add(timer); 
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                displayReminder(new Date(initialReminderTime[0]), event);
                long updatedReminderTime = calculateUpdatedReminderTime(initialReminderTime[0], event.interval);
                initialReminderTime[0] = updatedReminderTime; 
            }
        }, initialReminderTime[0] - System.currentTimeMillis(), event.interval * 60 * 1000);
    }

	private static long getEventTimeMillis(Date date, Time startTime) {
	    LocalDateTime eventDateTime = LocalDateTime.of(date.toLocalDate(), startTime.toLocalTime());
	    return eventDateTime.atZone(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli();
	}

    private static void displayReminder(Date reminderTime, Event event) {
	    ZoneId koreaZone = ZoneId.of("Asia/Seoul");
	    ZonedDateTime currentTime = ZonedDateTime.now(koreaZone);

	    // Calculate the initial reminder time based on (event startTime - timeframe)
	    LocalDateTime eventDateTime = LocalDateTime.of(event.date.toLocalDate(), event.startTime.toLocalTime());
	    ZonedDateTime eventTime = eventDateTime.atZone(koreaZone);
	    ZonedDateTime initialReminderTime = eventTime.minusMinutes(event.timeframe);
	    
	    long timeDifferenceMinutes = java.time.Duration.between(currentTime, eventTime).toMinutes();
	    
	    String reminderMessage;
	    if (timeDifferenceMinutes <= 0) {
		return;
	    } else {
	    	if (timeDifferenceMinutes < event.timeframe) {
                long hours = (timeDifferenceMinutes) / 60;
                long minutes = (timeDifferenceMinutes) % 60;
                if (hours > 0) {
                    reminderMessage = String.format("Event Reminder: %d hours and %d minutes left until %s",
                        hours, minutes, event.eventName);
                } else {
                reminderMessage = String.format("Event Reminder: %d minutes left until %s!", minutes, event.eventName);
                }
		    } else {
		    return;
	    	}
	    }
	    JOptionPane.showMessageDialog(null, reminderMessage, "Reminder", JOptionPane.INFORMATION_MESSAGE);
	}

    private static Date getCurrentDateOfCountry() {
        ZoneId koreaZone = ZoneId.of("Asia/Seoul");
        return new Date(ZonedDateTime.now(koreaZone).toInstant().toEpochMilli());
    }

    private static long calculateUpdatedReminderTime(long reminderTime, int interval) {
        return reminderTime + (interval * 60 * 1000);
    }

    private static boolean isSameDate(Date date1, Date date2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(date1).equals(sdf.format(date2));
    }

    public static void startEventReminders() {
        setupRemindersForAllEvents();
    }
}

