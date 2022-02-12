package org.stormroboticsnj.scoutingradar2022.database.field;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FieldDao {

    /**
     * Insert a field into the table
     * @param field The field to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Field field);

    /**
     * Insert a list of fields into the table
     * @param fields A list of fields to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertList(List<Field> fields);

    /**
     * Insert fields into the table
     * @param fields A list of fields to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Field... fields);

    /**
     * Get all fields in the table
     * @return A list of all fields in the table
     */
    @Query("SELECT * FROM fields")
    LiveData<List<Field>> getAll();

    /**
     * This will delete all fields in the table
     * BE CAREFUL
     */
    @Query("DELETE FROM fields")
    void deleteAll();

    /**
     * Delete a field from the table
     * @param field The field to delete
     */
    @Delete
    void delete(Field field);
}
