/*
 * Copyright 2016 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.speedbooster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.logic.characters.CharacterImpulseEvent;
import org.terasology.logic.characters.CharacterMovementComponent;
import org.terasology.logic.characters.events.OnEnterBlockEvent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3f;
import org.terasology.registry.In;
import org.terasology.speedbooster.component.SpeedBoosterComponent;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;

@RegisterSystem(RegisterMode.AUTHORITY)
public class SpeedBoosterSystem extends BaseComponentSystem implements UpdateSubscriberSystem {
    private static final Logger logger = LoggerFactory.getLogger(SpeedBoosterSystem.class);

    @In
    private BlockEntityRegistry blockEntityProvider;

    @In
    private EntityManager entityManager;

    @In
    private WorldProvider worldProvider;

    @Override
    public void update(float delta) {}

    @ReceiveEvent(components = {LocationComponent.class, CharacterMovementComponent.class})
    public void onEnterBlock(OnEnterBlockEvent event, EntityRef entity) {

        LocationComponent loc = entity.getComponent(LocationComponent.class); // get player LocationComponent
        Vector3f pos = loc.getWorldPosition(); // get player position
        pos.setY(pos.getY() -1); // get position below player
        Block block = worldProvider.getBlock(pos); // get block at that position
        CharacterMovementComponent cmc = entity.getComponent( CharacterMovementComponent.class );
        SpeedBoosterComponent sbc = block.getEntity().getComponent( SpeedBoosterComponent.class );
        if ( sbc != null ) {
            // this will increase the speed of player
            cmc.speedMultiplier = sbc.speedMultiplier;
            entity.saveComponent(cmc);
            Vector3f imp = cmc.getVelocity().normalize().scale(64).setY(6);
            entity.send(new CharacterImpulseEvent(imp));
        } else {
            // this will return speed of player to normal
            cmc.speedMultiplier = 1f;
            entity.saveComponent(cmc);
        }
    }



}

