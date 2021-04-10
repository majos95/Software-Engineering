o<template>
  <v-dialog :value="dialog"
            @input="$emit('dialog', false)"
            @keydown.esc="$emit('dialog', false)"
            max-width="75%">
    <v-card>
      <v-card-title>
        <span class="headline">{{question.title}}</span>
      </v-card-title>

      <v-card-text class="text-left" v-if="question">
        <div>
          <span v-html="convertMarkDown(question.content, question.image)" />
          <ul>
            <li v-for="option in question.options" :key="option.number">
              <span
                      v-if="option.correct"
                      v-html="convertMarkDown('**[★]** ', null)"
              />
              <span
                      v-html="convertMarkDown(option.content, null)"
                      v-bind:class="[option.correct ? 'font-weight-bold' : '']"
              />
            </li>
          </ul>
          <br />
        </div>
      </v-card-text>

      <v-dialog
              v-model="rejectDialog"
              max-width="750px"
      >
        <v-card>
          <v-card-title>
            <span>Reject Question</span>
            <v-spacer></v-spacer>
          </v-card-title>
          <v-card-actions>
            <v-card-text class="text-left">
              <div>
                <v-text-field v-model="justification" label="Justification" data-cy="justification"></v-text-field>
              </div>
            </v-card-text>
          </v-card-actions>
          <v-card-actions>
            <v-spacer />
            <v-btn color="primary" @click="rejectQuestion" data-cy="saveEvaluation">
              Submit
            </v-btn>
            <v-btn color="primary"  @click="closeDialogue">
              Cancel Alterations
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>

      <v-dialog
              v-model="approveDialog"
              scrollable max-width="790px"
      >
        <v-card style="height: 500px;">
          <v-card-title>
            <span>Approve Question</span>
            <v-spacer></v-spacer>
          </v-card-title>
          <h5> Alter the question and click on <em>Save and Approve</em> to make it AVAILABLE. If you wish to skip alterations,
          click on <em>Skip and Approve</em>. The question will be approved but will appear as DISABLED.</h5>
          <v-card-text class="text-left" v-if="editQuestion"  >
            <v-text-field v-model="editQuestion.title" label="Title" />
            <v-textarea
                    outline
                    rows="5"
                    v-model="editQuestion.content"
                    data-cy="Content"
                    label="Question"
            ></v-textarea>
            <div v-for="index in editQuestion.options.length" :key="index">
              <v-switch
                      v-model="editQuestion.options[index - 1].correct"
                      class="ma-4"
                      label="Correct"
              />
              <v-textarea
                      outline
                      rows="5"
                      v-model="editQuestion.options[index - 1].content"
                      :label="`Option ${index}`"
              ></v-textarea>
            </div>
          </v-card-text>

          <v-card-actions>
            <v-spacer />
            <v-btn color="primary" @click="saveQuestion" data-cy="saveQuestion">
              Save and Approve
            </v-btn>
            <v-btn color="primary" @click="approveQuestion" data-cy="skipApprove">
              Skip and Approve
            </v-btn>
            <v-btn color="primary"  @click="closeDialogue">
              Cancel Alterations
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>

      <v-card-actions>
        <v-spacer />
        <v-btn color="primary" @click="closeDialogue">Cancel</v-btn>
        <v-btn color="primary" @click="approveDialog = true"  data-cy="approve" >Approve Question</v-btn>
        <v-btn color="primary" @click="rejectDialog = true"  data-cy="reject" >Reject Question</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
  import { Component, Prop, Vue, Model } from 'vue-property-decorator';
  import Question from '../../../models/management/Question';
  import Evaluation from '@/models/management/Evaluation';
  import RemoteServices from '@/services/RemoteServices';
  import Image from '@/models/management/Image';
  import { convertMarkDown } from '@/services/ConvertMarkdownService';

  @Component
  export default class EvaluateQuestionDialog extends Vue {
    @Prop({ type: Question, required: true }) readonly question!: Question;
    @Model('dialog', Boolean) dialog!: boolean;

    approveDialog = false;
    rejectDialog = false;

    evaluation!: Evaluation;
    editQuestion!: Question;
    updateDefault = false;
    justification = '';

    async created() {
      this.evaluation = new Evaluation(await RemoteServices.findEvaluation(this.question));
      this.editQuestion = this.question;
    }

    // https://github.com/F-loat/vue-simplemde/blob/master/doc/configuration_en.md
    markdownConfigs: object = {
      status: false,
      spellChecker: false,
      insertTexts: {
        image: ['![image][image]', '']
      }
    };

    async setStatus(text: string) {
      try{
        if (this.question.id) {
          let questionNew = await RemoteServices.setQuestionStatus(this.question.id, text);
          if (questionNew) {
            this.editQuestion.status = text;
          }
        }
      } catch (error) {
            await this.$store.dispatch('error', error);
          }
    }

    convertMarkDown(text: string, image: Image | null = null): string {
      return convertMarkDown(text, image);
    }

    closeDialogue() {
      this.approveDialog = false;
      this.rejectDialog = false;
      this.$emit('close-evaluate-question-dialog');
    }


    async rejectQuestion() {
      this.evaluation.approvedEvaluation = false;

      if (this.evaluation && (this.justification === '' || this.justification == null)) {
        await this.$store.dispatch('error', 'Rejected Question must have a justification!');
        return;
      }

      if (this.evaluation && this.question) {
        try {
          await this.setStatus('REJECTED');
          this.evaluation.justification = this.justification;
          const result = await RemoteServices.submitEvaluation(this.evaluation, this.question);
          this.$emit('evaluate-question', result);
        } catch (error) {
          await this.$store.dispatch('error', error);
        }
      }
    }

    async approveQuestion() {
      this.evaluation.approvedEvaluation = true;

      if (this.evaluation && this.question) {
        try {
          if(this.updateDefault){
            this.evaluation.justification = this.justification;
            const result = await RemoteServices.submitEvaluation(this.evaluation, this.question);
            await this.setStatus('AVAILABLE');
            this.$emit('evaluate-question', result);
          } else {
            this.evaluation.justification = this.justification;
            const result = await RemoteServices.submitEvaluation(this.evaluation, this.question);
            this.$emit('evaluate-question', result);
          }
        } catch (error) {
          await this.$store.dispatch('error', error);
        }
      }
    }

    async saveQuestion() {
      if (
              this.editQuestion &&
              (!this.editQuestion.title || !this.editQuestion.content)
      ) {
        await this.$store.dispatch(
                'error',
                'Question must have title and content'
        );
        return;
      }
      try {
        const result =
                this.editQuestion.id != null
                        ? await RemoteServices.updateQuestion(this.editQuestion)
                        : await RemoteServices.createQuestion(this.editQuestion);

        this.$emit('save-question', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }

      this.updateDefault = true;
      this.approveQuestion();
    }
  }

</script>

<style scoped></style>