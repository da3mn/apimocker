package in.narate.apimocker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Component
public class H2DatabaseLoader implements org.springframework.beans.factory.InitializingBean {

    private final DataSource dataSource;

    @Value("${h2.backup.file:./data/backup.sql}")
    private String backupFile;

    public H2DatabaseLoader(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void afterPropertiesSet() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute("RUNSCRIPT FROM '" + backupFile + "';");
            System.out.println("H2 database restored from: " + backupFile);

        } catch (Exception e) {
            System.out.println("No backup found or failed to restore database.");
        }
    }
}
