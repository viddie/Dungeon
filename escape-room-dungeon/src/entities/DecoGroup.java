package entities;

public enum DecoGroup {
  Rubble(Deco.Rubble0, Deco.Rubble1, Deco.Rubble2, Deco.Rubble3),
  Chains(Deco.Chains0, Deco.Chains1, Deco.Chains2, Deco.Chains3, Deco.Chains4, Deco.Chains5, Deco.Chains6, Deco.Chains7, Deco.Chains8),
  F1FakePillars(Deco.StonePillar1, Deco.StonePillar2),
  ;

  private Deco[] elements;

  DecoGroup(Deco... decos){
    elements = decos;
  }

  public Deco[] getElements() {
    return elements;
  }
  public Deco getOne(int i){
    return elements[i%elements.length];
  }
}
