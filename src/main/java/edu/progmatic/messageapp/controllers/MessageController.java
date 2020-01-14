package edu.progmatic.messageapp.controllers;

import edu.progmatic.messageapp.UserStatistics;
import edu.progmatic.messageapp.modell.Message;
import edu.progmatic.messageapp.modell.RegisteredUser;
import edu.progmatic.messageapp.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class MessageController {
    private UserStatistics userStatistics = new UserStatistics();
    private InMemoryUserDetailsManager userService;
    private MessageService messageService;

    @Autowired
    public MessageController(@Qualifier("registerUser") UserDetailsService userService, MessageService messageService) {
        this.userService = (InMemoryUserDetailsManager) userService;
        this.messageService = messageService;
    }

    private Set<String> authors = new HashSet<>();


    @RequestMapping(value = "/messages", method = RequestMethod.GET)
    public String showMessages(
            @RequestParam(name = "limit", defaultValue = "100", required = false) Integer limit,
            @RequestParam(name = "orderby", defaultValue = "", required = false) String orderBy,
            @RequestParam(name = "order", defaultValue = "asc", required = false) String order,
            Model model) {

        List<Message> msgs = messageService.filterMessages(limit, orderBy, order);
        model.addAttribute("msgList", msgs);
        return "messageList";
    }

    @GetMapping("/message/{id}")
    public String showOneMessage(
            @PathVariable("id") Long msgId,
            Model model) {
        Optional<Message> message = Optional.ofNullable(messageService.getMessage(msgId));

        model.addAttribute("message", message);
        return "oneMessage";
    }

    @PostMapping(path = "/createmessage")
    public String createMessage(@Valid @ModelAttribute("message") Message m, BindingResult bindingResult) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        m.setAuthor(user.getUsername());
        if (bindingResult.hasErrors()) {
            return "showCreate";
        }
        messageService.createMessage(m);
        userStatistics.setName(m.getAuthor());
        authors.add(m.getAuthor());
        userStatistics.setAuthorCounter(authors.size());
        userStatistics.setMessageCounter(userStatistics.getMessageCounter() + 1);
        System.out.println("messages: " + userStatistics.getMessageCounter());
        System.out.println("authors: " + userStatistics.getAuthorCounter());
        return "redirect:/messages";
    }

    @GetMapping(path = "/showCreate")
    public String showCreate(Model model) {
        Message m = new Message();

        m.setAuthor(userStatistics.getName());
        model.addAttribute("message", m);
        return "showCreate";
    }

    @GetMapping("/filtermessage")
    public String filterMessage(@RequestParam(name = "author", defaultValue = "", required = false) String author,
                                @RequestParam(name = "text", defaultValue = "", required = false) String text,
                                @RequestParam(name = "dateFrom", defaultValue = "", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
                                @RequestParam(name = "dateTo", defaultValue = "", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo,
                                @RequestParam(name = "orderby", defaultValue = "asc", required = false) String orderBy,
                                @RequestParam(name = "messagefilter", defaultValue = "", required = false) String filterBy,
                                Model model
    ) {
        List<Message> msgs = messageService.filterMessages2(author, text, dateFrom, dateTo, orderBy, filterBy);
        model.addAttribute("msgList", msgs);

        return "messageList";
    }

    @GetMapping(path = "/login")
    public String login() {
        return "login";
    }

    @GetMapping(path = "/logout")
    public String logout() {
        return "logout";
    }

    @GetMapping(path = "/registerUser")
    public String registerUser(@ModelAttribute("registerUser") RegisteredUser registeredUser) {

        return "registerUser";
    }

    @PostMapping(path = "/register")
    public String register(@Valid @ModelAttribute("registerUser") RegisteredUser registeredUser, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "registerUser";
        }
        if (userService.userExists(registeredUser.getUsername())) {
            bindingResult.rejectValue("username", "registration.username", "Ez a felhasználónév már foglalt!");
            return "registerUser";
        }
        registeredUser.addAuthority("ROLE_USER");
        userService.createUser(registeredUser);
        return "redirect:/login";
    }
}
