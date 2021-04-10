package pt.ulisboa.tecnico.socialsoftware.tutor.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.Demo;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.UsersXmlExport;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.UsersXmlImport;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class UserService {
    private static Logger logger = LoggerFactory.getLogger(UserService.class);


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @PersistenceContext
    EntityManager entityManager;

    public User findByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }

    public User findByKey(Integer key) {
        return this.userRepository.findByKey(key);
    }

    public Integer getMaxUserNumber() {
        Integer result = userRepository.getMaxUserNumber();
        return result != null ? result : 0;
    }

    public User createUser(String name, String username, User.Role role) {

        if (findByUsername(username) != null) {
            throw new TutorException(DUPLICATE_USER, username);
        }

        User user = new User(name, username, getMaxUserNumber() + 1, role);
        userRepository.save(user);
        return user;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String getEnrolledCoursesAcronyms(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        return user.getEnrolledCoursesAcronyms();
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Set<QuestionDto> getAnsweredQuestions(String username){
        User user = this.userRepository.findByUsername(username);
        Set<QuizAnswer> quizAnswers = user.getQuizAnswers();
        if(quizAnswers == null || quizAnswers.isEmpty()){
            return new HashSet<>();
        }
        Set<QuestionAnswer> questionAnswerList = new HashSet<>();
        for(QuizAnswer quizAnswer: quizAnswers){
            questionAnswerList.addAll(quizAnswer.getQuestionAnswers());
        }
        return questionAnswerList.stream().map(QuestionAnswer::getQuizQuestion)
                .map(QuizQuestion::getQuestion).map(QuestionDto::new).collect(Collectors.toSet());
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<CourseDto> getCourseExecutions(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        return user.getCourseExecutions().stream().map(CourseDto::new).collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void addCourseExecution(int userId, int executionId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));

        user.addCourse(courseExecution);
        courseExecution.addUser(user);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Boolean getPrivacy(int userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        return user.getPrivacy();
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void togglePrivacy(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        user.togglePrivacy();
    }


    public String exportUsers() {
        UsersXmlExport xmlExporter = new UsersXmlExport();

        return xmlExporter.export(userRepository.findAll());
    }


    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void importUsers(String usersXML) {
        UsersXmlImport xmlImporter = new UsersXmlImport();

        xmlImporter.importUsers(usersXML, this);
    }



    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public User getDemoTeacher() {
        User user = this.userRepository.findByUsername(Demo.TEACHER_USERNAME);
        if (user == null)
            return createUser("Demo Teacher", Demo.TEACHER_USERNAME, User.Role.TEACHER);
        return user;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public User getDemoStudent() {
        User user = this.userRepository.findByUsername(Demo.STUDENT_USERNAME);
        if (user == null)
            return createUser("Demo Student", Demo.STUDENT_USERNAME, User.Role.STUDENT);
        return user;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public User getDemoAdmin() {
        User user =  this.userRepository.findByUsername(Demo.ADMIN_USERNAME);
        if (user == null)
            return createUser("Demo Admin", Demo.ADMIN_USERNAME, User.Role.DEMO_ADMIN);
        return user;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public User createDemoStudent() {
        String birthDate = DateHandler.now().toString();
        User newDemoUser = createUser("Demo-Student-" + birthDate, "Demo-Student-" + birthDate, User.Role.STUDENT);

        User demoUser = this.userRepository.findByUsername(Demo.STUDENT_USERNAME);

        CourseExecution courseExecution = demoUser.getCourseExecutions().stream().findAny().orElse(null);

        if (courseExecution != null) {
            courseExecution.addUser(newDemoUser);
            newDemoUser.addCourse(courseExecution);
        }

        return newDemoUser;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public User changeDashBoardPrivacy(Integer userid){
        if(userid == null){
            throw new TutorException(USER_IS_EMPTY);
        }
        User user = userRepository.findById(userid).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userid));
        if(user.getRole() != User.Role.STUDENT){
            throw new TutorException(USER_IS_NOT_STUDENT);
        }
        user.setDashboardPrivacy(!user.isDashboardPrivacy());
        userRepository.save(user);
        return user;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public User getDashBoardPrivacy(Integer userid){
        if(userid == null){
            throw new TutorException(USER_IS_EMPTY);
        }
        User user = userRepository.findById(userid).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userid));
        if(user.getRole() != User.Role.STUDENT){
            throw new TutorException(USER_IS_NOT_STUDENT);
        }
        return user;
    }
}
