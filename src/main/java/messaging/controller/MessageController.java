package messaging.controller;

import messaging.dto.MessageRequest;
import messaging.model.Message;
import messaging.model.SendStatus;
import messaging.repository.MessageRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class MessageController {

    @Inject
    MessageRepository messageRepository;

    @Transactional
    public Long enqueue(MessageRequest dto) {
        Message m = toEntity(dto);
        messageRepository.save(m);

        return m.getId();
    }

    @Transactional
    public List<Long> enqueueBulk(List<MessageRequest> dtos) {
        List<Long> ids = new ArrayList<>(dtos.size());
        for (MessageRequest dto : dtos) {
            Long id = enqueue(dto);
            ids.add(id);
        }
        return ids;
    }

    private Message toEntity(MessageRequest dto) {
        Message m = new Message();
        m.setFrom(dto.from);
        if (dto.to != null) m.setTo(dto.to);
        m.setSubject(dto.subject);
        m.setBodyText(dto.bodyText);
        m.setBodyHtml(dto.bodyHtml);
        
        m.setSendStatus(SendStatus.PENDING);
        m.setProviderMessageId(null);
        return m;
    }
}
