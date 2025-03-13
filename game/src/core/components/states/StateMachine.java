package core.components.states;

import com.badlogic.gdx.graphics.g2d.Sprite;
import core.utils.components.path.IPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StateMachine {

  private State currentState;
  private int frameCount = 0;
  private final List<State> states;
  private final Map<State, List<Transition>> transitions = new HashMap<>();

  public StateMachine(IPath path, SpritesheetConfig config){
    states = new ArrayList<>();
    states.add(new State("normal", path, config));
    currentState = states.get(0);
  }
  public StateMachine(IPath path){
    this(path, null);
  }
  public StateMachine(AnimationConfig config){
    states = new ArrayList<>();
    states.add(new State("normal", config));
    currentState = states.get(0);
  }
  public StateMachine(List<State> states){
    if(states.size() == 0) throw new IllegalArgumentException("State list can't be empty");
    this.states = states;
    currentState = states.get(0);
  }


  public State getCurrentState(){
    return currentState;
  }
  public State getState(String name){
    if(name == null) throw new IllegalArgumentException("name can't be empty");
    return states.stream().filter(s -> s.name.equals(name)).findFirst().orElse(null);
  }
  public State addState(State state){
    State existing = getState(state.name);
    if(existing != null) removeState(existing);
    addState(state);
    return existing;
  }

  public State removeState(String name){
    State existing = getState(name);
    if(existing != null) removeState(existing);
    return existing;
  }
  public boolean removeState(State state){
    removeAllTransitions(state);
    return states.remove(state);
  }

  public Transition addTransition(String from, String signal, String to){
    State stFrom = getState(from);
    State stTo = getState(to);
    if(stFrom == null) throw new IllegalArgumentException("State '"+from+"' doesn't exist");
    if(stTo == null) throw new IllegalArgumentException("State '"+to+"' doesn't exist");
    return addTransition(stFrom, signal, stTo);
  }
  public Transition addTransition(State from, String signal, State to){
    List<Transition> fromTransitions = getTransitionList(from);
    Transition existing = fromTransitions.stream().filter(t -> t.signal().equals(signal)).findFirst().orElse(null);
    if(existing != null) fromTransitions.remove(existing);
    fromTransitions.add(new Transition(signal, to));
    return existing;
  }

  private void removeAllTransitions(State state){
    //Remove the state from the transitions
    transitions.remove(state);
    //Also remove any transition targeting the state
    transitions.values().forEach(transitionList -> {
      List<Transition> toRemove = transitionList.stream().filter(t -> t.targetState() == state).collect(Collectors.toList());
      toRemove.forEach(transitionList::remove);
    });
  }
  public boolean removeTransition(String from, String signal){
    State stFrom = getState(from);
    if(stFrom == null) throw new IllegalArgumentException("State '"+from+"' doesn't exist");
    return removeTransition(stFrom, signal);
  }
  public boolean removeTransition(State from, String signal){
    List<Transition> fromTransitions = getTransitionList(from);
    Transition transition = fromTransitions.stream().filter(t -> t.signal().equals(signal)).findFirst().orElse(null);
    if(transition != null) return fromTransitions.remove(transition);
    return false;
  }
  private List<Transition> getTransitionList(State state){
    if(!transitions.containsKey(state)){
      transitions.put(state, new ArrayList<>());
    }
    return transitions.get(state);
  }


  public void sendSignal(Signal signal){
    Transition transition = getTransitionList(currentState).stream().filter(t -> t.signal().equals(signal.signal)).findFirst().orElse(null);
    if(transition == null) return;
    State newState = transition.targetState();
    newState.setData(signal.data);
    frameCount = 0;
    currentState = newState;
  }
  public void update(){
    frameCount++;
  }
  public Sprite getSprite(){
    return currentState.getSprite(frameCount);
  }

}
