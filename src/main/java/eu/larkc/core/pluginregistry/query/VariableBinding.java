/***
 *   Copyright (c) 1995-2010 Cycorp R.E.R. d.o.o
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package eu.larkc.core.pluginregistry.query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Default variable binding implementation.
 * 
 * @author Luka Bradesko, Blaz Fortuna
 */
public class VariableBinding implements Iterable<BindingSet> {

  HashSet<String> variableNames;
  ArrayList<BindingSet> bindingSets;

  public VariableBinding() {
    variableNames = new HashSet<String>();
    bindingSets = new ArrayList<BindingSet>();
  }

  public void addBindingSet(BindingSet bindingSet) {
    bindingSets.add(bindingSet);
    for (Binding b : bindingSet) {
      if (!variableNames.contains(b.getName())) {
        variableNames.add(b.getName());
      }
    }
  }

  public Set<String> GetVariableNames() {
    return variableNames;
  }

  @Override
  public Iterator<BindingSet> iterator() {
    return bindingSets.iterator();
  }

  /**
   * constructs test variable binding
   * @return dummy variable binding for testing
   */
  public static VariableBinding getTestVariableBinding() {
    VariableBinding vb = new VariableBinding();
    {
      BindingSet bs = new BindingSet();
      bs.addBinding(new Binding("book", Value.getUri("http://www.example/book/book5")));
      bs.addBinding(new Binding("who", Value.getLiteral("Janez")));
      vb.addBindingSet(bs);
    }
    {
      BindingSet bs = new BindingSet();
      bs.addBinding(new Binding("book", Value.getUri("http://www.example/book/book2")));
      bs.addBinding(new Binding("who", Value.getLiteral("Novak")));
      vb.addBindingSet(bs);
    }
    return vb;
  }
}
