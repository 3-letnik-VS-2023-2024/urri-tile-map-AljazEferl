package si.um.feri.lpm.tile.ecs.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import si.um.feri.lpm.tile.config.GameConfig;
import si.um.feri.lpm.tile.ecs.component.MowerComponent;
import si.um.feri.lpm.tile.ecs.component.PositionComponent;
import si.um.feri.lpm.tile.ecs.system.passive.TiledSystem;
import si.um.feri.lpm.tile.util.Mappers;

public class CameraMovementSystem extends IteratingSystem {

    private TiledSystem tiledSystem;

    private static final Family FAMILY = Family.all(
            PositionComponent.class,
            MowerComponent.class
    ).get();

    public CameraMovementSystem() {
        super(FAMILY);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        tiledSystem = engine.getSystem(TiledSystem.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent position = Mappers.POSITION.get(entity);

        GameConfig.POSITION_X = position.x;
        GameConfig.POSITION_Y = position.y;

        // MathUtils.lerp(GameConfig.POSITION_Y, position.y, 0.1f); //movement.speed;
    }
}
