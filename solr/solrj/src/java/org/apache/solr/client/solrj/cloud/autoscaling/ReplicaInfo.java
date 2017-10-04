/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.solr.client.solrj.cloud.autoscaling;

import java.io.IOException;
import java.util.Map;

import org.apache.solr.common.MapWriter;
import org.apache.solr.common.cloud.Replica;
import org.apache.solr.common.cloud.ZkStateReader;


public class ReplicaInfo implements MapWriter {
  final String name;
  String core, collection, shard;
  Map<String, Object> variables;

  public ReplicaInfo(String name, String coll, String shard, Map<String, Object> vals) {
    this.name = name;
    this.variables = vals;
    this.collection = coll;
    this.shard = shard;
    this.core = (String)vals.get("core");
  }

  @Override
  public void writeMap(EntryWriter ew) throws IOException {
    ew.put(name, (MapWriter) ew1 -> {
      if (variables != null) {
        for (Map.Entry<String, Object> e : variables.entrySet()) {
          ew1.put(e.getKey(), e.getValue());
        }
      }
    });
  }

  public String getName() {
    return name;
  }

  public String getCore() {
    return core;
  }

  public String getCollection() {
    return collection;
  }

  public String getShard() {
    return shard;
  }

  public Replica.Type getType() {
    return Replica.Type.get((String) variables.get(ZkStateReader.REPLICA_TYPE));
  }

  public Replica.State getState() {
    if (variables.get(ZkStateReader.STATE_PROP) != null) {
      return Replica.State.getState((String) variables.get(ZkStateReader.STATE_PROP));
    } else {
      // default to ACTIVE
      variables.put(ZkStateReader.STATE_PROP, Replica.State.ACTIVE.toString());
      return Replica.State.ACTIVE;
    }
  }

  public Map<String, Object> getVariables() {
    return variables;
  }

  public Object getVariable(String name) {
    return variables != null ? variables.get(name) : null;
  }

  @Override
  public String toString() {
    return "ReplicaInfo{" +
        "name='" + name + '\'' +
        ", collection='" + collection + '\'' +
        ", shard='" + shard + '\'' +
        ", variables=" + variables +
        '}';
  }
}
