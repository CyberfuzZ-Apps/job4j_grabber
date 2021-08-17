package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.Parse;
import ru.job4j.grabber.Post;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SqlRuParse implements Parse {

    private final DateTimeParser dateTimeParser;

    public SqlRuParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    @Override
    public List<Post> list(String link) {
        List<Post> rsl = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(link).get();
            Elements row = doc.select(".postslisttopic");
            for (Element td : row) {
                Element href = td.child(0);
                Element element = td.parent();
                Post post = new Post();
                post.setLink(href.attr("href"));
                post.setTitle(href.text());
                post.setCreated(dateTimeParser.parse(element.child(5).text()));
                rsl.add(post);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rsl;
    }

    @Override
    public Post detail(String link) {
        Post post = new Post();
        try {
            Document doc = Jsoup.connect(link).get();
            Elements row = doc.select(".msgBody");
            String description = row.get(1).text();
            post.setDescription(description);
            Elements dates = doc.select(".msgFooter");
            String date = dates.get(0).text();
            int indexLast = date.indexOf("[") - 1;
            date = date.substring(0, indexLast);
            post.setCreated(dateTimeParser.parse(date));
            Elements titles = doc.select(".messageHeader");
            String title = titles.get(0).text();
            post.setTitle(title.substring(0, title.length() - 6));
            post.setLink(link);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return post;
    }

    public static void main(String[] args) {
        SqlRuParse s = new SqlRuParse(new SqlRuDateTimeParser());
        System.out.println(s.list("https://www.sql.ru/forum/job-offers"));
        System.out.println("===============");
        System.out.println(s.detail("https://www.sql.ru/forum/1338039/razrabotchik-baz-dannyh-moskva"));
    }
}
