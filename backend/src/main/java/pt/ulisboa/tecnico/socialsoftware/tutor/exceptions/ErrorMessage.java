package pt.ulisboa.tecnico.socialsoftware.tutor.exceptions;

public enum ErrorMessage {

    CLARIFICATION_EMPTY("A clarification cannot be empty"),
    CLARIFICATION_INVALID_USER("User is not a teacher"),
    CLARIFICATION_NOT_ALLOWED("This doubt is already solved"),
    CLARIFICATION_DOUBT_IS_EMPTY("The clarification's question is empty"),
    CLARIFICATION_USER_IS_EMPTY("The clarification's content is empty"),

    INVALID_ACADEMIC_TERM_FOR_COURSE_EXECUTION("Invalid academic term for course execution"),
    INVALID_ACRONYM_FOR_COURSE_EXECUTION("Invalid acronym for course execution"),
    INVALID_CONTENT_FOR_OPTION("Invalid content for option"),
    INVALID_CONTENT_FOR_QUESTION("Invalid content for question"),
    INVALID_NAME_FOR_COURSE("Invalid name for course"),
    INVALID_NAME_FOR_TOPIC("Invalid name for topic"),
    INVALID_SEQUENCE_FOR_OPTION("Invalid sequence for option"),
    INVALID_SEQUENCE_FOR_QUESTION_ANSWER("Invalid sequence for question answer"),
    INVALID_TITLE_FOR_ASSESSMENT("Invalid title for assessment"),
    INVALID_TITLE_FOR_QUESTION("Invalid title for question"),
    INVALID_URL_FOR_IMAGE("Invalid url for image"),
    INVALID_TYPE_FOR_COURSE("Invalid type for course"),
    INVALID_TYPE_FOR_COURSE_EXECUTION("Invalid type for course execution"),
    INVALID_AVAILABLE_DATE_FOR_QUIZ("Invalid available date for quiz"),
    INVALID_CONCLUSION_DATE_FOR_QUIZ("Invalid conclusion date for quiz"),
    INVALID_RESULTS_DATE_FOR_QUIZ("Invalid results date for quiz"),
    INVALID_TITLE_FOR_QUIZ("Invalid title for quiz"),
    INVALID_TYPE_FOR_QUIZ("Invalid type for quiz"),
    INVALID_QUESTION_SEQUENCE_FOR_QUIZ("Invalid question sequence for quiz"),

    ASSESSMENT_NOT_FOUND("Assessment not found with id %d"),
    COURSE_EXECUTION_NOT_FOUND("Course execution not found with id %d"),
    OPTION_NOT_FOUND("Option not found with id %d"),
    QUESTION_ANSWER_NOT_FOUND("Question answer not found with id %d"),
    QUESTION_NOT_FOUND("Question not found with id %d"),
    QUIZ_ANSWER_NOT_FOUND("Quiz answer not found with id %d"),
    QUIZ_NOT_FOUND("Quiz not found with id %d"),
    QUIZ_QUESTION_NOT_FOUND("Quiz question not found with id %d"),
    TOPIC_CONJUNCTION_NOT_FOUND("Topic Conjunction not found with id %d"),
    TOPIC_NOT_FOUND("Topic not found with id %d"),
    USER_NOT_FOUND("User not found with id %d"),
    COURSE_NOT_FOUND("Course not found with name %s"),

    CANNOT_DELETE_COURSE_EXECUTION("The course execution cannot be deleted %s"),
    USERNAME_NOT_FOUND("Username %d not found"),

    QUIZ_USER_MISMATCH("Quiz %s is not assigned to student %s"),
    QUIZ_MISMATCH("Quiz Answer Quiz %d does not match Quiz Question Quiz %d"),
    QUESTION_OPTION_MISMATCH("Question %d does not have option %d"),
    COURSE_EXECUTION_MISMATCH("Course Execution %d does not have quiz %d"),

    DUPLICATE_TOPIC("Duplicate topic: %s"),
    DUPLICATE_USER("Duplicate user: %s"),
    DUPLICATE_COURSE_EXECUTION("Duplicate course execution: %s"),

    USERS_IMPORT_ERROR("Error importing users: %s"),
    QUESTIONS_IMPORT_ERROR("Error importing questions: %s"),
    TOPICS_IMPORT_ERROR("Error importing topics: %s"),
    ANSWERS_IMPORT_ERROR("Error importing answers: %s"),
    QUIZZES_IMPORT_ERROR("Error importing quizzes: %s"),

    QUESTION_IS_USED_IN_QUIZ("Question is used in quiz %s"),
    USER_NOT_ENROLLED("%s - Not enrolled in any available course"),
    QUIZ_NO_LONGER_AVAILABLE("This quiz is no longer available"),
    QUIZ_NOT_YET_AVAILABLE("This quiz is not yet available"),

