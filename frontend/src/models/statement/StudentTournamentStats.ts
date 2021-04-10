export default class StudentTournamentStats {
  amountOfParticipatedTournaments!: number;

  constructor(jsonObj?: StudentTournamentStats) {
    if (jsonObj) {
      this.amountOfParticipatedTournaments = jsonObj.amountOfParticipatedTournaments;
    }
  }
}