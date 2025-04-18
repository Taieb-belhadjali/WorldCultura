package WorldCultura.interfaces;

import java.util.List;

public interface IService<T> {
    void add(T t);
    void delete(T t);
    void update(T t);
    T find(T t);              // Optional: usually replaced with findById
    T findById(int id);       // More common for service layers
    List<T> getAll();         // Retrieve all entities
}
