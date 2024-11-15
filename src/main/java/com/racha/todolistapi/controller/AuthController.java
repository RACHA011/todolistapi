package com.racha.todolistapi.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.racha.todolistapi.model.Account;
import com.racha.todolistapi.model.Profile;
import com.racha.todolistapi.model.Task;
import com.racha.todolistapi.model.Team;
import com.racha.todolistapi.payload.AccountDTO;
import com.racha.todolistapi.payload.AccountViewDTO;
import com.racha.todolistapi.payload.PasswordDTO;
import com.racha.todolistapi.payload.ProfileDTOO;
import com.racha.todolistapi.payload.ProfileViewDTO;
import com.racha.todolistapi.payload.TeamProfileDTO;
import com.racha.todolistapi.payload.TokenDTO;
import com.racha.todolistapi.payload.UserLoginDTO;
import com.racha.todolistapi.payload.team.TeamViewDTO;
import com.racha.todolistapi.service.AccountService;
import com.racha.todolistapi.service.ProfileService;
import com.racha.todolistapi.service.TaskService;
import com.racha.todolistapi.service.TeamService;
import com.racha.todolistapi.service.TokenService;
import com.racha.todolistapi.util.apputils.AppUtil;
import com.racha.todolistapi.util.contants.AccountEnum;
import com.racha.todolistapi.util.contants.PhotosEnum;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*", maxAge = 3600, allowedHeaders = "*")
@Tag(name = "Auth Controller", description = "Controller for Account management")
@Slf4j
public class AuthController {
    static final String PHOTO_FOLDER_NAME = "photos";
    static final String THUMBNAIL_FOLDER_NAME = "thumbnails";
    static final int THUMBNAIL_WIDTH = 300;

    // profile id , team id , is the same as account id for all users

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TeamService teamService;

    // @Autowired
    // private ObjectMapper objectMapper;

