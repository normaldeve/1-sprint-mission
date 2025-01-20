package com.sprint.mission.discodeit.util;

import java.io.*;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FileIOUtil {

    public static <T> Map<UUID, T> loadFromFile(Path filePath) {
        if (!Files.exists(filePath)) {
            return new HashMap<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(filePath))) {
            return (Map<UUID, T>) ois.readObject();
        } catch (EOFException e) {
            return new HashMap<>();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("파일을 읽어오는 도중 문제가 발생하였습니다.", e);
        }
    }

    public static <T> void saveToFile(Map<UUID, T> data, Path filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(filePath))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 도중 문제가 발생하였습니다.", e);
        }
    }
}

