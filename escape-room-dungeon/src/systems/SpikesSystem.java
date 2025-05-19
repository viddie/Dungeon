package systems;

import components.SpikesComponent;
import components.VicinityComponent;
import core.Entity;
import core.Game;
import core.System;
import core.components.DrawComponent;
import core.components.PositionComponent;
import core.utils.Point;
import utils.Constants;
import utils.SoundManager;
import utils.Sounds;

import java.util.ArrayList;
import java.util.List;

public class SpikesSystem extends System {

  private static final int SHOW_BRIEFLY_FRAMES = 60;

  private final List<Data> showBriefly = new ArrayList<>();

  @Override
  public void execute() {
    filteredEntityStream(PositionComponent.class, SpikesComponent.class, DrawComponent.class)
      .map(this::buildDataObject)
      .forEach(this::execute);
  }

  public void execute(Data ssd) {
    //Update image
    if(ssd.sc().active()){
      ssd.dc().sendSignal("on");
    } else if(ssd.sc().showTimer() > 0){
      ssd.sc().showTimer(ssd.sc().showTimer() - 1);
      ssd.dc().sendSignal("on");
    } else {
      ssd.dc().sendSignal("off");
    }

    if(!ssd.sc().deadly()) return;

    PositionComponent heroPc = Game.hero().orElseThrow().fetchOrThrow(PositionComponent.class);
    Point heroPos = heroPc.position();
    Point spikePos = ssd.pc().position();
//    Point spikePos = ssd.pc().position().add(0.5f, 0.5f);

    //Check if player is overlapping
    if(heroPos.distance(spikePos) > 0.5f) return;

    //If yes, send player to the designated square
    heroPc.position(Constants.offset(ssd.sc().sendTo()));
    SoundManager.playSound(Sounds.SpikeTrap);
    if(ssd.sc().showBriefly()){
      ssd.sc().showTimer(SHOW_BRIEFLY_FRAMES);
    }

    //Prevent movement for X frames
  }

  private Data buildDataObject(Entity e){
    return new Data(
      e,
      e.fetchOrThrow(PositionComponent.class),
      e.fetchOrThrow(SpikesComponent.class),
      e.fetchOrThrow(DrawComponent.class)
    );
  }
  private record Data(Entity e, PositionComponent pc, SpikesComponent sc, DrawComponent dc) {}
}
