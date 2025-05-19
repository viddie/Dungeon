package entities;

import components.DebugRenderComponent;
import components.SpikesComponent;
import contrib.components.CollideComponent;
import core.Entity;
import core.components.DrawComponent;
import core.components.PositionComponent;
import core.components.states.AnimationConfig;
import core.components.states.SpritesheetConfig;
import core.components.states.State;
import core.components.states.StateMachine;
import core.utils.Point;
import core.utils.components.path.IPath;
import core.utils.components.path.SimpleIPath;
import utils.Constants;

import java.util.Arrays;

public class SpikesFactory {

  private static final IPath SPRITESHEET = new SimpleIPath("spritesheets/FD_Dungeon_Free.png");
  private static final SpritesheetConfig CONFIG_EXTENDED = new SpritesheetConfig(20*16, 21*16);
  private static final SpritesheetConfig CONFIG_RETRACTED = new SpritesheetConfig(22*16, 21*16);

  public static Entity createSpikes(Point pos, boolean isActive, boolean isDeadly, Point sendTo){
    Entity entity = new Entity("spikes");
    entity.add(new PositionComponent(Constants.offset(pos)));

    State stOn = new State("extended", SPRITESHEET, CONFIG_EXTENDED);
    State stOff = new State("retracted", SPRITESHEET, CONFIG_RETRACTED);
    StateMachine sm = new StateMachine(Arrays.asList(stOn, stOff));
    sm.addTransition(stOn, "off", stOff);
    sm.addTransition(stOff, "on", stOn);
    DrawComponent dc = new DrawComponent(sm);
    entity.add(dc);

    //Default image if off
    if(!isActive) dc.sendSignal("off");

    entity.add(new SpikesComponent(isActive, isDeadly, sendTo, 180));

//    entity.add(new DebugRenderComponent().drawCollider(true));

    return entity;
  }

}
