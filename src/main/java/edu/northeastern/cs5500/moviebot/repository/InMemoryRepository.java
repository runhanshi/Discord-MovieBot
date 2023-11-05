package edu.northeastern.cs5500.moviebot.repository;

import com.mongodb.lang.Nullable;
import edu.northeastern.cs5500.moviebot.model.Model;
import java.util.Collection;
import java.util.HashMap;
import javax.inject.Inject;
import org.bson.types.ObjectId;

public class InMemoryRepository<T extends Model> implements GenericRepository<T> {

    HashMap<ObjectId, T> collection;

    @Inject
    public InMemoryRepository() {
        collection = new HashMap<>();
    }

    @Nullable
    public T get(ObjectId id) {
        return collection.get(id);
    }

    @Override
    public T add(T item) {
        ObjectId id = item.getId();
        if (id == null) {
            id = new ObjectId();
            item.setId(id);
        }
        collection.put(id, item);
        return item;
    }

    @Override
    public T update(T item) {
        collection.put(item.getId(), item);
        return item;
    }

    @Override
    public void delete(ObjectId id) {
        collection.remove(id);
    }

    @Override
    public Collection<T> getAll() {
        return collection.values();
    }

    @Override
    public long count() {
        return collection.size();
    }
}