    @PostMapping("/token")
    public ResponseEntity<TokenDTO> token(@Valid @RequestBody UserLoginDTO userLogin) throws AuthenticationException {
        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userLogin.getEmail(), userLogin.getPassword()));
            TokenDTO token = new TokenDTO(tokenService.generateToken(authentication));

            return ResponseEntity.ok(token);
        } catch (AuthenticationException e) {
            log.debug(AccountEnum.TOKEN_GENERATION_ERROR.toString() + ": " + e.getMessage());
            return new ResponseEntity<>(new TokenDTO(null), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/user/add", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Add a new user")
    @ApiResponse(responseCode = "200", description = "user created successfully")
    public ResponseEntity<String> addUser(@Valid @RequestBody AccountDTO accountDTO) {
        try {
            Account account = new Account();
            Profile profile = new Profile();
            Team team = new Team();
            account.setEmail(accountDTO.getEmail());
            account.setPassword(accountDTO.getPassword());
            profile.setAccount(account);
            team.setAccount(account);

            // save the account to the database
            accountService.save(account);
            profileService.save(profile);
            teamService.save(team);

            return new ResponseEntity<>("User created successfully", HttpStatus.OK);
        } catch (Exception e) {
            log.debug(AccountEnum.ACCOUNT_ADDED.toString() + ": " + e.getMessage());
            return new ResponseEntity<>("Something went wrong", HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping(value = "/user", produces = "application/json")
    @Operation(summary = "List of all users")
    @ApiResponse(responseCode = "200", description = "List of users")
    @ApiResponse(responseCode = "401", description = "Forbidden")
    @ApiResponse(responseCode = "403", description = "UnAuthorized acccess")
    @SecurityRequirement(name = "todo-list01-api")
    public List<AccountViewDTO> users() {
        List<AccountViewDTO> accounts = new ArrayList<>();
        for (Account acount : accountService.findall()) {
            accounts.add(new AccountViewDTO(acount.getId(), acount.getEmail(), acount.getAuthorities()));
        }
        return accounts;
    }

    @PutMapping(value = "/profile/update-password", consumes = "application/json")
    @Operation(summary = "Update password")
    @ApiResponse(responseCode = "200", description = "password updated successfully")
    @SecurityRequirement(name = "todo-list01-api")
    public ResponseEntity<String> passwordUpdate(@Valid @RequestBody PasswordDTO passwordDTO,
            Authentication authentication) {
        try {
            Account account = validUser(authentication);
            account.setPassword(passwordDTO.getPassword());
            accountService.save(account);
            return new ResponseEntity<>("Password Updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            log.debug("Error updating the password: " + e.getMessage());
            return new ResponseEntity<>("Something went wrong", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/profile", produces = "application/json")
    @Operation(summary = "Get profile information")
    @ApiResponse(responseCode = "200", description = "profile imformation")
    @SecurityRequirement(name = "todo-list01-api")
    public ResponseEntity<ProfileViewDTO> profile(Authentication authentication) {

        try {
            Account account = validUser(authentication);
            Profile profile = profileService.findById(account.getId()).get();
            return new ResponseEntity<>(new ProfileViewDTO(profile.getId(), profile.getUsername(),
                    profile.getFirstname(), profile.getLastname(), profile.getProfilePicture()), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ProfileViewDTO(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping(value = "/profile/update-profile", consumes = "application/json")
    @Operation(summary = "Update profile")
    @ApiResponse(responseCode = "200", description = "Profile updated successfully")
    @SecurityRequirement(name = "todo-list01-api")
    public ResponseEntity<String> updateProfile(
            @RequestBody ProfileDTOO profileDTO,
            Authentication authentication) {
        try {
            Account account = validUser(authentication);
            Profile profile = profileService.findById(account.getId()).get();
            profile.setUsername(profileDTO.getUsername());

            profile.setFirstname(profileDTO.getFirstname());
            profile.setLastname(profileDTO.getLastname());

            profileService.save(profile);

            return new ResponseEntity<>("profile updated Successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("something went wrong", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "/profile/update-profile-picture", consumes = "multipart/form-data")
    @Operation(summary = "Update profile picture")
    @ApiResponse(responseCode = "200", description = "Profile picture updated successfully")
    @SecurityRequirement(name = "todo-list01-api")
    public ResponseEntity<String> updateProfilepicture(
            @Valid @RequestPart(required = true) MultipartFile profilePicture,
            Authentication authentication) {
        try {
            Account account = validUser(authentication);
            Profile profile = profileService.findById(account.getId()).get();
            // Handle the file upload
            if (!profilePicture.isEmpty()) {
                // Assuming you have a method to handle file storage
                String contentType = profilePicture.getContentType();

                if (contentType.equals("image/jpg")
                        || contentType.equals("image/jpeg")
                        || contentType.equals("image/png")) {
                    String profilePictureUrl = storeProfilePicture(profilePicture, profile);
                    profile.setProfilePicture(profilePictureUrl);
                }
            }

            profileService.save(profile);
            return new ResponseEntity<>("Profile picture updated Successfully", HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("somthing went wrong", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value = "/profile/delete")
    @Operation(summary = "Delete account")
    @ApiResponse(responseCode = "200", description = "account deleted successfully")
    @ApiResponse(responseCode = "401", description = "Forbidden")
    @ApiResponse(responseCode = "403", description = "Unauthorized acccess")
    @SecurityRequirement(name = "todo-list01-api")
    public ResponseEntity<String> deleteAccount(Authentication authentication) {
        Account account = validUser(authentication);
        Profile profile = profileService.findById(account.getId()).get();

        List<Task> allTask = taskService.findAll();

        for (Task task : allTask) {
            if (task.getAccount() == account) {
                try {
                    taskService.delete(task);
                } catch (Exception e) {
                    log.debug(
                            "somethong went wrong while deleting task: " + task.getTitle() + "\n -> " + e.getMessage());
                }
            }
        }

        profileService.delete(profile);
        accountService.delete(account);
        return new ResponseEntity<>("Account deleted successfully", HttpStatus.OK);
    }

    @GetMapping("/profile/profile-picture/{id}/download")
    @Operation(summary = "Download the profile picture")
    @ApiResponse(responseCode = "200", description = "Successfully downloaded the photo")
    @SecurityRequirement(name = "todo-list01-api")
    public ResponseEntity<?> download(@PathVariable("id") Long id) {

        Optional<Account> optionalAccount = accountService.findById(id);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            return downloadFile(account.getId(), PHOTO_FOLDER_NAME);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/profile/profile-picture/{id}/download-thumbnail")
    @Operation(summary = "Download the profile picture")
    @ApiResponse(responseCode = "200", description = "Successfully downloaded the photo")
    @SecurityRequirement(name = "todo-list01-api")
    public ResponseEntity<?> downloadThumbnail(@PathVariable("id") Long id) {

        Optional<Account> optionalAccount = accountService.findById(id);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            return downloadFile(account.getId(), THUMBNAIL_FOLDER_NAME);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // team work management

    @GetMapping("/team")
    @Operation(summary = "get all my friends")
    @SecurityRequirement(name = "todo-list01-api")
    public ResponseEntity<TeamViewDTO> getTeam(Authentication authentication) {
        try {
            Account account = validUser(authentication);
            Team team = teamService.findById(account.getId()).get();
            return new ResponseEntity<>(new TeamViewDTO(team.getId(), team.getFriends()), HttpStatus.OK);
        } catch (Exception e) {
            log.debug("error getting team : " + e.getMessage());
            return new ResponseEntity<>(new TeamViewDTO(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // i no longer remember the use for this
    @GetMapping("/team/getall")
    @Operation(summary = "get all profiles")
    @SecurityRequirement(name = "todo-list01-api")
    public ResponseEntity<List<TeamProfileDTO>> getAllProfile(Authentication authentication) {
        try {
            Account account = validUser(authentication);

            List<Profile> profiles = profileService.findAll();
            List<TeamProfileDTO> teamProfiles = new ArrayList<>();
            for (Profile profile : profiles) {
                if (profile.getId() != account.getId()) {
                    teamProfiles
                            .add(new TeamProfileDTO(profile.getId(), profile.getUsername(),
                                    profile.getProfilePicture()));
                }
            }

            return new ResponseEntity<>(teamProfiles, HttpStatus.OK);
        } catch (Exception e) {
            log.debug("Error getting profiles : " + e.getMessage());
            return new ResponseEntity<>(new ArrayList<TeamProfileDTO>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // is used to get friends profile information
    @GetMapping("/team/profile/{id}")
    @Operation(summary = "get all profiles")
    @SecurityRequirement(name = "todo-list01-api")
    public ResponseEntity<TeamProfileDTO> getProfileInfo(@PathVariable("id") Long id) {

        try {
            Profile profile = profileService.findById(id).get();
            return new ResponseEntity<>(
                    new TeamProfileDTO(profile.getId(), profile.getUsername(), profile.getProfilePicture()),
                    HttpStatus.OK);
        } catch (Exception e) {
            log.debug("Error fetching team profiles info : " + e.getMessage());
            return new ResponseEntity<>(new TeamProfileDTO(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // this mapping requires the user id to be able to add them as friends
    @PostMapping(value = "/team/add/{id}")
    @Operation(summary = "Add a new friend")
    @ApiResponse(responseCode = "200", description = "Friend is added successfully")
    @SecurityRequirement(name = "todo-list01-api")
    public ResponseEntity<String> addTeam(@PathVariable("id") Long id, Authentication authentication) {
        try {
            Account account = validUser(authentication);
            Team team = teamService.findById(account.getId()).get();
            List<Profile> profiles = profileService.findAll();

            boolean idInvalid = true;
            OUT: for (Profile profile : profiles) {
                // confirm if there is a profile with the certain id
                if (profile.getId() == id && id != account.getId()) {
                    List<Long> friendList = team.getFriends();

                    idInvalid = false;

                    // if there are no friends create new users
                    if (friendList == null) {
                        friendList = new ArrayList<>();
                    }

                    if (friendList.contains(id)) {
                        idInvalid = true;
                    } else {
                        // add a team member
                        friendList.add(id);
                        team.setFriends(friendList);
                    }

                    break OUT;
                }
            }
            if (idInvalid) {
                return new ResponseEntity<>("Invalid friend id", HttpStatus.BAD_REQUEST);
            }
            teamService.save(team);
            return new ResponseEntity<>("Friend added successfully", HttpStatus.OK);
        } catch (Exception e) {
            log.debug("Error while adding a friend: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value = "/team/removefriend/{id}")
    @Operation(summary = "Remove a friend from the team")
    @ApiResponse(responseCode = "200", description = "Friend removed successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @SecurityRequirement(name = "todo-list01-api")
    public ResponseEntity<String> deleteFriend(
            @PathVariable("id") Long id,
            Authentication authentication) {

        // Validate the user and get their account
        Account account = validUser(authentication);

        // Fetch the team associated with the account
        Team team = teamService.findById(account.getId()).orElse(null);

        System.out.println(team.toString());
        System.out.println();

        // if (team == null) {
        // return new ResponseEntity<>("Team not found", HttpStatus.NOT_FOUND);
        // }

        // Get the list of friends and remove the specified friend
        List<Long> friends = team.getFriends();
        System.out.println(friends.toString());
        boolean removed = friends.remove(id);

        if (!removed) {
            return new ResponseEntity<>("Friend not found in the list", HttpStatus.NOT_FOUND);
        }
        team.setFriends(friends);

        // Save the updated team (assuming you have a save method)
        teamService.save(team);

        return new ResponseEntity<>("Friend removed successfully", HttpStatus.OK);
    }

    private Account validUser(Authentication authentication) {
        String email = authentication.getName();
        return accountService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    private String storeProfilePicture(MultipartFile file, Profile profile) throws IOException {
        // Implement your file storage logic here
        // For example, save the file to the filesystem or a cloud storage service
        // Return the URL or path where the file is stored
        int length = 10;
        boolean useLetters = true;
        boolean useNumbers = true;

        try {
            String fileName = file.getOriginalFilename();
            String generetedString = RandomStringUtils.random(length, useLetters, useNumbers);
            String finalPhotoName = generetedString + fileName;
            String absoluteFileLocation = AppUtil.getPhotoUploadPath(finalPhotoName, PHOTO_FOLDER_NAME,
                    profile.getId());
            Path path = Paths.get(absoluteFileLocation);

            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            BufferedImage thumbnail = AppUtil.getThumbnail(file, THUMBNAIL_WIDTH);
            File thumbnailLocation = new File(
                    AppUtil.getPhotoUploadPath(finalPhotoName, THUMBNAIL_FOLDER_NAME, profile.getId()));
            ImageIO.write(thumbnail, file.getContentType().split("/")[1], thumbnailLocation);
            profile.setFinalFilename(finalPhotoName);
            return fileName;
        } catch (Exception e) {
            log.debug(PhotosEnum.PHOTO_UPLOUD_ERROR.toString() + ": " + e.getMessage());
            return null;
        }
    }

    public ResponseEntity<?> downloadFile(Long photoId, String folberName) {
        Optional<Account> optionalAccount = accountService.findById(photoId);
        Account account = optionalAccount.get();
        Profile profile = profileService.findById(account.getId()).get();
        if (optionalAccount.isPresent()) {

            Resource resource = null;
            try {
                resource = AppUtil.getFileResource(photoId, folberName, profile.getFinalFilename());
                if (resource == null) {
                    return new ResponseEntity<>("File Not Found", HttpStatus.NOT_FOUND);
                }
            } catch (IOException e) {
                return ResponseEntity.internalServerError().build();
            }

            String contentType = "application/octet-stream";
            String headerValue = "attachment; filename=\"" + profile.getProfilePicture() + "\"";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                    .body(resource);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

}
