package com.rhf.rider.data.seeder;

import com.rhf.rider.entity.Rider;
import com.rhf.rider.repository.RiderRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Component
public class CsvDataLoader implements CommandLineRunner {

    private final RiderRepository repo;

    public CsvDataLoader(RiderRepository repo) { this.repo = repo; }

    @Override
    public void run(String... args) throws Exception {
        if (repo.count() > 0) return;

        ClassPathResource resource = new ClassPathResource("rhfd_riders.csv");
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            reader.lines().skip(1).forEach(line -> {
                String[] cols = line.split(",");
                if (cols.length >= 4) {
                    Rider rider = new Rider();
                    rider.setName(cols[1].trim());
                    rider.setEmail(cols[2].trim());
                    rider.setPhone(cols[3].trim());
                    try { rider.setCreatedAt(Instant.parse(cols[3].trim())); }
                    catch (Exception e) { rider.setCreatedAt(Instant.now()); }
                    rider.setActive(true);
                    repo.save(rider);
                }
            });
        }
    }
}
