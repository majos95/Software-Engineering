import Vue from 'vue';
import Router from 'vue-router';
import Store from '@/store';

import LoginView from '@/views/LoginView.vue';
import CourseSelectionView from '@/views/CourseSelectionView.vue';
import DoubtsViewTest from '@/views/student/DiscussionsView.vue';

import HomeView from '@/views/HomeView.vue';
import ManagementView from '@/views/teacher/ManagementView.vue';
import QuestionsView from '@/views/teacher/questions/QuestionsView.vue';
import TopicsView from '@/views/teacher/TopicsView.vue';
import QuizzesView from '@/views/teacher/quizzes/QuizzesView.vue';
import StudentsView from '@/views/teacher/students/StudentsView.vue';
import EvaluateQuestionsView from './views/teacher/evaluations/EvaluateQuestionsView.vue'
import StudentView from '@/views/student/StudentView.vue';
import AvailableQuizzesView from './views/student/AvailableQuizzesView.vue';
import SolvedQuizzesView from './views/student/SolvedQuizzesView.vue';
import QuizView from './views/student/quiz/QuizView.vue';
import ResultsView from './views/student/quiz/ResultsView.vue';
import TournamentsView from './views/student/TournamentsView.vue';
import StatsView from './views/student/StatsView.vue';
import ScanView from './views/student/ScanView.vue';
import AdminManagementView from '@/views/admin/AdminManagementView.vue';
import NotFoundView from '@/views/NotFoundView.vue';
import ImpExpView from '@/views/teacher/impexp/ImpExpView.vue';
import AssessmentsView from '@/views/teacher/assessments/AssessmentsView.vue';
import CreateQuizzesView from '@/views/student/CreateQuizzesView.vue';
import CoursesView from '@/views/admin/Courses/CoursesView.vue';
import SolveDoubtsView from '@/views/teacher/doubts/SolveDoubtsView.vue';
import SubmittedQuestionsView from '@/views/student/question/SubmittedQuestionsView.vue';
import SeeStudentDiscussionsDashBoard from '@/views/student/SeeStudentDiscussionsDashBoard.vue';
import StudentsStatsView from '@/views/student/StudentsStatsView.vue';

Vue.use(Router);

let router = new Router({
  mode: 'history',
  base: process.env.BASE_URL,
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
      meta: { title: process.env.VUE_APP_NAME, requiredAuth: 'None' }
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: {
        title: process.env.VUE_APP_NAME + ' - Login',
        requiredAuth: 'None'
      }
    },
    {
      path: '/courses',
      name: 'courses',
      component: CourseSelectionView,
      meta: {
        title: process.env.VUE_APP_NAME + ' - Course Selection',
        requiredAuth: 'None'
      }
    },
    {
      path: '/management',
      name: 'management',
      component: ManagementView,
      children: [
        {
          path: 'questions',
          name: 'questions-management',
          component: QuestionsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Questions',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'evaluations',
          name: 'evaluate-questions-management',
          component: EvaluateQuestionsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Evaluate Questions',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'topics',
          name: 'topics-management',
          component: TopicsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Topics',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'quizzes',
          name: 'quizzes-management',
          component: QuizzesView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Quizzes',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'assessments',
          name: 'assessments-management',
          component: AssessmentsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Assessment Topics',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'students',
          name: 'students-management',
          component: StudentsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Students',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'impexp',
          name: 'impexp-management',
          component: ImpExpView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - ImpExp',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'discussions',
          name: 'discussions-management',
          component: SolveDoubtsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Discussions',
            requiredAuth: 'Teacher'
          }
        }
      ]
    },
    {
      path: '/student',
      name: 'student',
      component: StudentView,
      children: [
        {
          path: 'available',
          name: 'available-quizzes',
          component: AvailableQuizzesView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Available Quizzes',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'create',
          name: 'create-quizzes',
          component: CreateQuizzesView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Create Quizzes',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'questions',
          name: 'student-questions',
          component: SubmittedQuestionsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Student Questions',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'discussionStats',
          name: 'student-discussionStats',
          component: SeeStudentDiscussionsDashBoard,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Student DiscussionStats',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'solved',
          name: 'solved-quizzes',
          component: SolvedQuizzesView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Solved Quizzes',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'discussions',
          name: 'see-discussions',
          component: DoubtsViewTest,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Discussions',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'quiz',
          name: 'solve-quiz',
          component: QuizView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Quiz',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'results',
          name: 'quiz-results',
          component: ResultsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Results',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'tournaments',
          name: 'tournaments',
          component: TournamentsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Tournaments',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'stats',
          name: 'stats',
          component: StatsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Stats',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'globalStats',
          name: 'globalStats',
          component: StudentsStatsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Students Stats',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'scan',
          name: 'scan',
          component: ScanView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Scan',
            requiredAuth: 'Student'
          }
        }
      ]
    },
    {
      path: '/admin',
      name: 'admin',
      component: AdminManagementView,
      children: [
        {
          path: 'courses',
          name: 'courseAdmin',
          component: CoursesView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Manage Courses',
            requiredAuth: 'Admin'
          }
        }
      ]
    },
    {
      path: '**',
      name: 'not-found',
      component: NotFoundView,
      meta: { title: 'Page Not Found', requiredAuth: 'None' }
    }
  ]
});

router.beforeEach(async (to, from, next) => {
  if (to.meta.requiredAuth == 'None') {
    next();
  } else if (to.meta.requiredAuth == 'Admin' && Store.getters.isAdmin) {
    next();
  } else if (to.meta.requiredAuth == 'Teacher' && Store.getters.isTeacher) {
    next();
  } else if (to.meta.requiredAuth == 'Student' && Store.getters.isStudent) {
    next();
  } else {
    next('/');
  }
});

router.afterEach(async (to, from) => {
  document.title = to.meta.title;
  await Store.dispatch('clearLoading');
});

export default router;
