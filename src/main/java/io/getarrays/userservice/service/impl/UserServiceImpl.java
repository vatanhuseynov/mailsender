package io.getarrays.userservice.service.impl;

import io.getarrays.userservice.model.Confirmation;
import io.getarrays.userservice.model.User;
import io.getarrays.userservice.repository.ConfirmationRepository;
import io.getarrays.userservice.repository.UserRepository;
import io.getarrays.userservice.service.EmailService;
import io.getarrays.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ConfirmationRepository confirmationRepository;
    private final EmailService emailService;
    @Override
    public User saveUser(User user) {
       if(userRepository.existsByEmail(user.getEmail())) { throw new RuntimeException("email already exist");}

       user.setIsEnabled(false);
       userRepository.save(user);

        Confirmation confirmation = new Confirmation(user); // bu user a gore token yaradilir
        confirmationRepository.save(confirmation);  //confirmation table bu confirmation elave olunnur foreign key ile (user)
        /* send email to user with token*/
        // emailService.sendSimpleMailMessage(user.getName(),user.getEmail(), confirmation.getToken());
       // emailService.sendMimeMessageWithAttachments(user.getName(),user.getEmail(),confirmation.getToken());
       // emailService.sendMimeMessageWithEmbeddedFiles(user.getName(),user.getEmail(),confirmation.getToken());
      //   emailService.sendHtmlEmail(user.getName(),user.getEmail(),confirmation.getToken());
        emailService.sendHtmlEmailWithEmbeddedFiles(user.getName(),user.getEmail(),confirmation.getToken());
    return user;
    }

    @Override
    public Boolean verifyToken(String token) {
        Confirmation confirmation = confirmationRepository.findByToken(token);               //hemin tokene uygun confirmation tapiriq ve
        User user = userRepository.findByEmailIgnoreCase(confirmation.getUser().getEmail()); // hemin confir in icinde ki useru getEmail oturub  aliriq
        user.setIsEnabled(true);                                             // gonderilen TOKEN dogrulandiq da  useru aktiv edirik
        userRepository.save(user);
        //confirmationRepository.delete(confirmation);
        return Boolean.TRUE;
    }
}
