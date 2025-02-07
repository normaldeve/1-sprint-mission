package com.sprint.mission.discodeit.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.file.*;
import java.util.*;

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

    //.dat 파일을 읽기 쉬운 Json 파일로 변환하는 메서드
    public static void convertDSerToJson(Path datFilePath, Path jsonFilePath) {
        Map<UUID, Object> data = loadFromFile(datFilePath);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(jsonFilePath.toFile(), data);
        } catch (IOException e) {
            throw new RuntimeException("JSON 파일로 변환하는 도중 문제가 발생하였습니다.", e);
        }
    }

    // 파일 초기화 메서드
    public static void initializeFiles() {
        try {
            Files.deleteIfExists(Paths.get("./result/users.ser"));
            Files.deleteIfExists(Paths.get("./result/messages.ser"));
            Files.deleteIfExists(Paths.get("./result/channels.ser"));
        } catch (IOException e) {
            System.err.println("파일 초기화 중 오류 발생: " + e.getMessage());
        }
    }
}
