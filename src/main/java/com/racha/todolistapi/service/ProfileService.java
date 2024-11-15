package com.racha.todolistapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.racha.todolistapi.model.Profile;
import com.racha.todolistapi.repository.ProfileRepository;

@Service
public class ProfileService {
    @Autowired
    private ProfileRepository profileRepository;

    private static int number = 1;

    private static int getsomeNumber(){
        return number++;
    }

    public Profile save(Profile profile) {
        if (profile.getUsername() == null) {
            profile.setUsername("user" + getsomeNumber());
        }
        return profileRepository.save(profile);
    }

    public List<Profile> findAll() {
        return profileRepository.findAll();
    }

    public Optional<Profile> findByUsername(String username) {
        return profileRepository.findByUsername(username);
    }

    public Optional<Profile> findById(Long id) {
        return profileRepository.findById(id);
    }

    public void delete(Profile profile) {
        profileRepository.delete(profile);
    }

}
