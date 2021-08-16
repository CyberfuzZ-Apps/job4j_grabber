package ru.job4j.grabber;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {
    private final Connection cnn;

    public PsqlStore(Properties cfg) {
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
            cnn = DriverManager.getConnection(
                    cfg.getProperty("jdbc.url"),
                    cfg.getProperty("jdbc.username"),
                    cfg.getProperty("jdbc.password"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement statement = cnn.prepareStatement(
                "insert into post (name, text, link, created) values (?, ?, ?, ?)")) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getDescription());
            statement.setString(3, post.getLink());
            statement.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> posts = new ArrayList<>();
        try (PreparedStatement statement = cnn.prepareStatement(
                "select * from post")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    posts.add(new Post(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("link"),
                            resultSet.getString("text"),
                            resultSet.getTimestamp("created").toLocalDateTime()
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    @Override
    public Post findById(int id) {
        Post post = new Post();
        try (PreparedStatement statement = cnn.prepareStatement(
                "select * from post where id = ?")) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    post.setId(resultSet.getInt("id"));
                    post.setTitle(resultSet.getString("name"));
                    post.setLink(resultSet.getString("link"));
                    post.setDescription(resultSet.getString("text"));
                    post.setCreated(resultSet.getTimestamp("created").toLocalDateTime());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return post;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }

    public static void main(String[] args) {
        try (InputStream in = PsqlStore.class.getClassLoader()
                .getResourceAsStream("app.properties")) {
            Properties cfg = new Properties();
            cfg.load(in);
            PsqlStore psqlStore = new PsqlStore(cfg);
            Post post1 = new Post(
                    1,
                    "Заголовок1",
                    "Ссылка1",
                    "Описание1",
                    LocalDateTime.now().withNano(0)
            );
            Post post2 = new Post(
                    2,
                    "Заголовок2",
                    "Ссылка2",
                    "Описание2",
                    LocalDateTime.now().withNano(0).minusDays(1)
            );
            psqlStore.save(post1);
            psqlStore.save(post2);
            System.out.println(psqlStore.getAll());
            System.out.println("============");
            System.out.println(psqlStore.findById(post2.getId()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
