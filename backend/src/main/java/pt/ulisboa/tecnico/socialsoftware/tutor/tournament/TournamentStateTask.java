package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

public class TournamentStateTask implements Runnable {
    private Tournament tournament;
    private Tournament.State state;

    public TournamentStateTask(Tournament tournament, Tournament.State state) {
        this.tournament = tournament;
        this.state = state;
    }

    @Override
    public void run() {
        tournament.setState(state);
    }
}
