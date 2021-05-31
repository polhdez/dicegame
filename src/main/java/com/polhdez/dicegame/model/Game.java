package com.polhdez.dicegame.model;

import com.polhdez.dicegame.repository.UserRepository;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Random;

@Document(collection = "game")
public class Game {

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String id;

    private String playerId;

    private String type;
    private int firstRoll;
    private int secondRoll;
    private Boolean win;

    public Game() {}

    public Game(String playerId, String type){
        this.playerId = playerId;
        Random random = new Random();
        this.type = type;
        this.firstRoll = random.nextInt(5) + 1;
        this.secondRoll = random.nextInt(5) + 1;
        switch(type) {
            case "1":
                // Game where you win if you get more than a 7
                if (this.firstRoll + this.secondRoll > 7)
                    this.win = true;
                else
                    this.win = false;
                break;
            case "2":
                // Game where you win if you get more than a 10
                if (this.firstRoll + this.secondRoll > 10)
                    this.win = true;
                else
                    this.win = false;
                break;
            case "3":
                // Game where you win if you get less than a 6
                if (this.firstRoll + this.secondRoll < 6)
                    this.win = true;
                else
                    this.win = false;
                break;
        }
    }

    public String getId() {
        return this.id;
    }

    public String getPlayerId() {
        return this.playerId;
    }

    public int getFirstRoll() {
        return this.firstRoll;
    }

    public String getType() {
        return this.type;
    }

    public int getSecondRoll() {
        return this.secondRoll;
    }

    public Boolean getWin() {
        return this.win;
    }
}