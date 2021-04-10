o<template>
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
                </v-card-title>
            </template>

            <template v-slot:item.topics="{}"> </template>

            <template v-slot:item.status="{ item }">
                <v-chip :color="getStatusColor(item.status)" small>
                    <span>{{ item.status }}</span>
                </v-chip>
            </template>

            <template v-slot:item.creationDate="{ item }">
                {{ item.creationDate }}
            </template>

            <template v-slot:item.image="{ item }">
                <v-file-input
                        show-size
                        dense
                        small-chips
                        @change="handleFileUpload($event, item)"
                        accept="image/*"
                />
            </template>
            <template v-slot:item.evaluation="{ item }">
                <v-btn color="primary" dark @click="evaluateQuestion(item)" data-cy="evaluateQuestion"
                >Evaluate Question</v-btn
                >
            </template>
        </v-data-table>
        <evaluate-question-dialog
                v-if="currentQuestion"
                v-model="evaluateQuestionDialog"
                :question="currentQuestion"
                v-on:close-evaluate-question-dialog="onCloseEvaluateQuestionDialogue"
                v-on:evaluate-question="onEvaluateQuestion"
        />
    </v-card>
</template>

<script lang = "ts">
  import { Component, Vue } from 'vue-property-decorator';
  import RemoteServices from '@/services/RemoteServices';
  import Question from '@/models/management/Question';
  import Image from '@/models/management/Image';
  import Topic from '@/models/management/Topic';
  import EvaluateQuestionDialog from './EvaluateQuestionDialog.vue';

  @Component({
    components: {
      'evaluate-question-dialog': EvaluateQuestionDialog
    }
  })

  export default class EvaluateQuestionsView extends Vue {
    questions: Question[] = [];
    topics: Topic[] = [];
    currentQuestion: Question | null = null;
    evaluateQuestionDialog: boolean = false;
    evaluationDialog: boolean = false;
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
        text: 'Evaluation',
        value: 'evaluation',
        align: 'center',
        sortable: false
      }
    ];

    async created() {
      await this.$store.dispatch('loading');
      try {
        [this.topics, this.questions] = await Promise.all([
          RemoteServices.getTopics(),
          RemoteServices.getPendingQuestions()
        ]);
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

    getStatusColor(status: string) {
      if (status === 'PENDING') return 'yellow';
      if (status === 'REJECTED') return 'blue';
      else return 'green';
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

    evaluateQuestion(question: Question) {
      if (question.status === 'PENDING') {
        this.currentQuestion = question;
        this.evaluateQuestionDialog = true;
      } else {
        alert('You can only evaluate a pending question.');
      }
    }

    async onEvaluateQuestion(question: Question) {
      this.questions = await RemoteServices.getPendingQuestions();
      this.questions.unshift(question);
      this.onCloseEvaluateQuestionDialogue();
    }

    async onCloseEvaluateQuestionDialogue() {
      this.evaluateQuestionDialog = false;
      this.currentQuestion = null;
      try {
        this.questions = this.questions.filter(
          question => question.status == 'PENDING');
      } catch (error) {
        await this.$store.dispatch('error', error);
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
</style>