// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package codeu.chat.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import codeu.chat.util.Serializer;
import codeu.chat.util.Serializers;
import codeu.chat.util.Time;
import codeu.chat.util.Uuid;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// ignore anything that may cause problems when converting to JSON
// prevents crashes for items that cant be serialized.
@JsonIgnoreProperties(ignoreUnknown=true)
public final class User {
  
  // no need to serialize the serializer when converting to JOSN
  @JsonIgnore
  public static final Serializer<User> SERIALIZER = new Serializer<User>() {

    @Override
    public void write(OutputStream out, User value) throws IOException {

      Uuid.SERIALIZER.write(out, value.id);
      Serializers.STRING.write(out, value.name);
      Time.SERIALIZER.write(out, value.creation);

    }

    @Override
    public User read(InputStream in) throws IOException {
      return new User(
          Uuid.SERIALIZER.read(in),
          Serializers.STRING.read(in),
          Time.SERIALIZER.read(in)
      );
    }
  };

  // ignore objects that aren't built-in types (string, int, etc)
  // Jackons will default to the getters since instance vars are ignored
  @JsonIgnore
  public final Uuid id;
  public final String name;
  @JsonIgnore
  public final Time creation;

  public User(Uuid id, String name, Time creation) {
    this.id = id;
    this.name = name;
    this.creation = creation;
  }
  
  public String getUUID() {
	  return id.toString();
  }
  
  public String getCreationTime() {
	  return creation.toString();
  }

  @Override
  public boolean equals (Object toCompare)
  {
    if(!(toCompare instanceof User)) {
      return false;
    }
    User toCompareUser = (User)(toCompare);
    if(!this.id.equals(toCompareUser.id)) {
      return false;
    }
    if(!this.name.equals(toCompareUser.name)) {
      return false;
    }
    if(!this.creation.equals(toCompareUser.creation)) {
      return false;
    }
    return true;
  }

  public int hashCode() { return hash(this); }

  private static int hash(User user) {
    int hash = 0;
    hash+=user.id.hashCode();
    hash+=user.name.hashCode();
    hash+=user.creation.hashCode();
    return hash;
  }
}
