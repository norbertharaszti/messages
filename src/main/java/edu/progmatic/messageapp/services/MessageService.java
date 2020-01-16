package edu.progmatic.messageapp.services;

import edu.progmatic.messageapp.modell.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessageService {
    private static final Logger logger= LoggerFactory.getLogger(MessageService.class);

    private static List<Message> messages = new ArrayList<>();

    {
        messages.add(new Message("Aladár", "Mz/x jelkezz, jelkezz", LocalDateTime.now()));
        messages.add(new Message("Kriszta", "Bemutatom lüke Aladárt", LocalDateTime.now()));
        messages.add(new Message("Blöki", "Vauuu", LocalDateTime.now()));
        messages.add(new Message("Maffia", "miauuu", LocalDateTime.now()));
        messages.add(new Message("Aladár", "Kapcs/ford", LocalDateTime.now()));
        messages.add(new Message("Aladár", "Adj pénzt!", LocalDateTime.now()));
    }

    public List<Message> filterMessages(Integer limit, String orderBy, String order) {

        Comparator<Message> msgComp = Comparator.comparing((Message::getCreationDate));
        switch (orderBy) {
            case "text":
                msgComp = Comparator.comparing((Message::getText));
                break;
            case "id":
                msgComp = Comparator.comparing((Message::getId));
                break;
            case "author":
                msgComp = Comparator.comparing((Message::getAuthor));
                break;
            default:
                break;
        }
        if (order.equals("desc")) {
            msgComp = msgComp.reversed();
        }

        return messages.stream()
                .sorted(msgComp)
                .limit(limit).collect(Collectors.toList());
    }

    public Message getMessage(Long msgId) {
        Optional<Message> message = messages.stream().filter(m -> m.getId().equals(msgId)).findFirst();
        return message.get();
    }

    public void createMessage(Message m) {
        m.setCreationDate(LocalDateTime.now());
        m.setId((long) messages.size());
        messages.add(m);
    }

    public List<Message> filterMessages2(String author, String text, LocalDateTime dateFrom, LocalDateTime dateTo,
                                         String orderBy, String filterBy, boolean deleted) {
        List<Message> messages1 = new ArrayList<>();
        logger.info("filterMessages start");
        logger.debug("author: {}, filter: {}", author, filterBy);
        switch (filterBy) {
            case "nofilter":
                messages1.addAll(messages);
                logger.trace("#: {}",messages1.size());
                break;
            case "authorfilter":
                for (Message message : messages) {
                    if (message.getAuthor().equals(author)) {
                        messages1.add(message);
                    }
                }
                break;
            case "textfilter":
                for (Message message : messages) {
                    if (message.getText().equals(text)) {
                        messages1.add(message);
                    }
                }
                break;
            case "datefilter":
                for (Message message : messages) {
                    if (message.getCreationDate().isAfter(dateFrom) && message.getCreationDate().isBefore(dateTo)) {
                        messages1.add(message);
                    }
                }
                break;
            case "deletefilter":
                System.out.println(deleted);
                for (Message message : messages) {
                    if(deleted &&message.isDeleted()){
                        messages1.add(message);
                    } else if(!deleted&&!message.isDeleted()){
                        messages1.add(message);
                    }
                }
                break;
            default:
                break;
        }
        Comparator<Message> msgComp = Comparator.comparing((Message::getCreationDate));
        if (orderBy.equals("desc")) {
            msgComp = msgComp.reversed();
        }
        return messages1.stream()
                .sorted(msgComp).collect(Collectors.toList());
    }

    public void messageDelete(long msgId) {
        for (Message message : messages) {
            if (message.getId() == msgId) {
                if (!message.isDeleted())
                    message.setDeleted(true);
            }
        }
    }
}
