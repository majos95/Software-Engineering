<template>
  <v-dialog
    :value="dialog"
    @input="$emit('close-dialog')"
    @keydown.esc="$emit('close-dialog')"
    max-width="75%"
    max-height="80%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">
          New Discussion
        </span>
      </v-card-title>

      <v-card-text class="text-left" v-if="isCreateDiscussion">
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-flex xs24 sm12 md8>
              <v-text-field
                v-model="newDiscussion.title"
                label="Title"
                data-cy="Title"
              />
            </v-flex>
          </v-layout>
        </v-container>
      </v-card-text>

      <v-card-text class="text-left" v-if="isCreateDoubt">
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-flex xs24 sm12 md8>
              <v-text-field
                v-model="newDoubt.content"
                label="Content"
                data-cy="Content"
              />
            </v-flex>
          </v-layout>
        </v-container>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn
          color="blue darken-1"
          @click="$emit('close-dialog')"
          data-cy="cancelButton"
          >Cancel</v-btn
        >
        <v-btn color="blue darken-1" @click="saveDiscussion" data-cy="saveButton"
          >Create</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Doubt from '@/models/management/Doubt';
import Discussion from '@/models/management/Discussion';
@Component
export default class CreateDoubtDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Doubt, required: true }) readonly doubt!: Doubt;
  @Prop({ required: true }) readonly quizId!: number;
  @Prop({ type: Discussion, required: true }) readonly discussion!: Discussion;
  newDoubt!: Doubt;
  newDiscussion!: Discussion;
  quizQuestionId!: number;
  isCreateDiscussion: boolean = true;
  isCreateDoubt: boolean = true;
  created() {
    this.newDoubt = new Doubt(this.doubt);
    this.newDiscussion = new Discussion(this.discussion);
    this.quizQuestionId = this.quizId;
  }

  async saveDiscussion() {
    if (
      this.newDoubt &&
      (!this.newDoubt.content ||
        this.newDoubt.content == '' ||
        this.newDiscussion.title == '')
    ) {
      await this.$store.dispatch('error', 'Doubt must have Content');
      return;
    } else {
      this.newDoubt.creationDate = new Date(Date.now()).toLocaleString();
      console.log(this.newDoubt.creationDate);
      this.newDoubt.isNew = true;
      this.newDiscussion.postsDto.unshift(this.newDoubt);
      console.log(this.newDoubt);
      console.log(this.newDiscussion);
      try {
        const result = await RemoteServices.createDiscussion(
          this.newDiscussion,
          this.quizQuestionId,
          this.newDoubt
        );
        this.$emit('new-discussion', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>
