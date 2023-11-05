package edu.northeastern.cs5500.moviebot.service;

import static com.google.common.truth.Truth.assertThat;

import edu.northeastern.cs5500.moviebot.model.User;
import edu.northeastern.cs5500.moviebot.repository.GenericRepository;
import edu.northeastern.cs5500.moviebot.repository.MongoDBRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

public class MongoDBServiceTest {

    @Test
    public void getMongoDBURL() {
        String URL = MongoDBService.getMongoDBURL();
        assertThat(URL).isNotNull();
    }

    @Test
    public void getMongoDatabase() {
        MongoDBService mongoDBService = new MongoDBService();

        GenericRepository<User> repository1 =
                new MongoDBRepository<User>(User.class, mongoDBService);
        User user1 = new User();
        user1.getWatchedList().put("Coco", LocalDate.now());

        repository1.add(user1);
        assertThat(repository1).isNotNull();
    }
}
