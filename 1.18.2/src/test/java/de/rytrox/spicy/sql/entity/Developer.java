package de.rytrox.spicy.sql.entity;


import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Developer extends DeveloperWithoutMatchingConstructor {

    public Developer(@NotNull ResultSet resultSet) throws SQLException {
        this.id = resultSet.getInt("id");
        this.name = resultSet.getString("name");
    }

}
