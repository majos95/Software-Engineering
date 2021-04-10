<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="discussions"
      :search="search"
      data-cy="table"
      disable-pagination
      :mobile-breakpoint="0"
      :items-per-page="50"
      :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
      multi-sort
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
        </v-card-title>
      </template>

      <template v-slot:item.creationDate="{ item }">
        {{ item.postsDto[0].creationDate }}
      </template>

      <template v-slot:item.action="{ item }">
        <v-tooltip bottom v-if="!isSolved(item)">
          <template v-slot:activator="{ on }">
            <v-icon
              data-cy="createButton"
              small
              color="grey darken-2"
              class="mr-2"
              v-on="on"
              @click="solve(item)"
              >fas fa-edit</v-icon
            >
          </template>
          <span>Solve</span>
        </v-tooltip>

        <v-tooltip bottom v-if="item.visibility === 'PRIVATE'">
          <template v-slot:activator="{ on }">
            <v-icon
              data-cy="visibilityButton"
              small
              color="grey darken-2"
              class="mr-2"
              v-on="on"
              @click="changeVisibility(item.id, 'PUBLIC')"
              >fas fa-eye-slash</v-icon
            >
          </template>
          <span>Make public</span>
        </v-tooltip>

        <v-tooltip bottom v-if="item.visibility === 'PUBLIC'">
          <template v-slot:activator="{ on }">
            <v-icon
              data-cy="visibilityButton"
              small
              color="grey darken-2"
              class="mr-2"
              v-on="on"
              @click="changeVisibility(item.id, 'PRIVATE')"
              >fas fa-eye</v-icon
            >
          </template>
          <span>Make private</span>
        </v-tooltip>

        <v-tooltip bottom v-if="item.status === 'OPEN' && isSolved(item)">
          <template v-slot:activator="{ on }">
            <v-icon
              data-cy="closeDiscussionButton"
              small
              color="grey darken-2"
              class="mr-2"
              v-on="on"
              @click="closeDiscussion(item.id)"
              >fas fa-times
            </v-icon
            >
          </template>
          <span>Close discussion</span>
        </v-tooltip>

        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              data-cy="detailButton"
              small
              color="grey darken-2"
              class="mr-2"
              v-on="on"
              @click="details(item)"
              >fas fa-bars</v-icon
            >
          </template>
          <span>Details</span>
        </v-tooltip>
      </template>

      <template v-slot:item.status="{ item }">
        <v-chip :color="getStatusColor(item)" small>
          <span>{{ item.status }}</span>
        </v-chip>
        <v-tooltip bottom v-if="!isSolved(item)">
          <template v-slot:activator="{ on }">
            <v-icon small color="#ffcc00" v-on="on" @click="true"
              >fas fa-bell</v-icon
            >
          </template>
          <span>unsolved doubt!</span>
        </v-tooltip>
      </template>
      <!--
      <template v-slot:item.visibility="{ item }">
        <v-select
          v-model="item.visibility"
          :items="visibilityList"
          dense
          data-cy="visButton"
          @change="changeVisibility(item.id, item.visibility)"
        >
          <template v-slot:selection="{ item }">
            <v-chip :color="getStatusColor(item)" height="1px">
              <span>{{ item }}</span>
            </v-chip>
          </template>
        </v-select>
      </template>
      -->
    </v-data-table>

    <create-clarification-dialog
      v-if="currentDiscussion"
      v-model="createClarificationDialog"
      :discussion="currentDiscussion"
      v-on:new-clarification="onSolvedDoubt"
      v-on:close-dialog="onCloseDialog"
      :creating="creating"
    />
  </v-card>
</template>
<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import CreateClarificationDialog from '@/views/teacher/doubts/CreateClarificationDialog.vue';
import { ToggleButton } from 'vue-js-toggle-button';
import Discussion from '@/models/management/Discussion';

@Component({
  components: {
    'create-clarification-dialog': CreateClarificationDialog,
    ToggleButton: ToggleButton
  }
})
export default class SolveDoubtsView extends Vue {
  discussions: Discussion[] = [];
  createClarificationDialog: boolean = false;
  creating: boolean = false;
  search: string = '';
  currentDiscussion: Discussion | null = null;
  visibilityList = ['PRIVATE', 'PUBLIC'];
  headers: object = [
    {
      text: 'Question Title',
      value: 'questionTitle',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Creation Date',
      value: 'creationDate',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Status',
      value: 'status',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Actions',
      value: 'action',
      align: 'center',
      sortable: false,
      width: '10%'
    }
  ];

  onCloseDialog() {
    this.createClarificationDialog = false;
  }

  async solve(discussion: Discussion) {
    this.currentDiscussion = new Discussion(discussion);
    this.createClarificationDialog = true;
    this.creating = true;
  }

  async details(discussion: Discussion) {
    this.currentDiscussion = new Discussion(discussion);
    this.createClarificationDialog = true;
    this.creating = false;
  }

  isSolved(discussion: Discussion) {
    let list = discussion.postsDto;
    return list.length != 0 ? list[list.length - 1].status == 'SOLVED' : false;
  }

  getStatusColor(discussion: Discussion) {
    let status = discussion.status;
    if (status === 'CLOSED') return 'red';
    else return 'green';
  }
  async onSolvedDoubt() {
    this.createClarificationDialog = false;
    await this.$store.dispatch('loading');
    try {
      this.discussions = await RemoteServices.manageDiscussions();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.discussions = await RemoteServices.manageDiscussions();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async changeVisibility(discussionId: number, status: string) {
    try {
      await RemoteServices.changeVisibility(discussionId, status);
      let disc = this.discussions.find(
        discussion => discussion.id === discussionId
      );
      if (disc) {
        disc.visibility = status;
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  async closeDiscussion(discussionId: number) {
    try {
      await RemoteServices.closeDiscussion(discussionId);
      let disc = this.discussions.find(
              discussion => discussion.id === discussionId
      );
      if (disc) {
        disc.status = 'CLOSED'
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

}
</script>

<style lang="scss" scoped></style>
