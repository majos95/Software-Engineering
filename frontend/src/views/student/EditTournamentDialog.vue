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
          New Tournament
        </span>
			</v-card-title>

			<v-card-text class="text-left" v-if="editTournament">
				<v-container grid-list-md fluid>
					<v-layout column wrap>
						<v-flex xs24 sm12 md8>
							<p v-if="isCreateTournament"><b>Name:</b> {{ editTournament.name }} </p>
							<v-text-field
									v-if="!isCreateTournament"
									v-model="editTournament.name"
									label="Name"
									data-cy="Name"
							/>
						</v-flex>
						<v-flex xs24 sm12 md8>
							<p v-if="isCreateTournament"><b>Start Date:</b> {{ editTournament.startDate }} </p>
							<v-text-field
									v-if="!isCreateTournament"
									v-model="editTournament.startDate"
									label="Start Date - YYYY-MM-dd HH:mm:SS"
									data-cy="startDate"
							/>
						</v-flex>
						<v-flex xs24 sm12 md8>
							<p v-if="isCreateTournament"><b>End Date:</b> {{ editTournament.endDate }} </p>
							<v-text-field
									v-if="!isCreateTournament"
									v-model="editTournament.endDate"
									label="End Date - YYYY-MM-dd HH:mm:SS"
									data-cy="endDate"
							/>
						</v-flex>

						<v-card class="table">
							<v-data-table
									:headers="headers"
									:items="topics"
									:search="search"
									disable-pagination
									:hide-default-footer="true"
									:mobile-breakpoint="0"
									multi-sort
							>
								<template v-slot:item.selected="{ item }">
									<v-checkbox
											v-model="selectedTopics[item.name]"
											@change="onCheckboxChange(item.name)"
											data-cy="checkTopic"
											primary hide-details>
									</v-checkbox>
								</template>
							</v-data-table>
						</v-card>
						<v-flex xs24 sm12 md8>
							<p v-if="isCreateTournament"><b>Start Date:</b> {{ editTournament.numQuestions }} </p>
							<v-text-field
									v-if="!isCreateTournament"
									v-model="editTournament.numQuestions"
									label="Number of questions"
									data-cy="numQuestions"
							/>
						</v-flex>
					</v-layout>
				</v-container>
			</v-card-text>

			<v-card-actions>
				<v-spacer/>
				<v-btn
						color="blue darken-1"
						@click="$emit('close-dialog')"
						data-cy="cancelButton"
				>Cancel
				</v-btn
				>
				<v-btn color="blue darken-1" @click="saveTournament" data-cy="saveButton"
				>Save
				</v-btn
				>
			</v-card-actions>
		</v-card>
	</v-dialog>
</template>

<script lang="ts">
    import {Component, Model, Prop, Vue} from 'vue-property-decorator';
    import RemoteServices from '@/services/RemoteServices';
    import Course from '@/models/user/Course';
    import Tournament from '@/models/management/Tournament';
    import Topic from '@/models/management/Topic';
    import format from 'date-fns/format'

    @Component
    export default class EditTournamentDialog extends Vue {
        @Model('dialog', Boolean) dialog!: boolean;
        editTournament!: Tournament;
        isCreateTournament: boolean = false;
        topics: Topic[] = [];
        search: string = '';
        headers: object = [
            {text: 'Name', value: 'name', align: 'left', width: '30%'},
            {text: 'Selected', value: 'selected', align: 'center', sortable: false, width: '5%'}
        ];
        selectedTopics: {[name: string]: boolean } = {};
        counter: number = 0;

        async created() {
            this.editTournament = new Tournament();
            this.isCreateTournament = !!this.editTournament.name;

			try {
			  this.topics = await RemoteServices.getTopics();
			  for (let i = 0; i < this.topics.length; ++i)
			      this.selectedTopics[this.topics[i].name] = false;
			} catch (error) {
			  await this.$store.dispatch('error', error);
			}

			await this.$store.dispatch('loading');
        }

		onCheckboxChange(topicName: string) {
            if (this.counter === 0) {
                for (let i = 0; i < this.topics.length; ++i)
			      this.selectedTopics[this.topics[i].name] = false;
                this.counter++;
			}
            this.selectedTopics[topicName] = !this.selectedTopics[topicName];
		}

        async saveTournament() {
            if (this.editTournament) {
                for (let i = 0; i < this.topics.length; ++i) {
                    if (this.selectedTopics[this.topics[i].name]) {
                        this.editTournament.topics.push(this.topics[i]);
					}
				}
			}

            if (this.editTournament &&
				(!this.editTournament.startDate ||
				!this.editTournament.endDate ||
				!this.editTournament.topics ||
				!this.editTournament.numQuestions)) {
                await this.$store.dispatch(
                    'error',
                    'Tournament must have name, start date, end date and at least one topic!'
                );
				return;
			}

            if (this.editTournament) {

				try {
                    const result = await RemoteServices.createTournament(this.editTournament);
					this.$emit('new-tournament', result);
				} catch (error) {
                    await this.$store.dispatch('error', error);
                }
			}
        }
    }
</script>