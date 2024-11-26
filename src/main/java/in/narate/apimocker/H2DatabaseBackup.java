package in.narate.apimocker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Component
public class H2DatabaseBackup {

    private final DataSource dataSource;

    @Value("${h2.backup.file:./data/backup.sql}")
    private String backupFile;

    public H2DatabaseBackup(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Scheduled(fixedRate = 60000)
    public void backupDatabase() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute("SCRIPT TO '" + backupFile + "';");
            System.out.println("H2 database backed up to: " + backupFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

