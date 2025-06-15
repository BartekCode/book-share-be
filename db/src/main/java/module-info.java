module db {
    requires core;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires liquibase.core;
    requires java.sql;
    requires spring.jdbc;

    exports com.example.db.repository.book;
    exports com.example.db.repository.user;
    exports com.example.db.model.book;
    exports com.example.db.model.user;
    exports com.example.db.configuration;

    opens com.example.db.configuration to spring.core, spring.beans, spring.context;
    opens com.example.db.repository.book to spring.beans, spring.context, spring.core;
    opens com.example.db.repository.user to spring.beans, spring.context, spring.core;
    opens com.example.db.model.book to spring.beans, spring.context, spring.core;
}
