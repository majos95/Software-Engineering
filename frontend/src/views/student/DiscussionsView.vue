<template>
  <div>
    <v-card class="table">
      <v-data-table
        :headers="headers"
        :items="discussions"
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
              class="white--text"
              color="blue"
              style="text-decoration-color: white;"
              right to="discussionStats"
              ><v-icon
                color="white"
                small
                style="position: relative;  padding-left:0cm; padding-right: 0.1cm;"
                >fas fa-chart-bar</v-icon
              >Dashboard
            </v-btn>
          </v-card-title>
        </template>

        <template v-slot:item.questionTitle="{ item }">
          <v-chip small>
            <span>{{ item.questionTitle }}</span>
          </v-chip>
        </template>

        <template v-slot:item.title="{ item }">
          <v-chip @click="seeDiscussion(item)" small>
            <span>{{ item.title }}</span>
          </v-chip>
        </template>

        <template v-slot:item.status="{ item }">
          <v-chip :color="getStatusColor(item)" small>
            <span>{{ item.status }}</span>
          </v-chip>
        </template>

        <template v-slot:item.creationDate="{ item }">
          {{ item.postsDto[0].creationDate }}
        </template>

        <template v-slot:item.action="{ item }">
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                data-cy="seeDoubtButton"
                small
                class="mr-2"
                v-on="on"
                @click="seeDiscussion(item)"
                >visibility</v-icon
              >
            </template>
            <span>Show Doubt</span>
          </v-tooltip>
        </template>
      </v-data-table>
      <see-discussion-dialog
        v-if="discussion"
        v-model="seeDiscussionDialog"
        :discussion="discussion"
        :id="discussion.id"
        v-on:see-discussion="onSeeDiscussion"
        v-on:close-dialog="onCloseDialog"
        v-on:new-discussion="onNewDoubt"
      />
    </v-card>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Discussion from '@/models/management/Discussion';
import SeeDiscussionDialog from '@/views/student/SeeDiscussionDialog.vue';
import AnimatedNumber from '@/components/AnimatedNumber.vue';

@Component({
  components: {
    'see-discussion-dialog': SeeDiscussionDialog
  }
})
export default class DoubtView extends Vue {
  discussions: Discussion[] = [];
  discussion: Discussion | null = null;
  seeDiscussionDialog: boolean = false;
  search: string = '';
  headers: object = [
    { text: 'QuestionTitle', value: 'questionTitle', align: 'left' },
    { text: 'Title', value: 'title', align: 'center' },
    { text: 'Status', value: 'status', align: 'center' },
    { text: 'Creation Date', value: 'creationDate', align: 'center' },
    { text: 'Actions', value: 'action', align: 'center', sortable: false }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.discussions = await RemoteServices.getDiscussions();
      console.log(this.discussions);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  getStatusColor(discussion: Discussion) {
    if (discussion.status !== 'OPEN') return 'red';
    else return 'green';
  }

  seeDiscussion(currentDiscussion: Discussion): void {
    console.log(currentDiscussion);
    this.discussion = currentDiscussion;
    this.seeDiscussionDialog = true;
    this.discussion.postsDto.forEach(d => (d.showDoubt = false));
  }
  async onSeeDiscussion() {
    this.seeDiscussionDialog = false;
    this.discussion = null;
  }
  onCloseDialog() {
    this.seeDiscussionDialog = false;
    this.discussion = null;
  }

  async onNewDoubt() {
    await this.$store.dispatch('loading');
    try {
      this.seeDiscussionDialog = false;
      this.discussions = await RemoteServices.getDiscussions();
      console.log(this.discussions);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>

<style lang="scss" scoped>
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

.icon-wrapper,
.project-name {
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-wrapper {
  font-size: 100px;
  transform: translateY(0px);
  transition: all 0.6s;
}

.icon-wrapper {
  align-self: end;
}
.container {
  max-width: 1000px;
  margin-left: auto;
  margin-right: auto;
  padding-left: 10px;
  padding-right: 10px;
  h2 {
    font-size: 26px;
    margin: 20px 0;
    text-align: center;
    small {
      font-size: 0.5em;
    }
  }
  ul {
    overflow: hidden;
    padding: 0 5px;
    li {
      border-radius: 3px;
      padding: 15px 10px;
      display: flex;
      justify-content: space-between;
      margin-bottom: 10px;
    }
    .list-header {
      background-color: #1976d2;
      color: white;
      font-size: 14px;
      text-transform: uppercase;
      letter-spacing: 0.03em;
      text-align: center;
    }
    .col {
      width: 25%;
    }
    .last-col {
      max-width: 50px !important;
    }
    .list-row {
      background-color: #ffffff;
      cursor: pointer;
      box-shadow: 0 0 9px 0 rgba(0, 0, 0, 0.1);
    }
    .list-row:hover {
      background-color: #c8c8c8;
    }
  }
}
</style>
