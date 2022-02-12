package org.stormroboticsnj.scoutingradar2022.database.field;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "fields")
public class Field {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private Category category;
    private String name;
    private String abbreviation;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    @Ignore
    public String getCategoryString() {
        return category.toString();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Field) {
            Field field = (Field) obj;
            return field.getId() == id && field.getName().equals(name) && field.getAbbreviation().equals(abbreviation) && field.getCategory().equals(category);
        }
        return false;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public enum Category {
        OBJECTIVE_BUTTON,
        OBJECTIVE_SPINNER,
        SUBJECTIVE_SPINNER,
    }


}
