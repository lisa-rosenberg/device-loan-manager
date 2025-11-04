package dev.locker.repo.file;

import com.fasterxml.jackson.core.type.TypeReference;
import dev.locker.domain.User;
import dev.locker.repo.UserRepository;
import dev.locker.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * File-backed user repository.
 */
public class FileBackedUserRepository implements UserRepository {
    private static final Logger logger = LoggerFactory.getLogger(FileBackedUserRepository.class);
    private final Path file;
    private final Map<String, User> map = new ConcurrentHashMap<>();

    /**
     * Create a new FileBackedUserRepository with the given backing file.
     *
     * @param file the file to back the repository
     */
    public FileBackedUserRepository(Path file) {
        this.file = file;
        try {
            if (Files.exists(file)) {
                List<User> list = JsonUtil.mapper().readValue(Files.readAllBytes(file), new TypeReference<>() {
                });
                map.putAll(list.stream().collect(Collectors.toMap(User::id, u -> u)));
            }
        } catch (IOException e) {
            logger.error("Failed to load users from {}", file, e);
        }
    }

    @Override
    public List<User> findAll() {
        return List.copyOf(map.values());
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(map.get(id));
    }

    /**
     * Persist current users to the backing file.
     *
     * @throws IOException if writing fails
     */
    public synchronized void persist() throws IOException {
        List<User> list = findAll();
        Files.createDirectories(file.getParent());
        Files.writeString(file, JsonUtil.pretty(list));
    }
}
