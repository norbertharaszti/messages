package edu.progmatic.messageapp.controllers;

import edu.progmatic.messageapp.UserStatistics;
import edu.progmatic.messageapp.modell.Message;
import edu.progmatic.messageapp.modell.RegisteredUser;
import edu.progmatic.messageapp.services.MessageService;
import edu.progmatic.messageapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
    private UserService userService;
    private MessageService messageService;

    @Autowired
    public MessageController(UserService userService, MessageService messageService) {
        this.userService = userService;
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
        if (!SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            msgs.removeIf(Message::isDeleted);
        }
        model.addAttribute("msgList", msgs);
        return "messageList";
    }

    @GetMapping("/message/{id}")
    public String showOneMessage(
            @PathVariable("id") Long msgId,
            Model model) {
        Message message = messageService.getMessage(msgId);

        model.addAttribute("message", message);
        return "oneMessage";
    }

    @PostMapping(path = "/createmessage")
    public String createMessage(@Valid @ModelAttribute("message") Message m, BindingResult bindingResult) {
        RegisteredUser user = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
                                @RequestParam(name = "deleted", defaultValue = "", required = false) String deleteChoose,
                                @RequestParam(name = "messagefilter", defaultValue = "", required = false) String filterBy,
                                Model model
    ) {
        System.out.println(deleteChoose);
        boolean deleted = deleteChoose.equals("on");
        List<Message> msgs = messageService.filterMessages2(author, text, dateFrom, dateTo, orderBy, filterBy, deleted);
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

    @PostMapping(path = "/message/del/{id}")
    public String deleteMessage(@PathVariable("id") Long msgId) {
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            messageService.messageDelete(msgId);
        }
        return "redirect:/messages";
    }

    @ExceptionHandler(Exception.class)
    public String errorsHandler(Exception ex, Model model) {
        StringBuilder errorStack = new StringBuilder();
        StackTraceElement[] stackTraceElement = ex.getStackTrace();
        for (StackTraceElement traceElement : stackTraceElement) {
            errorStack.append(traceElement.toString());
            errorStack.append("/n");
        }
        model.addAttribute("exceptionMessage", errorStack);
        model.addAttribute("errorMessage",ex.getMessage());
        String errorMessage= ex.getMessage();
        System.out.println(errorMessage);
        return "error";
    }

}

