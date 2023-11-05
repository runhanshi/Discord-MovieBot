package edu.northeastern.cs5500.moviebot.repository;

import static org.junit.Assert.*;

import edu.northeastern.cs5500.moviebot.model.User;
import edu.northeastern.cs5500.moviebot.service.MongoDBService;
import java.util.HashMap;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

public class MongoDBRepositoryTest {

  private MongoDBRepository<User> repository;
  private static final ObjectId ID = new ObjectId("5399aba6e4b0ae375bfdca88");
  private User user = new User() {{
    setId(ID);
    setDiscordUserId(-1L);
    setSubscribed(false);
    setWatchedList(new HashMap<>());
  }};

  @Before
  public void setUp() throws Exception {
    repository = new MongoDBRepository<>(User.class, new MongoDBService());
  }

  @Test
  public void test() {
    repository.add(user);
    assertNotNull(repository.get(ID));
    assertNotNull(repository.getAll());
    assertTrue(repository.count() > 0);
    user.setSubscribed(true);
    repository.update(user);
    assertTrue(repository.get(ID).isSubscribed());
    repository.delete(ID);
    assertNull(repository.get(ID));
  }
}