package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.domain.Channel;
import com.sprint.mission.discodeit.domain.Message;
import com.sprint.mission.discodeit.domain.User;
import com.sprint.mission.discodeit.factory.service.FileServiceFactory;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.ChannelType;
import com.sprint.mission.discodeit.util.FileIOUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootApplication
public class DiscodeitApplication {
	static User setupUser(UserService userService) {
		User user = userService.create("student1", "010-1929-1188", "Abcdefgh12312!");
		return user;
	}

	static Channel setupChannel(ChannelService channelService) {
		Channel channel = channelService.create("자바 공지 채널", "공지 채널입니다", ChannelType.VOICE);
		return channel;
	}

	static Message messageCreateTest(MessageService messageService, Channel channel, User writer) {
		Message message = messageService.create("안녕하세요", writer, channel);
		System.out.println("메시지 생성 : " + message);
		return message;
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);
		UserService userService = context.getBean(UserService.class);
		System.out.println("userService = " + userService);
		ChannelService channelService = context.getBean(ChannelService.class);
		System.out.println("channelService = " + channelService);
		MessageService messageService = context.getBean(MessageService.class);
		System.out.println("messageService = " + messageService);

		try {
			Files.deleteIfExists(Paths.get("./result/users.ser"));
			Files.deleteIfExists(Paths.get("./result/messages.ser"));
			Files.deleteIfExists(Paths.get("./result/channels.ser"));
		} catch (IOException e) {
			System.err.println("파일 초기화 중 오류 발생: " + e.getMessage());
		}

		User user = setupUser(userService);
		Channel channel = setupChannel(channelService);
		Message message = messageCreateTest(messageService, channel, user);
	}
}
