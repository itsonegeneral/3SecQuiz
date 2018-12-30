package com.rstudio.a3secquiz;

public class Player {
    private int coins;
    private int lifes;
    private String name;
    private int viewedAds;

    public Player(){

    }
    public Player(String name,int coins, int lifes ) {
        this.coins = coins;
        this.lifes = lifes;
        this.name = name;
    }

    public int getCoins() {
        return coins;
    }

    public int getLifes() {
        return lifes;
    }

    public String getName() {
        return name;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public void setLifes(int lifes) {
        this.lifes = lifes;
    }

    public void setName(String name) {
        this.name = name;
    }
}
