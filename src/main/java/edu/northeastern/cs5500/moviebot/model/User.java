package edu.northeastern.cs5500.moviebot.model;

import java.time.LocalDate;
import java.util.HashMap;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class User implements Model {
    private ObjectId id;
    private long discordUserId;
    private boolean subscribed;
    private HashMap<String, LocalDate> watchedList = new HashMap<>();
}
