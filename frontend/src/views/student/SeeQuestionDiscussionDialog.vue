<template>
  <v-dialog
    :value="dialog"
    @input="$emit('close-dialog')"
    @keydown.esc="$emit('close-dialog')"
    max-width="45%"
    max-height="80%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">{{ Discussion.title }} - Detalhes </span>
      </v-card-title>

      <v-card-text v-for="item in discussion.postsDto">
        <v-container grid-list-md fluid>
          <v-textarea
            :value="item.content"
            :label="item.author + ' - ' + item.creationDate"
            outlined
            readonly
            auto-grow
            rows="1"
          >
            <v-icon
              slot="append"
              color="blue"
              v-if="!item.showDoubt && item.status === 'SOLVED'"
              v-on:click="item.showDoubt = true"
              >mdi-plus</v-icon
            >
            <v-icon
              slot="append"
              color="blue"
              v-if="item.showDoubt && item.status === 'SOLVED'"
              @click="item.showDoubt = false"
              >mdi-minus</v-icon
            >
          </v-textarea>
          <!--
          <p
            v-if="(item.id % 10) % 2 > 0"
            style="position: relative; top: -0.7cm; font-size: 10pt; color: green; padding-bottom: -0.1cm;"
          >
            ~Teacher marked a good question~
          </p>
          -->
          <v-textarea
            style="left: 1cm; width: 94%;"
            v-if="item.clarificationDto && item.showDoubt"
            :value="item.clarificationDto.description"
            :label="item.clarificationDto.author + ' respondeu...'"
            outlined
            readonly
            auto-grow
            rows="1"
          ></v-textarea>
        </v-container>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn
          color="blue darken-1"
          @click="$emit('close-dialog')"
          data-cy="cancelButton"
          >Back</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import Doubt from '@/models/management/Doubt';
import Discussion from '@/models/management/Discussion';
@Component
export default class SeeQuestionDoubtDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Discussion, required: true }) discussion!: Discussion;
  Discussion!: Discussion;
  created() {
    this.Discussion = new Discussion(this.discussion);
  }
}
</script>
