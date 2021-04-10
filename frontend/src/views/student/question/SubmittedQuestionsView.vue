o<template>
  <div>
    <template>
      <div class="container">
        <div v-if="stats != null" class="stats-container">
          <div class="items">
            <div class="icon-wrapper" ref="ProposedQuestions">
              <animated-number :number="stats[0]" />
            </div>
            <div class="project-name">
              <p>Total Proposed Questions</p>
            </div>
          </div>
          <div class="approvedVersion">
            <div class="icon-wrapper" ref="ApprovedQuestions">
              <animated-number :number="stats[1]" />
            </div>
            <div class="project-name">
              <p>Total Approved Questions</p>
            </div>
          </div>
        </div>
      </div>
    </template>
    <v-card class="table">
      <v-data-table
        :headers="headers"
        :custom-filter="customFilter"
        :items="questions"
        :search="search"
        multi-sort
        :mobile-breakpoint="0"
        :items-per-page="15"
        :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
      >
        <template v-slot:top>
          <v-card-title>
            <v-text-field
              v-model="search"
              append-icon="search"
              label="Search"
              class="mx-2"
            />

            <v-spacer />
            <v-btn
              color="primary"
              dark
              @click="newQuestion"
              data-cy="submitButton"
              >New Question</v-btn
            >
          </v-card-title>
        </template>

        <template v-slot:item.content="{ item }">
          <p
            v-html="convertMarkDown(item.content, null)"
            @click="showQuestionDialog(item)"
        /></template>

        <template v-slot:item.topics="{ item }">
          <edit-question-topics
            :question="item"
            :topics="topics"
            v-on:question-changed-topics="onQuestionChangedTopics"
          />
        </template>

        <template v-slot:item.status="{ item }">
          <v-chip :color="getStatusColor(item.status)" small>
            <span>{{ getStatusToList(item.status) }}</span>
          </v-chip>
        </template>

        <template v-slot:item.creationDate="{ item }">
          {{ item.creationDate }}
        </template>

        <template v-slot:item.image="{ item }">
          <v-file-input v-if="item.status === 'PENDING'"
            show-size
            dense
            small-chips
            @change="handleFileUpload($event, item)"
            accept="image/*"
          />
          </template>

        <template v-slot:item.action="{ item }">
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                small
                class="mr-2"
                v-on="on"
                @click="showQuestionDialog(item)"
                >visibility</v-icon
              >
            </template>
            <span>Show Question</span>
          </v-tooltip>
          <v-tooltip bottom>
            <template v-if="item.status === 'REJECTED'" v-slot:activator="{ on }">
              <v-icon small class="mr-2" v-on="on" @click="editQuestion(item)" data-cy="resubmit"
                > fas fa-retweet </v-icon
              >
            </template>
            <span>Resubmit Question</span>
          </v-tooltip>
          <v-tooltip bottom>
            <template v-if="item.status === 'PENDING'" v-slot:activator="{ on }">
              <v-icon
                small
                class="mr-2"
                v-on="on"
                @click="deleteQuestion(item)"
                color="red"
                data-cy="deleteQuestion"
                >delete</v-icon
              >
            </template>
            <span>Delete Question</span>
          </v-tooltip>
        </template>
      </v-data-table>
      <edit-question-dialog
        v-if="currentQuestion"
        :dialog="editQuestionDialog"
        :question="currentQuestion"
        v-on:close-edit-question-dialog="onCloseEditQuestionDialogue"
        v-on:save-question="onSaveQuestion"
      />
      <show-question-dialog
        v-if="currentQuestion"
        :dialog="questionDialog"
        :question="currentQuestion"
        v-on:close-show-question-dialog="onCloseShowQuestionDialog"
      />
    </v-card>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Question from '@/models/management/Question';
import Image from '@/models/management/Image';
import Topic from '@/models/management/Topic';
import EditSubmittedQuestionDialog from '@/views/student/question/EditSubmittedQuestionDialog.vue';
import ShowSubmittedQuestionDialog from '@/views/student/question/ShowSubmittedQuestionDialog.vue';
import EditSubmittedQuestionTopics from '@/views/student/question/EditSubmittedQuestionTopics.vue';