    NO_CORRECT_OPTION("Question does not have a correct option"),
    NOT_ENOUGH_QUESTIONS("Not enough questions to create a quiz"),
    ONE_CORRECT_OPTION_NEEDED("Questions need to have 1 and only 1 correct option"),
    CANNOT_CHANGE_ANSWERED_QUESTION("Can not change answered question"),
    QUIZ_HAS_ANSWERS("Quiz already has answers"),
    QUIZ_ALREADY_COMPLETED("Quiz already completed"),
    QUIZ_ALREADY_STARTED("Quiz was already started"),
    QUIZ_QUESTION_HAS_ANSWERS("Quiz question has answers"),
    FENIX_ERROR("Fenix Error"),
    AUTHENTICATION_ERROR("Authentication Error"),
    FENIX_CONFIGURATION_ERROR("Incorrect server configuration files for fenix"),


    ACCESS_DENIED("You do not have permission to view this resource"),



    COURSE_NAME_IS_EMPTY("The course name is empty"),
    COURSE_TYPE_NOT_DEFINED("The course type is not defined"),
    COURSE_EXECUTION_ACRONYM_IS_EMPTY("The course execution acronym is empty"),
    COURSE_EXECUTION_ACADEMIC_TERM_IS_EMPTY("The course execution academic term is empty"),

    QUIZ_NOT_CONSISTENT("Field %s of quiz is not consistent"),

    QUESTION_MISSING_DATA("Missing information for quiz"),
    QUESTION_MULTIPLE_CORRECT_OPTIONS("Questions can only have 1 correct option"),
    QUESTION_CHANGE_CORRECT_OPTION_HAS_ANSWERS("Can not change correct option of answered question"),
    QUESTION_NOT_PENDING("Expected Question to be in pending status"),
    QUESTION_NOT_ALTERED("Expected alterations on question for resubmission"),

    EVALUATION_NOT_AVAILABLE("Accessed Evaluation doesn't exist"),
    MUST_HAVE_JUSTIFICATION("Expected justification can't be empty"),

    TOURNAMENT_NAME_EMPTY("The tournament name is empty"),
    TOURNAMENT_START_DATE_EMPTY("The tournament start date is empty"),
    TOURNAMENT_END_DATE_EMPTY("The tournament end date is empty"),
    TOURNAMENT_INVALID_END_DATE("The tournament end date is before the start date"),
    TOURNAMENT_DATES_OVERLAP("The tournament's start and end dates overlap"),
    TOURNAMENT_NOT_ENOUGH_QUESTIONS("Not enough questions to create a tournament"),
    NOT_ENOUGH_TOPICS("Not enough topics to create a tournament"),
    TOURNAMENT_CREATOR_IS_NOT_STUDENT("The tournament creator is not a student"),
    INVALID_ENROLLMENT_CLOSED_TOURNAMENT("The tournament is closed"),
    INVALID_ENROLLMENT_CREATED_TOURNAMENT("The tournament hasn't been open yet"),
    INVALID_ENROLLMENT_CANCELLED_TOURNAMENT("The tournament has been cancelled"),
    TOURNAMENT_NOT_FOUND("Tournament with id %d not found"),
    TOURNAMENT_IS_CLOSED("Tournament with id %d is closed"),
    TOURNAMENT_IS_CREATED("Tournament with id %d hasn't open yet"),
    TOPIC_WITH_NAME_NOT_FOUND("Topic with name %s not found"),
    USER_NOT_TOURNAMENT_CREATOR("User %d is not the creator of the tournament"),
    CANNOT_CANCEL_CLOSED_TOURNAMENT("Closed tournaments cannot be cancelled"),
    CANNOT_CANCEL_CANCELLED_TOURNAMENT("Cancelled tournaments cannot be cancelled"),

    INVALID_USER_ID("Invalid user ID"),
    INVALID_TOURNAMENT_ID("Invalid tournament ID"),
    INVALID_ENROLLMENT_ATTEMPT_NOT_STUDENT("Invalid user role"),
    STUDENT_ALREADY_ENROLLED("The student is already enrolled in the tournament"),
    DOUBT_USER_IS_EMPTY("The doubt's user is empty"),
    DOUBT_QUESTION_IS_EMPTY("The doubt's question is empty"),
    DOUBT_CONTENT_IS_EMPTY("The doubt's content is empty"),
    DOUBT_USER_IS_NOT_A_STUDENT("Doubts can only be created by Students"),
    DOUBT_USER_HASNT_ANSWERED("Can't create doubts to unanswered questions"),
    DOUBT_NOT_FOUND("Doubt not found"),
    DISCUSSION_NOT_FOUND("Discussion not found"),
    DISCUSSION_CANNOT_BE_CLOSED("Discussion cannot be closed due to unsolved questions"),
    DISCUSSION_USER_IS_EMPTY(""),
    DISCUSSION_IS_EMPTY(""),
    DISCUSSION_USER_IS_NOT_A_STUDENT(""),
    USER_IS_EMPTY("User has no identifier"),
    USER_IS_NOT_STUDENT("User doens't havr the role of student, but should"),



    CLARIFICATION_INVALID_COURSE_TEACHER("This teacher cannot solve this clarification request"),

    CANNOT_OPEN_FILE("Cannot open file");

    public final String label;

    ErrorMessage(String label) {
        this.label = label;
    }
}