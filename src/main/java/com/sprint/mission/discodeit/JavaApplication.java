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
    private static UserService userService = new JCFUserSerivce();
    private static MessageService messageService = new JCFMessageService(userService);
    private static ChannelService channelService = new JCFChannelService(userService);

    public static void main(String[] args) {
        System.out.println("=========== User Service Test ===========");
        User user1 = userService.createUser("kim", "010-1234-1234", "1234");
        User user2 = userService.createUser("kim", "010-1234-1234", "1234");
        System.out.println(userService.getAllUser());
        User updateUser1 = userService.updateUserPasswordById(user1.getId(), "1111");
        System.out.println("updateUser1 = " + updateUser1);
        userService.deleteUser(user1);
        System.out.println(userService.getAllUser());

        System.out.println("=========== Message Service Test ===========");
        User fromUser = userService.createUser("kim", "010-1111-1111", "1234");
        User toUser = userService.createUser("Lee", "010-2222-2222", "1234");
        Message message1 = messageService.createMessage("Simple content", fromUser, toUser);
        System.out.println("message1 = " + message1);
        Message updateMessage = messageService.updateMessageId(message1.getId(), "Update content");
        System.out.println("updateMessage = " + updateMessage);
        messageService.deleteMessage(message1);
        System.out.println(messageService.getAllMessages());

        System.out.println("=========== Channel Service Test ===========");
        User owner = userService.createUser("1", "010-3333-3333", "1234");
        User member1 = userService.createUser("2", "010-4444-3333", "1234");
        User member2 = userService.createUser("3", "010-5555-3333", "1234");
        User member3 = userService.createUser("4", "010-6666-3333", "1234");
        List<User> members = List.of(owner, member1, member2, member3);
        Channel codeitChannel = channelService.createChannel(members, "Codeit", owner);
        System.out.println("codeitChannel = " + codeitChannel);
        User addUser = userService.createUser("5", "010-7777-3333", "1234");
        System.out.println("find channel : " + channelService.getChannelById(codeitChannel.getId()));
        channelService.deleteChannel(codeitChannel);
    }
}
