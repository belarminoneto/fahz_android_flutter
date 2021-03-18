package br.com.avanade.fahz.model.anamnesisModel;

public class UserAnamnesis {

    private String userCPF;

    private String lifeCPF;

    private int age;

    private int environment;

    private int pendencies;

    private String situation;

    private String token;

    private LifeAnamnesis life;

    public String getUserCPF() {
        return userCPF;
    }

    public void setUserCPF(String userCPF) {
        this.userCPF = userCPF;
    }

    public String getLifeCPF() {
        return lifeCPF;
    }

    public void setLifeCPF(String lifeCPF) {
        this.lifeCPF = lifeCPF;
    }

    public int getEnvironment() {
        return environment;
    }

    public void setEnvironment(int environment) {
        this.environment = environment;
    }

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String value) {
        this.token = value;
    }

    public LifeAnamnesis getLife() {
        return life;
    }

    public void setLife(LifeAnamnesis life) {
        this.life = life;
    }

    public int getPendencies() {
        return pendencies;
    }

    public void setPendencies(int pendencies) {
        this.pendencies = pendencies;
    }
}
