package si.um.feri.lpm.tile.screen;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import si.um.feri.lpm.tile.TileMapExample;
import si.um.feri.lpm.tile.assets.AssetDescriptors;
import si.um.feri.lpm.tile.assets.AssetPaths;
import si.um.feri.lpm.tile.common.GameManager;
import si.um.feri.lpm.tile.config.GameConfig;
import si.um.feri.lpm.tile.ecs.system.BoundsSystem;
import si.um.feri.lpm.tile.ecs.system.CameraMovementSystem;
import si.um.feri.lpm.tile.ecs.system.CleanUpSystem;
import si.um.feri.lpm.tile.ecs.system.CollisionSystem;
import si.um.feri.lpm.tile.ecs.system.HudRenderSystem;
import si.um.feri.lpm.tile.ecs.system.MovementSystem;
import si.um.feri.lpm.tile.ecs.system.MowerInputSystem;
import si.um.feri.lpm.tile.ecs.system.MowerMusicSystem;
import si.um.feri.lpm.tile.ecs.system.RenderSystem;
import si.um.feri.lpm.tile.ecs.system.WorldWrapSystem;
import si.um.feri.lpm.tile.ecs.system.debug.DebugCameraSystem;
import si.um.feri.lpm.tile.ecs.system.debug.DebugGridRenderSystem;
import si.um.feri.lpm.tile.ecs.system.debug.DebugInputSystem;
import si.um.feri.lpm.tile.ecs.system.debug.DebugRenderSystem;
import si.um.feri.lpm.tile.ecs.system.passive.EntityFactorySystem;
import si.um.feri.lpm.tile.ecs.system.passive.SoundSystem;
import si.um.feri.lpm.tile.ecs.system.passive.StartUpSystem;
import si.um.feri.lpm.tile.ecs.system.passive.TiledSystem;

/**
 * Artwork from https://goodstuffnononsense.com/about/
 * https://goodstuffnononsense.com/hand-drawn-icons/space-icons/
 */

public class GameScreen extends ScreenAdapter {

    private static final Logger log = new Logger(GameScreen.class.getSimpleName(), Logger.DEBUG);

    private final AssetManager assetManager;
    private final SpriteBatch batch;
    private final TileMapExample game;

    private OrthographicCamera camera;
    private Viewport viewport;
    private Viewport hudViewport;
    private ShapeRenderer renderer;
    private PooledEngine engine;
    private BitmapFont font;
    private TiledMap map;

    //OrthoCachedTiledMapRenderer mapRenderer;
    public GameScreen(TileMapExample game) {
        this.game = game;
        assetManager = game.getAssetManager();
        batch = game.getBatch();
        //https://gamedev.stackexchange.com/questions/127733/libgdx-how-to-handle-touchpad-input
    /*
    Stage stage = new Stage();
    Table table = new Table(skin); // You can skip table and add it directly to the stage as well.
    Touchpad touchpad = new Touchpad(GameConfig.TOUCH_RADIUS, skin);
    Gdx.input.setInputProcessor(stage);
    touchpad.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        // This is run when anything is changed on this actor.
        float deltaX = ((Touchpad) actor).getKnobPercentX();
        float deltaY = ((Touchpad) actor).getKnobPercentY();
        //...
      }
    });
    */
    }

    @Override
    public void show() {
        map = assetManager.get(AssetPaths.TILES); //Rethink add with manager?

        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.WIDTH, GameConfig.HEIGHT, camera);
        hudViewport = new FitViewport(GameConfig.WIDTH, GameConfig.HEIGHT);
        renderer = new ShapeRenderer();
        font = assetManager.get(AssetDescriptors.FONT32);
        engine = new PooledEngine();
        //passive systems
        engine.addSystem(new EntityFactorySystem(assetManager));
        engine.addSystem(new SoundSystem(assetManager));
        engine.addSystem(new TiledSystem(map));
        if (GameConfig.debug) {
            engine.addSystem(new DebugGridRenderSystem(viewport, renderer));
            engine.addSystem(new DebugCameraSystem(
                    GameConfig.WIDTH / 2, GameConfig.HEIGHT / 2, //center
                    camera
            ));
            engine.addSystem(new DebugRenderSystem(viewport, renderer));
            engine.addSystem(new DebugInputSystem());
        }
        engine.addSystem(new BoundsSystem());
        //engine.addSystem(new PlayerControlSystem());
        engine.addSystem(new WorldWrapSystem());
        engine.addSystem(new MovementSystem());
        engine.addSystem(new MowerInputSystem());
        engine.addSystem(new RenderSystem(batch, viewport));
        engine.addSystem(new StartUpSystem());
        engine.addSystem(new CleanUpSystem());
        engine.addSystem(new CollisionSystem());
        engine.addSystem(new CameraMovementSystem());
        engine.addSystem(new MowerMusicSystem(assetManager));

        engine.addSystem(new HudRenderSystem(batch, hudViewport, font));
        GameManager.INSTANCE.resetResult();

        //mapRenderer = new OrthoCachedTiledMapRenderer(map);
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) Gdx.app.exit();
//    if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) GameManager.INSTANCE.resetResult();


        ScreenUtils.clear(0f, 0f, 0f, 0f);
        engine.update(delta);

        if (GameManager.INSTANCE.isGameOver()) {
            //Gdx.app.exit();
        }
        //     game.setScreen(new MenuScreen(game));
        // }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        hudViewport.update(width, height, true);
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        renderer.dispose();
        engine.removeAllEntities();
    }

    public void printEngine() {
        ImmutableArray<EntitySystem> systems = engine.getSystems();
        for (EntitySystem system : systems) {
            System.out.println(system.getClass().getSimpleName());
        }
    }
}
