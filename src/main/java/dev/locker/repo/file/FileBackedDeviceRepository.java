package dev.locker.repo.file;

import com.fasterxml.jackson.core.type.TypeReference;
import dev.locker.domain.Device;
import dev.locker.repo.DeviceRepository;
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
 * File-backed device repository.
 */
public class FileBackedDeviceRepository implements DeviceRepository {
    private static final Logger logger = LoggerFactory.getLogger(FileBackedDeviceRepository.class);
    private final Path file;
    private final Map<String, Device> map = new ConcurrentHashMap<>();

    /**
     * Create a new FileBackedDeviceRepository with the given backing file.
     *
     * @param file the file to back the repository
     */
    public FileBackedDeviceRepository(Path file) {
        this.file = file;
        try {
            if (Files.exists(file)) {
                List<Device> list = JsonUtil.mapper().readValue(Files.readAllBytes(file), new TypeReference<>() {
                });
                map.putAll(list.stream().collect(Collectors.toMap(Device::id, d -> d)));
            }
        } catch (IOException e) {
            logger.error("Failed to load devices from {}", file, e);
        }
    }

    @Override
    public List<Device> findAll() {
        return List.copyOf(map.values());
    }

    @Override
    public Optional<Device> findById(String id) {
        return Optional.ofNullable(map.get(id));
    }

    @Override
    public void save(Device device) {
        map.put(device.id(), device);
    }

    /**
     * Persist current repository contents to the backing file. This method is synchronized
     * to avoid concurrent writes.
     *
     * @throws IOException if writing to disk fails
     */
    public synchronized void persist() throws IOException {
        List<Device> list = findAll();
        Files.createDirectories(file.getParent());
        Files.writeString(file, JsonUtil.pretty(list));
    }
}
