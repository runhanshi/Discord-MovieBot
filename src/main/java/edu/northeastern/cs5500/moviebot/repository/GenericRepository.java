package edu.northeastern.cs5500.moviebot.repository;

import com.mongodb.lang.Nullable;
import java.util.Collection;
import org.bson.types.ObjectId;

public interface GenericRepository<T> {
    public T get(@Nullable ObjectId id);

    public T add(@Nullable T item);

    public T update(@Nullable T item);

    public void delete(@Nullable ObjectId id);

    public Collection<T> getAll();

    public long count();
}
