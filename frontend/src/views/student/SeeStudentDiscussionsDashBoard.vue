<template>
  <div>
    <v-btn style="position: relative; top:0.8cm; right: 1.5cm; " color="blue" class="white--text" to="/student/discussions"
    ><v-icon style="position: relative; right: 0.25cm;">fas fa-arrow-left</v-icon
    >Back</v-btn
    >
    <v-btn style="position: relative; top:0.8cm; right: 1.25cm; " color="blue" class="white--text" @click="changePrivacy()"
      ><v-icon style="position: relative; right: 0.25cm;">{{ privacyIcon() }}</v-icon
      >{{ privacyMessage() }}</v-btn
    >
    <div class="stats-container" style="position:relative; top: 1cm;">
      <div class="items">
        <div class="icon-wrapper" ref="discussionsMade">
          <animated-number> {{ discussions.length }}</animated-number>
        </div>
        <div class="project-name">
          <p>Total Discussions Made</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="discussionsMade">
          <animated-number>
            {{
              discussions.filter(disc => disc.status === 'OPEN').length
            }}</animated-number
          >
        </div>
        <div class="project-name">
          <p>Total Open Discussions</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="discussionsMade">
          <animated-number>
            {{
              discussions.length -
                discussions.filter(disc => disc.status === 'OPEN').length
            }}</animated-number
          >
        </div>
        <div class="project-name">
          <p>Total Closed Discussions</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="discussionsMade">
          <animated-number>
            {{
              discussions.filter(
                disc =>
                  disc.postsDto[disc.postsDto.length - 1].status === 'SOLVED'
              ).length
            }}</animated-number
          >
        </div>
        <div class="project-name">
          <p>Total Solved Discussions</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import AnimatedNumber from '@/components/AnimatedNumber.vue';
import Discussion from '@/models/management/Discussion';

@Component({
  components: { AnimatedNumber }
})
export default class SeeStudentDiscussionsDashBoard extends Vue {
  discussions: Discussion[] = [];
  privacy: boolean = false;
  async created() {
    await this.$store.dispatch('loading');
    try {
      this.privacy = await RemoteServices.getPrivacy();
      console.log(this.privacy);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
    await this.$store.dispatch('loading');
    try {
      this.discussions = await RemoteServices.getDiscussions();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async changePrivacy() {
    await this.$store.dispatch('loading');
    try {
      this.privacy = await RemoteServices.setPrivacy();
      console.log(this.privacy);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  privacyMessage(): string {
    if (this.privacy) {
      return 'Make public';
    } else {
      return 'Make private';
    }
  }

  privacyIcon(): string {
    if (this.privacy == false) {
      return 'fas fa-eye';
    } else {
      return 'fas fa-eye-slash';
    }
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
