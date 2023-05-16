import entity.Player;
import entity.Sprite;

import java.util.List;

public class Game {

    private Player player;
    private List<Sprite> sprites;


    public Game()
    {
        player = new Player();

    }

    public Player getPlayer()
    {
        return player;
    }




}
