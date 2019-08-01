package com.application.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceInitializedEvent;

import javax.sql.DataSource;
import java.sql.SQLException;

public class CustDataSourceInitializedEvent extends DataSourceInitializedEvent {

    public CustDataSourceInitializedEvent(DataSource source)  {
        super(source);
        try {
            System.out.println("starting to init sql schema.....");
            System.out.println(source.getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
