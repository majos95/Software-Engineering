package pt.ulisboa.tecnico.socialsoftware.tutor.doubt.service


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationService
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.doubt.Discussion
import pt.ulisboa.tecnico.socialsoftware.tutor.doubt.DiscussionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.doubt.DiscussionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.doubt.Doubt
import pt.ulisboa.tecnico.socialsoftware.tutor.doubt.DoubtDto
import pt.ulisboa.tecnico.socialsoftware.tutor.doubt.DoubtService
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.doubt.DoubtRepositor

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*


import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime


@DataJpaTest
class CreateDiscussionTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String COURSE2_NAME = "Distributed Systems"
    public static final String QUESTION_TITLE = 'question title'
    public static final String QUESTION2_TITLE = 'question2 title'
    public static final String QUESTION_CONTENT = 'question content'
    public static final String QUESTION2_CONTENT = 'question2 content'
    public static final String QUIZ_TITLE = "quiz title"
    public static final Integer QUIZ_SERIES = 1
    public static final Integer QUIZ_KEY = 2
    public static final Integer QUESTION_KEY = 52
    public static final Integer QUESTION2_KEY = 54
    public static final String USER_NAME = "user"
    public static final String USERNAME_NAME = "username"
    public static final String TEACHER_NAME = "teacher"
    public static final String TEACHER_USERNAME = "teacher username"
    public static final Integer TEACHER_KEY = 3
    public static final Integer USER_KEY = 90000
    public static final String DOUBT_CONTENT = "doubt content"
    public static final String DOUBT_TITLE = "doubt title"
    public static final String DOUBT2_CONTENT = "doubt2 content"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"




    @Autowired
    DoubtService doubtService

    @Autowired
    DoubtRepositor doubtRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    UserRepository userRepository


    @Autowired
    QuizRepository quizRepository

    @Autowired
    QuizQuestionRepository quizQuestionRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    QuizAnswerRepository quizAnswerRepository

    @Autowired
    QuestionAnswerRepository questionAnswerRepository

    @Autowired
    ClarificationService clarificationService

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    DiscussionRepository discussionRepository

    def question
    def question2
    def questiondto
    def questiondto2
    def optiondto
    def course
    def course2
    def quiz
    def quiz2
    def quizdto
    def teacher
    def student
    def options
    def quizquestion
    def quizquestion2
    def quizanswer
    def quizanswer2
    def questionanswer
    def questionanswer2
    def courseExecution
    def courseExecution2


    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        course2 = new Course(COURSE2_NAME, Course.Type.TECNICO)
        courseRepository.save(course)
        courseRepository.save(course2)


        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        courseExecution2 = new CourseExecution(course2, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution2)

        quiz = new Quiz()
        quiz.setKey(QUIZ_KEY)
        quiz.setType(Quiz.QuizType.GENERATED.toString())
        quiz.setCourseExecution(courseExecution)
        quiz.setAvailableDate(DateHandler.now())
        quiz2 = new Quiz()
        quiz2.setKey(QUIZ_KEY)
        quiz2.setType(Quiz.QuizType.GENERATED.toString())
        quiz2.setCourseExecution(courseExecution)
        quiz2.setAvailableDate(DateHandler.now())
        quizRepository.save(quiz)
        quizRepository.save(quiz2)

        student  = new User(USER_NAME, USERNAME_NAME, USER_KEY, User.Role.STUDENT)
        teacher = new User(TEACHER_NAME, TEACHER_USERNAME, TEACHER_KEY, User.Role.TEACHER)
        userRepository.save(student)
        userRepository.save(teacher)

        questiondto = new QuestionDto()
        questiondto.setKey(QUESTION_KEY)
        questiondto.setTitle(QUESTION_TITLE)
        questiondto.setContent(QUESTION_CONTENT)
        questiondto.setStatus(Question.Status.AVAILABLE.name())

        optiondto = new OptionDto()
        optiondto.setContent(QUESTION_CONTENT)
        optiondto.setCorrect(true)
        options = new ArrayList<OptionDto>()
        options.add(optiondto)
        questiondto.setOptions(options)

        questiondto2 = new QuestionDto()
        questiondto2.setKey(QUESTION2_KEY)
        questiondto2.setTitle(QUESTION2_TITLE)
        questiondto2.setContent(QUESTION2_CONTENT)
        questiondto2.setStatus(Question.Status.AVAILABLE.name())

        questiondto2.setOptions(options)

        question = new Question(course, questiondto)
        question2 = new Question(course2, questiondto2)

        questionRepository.save(question)
        questionRepository.save(question2)

        quizanswer = new QuizAnswer(student, quiz)
        quizanswer2 = new QuizAnswer(student, quiz2)
        student.addQuizAnswer(quizanswer)

        quizquestion = new QuizQuestion(quiz, question, 1)
        questionanswer = new QuestionAnswer(quizanswer, quizquestion, 30);
        quizanswer.addQuestionAnswer(questionanswer);
        quizquestion.addQuestionAnswer(questionanswer);

        quizquestion2 = new QuizQuestion(quiz, question2, 2)
        questionanswer2 = new QuestionAnswer(quizanswer2, quizquestion2, 60)
        quizanswer2.addQuestionAnswer(questionanswer2)
        quizquestion2.addQuestionAnswer(questionanswer2)

        quizQuestionRepository.save(quizquestion)
        quizQuestionRepository.save(quizquestion2)
        questionAnswerRepository.save(questionanswer)
        questionAnswerRepository.save(questionanswer2)
        quizAnswerRepository.save(quizanswer)
        quizAnswerRepository.save(quizanswer2)


    }

    def "create a Discussion with a User, Content and a Question"() {
        given: "a DoubtDto and DiscussionDto"
        def doubtdto = new DoubtDto()
        doubtdto.setContent(DOUBT_CONTENT)
        doubtdto.setAuthor(USER_NAME)
        def discussionDto = new DiscussionDto()
        discussionDto.addPostDto(doubtdto)
        discussionDto.setQuestionTitle(QUESTION_TITLE)
        discussionDto.setStatus(Discussion.Status.OPEN)
        discussionDto.setTitle(DOUBT_TITLE)


        when: "A discussion is created"
        doubtService.createDiscussion(discussionDto, questionanswer.getId(), student.getId())

        then:
        discussionRepository.count() == 1L
        def result = discussionRepository.findAll().get(0)

        result.getId() != null
        def getPostsList = new ArrayList<Doubt>(result.getPosts())
        getPostsList.get(0).getContent() == DOUBT_CONTENT
        getPostsList.get(0).getAuthor().getName() == USER_NAME
        result.getAuthor().getRole() == User.Role.STUDENT
        result.getStatus() == Discussion.Status.OPEN;
        result.getQuestion().getTitle() == QUESTION_TITLE
        questionanswer.getDiscussions().get(0).equals(result)
    }

    @Unroll
    def "invalid arguments: userid =#userid | questionid =#questionid || errormessage =#errormessage"(){
        given: "a doubtdto"
        def doubtdto = new DoubtDto()
        doubtdto.setContent(DOUBT_CONTENT)
        doubtdto.setAuthor(USER_NAME)
        def discussionDto = new DiscussionDto()
        discussionDto.addPostDto(doubtdto)
        discussionDto.setQuestionTitle(QUESTION_TITLE)
        discussionDto.setStatus(Discussion.Status.OPEN)
        discussionDto.setTitle(DOUBT_TITLE)

        when: "A doubt is created"
        doubtService.createDiscussion(discussionDto, questionid, userid)

        then:
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:
        userid | questionid || errorMessage
        null   |   3        || DOUBT_USER_IS_EMPTY
        5      | null       || DOUBT_QUESTION_IS_EMPTY
        9000   | 7          || USER_NOT_FOUND
        9      | 9000       || QUESTION_NOT_FOUND

    }



    @Unroll
    def "invalid data in database where content is #content"(){
        given: "a DoubtDto"
        def doubtdto = new DoubtDto()
        doubtdto.setContent(content)
        def discussionDto = new DiscussionDto()
        discussionDto.addPostDto(doubtdto)
        discussionDto.setQuestionTitle(QUESTION_TITLE)
        discussionDto.setStatus(Discussion.Status.OPEN)
        discussionDto.setTitle(DOUBT_TITLE)

        when: "A doubt is created"
        doubtService.createDiscussion(discussionDto, questionanswer.getId(), student.getId())

        then:
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:
        content || errorMessage
        "     " || DOUBT_CONTENT_IS_EMPTY
        null    || DOUBT_CONTENT_IS_EMPTY

    }


    def "create a Doubt but the User is not a Student"() {
        given: "a DoubtDto"
        def doubtdto = new DoubtDto()
        doubtdto.setContent(DOUBT_CONTENT)
        doubtdto.setAuthor(USER_NAME)
        def discussionDto = new DiscussionDto()
        discussionDto.addPostDto(doubtdto)
        discussionDto.setQuestionTitle(QUESTION_TITLE)
        discussionDto.setStatus(Discussion.Status.OPEN)
        discussionDto.setTitle(DOUBT_TITLE)

        when: "A doubt is created"
        doubtService.createDiscussion(discussionDto, questionanswer.getId(), teacher.getId())

        then:
        def exception = thrown(TutorException)
        exception.errorMessage == DOUBT_USER_IS_NOT_A_STUDENT
    }


    def "create two Doubts with a User, Content, Question"() {
        given: "two DoubtDtos"
        def doubtdto = new DoubtDto()
        def doubtdto2 = new DoubtDto()
        doubtdto.setContent(DOUBT_CONTENT)
        doubtdto.setAuthor(USER_NAME)
        doubtdto.setStatus(Doubt.Status.UNSOLVED)
        doubtdto2.setContent(DOUBT2_CONTENT)
        doubtdto2.setAuthor(USER_NAME)
        doubtdto2.setStatus(Doubt.Status.UNSOLVED)
        def discussionDto = new DiscussionDto()
        discussionDto.addPostDto(doubtdto)
        discussionDto.setQuestionTitle(DOUBT_TITLE)
        discussionDto.setStatus(Discussion.Status.OPEN)
        discussionDto.setTitle(DOUBT_TITLE)
        def discussionDto2 = new DiscussionDto()
        discussionDto2.addPostDto(doubtdto2)
        discussionDto2.setQuestionTitle(QUESTION2_TITLE)
        discussionDto2.setStatus(Discussion.Status.OPEN)
        discussionDto2.setTitle(DOUBT_TITLE)

        when:"The doubts are created"
        doubtService.createDiscussion(discussionDto, questionanswer.getId(), student.getId())
        doubtService.createDiscussion(discussionDto2, questionanswer2.getId(), student.getId())

        then:
        discussionRepository.count() == 2L
        def result = discussionRepository.findAll().get(0)
        def getPostsList = new ArrayList<Doubt>(result.getPosts())
        result.getId() != null
        getPostsList.get(0).getContent() == DOUBT_CONTENT
        result.getAuthor().getName() == USER_NAME
        result.getAuthor().getRole() == User.Role.STUDENT
        questionanswer.getDiscussions().get(0).equals(result)
        student.getDiscussions().get(0).equals(result)
        def result2 = discussionRepository.findAll().get(1)
        def getPostsList2 = new ArrayList<Doubt>(result2.getPosts())
        result2.getId() != null
        getPostsList2.get(0).getContent() == DOUBT2_CONTENT
        result2.getAuthor().getName() == USER_NAME
        result2.getAuthor().getRole() == User.Role.STUDENT
        questionanswer2.getDiscussions().get(0).equals(result2)
        student.getDiscussions().get(1).equals(result2)
        student.getDiscussions().size() == 2

    }


    @TestConfiguration
    static class DoubtServiceImplTestContextConfiguration {

        @Bean
        DoubtService doubtService() {
            return new DoubtService()
        }

        @Bean
        ClarificationService clarificationService() {
            return new ClarificationService()
        }
    }

}
