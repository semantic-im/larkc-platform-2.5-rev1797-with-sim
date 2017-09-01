# About
This is a fork from [Larkc project](http://www.larkc.org/) version 2.5 in order to add [SIM](http://semantic-im.github.io/).
# Notice
We have received a DMCA takedown notice from Cycorp, Inc., a Larkc partner, for all files from /src/main/java/com/cyc/\* and src/main/resources/cyc-tiny/\*.

Looking at those files we can see most of them are Apache licensed. Some examples:
* src/main/java/com/cyc/cycjava/cycl/cfasl_kernel.java
* src/main/java/com/cyc/cycjava/cycl/c_backend.java
* src/main/java/com/cyc/cycjava/cycl/deductions_high.java

	/***
	 *   Copyright (c) 1995-2009 Cycorp Inc.
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
	 *
	 *  Substantial portions of this code were developed by the Cyc project
	 *  and by Cycorp Inc, whose contribution is gratefully acknowledged.
	*/

Also, when joining the Larkc project, Cycorp pledged to provide the code under Apache license, the same license that whole Larkc project was developed under.

These are some quotes from official Larkc documents:
>...
CycEur will make Research Cyc available under Apache 2 or similar license.

>...
Cycorp Europe has provided source code and IP releases for the components of Research Cyc necessary to realise the LarKC Platform and to distribute it under the Apache 2.0 or a substantially similar open source licence. This contributed material will include the core of the Research Cyc inference engine, including the Java runtime, inference strategist and tactician meta-reasoning infrastructure, and existing support for incomplete reasoning, distribution, and handling inference plug-ins. The code released is a substantial contribution, having resulted from more than twenty years of development, and several million dollars of investment by Cycorp Europe's parent company. Cycorp Europe believes that the LarKC project has the potential to revolutionise AI research in Europe and beyond, and that achieving that would more than justify the donation of this corporate asset. 

Nevertheless, in order to avoid the takedown of whole repo, we have complied with the request and we have eliminated (rewritten git history) from the repo all files identified by Cycorp.