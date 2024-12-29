import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        List<Visitor> visitors = readVisitorsFromJson("books.json");

        // Задание 1: Вывести список посетителей и их количество.
        System.out.println("Список посетителей:");
        visitors.forEach(visitor -> System.out.println(visitor.getFirstName() + " " + visitor.getLastName()));
        System.out.println("Количество посетителей: " + visitors.size());

        // Задание 2: Вывести список и количество книг, добавленных посетителями в избранное, без повторений.
        Set<Book> uniqueBooks = visitors.stream()
                .flatMap(visitor -> visitor.getFavoriteBooks().stream())
                .collect(Collectors.toSet());
        System.out.println("Количество уникальных книг в избранном: " + uniqueBooks.size());

        // Задание 3: Отсортировать по году издания и вывести список книг.
        List<Book> sortedBooks = uniqueBooks.stream()
                .sorted(Comparator.comparingInt(Book::getPublicationYear))
                .toList();
        System.out.println("Список книг, отсортированных по году издания:");
        sortedBooks.forEach(book -> System.out.println(book.getTitle() + " (" + book.getPublicationYear() + ")"));

        // Задание 4: Проверить, есть ли у кого-то в избранном книга автора “Jane Austen”.
        boolean hasJaneAusten = visitors.stream()
                .flatMap(visitor -> visitor.getFavoriteBooks().stream())
                .anyMatch(book -> "Jane Austen".equals(book.getAuthor()));
        System.out.println("Есть ли книги автора 'Jane Austen' в избранном: " + hasJaneAusten);

        // Задание 5: Вывести максимальное число добавленных в избранное книг.
        int maxFavoriteBooks = visitors.stream()
                .mapToInt(visitor -> visitor.getFavoriteBooks().size())
                .max()
                .orElse(0);
        System.out.println("Максимальное количество добавленных в избранное книг: " + maxFavoriteBooks);

        // Задание 6: Группировка посетителей по категориям для SMS.
        double averageFavorites = visitors.stream()
                .mapToInt(visitor -> visitor.getFavoriteBooks().size())
                .average()
                .orElse(0);

        List<SmsMessage> smsMessages = visitors.stream()
                .filter(Visitor::isSubscribed)
                .map(visitor -> {
                    int favoriteCount = visitor.getFavoriteBooks().size();
                    String message;
                    if (favoriteCount > averageFavorites) {
                        message = "you are a bookworm";
                    } else if (favoriteCount < averageFavorites) {
                        message = "read more";
                    } else {
                        message = "fine";
                    }
                    return new SmsMessage(visitor.getPhoneNumber(), message);
                })
                .toList();

        System.out.println("SMS-сообщения:");
        smsMessages.forEach(sms -> System.out.println(sms.getPhoneNumber() + ": " + sms.getMessage()));
    }

    private static List<Visitor> readVisitorsFromJson(String filePath) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader("src/main/resources/" + filePath)) {
            Type visitorListType = new TypeToken<List<Visitor>>() {}.getType();
            return gson.fromJson(reader, visitorListType);
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}