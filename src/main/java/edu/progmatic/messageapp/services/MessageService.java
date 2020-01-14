package edu.progmatic.messageapp.services;

import edu.progmatic.messageapp.modell.Message;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private static List<Message> messages = new ArrayList<>();

    {
        messages.add(new Message("Aladár", "Mz/x jelkezz, jelkezz", LocalDateTime.now()));
        messages.add(new Message("Kriszta", "Bemutatom lüke Aladárt", LocalDateTime.now()));
        messages.add(new Message("Blöki", "Vauuu", LocalDateTime.now()));
        messages.add(new Message("Maffia", "miauuu", LocalDateTime.now()));
        messages.add(new Message("Aladár", "Kapcs/ford", LocalDateTime.now()));
        messages.add(new Message("Aladár", "Adj pénzt!", LocalDateTime.now()));
    }

    public List<Message> filterMessages( Integer limit, String orderBy, String order) {
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

        List<Message> msgs = messages.stream()
                .sorted(msgComp)
                .limit(limit).collect(Collectors.toList());

        return msgs;
    }
    public Message getMessage(Long msgId) {
        Optional<Message> message=messages.stream().filter(m->m.getId().equals(msgId)).findFirst();
        return message.get();
    }
    public void createMessage(Message m){
        m.setCreationDate(LocalDateTime.now());
        m.setId((long) messages.size());
        messages.add(m);
    }
    public List<Message> filterMessages2(String author, String text, LocalDateTime dateFrom, LocalDateTime dateTo, String orderBy, String filterBy)
    {
        List<Message> messages1 = new ArrayList<>();
        switch (filterBy) {
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
            default:
                break;
        }
        Comparator<Message> msgComp = Comparator.comparing((Message::getCreationDate));
        if (orderBy.equals("desc")) {
            msgComp = msgComp.reversed();
        }
        List<Message> msgs = messages1.stream()
                .sorted(msgComp).collect(Collectors.toList());
        return msgs;
    }
}
