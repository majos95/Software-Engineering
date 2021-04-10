package pt.ulisboa.tecnico.socialsoftware.tutor.evaluation;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;
import java.security.Principal;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.CLARIFICATION_INVALID_USER;
//trying to merge

@RestController
public class EvaluationController {

    private EvaluationService evaluationService;

    EvaluationController(EvaluationService evaluationService){ this.evaluationService = evaluationService;}

    @GetMapping("/evaluations/{questionId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#questionId, 'QUESTION.ACCESS')")
    public EvaluationDto findEvaluation(@PathVariable Integer questionId){
        return this.evaluationService.findEvaluationByKey(questionId);
    }

    @PutMapping("/evaluations/{questionId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#questionId, 'QUESTION.ACCESS')")
    public EvaluationDto submitEvaluation(Principal principal, @PathVariable Integer questionId, @Valid @RequestBody EvaluationDto evaluationDto){

        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        } if (user.getRole() != User.Role.TEACHER) {
            throw new TutorException(CLARIFICATION_INVALID_USER);
        }

        return this.evaluationService.submitEvaluation(user.getUsername(), questionId, evaluationDto);
    }
}

