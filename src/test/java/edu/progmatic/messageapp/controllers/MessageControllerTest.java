package edu.progmatic.messageapp.controllers;

import edu.progmatic.messageapp.modell.Message;
import edu.progmatic.messageapp.services.MessageService;
import edu.progmatic.messageapp.services.UserService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class MessageControllerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void showOneMessage() {
    }

    @Test
    public void filterMessage() throws Exception {
        MessageService ms = Mockito.mock(MessageService.class);
        UserService us= Mockito.mock(UserService.class);
        List<Message> msgList=new ArrayList<>();
        msgList.add(new Message("Aladár","Kapcs-ford", LocalDateTime.now()));
        Mockito.when(ms.filterMessages2(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.anyBoolean())).thenReturn(msgList);
        MockMvc mockMvc= MockMvcBuilders.standaloneSetup(new MessageController(us,ms)).build();
        mockMvc.perform(MockMvcRequestBuilders.get("/filtermessage")).andExpect(MockMvcResultMatchers.model().attributeExists("msgList"))
        .andExpect(MockMvcResultMatchers.view().name("messageList"));
    }
}