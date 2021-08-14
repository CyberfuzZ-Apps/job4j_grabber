package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {

    private static Properties properties = new Properties();
    private static Connection connection;

    public static void connect() {
        try {
            Class.forName(properties.getProperty("jdbc.driver"));
            connection = DriverManager.getConnection(
                    properties.getProperty("jdbc.url"),
                    properties.getProperty("jdbc.username"),
                    properties.getProperty("jdbc.password"));
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        readProperties();
        connect();
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDetail job = newJob(Rabbit.class).build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(
                            Integer.parseInt(
                                    properties.getProperty("rabbit.interval")))
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
        } catch (SchedulerException | InterruptedException se) {
            se.printStackTrace();
        }
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("Rabbit runs here ...");
            try (PreparedStatement pStatement = connection.prepareStatement(
                    "insert into rabbit (created_date) values (?);")) {
                pStatement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                pStatement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void readProperties() {
        try (InputStream in = AlertRabbit.class.getClassLoader()
                             .getResourceAsStream("rabbit.properties")) {
            properties.load(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
