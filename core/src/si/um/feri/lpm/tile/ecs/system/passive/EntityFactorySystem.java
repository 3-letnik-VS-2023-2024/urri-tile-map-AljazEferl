package si.um.feri.lpm.tile.ecs.system.passive;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import si.um.feri.lpm.tile.assets.AssetDescriptors;
import si.um.feri.lpm.tile.assets.RegionNames;
import si.um.feri.lpm.tile.config.GameConfig;
import si.um.feri.lpm.tile.ecs.component.BoundsComponent;
import si.um.feri.lpm.tile.ecs.component.DimensionComponent;
import si.um.feri.lpm.tile.ecs.component.MovementComponent;
import si.um.feri.lpm.tile.ecs.component.MowerComponent;
import si.um.feri.lpm.tile.ecs.component.PositionComponent;
import si.um.feri.lpm.tile.ecs.component.TextureComponent;
import si.um.feri.lpm.tile.ecs.component.WorldWrapComponent;
import si.um.feri.lpm.tile.ecs.component.ZOrderComponent;

public class EntityFactorySystem extends EntitySystem {

    private static final int MOWER_Z_ORDER = 3;

    private final AssetManager assetManager;

    private PooledEngine engine;
    private TextureAtlas gamePlayAtlas;

    public EntityFactorySystem(AssetManager assetManager) {
        this.assetManager = assetManager;
        setProcessing(false);   // passive
        init();
    }

    private void init() {
        gamePlayAtlas = assetManager.get(AssetDescriptors.GAME_PLAY);
    }

    @Override
    public void addedToEngine(Engine engine) {
        this.engine = (PooledEngine) engine;
    }


    public void createMower() {
        PositionComponent position = engine.createComponent(PositionComponent.class);
        position.x = (GameConfig.W_WIDTH - GameConfig.MOWER_HEIGHT) / 2;
        position.r = 90;

        DimensionComponent dimension = engine.createComponent(DimensionComponent.class);
        dimension.width = GameConfig.MOWER_WIDTH;
        dimension.height = GameConfig.MOWER_HEIGHT;

        BoundsComponent bounds = engine.createComponent(BoundsComponent.class);
        bounds.rectangle.setPosition(position.x, position.y);
        bounds.rectangle.setSize(dimension.width, dimension.height);

        MovementComponent movement = engine.createComponent(MovementComponent.class);

        MowerComponent mowerComponent = engine.createComponent(MowerComponent.class);

        WorldWrapComponent worldWrap = engine.createComponent(WorldWrapComponent.class);

        TextureComponent texture = engine.createComponent(TextureComponent.class);
        texture.region = gamePlayAtlas.findRegion(RegionNames.CAR1);

        ZOrderComponent zOrder = engine.createComponent(ZOrderComponent.class);
        zOrder.z = MOWER_Z_ORDER;

        Entity entity = engine.createEntity();
        entity.add(position);
        entity.add(dimension);
        entity.add(bounds);
        entity.add(movement);
        entity.add(mowerComponent);
        entity.add(worldWrap);
        entity.add(texture);
        entity.add(zOrder);

        engine.addEntity(entity);
    }
}
