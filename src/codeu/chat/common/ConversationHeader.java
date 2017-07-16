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
import java.util.HashMap;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import codeu.chat.util.Serializer;
import codeu.chat.util.Serializers;
import codeu.chat.util.Time;
import codeu.chat.util.Uuid;

@JsonIgnoreProperties(ignoreUnknown=true)
public final class ConversationHeader {

  // no need to serialize the serializer when converting to JOSN
  public static final Serializer<ConversationHeader> SERIALIZER = new Serializer<ConversationHeader>() {

    @Override
    public void write(OutputStream out, ConversationHeader value) throws IOException {

      Uuid.SERIALIZER.write(out, value.id);
      Uuid.SERIALIZER.write(out, value.owner);
      Time.SERIALIZER.write(out, value.creation);
      Serializers.STRING.write(out, value.title);

    }

    @Override
    public ConversationHeader read(InputStream in) throws IOException {

      return new ConversationHeader(
              Uuid.SERIALIZER.read(in),
              Uuid.SERIALIZER.read(in),
              Time.SERIALIZER.read(in),
              Serializers.STRING.read(in)
      );

    }
  };
  
  //ignore objects that aren't built-in types (string, int, etc)
  // Jackon will default to the getters since instance vars are ignored
  @JsonIgnore
  public final Uuid id;
  @JsonIgnore
  public final Uuid owner;
  @JsonIgnore
  public final Time creation;
  public final String title;
  // default type a user is assigned when they call "c-join" on this convo
  public UserType defaultType = UserType.MEMBER;
  
  // a hashmap with user id as key and their usertype as value
  private HashMap<Uuid, UserType> userAccessRoles = new HashMap<Uuid, UserType>();

  public ConversationHeader(Uuid id, Uuid owner, Time creation, String title) {

    this.id = id;
    this.owner = owner;
    this.creation = creation;
    this.title = title;

  }
  
  public String getUUID() {
	  return id.toString();
  }
  
  public String getOwnerUUID() {
	  return owner.toString();
  }
  
  public long getCreationTime() {
	  return creation.inMs();
  }
  
  @Override
  public boolean equals(Object toCompare)
  {
    if(!(toCompare instanceof ConversationHeader)) {
      return false;
    }
    ConversationHeader toCompareConv = (ConversationHeader)(toCompare);
    if(!this.id.equals(toCompareConv.id)) {
      return false;
    }
    if(!this.owner.equals(toCompareConv.owner)) {
      return false;
    }
    if(!this.creation.equals(toCompareConv.creation)) {
      return false;
    }
    if(!this.title.equals(toCompareConv.title)) {
      return false;
    }
    return true;
  }

  public int hashCode() { return hash(this); }

  private static int hash(ConversationHeader conv) {
    int hash = 0;
    hash+=conv.id.hashCode();
    hash+=conv.owner.hashCode();
    hash+=conv.creation.hashCode();
    hash+=conv.title.hashCode();
    return hash;
  }
  
  public UserType getAccessOf(Uuid idOfUser) {
	  UserType type = this.userAccessRoles.get(idOfUser);
	  // if user is not in map, assign them default type
	  if (type == null) {
		  return this.defaultType;
	  }
	  return type;
  }

  public void setAccessOf(Uuid idOfUser, UserType type) {
	  userAccessRoles.put(idOfUser, type);
  }
}
