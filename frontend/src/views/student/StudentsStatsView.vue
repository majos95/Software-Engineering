o<template>
  <div>
    <h2>Students Statistics</h2>
        <div v-if="this.privateStats == true" class="container">
            <div class="statsContainer">
              <h3>Your information is currently public. This implies that other platform users can see your statistics.<br/>Do you wish for this information to be public?
              </h3>
              <v-switch v-model="this.privateHelper" justify="center" align="end" @change="saveChanges" data-cy="makePrivate"/>
            </div>
        </div>

        <div v-if="this.privateStats == false" class="container">
            <div class="statsContainer">
                <h3>Your information is currently private. This implies that no one can see your statistics.<br/>Do you wish for this information to be public?
                </h3>
                <v-switch v-model="this.privateHelper" justify="center" align="end" @change="saveChanges" data-cy="makePublic"/>
            </div>
        </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import AnimatedNumber from '@/components/AnimatedNumber.vue';
import RemoteServices from '@/services/RemoteServices';

@Component({
  components: {
    AnimatedNumber
  }
})
export default class StudentsStatsView extends Vue {
  privateStats: Boolean = false;
  privateHelper: Boolean = false;

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.privateStats = await RemoteServices.getStudentPrivacySettings();
      this.privateHelper = this.privateStats;
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  saveChanges(){
    RemoteServices.toggleStudentPrivacySettings();
  }
}
</script>

<style lang="scss" scoped>
.statsContainer{
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: center;
    align-items: stretch;
    align-content: center;
    height: 100%;
    background-color: rgba(255, 255, 255, 0.75);
    border-radius: 5px;
    flex-basis: 25%;
    margin: 20px;
}
</style>
