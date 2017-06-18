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
public final class Message {
	
  // no need to serialize the serializer when converting to JOSN
  @JsonIgnore
  public static final Serializer<Message> SERIALIZER = new Serializer<Message>() {

    @Override
    public void write(OutputStream out, Message value) throws IOException {

      Uuid.SERIALIZER.write(out, value.id);
      Uuid.SERIALIZER.write(out, value.next);
      Uuid.SERIALIZER.write(out, value.previous);
      Time.SERIALIZER.write(out, value.creation);
      Uuid.SERIALIZER.write(out, value.author);
      Serializers.STRING.write(out, value.content);

    }

    @Override
    public Message read(InputStream in) throws IOException {

      return new Message(
          Uuid.SERIALIZER.read(in),
          Uuid.SERIALIZER.read(in),
          Uuid.SERIALIZER.read(in),
          Time.SERIALIZER.read(in),
          Uuid.SERIALIZER.read(in),
          Serializers.STRING.read(in)
      );

    }
  };
  //ignore objects that aren't built-in types (string, int, etc)
  // Jackon will default to the getters since instance vars are ignored
  @JsonIgnore
  public final Uuid id;
  @JsonIgnore
  public final Uuid previous;
  @JsonIgnore
  public final Time creation;
  @JsonIgnore
  public final Uuid author;
  public final String content;
  @JsonIgnore
  public Uuid next;

  public Message(Uuid id, Uuid next, Uuid previous, Time creation, Uuid author, String content) {

    this.id = id;
    this.next = next;
    this.previous = previous;
    this.creation = creation;
    this.author = author;
    this.content = content;

  }
  
  public String getUUID() {
	  return id.toString();
  }
  
  public String getAuthorUUID() {
	  return author.toString();
  }
  
  public long getCreationTime() {
	  return creation.inMs();
  }

  @Override
  public boolean equals(Object toCompare)
  {
    if(!(toCompare instanceof Message))
    {
      return false;
    }
    Message toCompareMessage = (Message)(toCompare);
    if(!this.id.equals(toCompareMessage.id)) {
      return false;
    }
    if(!this.next.equals(toCompareMessage.next)) {
      return false;
    }
    if(!this.previous.equals(toCompareMessage.previous)) {
      return false;
    }
    if(!this.creation.equals(toCompareMessage.creation)) {
      return false;
    }
    if(!this.author.equals(toCompareMessage.author)) {
      return false;
    }
    if(!this.content.equals(toCompareMessage.content)) {
      return false;
    }
    return true;
  }

  public int hashCode() { return hash(this); }

  private static int hash(Message mess) {
    int hash = 0;
    hash+=mess.id.hashCode();
    hash+=mess.next.hashCode();
    hash+=mess.previous.hashCode();
    hash+=mess.creation.hashCode();
    hash+=mess.author.hashCode();
    hash+=mess.content.hashCode();

    return hash;
  }

}
