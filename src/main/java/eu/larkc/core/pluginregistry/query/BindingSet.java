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
import java.util.Iterator;

/**
 * @author Blaz Fortuna
 */
public class BindingSet implements Iterable<Binding> {

  ArrayList<Binding> bindingSet;

  public BindingSet() {
    bindingSet = new ArrayList<Binding>();
  }

  public void addBinding(Binding binding) {
    bindingSet.add(binding);
  }

  public boolean isEmpty() {
    return bindingSet.isEmpty();
  }

  @Override
  public Iterator<Binding> iterator() {
    return bindingSet.iterator();
  }
}
