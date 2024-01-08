package si.um.feri.lpm.tile.ecs.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Intersector;

import si.um.feri.lpm.tile.assets.AssetDescriptors;
import si.um.feri.lpm.tile.assets.RegionNames;
import si.um.feri.lpm.tile.common.GameManager;
import si.um.feri.lpm.tile.ecs.component.BoundsComponent;
import si.um.feri.lpm.tile.ecs.component.MovementComponent;
import si.um.feri.lpm.tile.ecs.component.MowerComponent;
import si.um.feri.lpm.tile.ecs.component.ObstacleComponent;
import si.um.feri.lpm.tile.ecs.component.TextureComponent;
import si.um.feri.lpm.tile.ecs.system.passive.SoundSystem;
import si.um.feri.lpm.tile.ecs.system.passive.TiledSystem;
import si.um.feri.lpm.tile.util.Mappers;

public class CollisionSystem extends EntitySystem {

    private TextureAtlas gamePlayAtlas;
    private static final Family FAMILY_MOWER = Family.all(MowerComponent.class, BoundsComponent.class).get();
    private static final Family FAMILY_OBSTACLE = Family.all(ObstacleComponent.class, BoundsComponent.class).get();

    private SoundSystem soundSystem;
    private TiledSystem tiledSystem;
    private final AssetManager assetManager;

    public CollisionSystem( AssetManager assetManager) {
        this.assetManager = assetManager;
    }


    @Override
    public void addedToEngine(Engine engine) {
        soundSystem = engine.getSystem(SoundSystem.class);
        tiledSystem = engine.getSystem(TiledSystem.class);
    }

    @Override
    public void update(float deltaTime) {
        if (GameManager.INSTANCE.isGameOver()) return;

        ImmutableArray<Entity> mowers = getEngine().getEntitiesFor(FAMILY_MOWER);
        ImmutableArray<Entity> obstacles = getEngine().getEntitiesFor(FAMILY_OBSTACLE);

        for (Entity mower : mowers) {
            BoundsComponent firstBounds = Mappers.BOUNDS.get(mower);
            TextureComponent mowerTexture = Mappers.TEXTURE.get(mower);
            boolean collidedWithObstacle = false;

            if (tiledSystem.collideWith(firstBounds.rectangle)) {
                // soundSystem.pick();
            }

            for (Entity obstacle : obstacles) {
                ObstacleComponent obstacleComponent = Mappers.OBSTACLE.get(obstacle);
                if (obstacleComponent.hit) {
                    continue;
                }

                BoundsComponent secondBounds = Mappers.BOUNDS.get(obstacle);
                if (Intersector.overlaps(firstBounds.rectangle, secondBounds.rectangle)) {
                     //obstacleComponent.hit = true;
                     //GameManager.INSTANCE.damage();
                    soundSystem.obstacle();
                    gamePlayAtlas = assetManager.get(AssetDescriptors.GAME_PLAY);
                    mowerTexture.region = gamePlayAtlas.findRegion(RegionNames.CAR2);

                    mower.getComponent(MovementComponent.class).speed *= 0.9;
                    collidedWithObstacle = true;
                }
            }


            if (!collidedWithObstacle) {
                gamePlayAtlas = assetManager.get(AssetDescriptors.GAME_PLAY);
                mowerTexture.region = gamePlayAtlas.findRegion(RegionNames.CAR1);
            }
        }

    }
}
