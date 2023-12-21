package si.um.feri.lpm.tile.config;

public class GameConfig {

    public static final float MOWER_WIDTH = 48f;
    public static final float MOWER_HEIGHT = 64f;
    public static final float MAX_MOWER_R_SPEED = 64; //1s 360
    public static final float MAX_MOWER_SPEED = 2;
    public static final float TOUCH_RADIUS = 20;

    public static float WIDTH = 32 * 14;
    public static float HEIGHT = 32 * 24f;
    public static float W_WIDTH = 32 * 20;
    public static float W_HEIGHT = 32 * 100f;

    public static float POSITION_X = (W_WIDTH - MOWER_WIDTH) / 2;
    public static float POSITION_Y = 0;
    
    public static boolean debug = false;

    private GameConfig() {
    }
}
