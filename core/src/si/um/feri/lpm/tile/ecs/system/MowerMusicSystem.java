package si.um.feri.lpm.tile.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.assets.AssetManager;

import si.um.feri.lpm.tile.assets.AssetDescriptors;
import si.um.feri.lpm.tile.ecs.component.MovementComponent;
import si.um.feri.lpm.tile.ecs.component.MowerComponent;
import si.um.feri.lpm.tile.util.Mappers;

public class MowerMusicSystem extends IteratingSystem {

    private static final Family FAMILY = Family.all(
            MowerComponent.class,
            MovementComponent.class
    ).get();

    private final AssetManager assetManager;
    private int state = 0;

    public MowerMusicSystem(AssetManager assetManager) {
        super(FAMILY);
        this.assetManager = assetManager;
        state = 0;
        assetManager.get(AssetDescriptors.MOWER_LOOP).setLooping(true);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        MovementComponent movement = Mappers.MOVEMENT.get(entity);
        switch (state) {
            case 0:
                if (movement.speed > 0.02f) {
                    assetManager.get(AssetDescriptors.MOWER_START).play();
                    state = 1;
                }
                break;
            case 1:
                if (!assetManager.get(AssetDescriptors.MOWER_START).isPlaying()) {
                    assetManager.get(AssetDescriptors.MOWER_LOOP).play();
                    state = 2;
                }
                break;
            case 2:
                if (movement.speed < 0.001f) {
                    assetManager.get(AssetDescriptors.MOWER_LOOP).stop();
                    state = 3;
                }
                break;
            case 3:
                if (movement.speed > 0.02f) {
                    if (assetManager.get(AssetDescriptors.MOWER_STOP).isPlaying())
                        assetManager.get(AssetDescriptors.MOWER_STOP).stop();
                    state = 0;  // starts
                }
        }
    }
}
