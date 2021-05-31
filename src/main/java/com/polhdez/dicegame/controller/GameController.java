package com.polhdez.dicegame.controller;

import com.polhdez.dicegame.model.Game;
import com.polhdez.dicegame.model.User;
import com.polhdez.dicegame.repository.GameRepository;
import com.polhdez.dicegame.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
public class GameController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    GameRepository gameRepository;

    // We just need the header with the token to play
    @GetMapping("play")
    public ResponseEntity playGame(@RequestHeader("Authorization") String token, @RequestParam String type) {
        try {
            // Find user by token so it cant play as other players
            User user = userRepository.findByToken(token);

            // If token doesn't match throw error
            if (user == null)
                return new ResponseEntity("Invalid token!", HttpStatus.FORBIDDEN);
            Game game = new Game(user.getId(), type);
            return new ResponseEntity(gameRepository.save(game), HttpStatus.OK);
        }

        // If some random error, throw server error
        catch(Exception e) {
            return new ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("players")
    public ResponseEntity listGamesByPlayer(@RequestHeader("Authorization") String token, @RequestParam String type) {
        try {
            User user = userRepository.findByToken(token);
            return new ResponseEntity(gameRepository.findAllByTypeAndPlayerId(type, user.getId()), HttpStatus.OK);
        }
        catch (Exception e)  {
            return new ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("ranking")
    public ResponseEntity getRanking(@RequestParam String type) {
        try {
            List<Game> games = gameRepository.findAllByType(type);
            double wins = 0;
            for (Game game : games) {
                if (game.getWin())
                    wins++;
            }
            double winPercent = wins * 100 / games.size();
            return new ResponseEntity<>(winPercent, HttpStatus.OK);
        }
        catch (Exception e)  {
            return new ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("ranking/loser")
    public ResponseEntity getLoser(@RequestParam String type) {
        try {
            User loserPlayer = null;
            int maxLoses = 0;
            for (User player : userRepository.findAll()) {
                int loses = 0;
                for (Game game : gameRepository.findAllByTypeAndPlayerId(type, player.getId())) {
                    if (!game.getWin()) {
                        loses++;
                    }
                }
                if (loses > maxLoses) {
                    loserPlayer = player;
                    maxLoses = loses;
                }
            }
            return new ResponseEntity<>(loserPlayer, HttpStatus.OK);
        }
        catch(Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("ranking/winner")
    public ResponseEntity getWinner(@RequestParam String type) {
        try {
            User winnerPlayer = null;
            int maxWins = 0;
            for (User player : userRepository.findAll()) {
                int wins = 0;
                for (Game game : gameRepository.findAllByTypeAndPlayerId(type, player.getId())) {
                    if (game.getWin()) {
                        wins++;
                    }
                }
                if (wins > maxWins) {
                    winnerPlayer = player;
                    maxWins = wins;
                }
            }
            return new ResponseEntity<>(winnerPlayer, HttpStatus.OK);
        }
        catch(Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

