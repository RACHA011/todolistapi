package com.racha.todolistapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.racha.todolistapi.model.Account;
import com.racha.todolistapi.model.Profile;
import com.racha.todolistapi.model.Team;
import com.racha.todolistapi.service.AccountService;
import com.racha.todolistapi.service.ProfileService;
import com.racha.todolistapi.service.TeamService;
import com.racha.todolistapi.util.contants.Authority;

@Component
public class SeedData implements CommandLineRunner {
    @Autowired
    AccountService accountService;

    @Autowired
    ProfileService profileService;

    @Autowired
    TeamService teamService;

    @Override
    public void run(String... args) throws Exception {
        Account account01 = new Account();
        Account account02 = new Account();
        Profile profile01 = new Profile();
        Profile profile02 = new Profile();
        Team team01 = new Team();
        Team team02 = new Team();

        account01.setEmail("user@user.com");
        account01.setPassword("password");
        account01.setAuthorities(Authority.USER.toString());
        profile01.setAccount(account01);
        team01.setAccount(account01);

        account02.setEmail("admin@admin.com");
        account02.setPassword("password");
        account02.setAuthorities(Authority.USER.toString());
        account02.setAuthorities(Authority.ADMIN.toString());
        profile02.setAccount(account02);
        team02.setAccount(account02);

        accountService.save(account01);
        accountService.save(account02);

        profileService.save(profile01);
        profileService.save(profile02);

        teamService.save(team01);
        teamService.save(team02);
    }
}
