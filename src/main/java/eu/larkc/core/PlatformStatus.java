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
package eu.larkc.core;

/**
 * Enum used by the LarKC platform to keep track of the platform status. The
 * life-cycle of a successful start of the platform looks as follows:
 * <ol>
 * <li>NOT_INITIALIZED</li>
 * <li>INITIALIZING</li>
 * <li>RUNNING</li>
 * <li>STOPPED</li>
 * </ol>
 * The life-cycle of a failed attempt to start the platform will never be in the
 * <code>RUNNING</code> state.
 * <ol>
 * <li>NOT_INITIALIZED</li>
 * <li>INITIALIZING</li>
 * <li>STOPPED</li>
 * </ol>
 * 
 * @author Christoph Fuchs
 * 
 */
public enum PlatformStatus {
	/**
	 * The platform has not been initialized yet.
	 */
	NOT_INITIALIZED,
	/**
	 * The platform is initializing.
	 */
	INITIALIZING,
	/**
	 * The platform is running.
	 */
	RUNNING,
	/**
	 * The platform was stopped. Either intentionally or automatically if an
	 * error occured during initialization.
	 */
	STOPPED
}
