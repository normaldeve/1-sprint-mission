package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserSerivce;

import java.util.List;

public class JavaApplication {
    public static void main(String[] args) {
        UserService userService = JCFUserSerivce.getInstance();
        MessageService messageService = JCFMessageService.getInstance();
        ChannelService channelService = JCFChannelService.getInstance();

        System.out.println("<회원 생성하기>");
        User user1 = userService.createUser("김민준", "010-1111-1111", "Abcdefgh!");
        User user2 = userService.createUser("이서윤", "010-2222-1111", "Abcdefgh!");
        User user3 = userService.createUser("박지훈", "010-3333-1111", "Abcdefgh!");
        User user4 = userService.createUser("최지아", "010-4444-1111", "Abcdefgh!");
        User user5 = userService.createUser("정다은", "010-5555-1111", "Abcdefgh!");
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
        User findUser1 = userService.getUserById("010-1111-1111");
        System.out.println("findUser : " + findUser1);
        System.out.println();
        System.out.println("===============================================================================");
        System.out.println();
        System.out.println("<회원 비밀번호 업데이트 하기>");
        User updateUser1 = userService.updateUserPassword("010-1111-1111", "Zdefdasdf!@");
        System.out.println("UpdatePassword : " + updateUser1);
        System.out.println();
        System.out.println("===============================================================================");
        System.out.println();
        System.out.println("<회원 정보 삭제하기>");
        userService.deleteUser("010-1111-1111");
        System.out.println(userService.getAllUser());
        System.out.print(" - 회원 삭제 시 저장되어 있지 않는 회원일 경우 문제가 발생 : ");
        try {
            User unRegisterUser = new User("김미영", "010-4343-3434", "Rasdffa1234!");
            userService.deleteUser("010-4343-3434");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println();
        System.out.println("===============================================================================");
        System.out.println();
        System.out.println("<메시지를 보낼 수 있는 테스트 채널을 생성함>");
        Channel testChannel = channelService.createChannel(userService.getAllUser(), "테스트1", user2);
        Channel testChannel2 = channelService.createChannel(userService.getAllUser(), "테스트2", user3);
        System.out.println(testChannel);
        System.out.println("<메시지 생성하기>");
        Message message2_1 = messageService.createMessage("안녕하세요 지훈님 저는 이서윤이라고 합니다.", user2, testChannel);
        Message message2_2 = messageService.createMessage("저는 요즘 스프링에 대해 공부하고 있습니다^^", user2, testChannel);
        Message message3_1 = messageService.createMessage("테스트 확인 용 메시지입니다!", user3, testChannel2);
        System.out.println(message2_1);
        Message message3 = messageService.createMessage("네 만나서 반갑습니다!", user3, testChannel);
        System.out.println(message3);
        System.out.println(" - 메시지 생성 시 발생할 수 있는 문제");
        System.out.println("    - 1. 내용이 작성되지 않은 메시지 : ");
        try {
            Message message4 = messageService.createMessage("", user4, testChannel);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.print("    - 2. 채널에 등록되지 않은 회원이 메시지를 보낼 때 : ");
        User unRegistUser1 = userService.createUser("미등록회원", "010-1919-9191", "Abcdefgh!");
        try {
            Message unregistMessage = messageService.createMessage("안녕하세요 만나서 반갑습니다", unRegistUser1, testChannel);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println();
        System.out.println("===============================================================================");
        System.out.println();
        System.out.println("<하나의 채널에서 전체 메시지 조회하기>");
        System.out.print("테스트 1채널 전체 메시지 조회");
        List<Message> allMessages = messageService.getMessageByChannel("테스트1");
        System.out.println(allMessages);
        System.out.println();
        System.out.print("테스트 2채널 전체 메시지 조회");
        List<Message> allMessages2 = messageService.getMessageByChannel("테스트2");
        System.out.println(allMessages2);
        System.out.println("<단일 메시지 조회하기>");
        List<Message> getMessage = messageService.getMessageByUser(user2);
        System.out.println(getMessage);
        System.out.println();
        System.out.println("===============================================================================");
        System.out.println();
        System.out.println("<메시지 내용 수정하기>");
        Message updateMessage = messageService.updateMessageContent(message2_1.getId(), "메시지 내용 업데이트");
        System.out.println(updateMessage);
        System.out.println();
        System.out.println("===============================================================================");
        System.out.println();
        System.out.println("<메시지 삭제하기>");
        messageService.deleteMessage(message2_1);
        System.out.println("전체 메시지 조회하기: " + messageService.getAllMessages());
        System.out.print("임시로 만든 회원 삭제하기 : ");
        userService.deleteUser("010-1919-9191");
        System.out.println("임시로 만든 채널 삭제하기 : ");
        boolean deleteResult1 = channelService.deleteChannel("테스트1");
        boolean deleteResult2 = channelService.deleteChannel("테스트2");
        System.out.println();
        System.out.println("===============================================================================");
        System.out.println();
        System.out.println("<채널 생성하기>");
        Channel channel1 = channelService.createChannel(userService.getAllUser(), "코드잇 디스코드", user2);
        Channel channel2 = channelService.createChannel(userService.getAllUser(), "코테 준비", user3);
        User channelCreator = userService.createUser("김자바", "010-8739-9343", "Abcdefgh!");
        channelService.createChannel(userService.getAllUser(), "자바 공부합시다", channelCreator);
        System.out.println(channel1);
        System.out.println(channel2);
        System.out.println(" - 채널 생성 시 발생할 수 있는 문제");
        System.out.print("     - 기존 채널과 동일한 이름으로 채널을 개설하는 경우 : ");
        try {
            Channel sameChannel = channelService.createChannel(userService.getAllUser(), "코드잇 디스코드", user4);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println();
        System.out.println("===============================================================================");
        System.out.println();
        System.out.println("<채널 단일 조회하기>");
        Channel findChannel1 = channelService.getChannelByName("코드잇 디스코드");
        System.out.println("코드잇 디스코드 채널 찾기 : " + findChannel1);
        System.out.println("<전체 채널 조회하기>");
        List<Channel> allChannel = channelService.getAllChannel();
        System.out.println(allChannel);
        System.out.println("<하나의 회원이 포함되어 있는 채널 조회>");
        List<Channel> channelsByUserId = channelService.getChannelsByUserId(channelCreator);
        System.out.println("김자바 회원이 포함되어 있는 채널 조회 : " + channelsByUserId);
        System.out.println();
        System.out.println("===============================================================================");
        System.out.println();
        System.out.println("<채널에 새로운 멤버 추가하기>");
        User newUser = userService.createUser("홍길동", "010-1234-4321", "Abcdefgh!");
        Channel updateChannel2 = channelService.addUserToChannel("코테 준비", newUser);
        System.out.println("코테 준비 채널에 신규 회원 추가 : " + updateChannel2);
        System.out.println("<채널에 있는 멤버 채널 나가기>");
        channelService.removeUserFromChannel(channel2.getName(), user4);
        System.out.println(channel2);
        System.out.println();
        System.out.println("===============================================================================");
        System.out.println();
        System.out.println("<채널 삭제하기>");
        channelService.deleteChannel("코테 준비");
        System.out.println("전체 채널 조회하기 : " + channelService.getAllChannel());
        System.out.print(" - 채널 삭제 시 잘못된 채널명을 입력하면 문제가 발생 : ");
        try {
            channelService.deleteChannel("잘못된이름");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println();
        System.out.println("===============================================================================");
        System.out.println();
    }
}