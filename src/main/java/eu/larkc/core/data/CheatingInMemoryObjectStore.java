/*
   This file is part of the LarKC platform 
   http://www.larkc.eu/

   Copyright 2010 LarKC project consortium

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package eu.larkc.core.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Cheating class to pass objects as object references. The data layer should be
 * cleaned up and this class should no longer be used. Objects are never
 * removed, so this is also a memory leak. FIXME: find an alternative and delete
 * this class.
 * 
 * @author spyros
 */
public class CheatingInMemoryObjectStore {

	private int counter = 0;

	private Map<String, Object> storedObjects = new HashMap<String, Object>();

	private static CheatingInMemoryObjectStore instance = null;

	private CheatingInMemoryObjectStore() {
	}

	/**
	 * Store an object
	 * 
	 * @param object
	 * @return
	 */
	public String storeObject(Object object) {
		if (object != null) {
			String newID = (counter++) + "";
			storedObjects.put(newID, object);
			return newID;
		} else
			return "";
	}

	/**
	 * Get the instance of the store TODO Describe the purpose of this method.
	 * 
	 * @return
	 */
	public static CheatingInMemoryObjectStore getInstance() {
		if (instance == null)
			instance = new CheatingInMemoryObjectStore();
		return instance;
	}

	/**
	 * TODO Describe the purpose of this method.
	 * 
	 * @param stringValue
	 * @return
	 */
	public Object get(String id) {
		return storedObjects.get(id);
	}

}