import AnimatedNumber from '@/components/AnimatedNumber.vue';
@Component({
  components: {
    'show-question-dialog': ShowSubmittedQuestionDialog,
    'edit-question-dialog': EditSubmittedQuestionDialog,
    'edit-question-topics': EditSubmittedQuestionTopics,
    AnimatedNumber
  }
})
export default class SubmittedQuestionsView extends Vue {
  stats: number[] = [];
  questions: Question[] = [];
  topics: Topic[] = [];
  currentQuestion: Question | null = null;
  editQuestionDialog: boolean = false;
  questionDialog: boolean = false;
  search: string = '';
  headers: object = [
    { text: 'Question', value: 'content', align: 'left' },
    {
      text: 'Topics',
      value: 'topics',
      align: 'center',
      sortable: false
    },
    { text: 'Title', value: 'title', align: 'center' },
    { text: 'Status', value: 'status', align: 'center' },
    {
      text: 'Creation Date',
      value: 'creationDate',
      align: 'center'
    },
    {
      text: 'Image',
      value: 'image',
      align: 'center',
      sortable: false
    },
    {
      text: 'Actions',
      value: 'action',
      align: 'center',
      sortable: false
    }
  ];
  async created() {
    await this.$store.dispatch('loading');
    try {
      [this.topics, this.questions] = await Promise.all([
        RemoteServices.getTopics(),
        RemoteServices.getSubmittedQuestions()
      ]);

      this.stats = await RemoteServices.getStudentSubmittedQuestionStats();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
  customFilter(value: string, search: string, question: Question) {
    // noinspection SuspiciousTypeOfGuard,SuspiciousTypeOfGuard
    return (
      search != null &&
      JSON.stringify(question)
        .toLowerCase()
        .indexOf(search.toLowerCase()) !== -1
    );
  }
  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
  onQuestionChangedTopics(questionId: Number, changedTopics: Topic[]) {
    let question = this.questions.find(
      (question: Question) => question.id == questionId
    );
    if (question) {
      question.topics = changedTopics;
    }
  }
  getStatusColor(status: string) {

    if (status === 'REMOVED') return 'orange';
    else if (status === 'DISABLED') return 'green';
    else if (status === 'REJECTED') return 'red';
    else if (status === 'PENDING') return 'yellow';
    else return 'green';
  }

  getStatusToList(status: string){
    if (status === 'REMOVED') return 'REMOVED';
    else if (status === 'DISABLED') return 'APPROVED';
    else if (status === 'REJECTED') return 'REJECTED';
    else if (status === 'PENDING') return 'PENDING';
    else return 'APPROVED';
  }


  async handleFileUpload(event: File, question: Question) {
    if (question.id) {
      try {
        const imageURL = await RemoteServices.uploadImage(event, question.id);
        question.image = new Image();
        question.image.url = imageURL;
        confirm('Image ' + imageURL + ' was uploaded!');
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
  showQuestionDialog(question: Question) {
    this.currentQuestion = question;
    this.questionDialog = true;
  }
  onCloseShowQuestionDialog() {
    this.questionDialog = false;
  }
  newQuestion() {
    this.currentQuestion = new Question();
    this.editQuestionDialog = true;
  }
  editQuestion(question: Question) {
    if (question.status === 'REJECTED') {
      this.currentQuestion = question;
      this.editQuestionDialog = true;
    } else {
      alert('You can only edit a Rejected Question for resubmission');
    }
  }
  async onSaveQuestion(question: Question) {
    this.questions = this.questions.filter(q => q.id !== question.id);
    this.questions.unshift(question);

    this.stats = await RemoteServices.getStudentSubmittedQuestionStats();
    this.onCloseEditQuestionDialogue();
  }
  onCloseEditQuestionDialogue() {
    this.editQuestionDialog = false;
    this.currentQuestion = null;
  }
  async deleteQuestion(toDeletequestion: Question) {
    if (
      toDeletequestion.id &&
      confirm('Are you sure you want to delete this question?')
    ) {
      try {
        await RemoteServices.deleteQuestion(toDeletequestion.id);
        this.questions = this.questions.filter(
          question => question.id != toDeletequestion.id
        );
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.question-textarea {
  text-align: left;
  .CodeMirror,
  .CodeMirror-scroll {
    min-height: 200px !important;
  }
}
.option-textarea {
  text-align: left;
  .CodeMirror,
  .CodeMirror-scroll {
    min-height: 100px !important;
  }
}

.stats-container {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  justify-content: center;
  align-items: stretch;
  align-content: center;
  height: 100%;

  .items {
    background-color: rgba(255, 255, 255, 0.75);
    color: #1976d2;
    border-radius: 5px;
    flex-basis: 25%;
    margin: 20px;
    cursor: pointer;
    transition: all 0.6s;
  }
  .approvedVersion {
    background-color: rgba(255, 255, 255, 0.75);
    color: #4CAF50;
    border-radius: 5px;
    flex-basis: 25%;
    margin: 20px;
    cursor: pointer;
    transition: all 0.6s;
  }
}

.icon-wrapper,
.project-name {
  display: flex;
  align-items: center;
  justify-content: center {
  }
}
.icon-wrapper {
  font-size: 100px;
  transform: translateY(0px);
  transition: all 0.6s;
}

.icon-wrapper {
  align-self: end;
}

.project-name {
  align-self: start;
}
.project-name p {
  font-size: 24px;
  font-weight: bold;
  letter-spacing: 2px;
  transform: translateY(0px);
  transition: all 0.5s;
}

.items:hover {
  border: 3px solid black;

  & .project-name p {
    transform: translateY(-10px);
  }
  & .icon-wrapper i {
    transform: translateY(5px);
  }
}
</style>
