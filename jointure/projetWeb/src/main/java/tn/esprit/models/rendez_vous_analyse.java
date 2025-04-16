package tn.esprit.models;

public class rendez_vous_analyse {
    private int rendez_vous_id , analyse_id ;

    public rendez_vous_analyse() {}

    public rendez_vous_analyse(int rendez_vous_id, int analyse_id) {
        this.rendez_vous_id = rendez_vous_id;
        this.analyse_id = analyse_id;
    }

    public int getRendez_vous_id() {
        return rendez_vous_id;
    }

    public void setRendez_vous_id(int rendez_vous_id) {
        this.rendez_vous_id = rendez_vous_id;
    }

    public int getAnalyse_id() {
        return analyse_id;
    }

    public void setAnalyse_id(int analyse_id) {
        this.analyse_id = analyse_id;
    }

    @Override
    public String toString() {
        return "rendez_vous_analyse{" +
                "rendez_vous_id=" + rendez_vous_id +
                ", analyse_id=" + analyse_id +
                '}';
    }
}
