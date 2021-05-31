package com.polhdez.dicegame.repository;

import com.polhdez.dicegame.model.Game;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GameRepository extends MongoRepository<Game,String> {
    List<Game> findAllByTypeAndPlayerId(String type, String playerId);
    List<Game> findAllByType(String type);
}
