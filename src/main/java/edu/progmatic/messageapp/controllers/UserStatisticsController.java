package edu.progmatic.messageapp.controllers;

import edu.progmatic.messageapp.UserStatistics;
import org.springframework.beans.factory.annotation.Autowired;

public class UserStatisticsController {
    private final UserStatistics userStatistics;

    @Autowired
    public UserStatisticsController(UserStatistics userStatistics) {
        this.userStatistics = userStatistics;
    }
}
