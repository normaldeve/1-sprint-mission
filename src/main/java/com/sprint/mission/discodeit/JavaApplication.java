package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.factory.Factory;
import com.sprint.mission.discodeit.factory.FileFactory;
import com.sprint.mission.discodeit.factory.JCFFactory;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.FileIOUtil;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class JavaApplication {
    public static void main(String[] args) {
        Factory factory = JCFFactory.getInstance();
//        Factory factory = FileFactory.getInstance();
        UserService userService = factory.getUserService();
        MessageService messageService = factory.getMessageService();
        ChannelService channelService = factory.getChannelService();
        userService.setDependency(messageService, channelService);
        messageService.setDependency(userService, channelService);
        channelService.setDependency(userService,messageService);

        try {
            Files.deleteIfExists(Paths.get("./result/users.dat"));
            Files.deleteIfExists(Paths.get("./result/messages.dat"));
            Files.deleteIfExists(Paths.get("./result/channels.dat"));
        } catch (IOException e) {
            System.err.println("파일 초기화 중 오류 발생: " + e.getMessage());
        }


        System.out.println("<회원 생성하기>");
        User user1 = userService.createUser("김민준", "010-1111-1111", "Abcdefgh12312!");
        User user2 = userService.createUser("이서윤", "010-2222-1111", "Abcdefgh12312!!");
        User user3 = userService.createUser("박지훈", "010-3333-1111", "Abcdefgh12312!!");
        User user4 = userService.createUser("이채은", "010-4444-1111", "Abcdefgh12312!!");
        User user5 = userService.createUser("정다은", "010-5555-1111", "Abcdefgh12312!!");
        System.out.println(" - 회원 생성 시 발생할 수 있는 문제");
        System.out.print("    - 1. 동일한 전화번호로 회원 가입을 진행 : ");
        try {
            User user1_1 = userService.createUser("홍지훈", "010-1111-1111", "Abcdefgh!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.print("    - 2. 올바르지 않은 전화번호 형식 : ");
        try {
            User unCorrectPhone = userService.createUser("김철수", "010-12345-12345", "Abcdefgh!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.print("    - 3. 올바르지 않은 비밀번호 형식 : ");
        try {
            User unCorrectPass = userService.createUser("김영미", "010-1234-4321", "1234");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println("회원 생성 시 문제가 발생하면 저장되지 않음을 확인: " + userService.getAllUser());
        System.out.println();
        System.out.println("===============================================================================");
        System.out.println();
        System.out.println("<회원 전체 조회하기>");
        List<User> users = userService.getAllUser();
        System.out.println(users);
        System.out.println();
        System.out.println("<단일 회원 조회하기>");
        Optional<User> findUser1 = userService.getUserByPhone("010-1111-1111");
        System.out.println("findUser : " + findUser1);
        System.out.println();
        System.out.println("===============================================================================");
        System.out.println();
        System.out.println("<회원 비밀번호 업데이트 하기>");
        userService.updateUserPassword(user1, "Zdefdasdf!@");
        System.out.println("UpdatePassword : " + user1);
        System.out.println();
        System.out.println("===============================================================================");
        System.out.println();
        System.out.println("<회원 정보 삭제하기>");
        userService.deleteUser(user1);
        System.out.println(userService.getAllUser());
        System.out.print(" - 회원 삭제 시 저장되어 있지 않는 회원일 경우 문제가 발생 : ");
        try {
            User unRegisterUser = new User("김미영", "010-4343-3434", "Rasdffa1234!");
            userService.deleteUser(unRegisterUser);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println();
        System.out.println("===============================================================================");
        System.out.println();
        System.out.println("<메시지를 보낼 수 있는 테스트 채널을 생성함>");
        Channel testChannel = channelService.createChannel("테스트1", user4);
        Channel testChannel2 = channelService.createChannel("테스트2", user5);
        testChannel.addManyUser(userService.getAllUser());
        testChannel2.addManyUser(userService.getAllUser());
        System.out.println(testChannel);
        System.out.println("<메시지 생성하기>");
        Message message2_1 = messageService.createMessage("안녕하세요 지훈님 저는 이서윤이라고 합니다.", user2, testChannel);
        Message message2_2 = messageService.createMessage("저는 요즘 스프링에 대해 공부하고 있습니다^^", user2, testChannel);
        Message message3_1 = messageService.createMessage("테스트 확인 용 메시지입니다!", user3, testChannel2);
        System.out.println(message2_1);
        Message message3 = messageService.createMessage("네 만나서 반갑습니다!", user3, testChannel);
        System.out.println(message3);
        System.out.println(" - 메시지 생성 시 발생할 수 있는 문제");
        System.out.print("    - 1. 내용이 작성되지 않은 메시지 : ");
        try {
            Message message4 = messageService.createMessage("", user4, testChannel);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.print("    - 2. 채널에 등록되지 않은 회원이 메시지를 보낼 때 : ");
        User unRegistUser1 = new User("미등록회원", "010-1919-9191", "Abcdefgh!");
        try {
            Message unregistMessage = messageService.createMessage("안녕하세요 만나서 반갑습니다", unRegistUser1, testChannel);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println();
        System.out.println("user2 = " + user2);
        System.out.println("===============================================================================");
        System.out.println();
        System.out.println("<특정 채널에서 전체 메시지 조회하기>");
        System.out.print("테스트 1채널 전체 메시지 조회");
        List<Message> allMessages = messageService.getMessageByChannel(testChannel);
        System.out.println(allMessages);
        System.out.println();
        System.out.print("테스트 2채널 전체 메시지 조회");
        List<Message> allMessages2 = messageService.getMessageByChannel(testChannel2);
        System.out.println(allMessages2);
        System.out.println("<단일 메시지 조회하기>");
        List<Message> getMessage = messageService.getMessageByUser(user2);
        System.out.println(getMessage);
        System.out.println();
        System.out.println("===============================================================================");
        System.out.println();
        System.out.println("<메시지 내용 수정하기>");
        messageService.updateMessageContent(message2_1, "메시지 내용 업데이트");
        System.out.println(message2_1);
        System.out.println();
        System.out.println("===============================================================================");
        System.out.println();
        System.out.println("<메시지 삭제하기>");
        messageService.removeMessageByWriter(user2, message2_1.getId());
        System.out.println("전체 메시지 조회하기: " + messageService.getMessageByUser(user2));
        System.out.println("임시로 만든 채널 삭제하기 : ");
        channelService.deleteChannel(testChannel);
        channelService.deleteChannel(testChannel2);
        System.out.println(channelService.getAllChannel());
        System.out.println();
        System.out.println("===============================================================================");
        System.out.println();
        System.out.println("<채널 생성하기>");
        Channel channel1 = channelService.createChannel("코드잇 디스코드", user2);
        Channel channel2 = channelService.createChannel("코테 준비", user3);
        messageService.createMessage("열심히 공부하는 코테 준비 채널입니다!", user4, channel2);
        messageService.createMessage("열심히 하겠습니다!!", user3, channel2);
        channelService.addManyUserToChannel(channel1, userService.getAllUser());
        channelService.addManyUserToChannel(channel2, userService.getAllUser());
        User channelCreator = userService.createUser("김자바", "010-8739-9343", "Abcdefgh!");
        Channel javaChannel = channelService.createChannel("자바 공부합시다", channelCreator);
        channelService.addManyUserToChannel(javaChannel, userService.getAllUser());
        System.out.println(channel1);
        System.out.println(channel2);
        System.out.println(" - 채널 생성 시 발생할 수 있는 문제");
        System.out.print("     - 기존 채널과 동일한 이름으로 채널을 개설하는 경우 : ");
        try {
            Channel sameChannel = channelService.createChannel("코드잇 디스코드", user4);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println();
        System.out.println("===============================================================================");
        System.out.println();
        System.out.println("<채널 단일 조회하기>");
        System.out.println("코드잇 디스코드 채널 찾기 : " + channelService.getChannelByName("코드잇 디스코드"));
        System.out.println("<전체 채널 조회하기>");
        System.out.println(channelService.getAllChannel());
        System.out.println("<하나의 회원이 포함되어 있는 채널 조회>");
        List<Channel> channelsByUserId = channelService.getChannelsByUserId(channelCreator);
        System.out.println("김자바 회원이 포함되어 있는 채널 조회 : " + channelsByUserId);
        System.out.println();
        System.out.println("===============================================================================");
        System.out.println();
        System.out.println("<채널에 새로운 멤버 추가하기>");
        User newUser = userService.createUser("홍길동", "010-1234-4321", "Abcdefgh!");
        Channel updateChannel2 = channelService.addUserToChannel(channel2, newUser);
        System.out.println("코테 준비 채널에 신규 회원 추가 : " + updateChannel2);
        System.out.println("<채널에 있는 멤버 채널 나가기>");
        channelService.removeUserToChannel(channel2, user3);
        System.out.println(channel2);
        System.out.println();
        System.out.println("===============================================================================");
        System.out.println();
        System.out.println("<심화> - 채널을 삭제하면 해당 채널에 있던 메시지가 조회되면 안된다.");
        System.out.println("코드잇 채널에 메시지를 여러 개 생성");
        messageService.createMessage("안녕하세요 처음 인사드립니다.", user3, channel1);
        messageService.createMessage("안녕하세요 만나서 반갑습니다.", user4, channel1);
        messageService.createMessage("오 디스코드는 처음 들어와보네요!.", user5, channel1);
        System.out.println("코드잇 채널 삭제 이전 해당 채널에 존재하는 메시지 출력해보기");
        System.out.println(messageService.getMessageByChannel(channel1));
        System.out.println("코드잇 채널 삭제 이후 해당 채널에 존재하는 메시지 출력해보기");
        channelService.deleteChannel(channel1);
        System.out.println(messageService.getMessageByChannel(channel1));
        if (factory instanceof FileFactory) {
            FileIOUtil.convertDatToJson(Paths.get("./result/users.dat"), Paths.get("./json/users.json"));
            FileIOUtil.convertDatToJson(Paths.get("./result/messages.dat"), Paths.get("./json/messages.json"));
            FileIOUtil.convertDatToJson(Paths.get("./result/channels.dat"), Paths.get("./json/channels.json"));
        }
    }
}