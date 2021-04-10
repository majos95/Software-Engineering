package pt.ulisboa.tecnico.socialsoftware.tutor.clarification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.security.Principal;

@RestController
public class ClarificationController {



    @Autowired
    ClarificationService clarificationService;

    @PostMapping("/doubts/{doubtId}/solve")
    //@PreAuthorize("(hasRole('ROLE_TEACHER') and hasPermission(#doubtId, 'CLARIFICATION.CREATE')) or (hasRole('ROLE_TEACHER') and hasPermission(#clarificationDto,'CLARIFICATION.DEMO'))")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ClarificationDto createClarification(Principal principal, @PathVariable int doubtId,
                                                @RequestBody ClarificationDto clarificationDto) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return clarificationService.createClarification(clarificationDto,doubtId,user.getId());
    }



}
