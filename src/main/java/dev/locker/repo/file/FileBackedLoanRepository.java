package dev.locker.repo.file;

import com.fasterxml.jackson.core.type.TypeReference;
import dev.locker.domain.Loan;
import dev.locker.repo.LoanRepository;
import dev.locker.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * File-backed loan repository.
 */
public class FileBackedLoanRepository implements LoanRepository {
    private static final Logger logger = LoggerFactory.getLogger(FileBackedLoanRepository.class);
    private final Path file;
    private final List<Loan> loans = new CopyOnWriteArrayList<>();

    /**
     * Create a new FileBackedLoanRepository with the given backing file.
     *
     * @param file the file to back the repository
     */
    public FileBackedLoanRepository(Path file) {
        this.file = file;
        try {
            if (Files.exists(file)) {
                List<Loan> list = JsonUtil.mapper().readValue(Files.readAllBytes(file), new TypeReference<>() {
                });
                loans.addAll(list);
            }
        } catch (IOException e) {
            logger.error("Failed to load loans from {}", file, e);
        }
    }

    @Override
    public List<Loan> findAll() {
        return List.copyOf(loans);
    }

    @Override
    public Optional<Loan> findOpenLoanByDevice(String deviceId) {
        return loans.stream().filter(l -> l.deviceId().equals(deviceId) && l.returnedAt() == null).findFirst();
    }

    @Override
    public void save(Loan loan) {
        loans.add(loan);
    }

    @Override
    public void update(Loan loan) {
        for (int i = 0; i < loans.size(); i++) {
            Loan l = loans.get(i);
            if (l.equals(loan)) {
                loans.set(i, loan);
                return;
            }
        }
        loans.add(loan);
    }

    /**
     * Persist current loans to the backing file.
     *
     * @throws IOException if writing fails
     */
    public synchronized void persist() throws IOException {
        List<Loan> list = findAll();
        Files.createDirectories(file.getParent());
        Files.writeString(file, JsonUtil.pretty(list));
    }
}
