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

      <v-text-field style="left: 0.95cm; width: 15.9cm;"
              v-if="newDoubt && canCreateNewDoubt && discussion.status === 'OPEN'"
              label="Doubt here ..."
              outlined
              v-model="newDoubt.content"
                    data-cy="doubtinput"
      ></v-text-field>

      <v-card-actions>
        <v-spacer />
        <v-btn
          color="blue darken-1"
          @click="$emit('close-dialog')"
          data-cy="cancelButton"
          >Back</v-btn
        >
        <v-btn
          v-if=" newDoubt && discussion.status === 'OPEN' && canCreateNewDoubt"
          color="blue darken-1"
          data-cy="saveButton"
          @click="addDoubt"
          >Save</v-btn
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
export default class EditDoubtDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Discussion, required: true }) discussion!: Discussion;
  @Prop({ type: Number, required: true }) readonly id!: number;

  Discussion!: Discussion;
  newDoubt: Doubt | null = null;
  canCreateNewDoubt: boolean = true;
  created() {
    console.log(this.discussion);
    console.log('nibba');
    this.newDoubt = new Doubt();
    this.Discussion = new Discussion(this.discussion);
    this.canCreateNewDoubt =
      this.discussion.postsDto[this.discussion.postsDto.length - 1]
        .clarificationDto != null;
    console.log(this.canCreateNewDoubt);
  }

  async addDoubt() {
    if (this.newDoubt && !this.newDoubt.content) {
      await this.$store.dispatch('error', 'Doubt must have Content');
      return;
    } else if (this.newDoubt) {
      this.newDoubt.creationDate = new Date(Date.now()).toLocaleString();
      this.newDoubt.isNew = true;
      try {
        const result = await RemoteServices.addDoubt(this.id, this.newDoubt);
        this.newDoubt = null;
        this.$emit('new-discussion', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>
