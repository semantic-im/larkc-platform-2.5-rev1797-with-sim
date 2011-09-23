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
package eu.larkc.core.pluginManager.remote.GAT;

/**
 * Contains the values of some pre-defined JavaGAT software description
 * properties. Used for validation of the user settings.
 * 
 * @author Alexey Cheptsov
 * 
 */
public class GatOptions {

	public enum FileAdaptors {
		/**
		 * The local file adaptor.
		 */
		local,
		/**
		 * The file adaptors for scp.
		 */
		commandlinessh, sshtrilead,
		/**
		 * The file adaptor for GT4.
		 */
		gt4gridftp
	}

	public enum Brokers {
		/**
		 * The local broker.
		 */
		local,
		/**
		 * The brokers for ssh.
		 */
		commandlinessh, sshtrilead,
		/**
		 * The broker for GT4.
		 */
		wsgt4new
	}

}
