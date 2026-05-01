package com.campusloop.service;

import com.campusloop.model.Message;
import com.campusloop.model.User;
import com.campusloop.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public Message sendMessage(Message message) {
        return messageRepository.save(message);
    }

    public List<Message> getConversation(User u1, User u2) {
        return messageRepository.findConversation(u1, u2);
    }

    public List<Message> getInbox(User user) {
        return messageRepository.findByReceiverOrderByTimestampDesc(user);
    }
}
