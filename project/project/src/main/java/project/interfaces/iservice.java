package project.interfaces;

import java.util.List;

public interface iservice<T> {
    void add(T t);                    // Create
    void update(T t);                 // Update
    void delete(int id);             // Delete by ID
    T getById(int id);               // Read single item by ID
    List<T> getAll();                // Read all items
}
