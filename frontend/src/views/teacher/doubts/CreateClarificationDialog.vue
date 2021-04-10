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
        <span v-if="creating" class="headline"
          >{{ discussion.title }} - Resolver
        </span>
        <span v-if="!creating" class="headline"
          >{{ discussion.title }} - Detalhes
        </span>
      </v-card-title>

      <v-card-text
        v-bind:key="item"
        v-for="item in discussion.postsDto"
      >
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
              v-if="
                !item.showDoubt && item.clarificationDto.description !== 'vazio'
              "
              @click="item.showDoubt = true"
              >mdi-plus</v-icon
            >
            <v-icon
              slot="append"
              color="blue"
              v-if="
                item.showDoubt && item.clarificationDto.description !== 'vazio'
              "
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
          <v-text-field
            data-cy="ResponseInput"
            v-if="item.status === 'UNSOLVED' && creating && newClarification"
            label="Responder ..."
            outlined
            v-model="newClarification.description"
          ></v-text-field>
          <v-textarea
            style="left: 1cm; width: 94%;"
            v-if="item.status === 'SOLVED' && item.showDoubt"
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
        <v-btn
          v-if="creating"
          color="blue darken-1"
          data-cy="saveButton"
          @click="saveClarification"
          >Save</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import Doubt from '../../../models/management/Doubt';
import Clarification from '../../../models/management/Clarification';
import RemoteServices from '@/services/RemoteServices';
import Discussion from '@/models/management/Discussion';
@Component
export default class CreateClarificationDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Discussion, required: true }) readonly discussion!: Discussion;
  @Prop({ type: Boolean, required: true }) readonly creating!: boolean;
  newClarification: Clarification | undefined;
  currentDoubt = this.discussion.postsDto[this.discussion.postsDto.length - 1];

  created() {
    this.newClarification = new Clarification();
    this.newClarification.author = this.discussion.postsDto[0].author;
  }

  async saveClarification() {
    if (
      this.newClarification &&
      (!this.newClarification.description || !this.newClarification.author)
    ) {
      await this.$store.dispatch('error', 'Clarification must have a text');
      return;
    }

    if (
      this.newClarification &&
      this.newClarification.description &&
      this.newClarification.author &&
      this.currentDoubt.id != null
    ) {
      try {
        await RemoteServices.createClarification(
          this.currentDoubt.id,
          this.newClarification
        );
        this.$emit('new-clarification');
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>
